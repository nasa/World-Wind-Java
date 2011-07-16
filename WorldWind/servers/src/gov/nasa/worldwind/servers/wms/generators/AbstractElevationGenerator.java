/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.servers.wms.WMSServer;
import gov.nasa.worldwind.servers.wms.WMSGetMapRequest;
import gov.nasa.worldwind.servers.wms.WMSServiceException;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

/**
 * @author garakl
 * @version $Id$
 */
abstract class AbstractElevationGenerator extends AbstractMapGenerator
{
    public AbstractElevationGenerator()
    {
        super();
    }

    public String getDataType()
    {
        return "elevation";
    }

    public String getPixelType(String format) throws WMSServiceException
    {
        if ("application/bil32".equalsIgnoreCase(format))
            return AVKey.FLOAT32;
        else if ("application/bil16".equalsIgnoreCase(format))
            return AVKey.INT16;
        else if ("application/bil".equalsIgnoreCase(format))
            return AVKey.INT16;
        else if ("image/bil".equalsIgnoreCase(format))
            return AVKey.INT16;

        String msg = Logging.getMessage("WMS.Server.UnknownOrUnsupportedDataFormat", format);
        Logging.logger().severe(msg);
        throw new WMSServiceException(msg);
    }
}
