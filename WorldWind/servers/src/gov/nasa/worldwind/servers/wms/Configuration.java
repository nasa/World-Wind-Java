/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.cache.FileStoreFilter;
import gov.nasa.worldwind.servers.wms.security.*;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.WWXML;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author brownrigg
 * @version $Id$
 */
public class Configuration
{
    private static final String ROOT_NODE = "/wms-config";
    private static final String SERVER = "//server";
    private static final String DATA_FILE_STORE_CONFIG_FILE = SERVER + "/datafilestore/config-file";
    private static final String DATA_FILE_STORE_FILTER = SERVER + "/datafilestore/filestore-filter";

    private static final String AUTO_DISCOVERY_RUN = SERVER + "/auto-discovery/@run";
    private static final String AUTO_DISCOVERY_PERIOD = SERVER + "/auto-discovery/@period";

    private static final String XPATH_SECURITY = "//security";

    private static final String XPATH_SECURITY_POLICY = "./policy";
    private static final String XPATH_SECURITY_POLICY_NAME = "@name";

    private static final String XPATH_SECURITY_ACCESS = "./access";

    private static final String MAPSOURCE_FACTORY = SERVER + "/mapsource-factory";

    private static final String MAPSOURCE = "./mapsource";

    private static final String MAPSOURCE_REF = "@ref";
    private static final String MAPSOURCE_NAME = "@name";
    private static final String MAPSOURCE_TITLE = "@title";
    private static final String MAPSOURCE_ROOTDIR = "./root-dir";
    private static final String MAPSOURCE_CLASS = "./class";
    private static final String MAPSOURCE_DESCRIPTION = "./description";
    private static final String MAPSOURCE_KEYWORDS = "./description/@keywords";
    private static final String MAPSOURCE_PROPERTIES = "./property";
    private static final String MAPSOURCE_PROPNAME = "@name";
    private static final String MAPSOURCE_PROPVAL = "@value";

    private static final String MAPSOURCE_SCALE_HINT_MIN = "./scale-hint/@min";
    private static final String MAPSOURCE_SCALE_HINT_MAX = "./scale-hint/@max";

    private FileStoreFilter dataFileStoreFilter;
    private String dataFileStoreConfigFile;
    private boolean runAutoDiscoveryTask = false;
    private int repeatAutoDiscoveryTask = 0;

    private Map<String, MapSource> mapSources = new ConcurrentHashMap<String, MapSource>();

    public Configuration(InputStream configFile) throws Exception
    {
        try
        {
            // NOTE: our tedious use of try-catch blocks around each configuration element
            // is an attempt to provide as much information about an improper
            // configuration as possible. We use private worker methods to hide some
            // of the tedium.

            DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docfac.newDocumentBuilder();
            Document doc = builder.parse(configFile);

            XPathFactory xpfac = XPathFactory.newInstance();
            XPath xpath = xpfac.newXPath();

            getAutoDiscovery(xpath, doc);

            readPolicies(xpath, doc);

            readAccessRestrictions(xpath, doc);

            // get the data file store configuration file path, and create the file store filter.
            getDataFileStoreConfigFile(xpath, doc);
            getDataFileStoreFilter(xpath, doc);

            // create the data configuration and map source factories.
//            getMapSourceFactory(xpath, doc);

            // extract the map sources...
            readMapSources(xpath, doc);

            // register WMSSecurityManager if there are at least one ACL and SecurityAccess defined
            if (WMSSecurity.getSecurityAccessManager().hasAccessRestrictions()
                && WMSSecurity.getPolicyManager().hasPolicies()
                    )
            {
                WMSSecurity.setSecurityManager(new WMSSecurityManager());
            }
        }
        catch (Exception ex)
        {
            Logging.logger().severe(ex.getMessage());

            String msg = Logging.getMessage("WMS.Config.Failed", ex.getMessage());
            Logging.logger().severe(msg);
            throw new Exception(msg);
        }
    }

    public String getDataFileStoreConfigurationFile()
    {
        return this.dataFileStoreConfigFile;
    }

