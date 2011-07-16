/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.xml.XMLWriter;
import gov.nasa.worldwind.util.Logging;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MapSource corresponds to the <code>&lt;MapSource&gt;</code> configuration elements in the
 * <code>WEB-INF/config.xml</code> configuration file. The XML form for a MapSource is: <p/>
 * <pre>
 *     &lt;MapSource name=" layer name " title=" layer title "&gt;
 *         &lt;description keywords="..."&gt; layer description &lt;/description&gt;
 *         &lt;root-dir&gt; path to root of data directory for layer &lt;/root-dir&gt;
 *         &lt;class&gt;...name of class implementing MapGenerator to serve layer...&lt;/class&gt;
 *         &lt;!-- zero or more properties recognized by MapGenerator class
 *             &lt;property name="..." value="..." /&gt;
 *          --&gt;
 *     &lt;/MapSource&gt;
 * </pre>
 * <p/> <p> See the javadoc for the optional or required properties recognized by a specific MapGenerator implementing
 * class. </p> <p>Note that the name, title, keyword attributes, along with the description element, are used in the WMS
 * GetCapabilities response, and are thus potentially forward-facing text strings. </p>
 *
 * @author garakl
 * @version $Id$
 */
public class MapSource
{
    protected MapSource parent;

    protected String name;
    protected boolean hideName = false;

    protected String title;
    protected String rootDir;
    protected Class serviceClass;
    protected String description;
    protected String keywords;
    protected String lastUpdate = null;
    protected AVList params = new AVListImpl();
    protected MapGenerator mapGenerator;
    protected Vector<MapSource> nestedMapSources = new Vector<MapSource>(1);

//  <ScaleHint max="362.038672" min="1.414214"/>

    protected double scaleHintMin = 0d;
    protected double scaleHintMax = 0d;

//    private static final String nestedNamesSep = "|";

    protected MapSource(MapSource parent, AVList params)
    {
        this.params = (null != params) ? params : new AVListImpl();
        this.parent = parent;

        this.init();
    }

    protected MapSource()
    {
    }

