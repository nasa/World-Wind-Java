/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.ByteBufferRaster;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.avlist.AVKey;

import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;

abstract class AbstractMapGenerator implements MapGenerator
{
    protected String threadId = "";
    protected MapSource mapSource = null;
    protected boolean initialized = false;
    private   WMSServerApplication wmsApp = null;

    public String getThreadId()
    {
        return threadId;
    }

    public void setThreadId(String prefix)
    {
        this.threadId = new StringBuffer(prefix).append(" (").append(Thread.currentThread().getId()).append(
            "): ").toString();
    }

    public AbstractMapGenerator()
    {
        super();
    }

    public MapSource getMapSource()
    {
        return this.mapSource;
    }

    @Override
    public boolean isInitialized()
    {
        return this.initialized;
    }

    @Override
    public void markAsInitialized()
    {
        this.initialized = true;
    }

    public void setMapSource(MapSource mapSource)
    {
        this.mapSource = mapSource;
    }

    protected String getProperty(Properties props, String key, String defaultVal, String name)
    {
        String retval = defaultVal;
        try
        {
            if (null != props && null != key && !"".equals(key))
            {
                String s = props.getProperty(key);
                if (null != s)
                    retval = s;
            }
        }
        catch (Exception ex)
        {
            Logging.logger().info(ex.getMessage());
            retval = defaultVal;
        }
        return retval;
    }

    //
    // Convenience method for parsing/validating optional configuration parameters.
    //
    protected int getProperty(Properties props, String key, int defaultVal, String name)
    {
        int retval = defaultVal;
        String tmp = this.getProperty(props, key, null, name);
        if (tmp != null)
        {
            try
            {
                retval = Integer.parseInt(tmp);
            }
            catch (NumberFormatException ex)
            {
                Logging.logger().info("Could not decode '" + key + "' property in config file for " + name);
            }
        }
        return retval;
    }

    protected double getProperty(Properties props, String key, double defaultVal, String name)
    {
        double retval = defaultVal;
        String tmp = this.getProperty(props, key, null, name);
        if (tmp != null)
        {
            try
            {
                retval = Double.parseDouble(tmp);
            }
            catch (NumberFormatException ex)
            {
                Logging.logger().info("Could not decode '" + key + "' property in config file for " + name);
            }
        }
        return retval;
    }

    public boolean hasCoverage(Sector sector)
    {
        Sector bbox = this.getBBox();
        return null != sector && null != bbox && sector.intersects(bbox);
    }

    @Override
    public WMSServerApplication getApplicationContext()
    {
        return this.wmsApp;
    }

    @Override
    public void setApplicationContext(WMSServerApplication wmsApp)
    {
        this.wmsApp = wmsApp;        
    }

    public abstract class AbstractServiceInstance implements ServiceInstance
    {
        public AbstractServiceInstance()
        {
            super();
        }

        public java.util.List<File> serviceRequest(WMSGetImageryListRequest req) throws IOException, WMSServiceException
        {
            throw new WMSServiceException("TODO: not implemented yet");
        }
    }
}