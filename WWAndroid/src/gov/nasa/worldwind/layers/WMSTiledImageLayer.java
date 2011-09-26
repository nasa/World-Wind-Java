/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import java.net.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class WMSTiledImageLayer extends BasicTiledImageLayer implements TileUrlBuilder
{
    protected static final String MAX_VERSION = "1.3.0";

    protected String layerNames;
    protected String styleNames;
    protected String imageFormat;
    protected String wmsVersion;
    protected String crs;
    protected String urlTemplate;

    public WMSTiledImageLayer(AVList params)
    {
        super(params);
    }

    public WMSTiledImageLayer(Element element)
    {
        super(element);
    }

    @Override
    protected void initWithConfigDoc(Element element)
    {
        super.initWithConfigDoc(element);

        XPath xpath = WWXML.makeXPath();

        // Determine parameters required for URL construction.
        this.layerNames = WWXML.getText(element, "Service/LayerNames", xpath);
        this.styleNames = WWXML.getText(element, "Service/StyleNames", xpath);
        this.imageFormat = WWXML.getText(element, "ImageFormat", xpath);

        String version = WWXML.getText(element, "Service/@version", xpath) ;
        if (version == null || version.compareTo(MAX_VERSION) >= 0)
        {
            this.wmsVersion = MAX_VERSION;
            this.crs = "&crs=CRS:84";
        }
        else
        {
            this.wmsVersion = version;
            this.crs = "&srs=EPSG:4326";
        }

        this.urlBuilder = this;
    }

    public URL getURL(Tile tile, String altImageFormat) throws MalformedURLException
    {
        return buildURL(tile, altImageFormat);
    }

    // TODO consider rewriting using android.net.URI.Builder
    protected URL buildURL(Tile tile, String altImageFormat) throws MalformedURLException
    {
        StringBuffer sb;
        if (this.urlTemplate == null)
        {
            sb = new StringBuffer(this.addQuerySeparator(tile.getLevel().getService()));

            if (!sb.toString().toLowerCase().contains("service=wms"))
                sb.append("service=WMS");
            sb.append("&request=GetMap");
            sb.append("&version=").append(this.wmsVersion);
            sb.append(this.crs);
            sb.append("&layers=").append(this.layerNames);
            sb.append("&styles=").append(this.styleNames != null ? this.styleNames : "");
            sb.append("&transparent=TRUE");

            this.urlTemplate = sb.toString();
        }
        else
        {
            sb = new StringBuffer(this.urlTemplate);
        }

        String format = (altImageFormat != null) ? altImageFormat : this.imageFormat;
        if (null != format)
            sb.append("&format=").append(format);

        sb.append("&width=").append(tile.getWidth());
        sb.append("&height=").append(tile.getHeight());

        Sector s = tile.getSector();
        sb.append("&bbox=");
        sb.append(s.minLongitude.degrees);
        sb.append(",");
        sb.append(s.minLatitude.degrees);
        sb.append(",");
        sb.append(s.maxLongitude.degrees);
        sb.append(",");
        sb.append(s.maxLatitude.degrees);

        return new URL(sb.toString().replace(" ", "%20"));
    }

    /**
     * Add a query separator (? or &) to the end of a URL. No action is taken if the URL already contains a query
     * separator.
     *
     * @param url URL to which to add a separator.
     *
     * @return The URL with a trailing question mark or ampersand.
     */
    protected String addQuerySeparator(String url)
    {
        if (url == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.error(message);
            throw new IllegalArgumentException(message);
        }

        url = url.trim();
        int qMarkIndex = url.indexOf("?");
        if (qMarkIndex < 0)
            url += "?";
        else if (qMarkIndex != url.length() - 1)
            if (url.lastIndexOf("&") != url.length() - 1)
                url += "&";

        return url;
    }
}