    public MapSource(
            MapSource parent,
            String name,
            String title,
            String rootDir,
            Class serviceClass,
            String description,
            String keywords,
            double scaleHintMin,
            double scaleHintMax,
            Properties props
    )
    {
        this.hideName = false;

        String sourceName = name;
        if (sourceName == null || sourceName.trim().length() == 0)
        {
            // There are MapSources configured with "title", but without "name".
            // Our implementation requires every map source to have a name
            // therefore, we will have a flag "hideName" set if originally the name was missing
            this.hideName = true;
            sourceName = title;
        }

        if (sourceName == null || sourceName.length() == 0)
        {
            String message = "MapSource name and title are null";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.parent = parent;
        this.name = sourceName.replace(",", "_");  // commas trip up "Layers=" clause in GetMap/Feature requests...
        this.title = title;
        this.rootDir = rootDir;
        this.serviceClass = serviceClass;
        this.description = description;
        this.keywords = keywords;
        this.scaleHintMin = scaleHintMin;
        this.scaleHintMax = scaleHintMax;

        if (null != props)
        {
            if (null == this.params)
            {
                this.params = new AVListImpl();
            }

            Enumeration em = props.keys();
            while (em.hasMoreElements())
            {
                String key = (String) em.nextElement();
                if (null != key && 0 != key.trim().length())
                {
                    this.params.setValue(key, props.get(key.trim()));
                }
            }
        }

        this.init();
    }

    private void init()
    {
        if (this.hasValue(AVKey.LAST_UPDATE))
        {
            this.lastUpdate = this.parseLastUpdate(this.getValueAsString(AVKey.LAST_UPDATE));
        }

        if (null == this.lastUpdate && null != this.parent)
        {
            this.lastUpdate = this.parent.getLastUpdate();
        }
    }

    public void setScaleHint(double min, double max)
    {
        this.scaleHintMin = min;
        this.scaleHintMax = max;
    }

    public double getScaleHintMin()
    {
        return this.scaleHintMin;
    }

    public double getScaleHintMax()
    {
        return this.scaleHintMax;
    }

    public String getName()
    {
        return this.name;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getRootDir()
    {
        return this.rootDir;
    }

    public Class getServiceClass()
    {
        return this.serviceClass;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getKeywords()
    {
        return this.keywords;
    }

    public AVList getParameters()
    {
//        return this.params.copy();
        return this.params;
    }

    public Properties getProperties()
    {
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : this.params.getEntries())
        {
            props.put(entry.getKey(), entry.getValue());
        }
        return props;
    }

    public String getMissingDataSignal()
    {
        if (this.hasValue(AVKey.MISSING_DATA_SIGNAL))
        {
            return this.getValueAsString(AVKey.MISSING_DATA_SIGNAL);
        }
        else
        {
            return "-9999"; // "-32768" or "--32767" (rare) or "0"
        }
    }

    public String getMissingDataReplacement()
    {
        if (this.hasValue(AVKey.MISSING_DATA_REPLACEMENT))
        {
            return this.getValueAsString(AVKey.MISSING_DATA_REPLACEMENT);
        }
        else
        {
            return "" + Short.MIN_VALUE;
        }
    }

    public String getExtremeElevationsMinimum()
    {
        if (this.hasValue(AVKey.ELEVATION_MIN))
        {
            return this.getValueAsString(AVKey.ELEVATION_MIN);
        }
        else
        {
            return null;
        }
    }

    public String getExtremeElevationsMaximum()
    {
        if (this.hasValue(AVKey.ELEVATION_MAX))
        {
            return this.getValueAsString(AVKey.ELEVATION_MAX);
        }
        else
        {
            return null;
        }
    }

    protected String parseLastUpdate(String datetime)
    {
        if (null != datetime && datetime.length() > 0)
        {
            // try first as a long number
            try
            {
                long time = Long.parseLong(datetime);
                return datetime;
            }
            catch (Exception e)
            {
                Logging.logger().finest(e.getMessage());
            }
            try
            {
                SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = sdf.parse(datetime);
                String s = Long.toString(d.getTime());
                Logging.logger().finest("Last time converted from " + datetime + " to " + s);
                return s;
            }
            catch (Exception e)
            {
                Logging.logger().finest(e.getMessage());
            }
        }
        return null;
    }

    public String getLastUpdate()
    {
        if (null != this.lastUpdate)
        {
            return this.lastUpdate;
        }

        if (this.hasValue(AVKey.LAST_UPDATE))
        {
            this.lastUpdate = this.parseLastUpdate(this.getValueAsString(AVKey.LAST_UPDATE));
        }

        if (null == this.lastUpdate)
        {   // let's check cildren layers
            long lastTime = 0L;
            Iterator<MapSource> iter = this.getChildren();
            while (iter.hasNext())
            {
                String s = iter.next().getLastUpdate();
                if (s != null)
                {
                    try
                    {
                        long time = Long.parseLong(s);
                        if (time > lastTime)
                        {
                            lastTime = time;
                            this.lastUpdate = s;
                            Logging.logger().info("Set children's last update time: " + s);
                        }
                    }
                    catch (Exception ignore)
                    {
                    }
                }
            }
        }

        return this.lastUpdate;
    }

    public void setParent(MapSource ms)
    {
        this.parent = ms;
    }

    public MapSource getParent()
    {
        return this.parent;
    }

    public MapGenerator getMapGenerator() throws Exception
    {
        if (this.mapGenerator == null)
        {
            this.mapGenerator = (MapGenerator) this.getServiceClass().newInstance();
        }

        return this.mapGenerator;
    }

    public void addChild(MapSource ms)
    {
        this.nestedMapSources.add(ms);
    }

    public Iterator<MapSource> getChildren()
    {
        return this.nestedMapSources.iterator();
    }

    public String toXML()
    {
        Writer writer = new java.io.StringWriter();

        try
        {
            XMLWriter xmlwriter = new XMLWriter(writer);
            this.doWriteXML(xmlwriter);
        }
        catch (Exception e)
        {
            Logging.logger().severe(e.getMessage());
        }
        return writer.toString();
    }

    protected void doWriteXML(XMLWriter xmlwriter) throws Exception
    {
        xmlwriter.openElement("Layer");

        this.writeLayerXML(xmlwriter);
        this.writeChildrenXML(xmlwriter);

        xmlwriter.closeElement("Layer");

//    @XmlElement(name = "Dimension")
//    protected List<Dimension> dimension;
//    @XmlElement(name = "Attribution")
//    protected Attribution attribution;
//    @XmlElement(name = "AuthorityURL")
//    protected List<AuthorityURL> authorityURL;
//    @XmlElement(name = "Identifier")
//    protected List<Identifier> identifier;
//    @XmlElement(name = "MetadataURL")
//    protected List<MetadataURL> metadataURL;
//    @XmlElement(name = "DataURL")
//    protected List<DataURL> dataURL;
//    @XmlElement(name = "FeatureListURL")
//    protected List<FeatureListURL> featureListURL;
//    @XmlElement(name = "Style")
//    protected List<Style> style;
//    @XmlElement(name = "MinScaleDenominator")
//    protected Double minScaleDenominator;
//    @XmlElement(name = "MaxScaleDenominator")
//    protected Double maxScaleDenominator;
//    @XmlElement(name = "Layer")
//    protected List<Layer> layer;
//    @XmlAttribute
//    protected BigInteger cascaded;
//    @XmlAttribute
//    protected BigInteger fixedHeight;
//    @XmlAttribute
//    protected BigInteger fixedWidth;
//    @XmlAttribute
//    protected Boolean noSubsets;
//    @XmlAttribute
//    protected Boolean opaque;
//    @XmlAttribute
//    protected Boolean queryable;
    }

    protected void writeLayerXML(XMLWriter xmlwriter) throws Exception
    {
        this.writeLayerAttributes(xmlwriter);
        this.writeLayerElements(xmlwriter);
    }

    protected void writeLayerAttributes(XMLWriter xmlwriter) throws Exception
    {
        xmlwriter.addAttribute("queryable", "false");
    }

    protected void writeLayerElements(XMLWriter xmlwriter) throws Exception
    {
        if (!this.hideName && !"".equals(this.getName()))
        {
            xmlwriter.addElement("Name", this.getName());
        }

        xmlwriter.addElement("Title", this.getTitle());

        String desc = this.getDescription();
        if (null != desc && 0 != (desc = desc.trim()).length())
        {
            xmlwriter.addElement("Abstract", desc);
        }

        String keywords = this.getKeywords();
        if (keywords != null && 0 != (keywords = keywords.trim()).length())
        {
            xmlwriter.openElement("KeywordList");
            String[] keys = keywords.split(";");
            for (String key : keys)
            {
                xmlwriter.addElement("Keyword", key);
            }
            xmlwriter.closeElement("KeywordList");
        }

        //BoundingBox
        MapGenerator mapGen = this.getMapGenerator();
        if (mapGen != null)
        {
            String crs = mapGen.getCRS()[0];
            xmlwriter.addElement("CRS", crs);  // TODO: ONLY ONE FOR NOW

            Sector bnds = mapGen.getBBox();
            if (bnds != null)
            {
                xmlwriter.openElement("EX_GeographicBoundingBox");
                xmlwriter.addElement("westBoundLongitude", bnds.getMinLongitude().degrees);
                xmlwriter.addElement("eastBoundLongitude", bnds.getMaxLongitude().degrees);
                xmlwriter.addElement("southBoundLatitude", bnds.getMinLatitude().degrees);
                xmlwriter.addElement("northBoundLatitude", bnds.getMaxLatitude().degrees);
                xmlwriter.closeElement("EX_GeographicBoundingBox");

                xmlwriter.openElement("BoundingBox");
                xmlwriter.addAttribute("miny", bnds.getMinLatitude().degrees);
                xmlwriter.addAttribute("minx", bnds.getMinLongitude().degrees);
                xmlwriter.addAttribute("maxy", bnds.getMaxLatitude().degrees);
                xmlwriter.addAttribute("maxx", bnds.getMaxLongitude().degrees);
                xmlwriter.addAttribute("CRS", crs);
                xmlwriter.closeElement("BoundingBox");
            }
        }

        // TODO ScaleHint is a part of the WMS Spec v.1.1.1, for v.1.3.0 use ScaleDenominator
//        double scaleMin = this.getScaleHintMin();
//        double scaleMax = this.getScaleHintMax();
//
//        if (0d != scaleMin && 0d != scaleMax)
//        {
//            xmlwriter.openElement("ScaleHint");
//            xmlwriter.addAttribute("min", scaleMin);
//            xmlwriter.addAttribute("max", scaleMax);
//            xmlwriter.closeElement("ScaleHint");
//        }

        if (null != this.getLastUpdate())
        {
            xmlwriter.addElement("LastUpdate", this.getLastUpdate());
        }

        if (mapGen != null && "elevation".equalsIgnoreCase(mapGen.getDataType()))
        {
            if (null != this.getMissingDataSignal())
            {
                xmlwriter.addElement("MissingDataSignal", this.getMissingDataSignal());
            }

            if (null != this.getExtremeElevationsMinimum()
                && null != this.getExtremeElevationsMaximum())
            {
                xmlwriter.openElement("ExtremeElevations");
                xmlwriter.addAttribute("min", this.getExtremeElevationsMinimum());
                xmlwriter.addAttribute("max", this.getExtremeElevationsMaximum());
                xmlwriter.closeElement("ExtremeElevations");
            }
        }
    }

    protected void writeChildrenXML(XMLWriter xmlwriter) throws Exception
    {
        // include any children map sources
        Iterator<MapSource> iter = this.getChildren();
        while (iter.hasNext())
        {
            xmlwriter.addXML(iter.next().toXML());
        }
    }

    public String getStringValue(String key)
    {
        return this.getValueAsString(key);
    }

    public String getValueAsString(String key)
    {
        Object o = this.params.getValue(key);
        if (null == o)
        {
            return null;
        }
        else if (o instanceof String)
        {
            return (String) o;
        }

        return "" + o;
    }

    public Object getValue(String key)
    {
        return this.params.getValue(key);
    }

    public void setValue(String key, Object o)
    {
        this.params.setValue(key, o);
    }

    public boolean hasValue(String key)
    {
        return (null != this.params && this.params.hasKey(key) && null != this.params.getValue(key));
    }
}
