/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.BasicFactory;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWUnrecognizedException;
import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.wms.generators.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;

/**
 * @author dcollins
 * @version $Id$
 */
public class BasicMapSourceFactory extends BasicFactory
{
    protected WMSServerApplication app = null;

    public BasicMapSourceFactory(WMSServerApplication app)
    {
        this.app = app;
    }

    @Override
    protected Object doCreateFromElement(Element domElement, AVList params) throws Exception
    {
        // TODO: add construction of these map sources:
        // BMNG, DTED, SRTM3, SRTM30, SRTM3v4, USGS NED, Esat, NAIP, RPF, Skankort (optional)

        if (WMSDataConfigurationUtils.isWorldWindTiledConfig(domElement, params))
        {
            return this.createFromWorldWindConfigDocument(domElement, params);
        }
        else
        {
            String msg = Logging.getMessage("generic.UnrecognizedDataConfiguration",
                DataConfigurationUtils.getDataConfigDisplayName(domElement));
            throw new WWUnrecognizedException(msg);
        }
    }

    //**************************************************************//
    //********************  WWJ Client Tile Cache  *****************//
    //**************************************************************//

    protected MapSource createFromWorldWindConfigDocument(Element domElement, AVList params)
    {
        DataConfigurationUtils.getLevelSetConfigParams(domElement, params);

        String type = DataConfigurationUtils.getDataConfigType(domElement);
        if (type != null && type.equalsIgnoreCase("ElevationModel"))
        {
            this.getElevationModelParams(domElement, params);
        }

        String serviceName = (null != params && params.hasKey(AVKey.SERVICE_NAME))
            ? params.getStringValue(AVKey.SERVICE_NAME) : AVKey.SERVICE_NAME_OFFLINE;

        MapSource ms;

        if (AVKey.SERVICE_NAME_LOCAL_RASTER_SERVER.equals(serviceName))
        {
            ms = new WorldWindTiledLayerMapSource(this.app.getDataFileStore(),
                domElement, params, RasterServerBackedTiledLayer.class);
        }
        else
        {
            ms = new WorldWindTiledLayerMapSource(this.app.getDataFileStore(),
                domElement, params, WorldWindTiledLayer.class);
        }

        return ms;
    }

    protected void getElevationModelParams(Element domElement, AVList params)
    {
        XPath xpath = WWXML.makeXPath();

        // Image format properties.
        if (params.getValue(AVKey.DATA_TYPE) == null)
        {
            String s = WWXML.getText(domElement, "DataType/@type", xpath);
            if (s != null && s.length() > 0)
            {
                s = WWXML.parseDataType(s);
                if (s != null && s.length() > 0)
                {
                    params.setValue(AVKey.DATA_TYPE, s);
                }
            }
        }

        if (params.getValue(AVKey.BYTE_ORDER) == null)
        {
            String s = WWXML.getText(domElement, "DataType/@byteOrder", xpath);
            if (s != null && s.length() > 0)
            {
                s = WWXML.parseByteOrder(s);
                if (s != null && s.length() > 0)
                {
                    params.setValue(AVKey.BYTE_ORDER, s);
                }
            }
        }

        if (params.getValue(AVKey.PIXEL_FORMAT) == null)
        {
            params.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);
        }

        // Elevation data properties.
        WWXML.checkAndSetDoubleParam(domElement, params, AVKey.MISSING_DATA_SIGNAL, "MissingData/@signal", xpath);
        WWXML.checkAndSetDoubleParam(domElement, params, AVKey.MISSING_DATA_REPLACEMENT, "MissingData/@replacement",
            xpath);
        WWXML.checkAndSetDoubleParam(domElement, params, AVKey.ELEVATION_MAX, "ExtremeElevations/@max", xpath);
        WWXML.checkAndSetDoubleParam(domElement, params, AVKey.ELEVATION_MIN, "ExtremeElevations/@min", xpath);
    }
}