    public FileStoreFilter getDataFileStoreFilter()
    {
        return this.dataFileStoreFilter;
    }

    public boolean runAutoDiscoveryTask()
    {
        return this.runAutoDiscoveryTask;
    }

    public int getAutoDiscoveryPeriod()
    {
        return this.repeatAutoDiscoveryTask;
    }

    public Collection<MapSource> getMapSources()
    {
        return this.mapSources.values();
    }


    private void getAutoDiscovery(XPath xpath, Document doc) throws Exception
    {
        try
        {
            String str = xpath.evaluate(AUTO_DISCOVERY_RUN, doc);
            this.runAutoDiscoveryTask = "true".equalsIgnoreCase(str);
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.Config.MissingAttribute", "run", "auto-discovery");
            Logging.logger().severe(msg);
            throw new Exception(msg);
        }

        try
        {
            String str = xpath.evaluate(AUTO_DISCOVERY_PERIOD, doc);
            this.repeatAutoDiscoveryTask = (null == str || str.length() == 0) ? 0 : (1000 * Integer.parseInt(str));
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.Config.MissingAttribute", "period", "auto-discovery");
            Logging.logger().severe(msg);
            throw new Exception(msg);
        }
    }


    private void getDataFileStoreConfigFile(XPath xpath, Document doc)
    {
        try
        {
            String path = WWXML.getText(doc.getDocumentElement(), DATA_FILE_STORE_CONFIG_FILE, xpath);
            if (!WWUtil.isEmpty(path))
            {
                if (!(new File(path).exists()) && !path.startsWith("WEB-INF"))
                {
                    path = "WEB-INF" + File.separator + path;
                }

                if (new File(path).exists())
                {
                    this.dataFileStoreConfigFile = path;
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("FileStore.ConfigurationNotFound", ex.getMessage());
            Logging.logger().fine(msg);
        }

        // Fallback to a default data file store configuration file.
        this.dataFileStoreConfigFile = "WEB-INF/DataFileStore.xml";
        Logging.logger().fine("Fallback to a default data file store configuration file " + this.dataFileStoreConfigFile);
    }

    private void getDataFileStoreFilter(XPath xpath, Document doc)
    {
        String className = null;

        try
        {
            className = WWXML.getText(doc.getDocumentElement(), DATA_FILE_STORE_FILTER, xpath);
            if (className != null && className.length() > 0)
            {
                this.dataFileStoreFilter = (FileStoreFilter) WorldWind.createComponent(className);
                return;
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("FileStore.CannotCreateFileStoreFilter", ex.getMessage());
            Logging.logger().severe(msg);
        }

        // Fallback to a default data file store configuration file.
        this.dataFileStoreFilter = new WMSDataConfigurationFilter();
        Logging.logger().info("Falling back to default data file-store filter: " + this.dataFileStoreFilter.getClass().getName());
    }

    private void readAccessRestrictions(XPath xpath, Document doc) throws Exception
    {
        try
        {
            NodeList list = null;
            Node security = (Node) xpath.evaluate(XPATH_SECURITY, doc, XPathConstants.NODE);

            if (null != security
                && security.hasChildNodes()
                && null != (list = (NodeList) xpath.evaluate(XPATH_SECURITY_ACCESS, security, XPathConstants.NODESET))
                && 0 < list.getLength()
                    )
            {
                for (int i = 0; i < list.getLength(); i++)
                {
                    try
                    {
                        Node access = list.item(i);
                        SecurityAccess sa = SecurityAccessFactory.create(xpath, access);
                        WMSSecurity.getSecurityAccessManager().add(sa);
                    }
                    catch (Exception ex)
                    {
                        Logging.logger().finest(ex.getMessage());
                    }
                }
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.Config.ParsingError", ex.toString());
            Logging.logger().fine(msg);
            throw new Exception(msg);
        }
    }


    private void readPolicies(XPath xpath, Document doc) throws Exception
    {
        try
        {
            NodeList policies = null;
            Node security = (Node) xpath.evaluate(XPATH_SECURITY, doc, XPathConstants.NODE);

            if (null != security
                && security.hasChildNodes()
                && null != (policies = (NodeList) xpath.evaluate(XPATH_SECURITY_POLICY, security, XPathConstants.NODESET))
                && 0 < policies.getLength()
                    )
            {
                for (int i = 0; i < policies.getLength(); i++)
                {
                    try
                    {
                        Node policy = policies.item(i);
                        WMSSecurity.getPolicyManager().add(this.parsePolicy(xpath, policy));
                    }
                    catch (Exception ex)
                    {
                        String msg = Logging.getMessage("WMS.Security.ErrorParsingPolicy", ex.getMessage());
                        Logging.logger().fine(msg);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.Config.ParsingError", ex.toString());
            Logging.logger().fine(msg);
            throw new Exception(msg);
        }
    }

    private Policy parsePolicy(XPath xpath, Node n) throws Exception
    {
        String name = xpath.evaluate(XPATH_SECURITY_POLICY_NAME, n);

        Policy policy = new Policy(name);

        if (n.hasAttributes())
        {
            NamedNodeMap attributes = n.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++)
            {
                Node attr = attributes.item(i);
                policy.setValue(attr.getNodeName().trim(), attr.getNodeValue().trim());
            }
        }
        return policy;
    }


    private void readMapSources(XPath xpath, Document doc) throws Exception
    {
        try
        {
            // These can be hierarchical, so we get all the top level MapSources, 
            // and let our helper method descend any nested definitions...
            Node root = (Node) xpath.evaluate(ROOT_NODE, doc, XPathConstants.NODE);
            NodeList nlist = (NodeList) xpath.evaluate(MAPSOURCE, root, XPathConstants.NODESET);
            for (int i = 0; i < nlist.getLength(); i++)
            {
                try
                {
                    Node n = nlist.item(i);
                    MapSource ms = parseMapSource(xpath, n, null);

                    if (null != ms && null != ms.getName())
                    {
                        if (this.mapSources.containsKey(ms.getName()))
                        {
                            String msg = Logging.getMessage("WMS.MapSource.DuplicateFound", ms.getName());
                            Logging.logger().fine(msg);
                        }
                        else
                        {
                            this.mapSources.put(ms.getName(), ms);
                        }
                    }
                }
                catch (Exception ex)
                {
                    String msg = Logging.getMessage("WMS.MapSource.ParsingError",
                            Integer.toString(i), ex.getMessage());
                    Logging.logger().finest(msg);
                }
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.Config.ParsingError", ex.toString());
            Logging.logger().fine(msg);
            throw new Exception(msg);
        }
    }

    private MapSource parseMapSource(XPath xpath, Node n, MapSource parent) throws Exception
    {
        String ref = xpath.evaluate(MAPSOURCE_REF, n);

        if (null != ref && 0 < ref.trim().length())
        {
            if (null == parent)
            {
                String msg = Logging.getMessage("WMS.MapSource.ParentMapsourceRequired", ref);
                Logging.logger().fine(msg);
                throw new Exception(msg);
            }

            if (!(this.mapSources.containsKey(ref)))
            {
                String msg = Logging.getMessage("WMS.MapSource.ReferredMapsourceNotFound", parent.getName(), ref);
                Logging.logger().fine(msg);
                throw new Exception(msg);
            }

            return this.mapSources.get(ref);
        }

        String name = xpath.evaluate(MAPSOURCE_NAME, n);
        String dir = xpath.evaluate(MAPSOURCE_ROOTDIR, n);
        String title = xpath.evaluate(MAPSOURCE_TITLE, n);
        String generatorClassName = xpath.evaluate(MAPSOURCE_CLASS, n);

        /*** TODO -- need to re-evaluate how to determine properly configured MapSources, in the context
         * of our new support for hierarchical named and un-named sources.
         */
        if (!"".equals(name))
        {
            if ("".equals(title))
            {
                String msg = Logging.getMessage("WMS.MapSource.MissingAttribute", "title", name);
                Logging.logger().severe(msg);
                throw new Exception(msg);
            }

            if ("".equals(dir))
            {
                String msg = Logging.getMessage("WMS.MapSource.MissingElement", "root-dir", name);
                Logging.logger().severe(msg);
                throw new Exception(msg);
            }

            if ("".equals(generatorClassName))
            {
                String msg = Logging.getMessage("WMS.MapSource.MissingElement", "class", name);
                Logging.logger().severe(msg);
                throw new Exception(msg);
            }
        }
        else if ("".equals(name))
        {
            if ("".equals(title))
            {
                String msg = Logging.getMessage("WMS.MapSource.MissingAttribute", "title", "unnamed");
                Logging.logger().severe(msg);
                throw new Exception(msg);
            }

            if (null == generatorClassName || "".equals(generatorClassName))
            {
                generatorClassName = "gov.nasa.worldwind.servers.wms.generators.DummyMapGenerator";
            }
        }


        Class generatorServiceClass;
        try
        {
            generatorServiceClass = Class.forName(generatorClassName);
            if (!MapGenerator.class.isAssignableFrom(generatorServiceClass))
            {
                throw new Exception();
            }
        }
        catch (Exception ex)
        {
            Logging.logger().finest(ex.getMessage());

            String msg = Logging.getMessage("WMS.MapSource.UnableToCreateClass", generatorClassName, name);
            Logging.logger().severe(msg);
            throw new Exception(msg);
        }


        // Get optional description...
        String description = xpath.evaluate(MAPSOURCE_DESCRIPTION, n);
        String keywords = xpath.evaluate(MAPSOURCE_KEYWORDS, n);

        double scaleHintMin = 0d, scaleHintMax = 0d;
        String scaleHint = xpath.evaluate(MAPSOURCE_SCALE_HINT_MIN, n);
        if (scaleHint != null && 0 < scaleHint.length())
        {
            try
            {
                scaleHintMin = Double.parseDouble(scaleHint);
            }
            catch (NumberFormatException ex)
            {
                Logging.logger().finest("Unable to parse `min` value in the `scale-hint' : " + ex.getMessage());
            }
        }
        else
        {
            Logging.logger().finest("missing `min` value in the `scale-hint' section of the mapsource " + name);
        }

        scaleHint = xpath.evaluate(MAPSOURCE_SCALE_HINT_MAX, n);
        if (scaleHint != null && 0 < scaleHint.length())
        {
            try
            {
                scaleHintMax = Double.parseDouble(scaleHint);
            }
            catch (NumberFormatException ex)
            {
                Logging.logger().severe("Unable to parse `max` value in the `scale-hint' section : " + ex.getMessage());
            }
        }
        else
        {
            Logging.logger().finest("missing `max` value in the `scale-hint' section of the mapsource " + name);
        }

        // Get any optional MapSource-specific properties...
        Properties properties = new Properties();
        NodeList props = (NodeList) xpath.evaluate(MAPSOURCE_PROPERTIES, n, XPathConstants.NODESET);
        if (props.getLength() > 0)
        {
            for (int j = 0; j < props.getLength(); j++)
            {
                Node p = props.item(j);
                String propName = xpath.evaluate(MAPSOURCE_PROPNAME, p);
                String propValue = xpath.evaluate(MAPSOURCE_PROPVAL, p);
                if ("".equals(propName) || "".equals(propValue))
                {
                    Logging.logger().info("Missing name/value for <property> in MapSource " + name);
                }
                else
                {
                    properties.put(propName, propValue);
                }
            }
        }

        MapSource map = new MapSource(parent, name, title, dir, generatorServiceClass, description, keywords, scaleHintMin, scaleHintMax, properties);

        // handle any nested definitions...
        NodeList nlist = (NodeList) xpath.evaluate(MAPSOURCE, n, XPathConstants.NODESET);
        for (int i = 0; i < nlist.getLength(); i++)
        {
            Node node = nlist.item(i);
            MapSource childMap = parseMapSource(xpath, node, map);
            map.addChild(childMap);
        }

        return map;
    }
}
