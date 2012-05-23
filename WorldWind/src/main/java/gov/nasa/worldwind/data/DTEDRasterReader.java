/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.data;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.formats.dted.DTED;
import gov.nasa.worldwind.util.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class DTEDRasterReader extends AbstractDataRasterReader
{
    protected static final String[] dtedMimeTypes = new String[]{
            "application/dted",
            "application/dt0", "application/dted-0",
            "application/dt1", "application/dted-1",
            "application/dt2", "application/dted-2",
    };

    protected static final String[] dtedSuffixes = new String[]
            {"dt0", "dt1", "dt2"};

    public DTEDRasterReader()
    {
        super(dtedMimeTypes, dtedSuffixes);
    }

    @Override
    protected boolean doCanRead(Object source, AVList params)
    {
        File file = this.getFile(source);
        if (null == file)
        {
            return false;
        }

        boolean canRead = false;
        try
        {
            AVList metadata = DTED.readMetadata(file);
            if (null != metadata)
            {
                if (null != params)
                {
                    params.setValues(metadata);
                }

                canRead = AVKey.ELEVATION.equals(metadata.getValue(AVKey.PIXEL_FORMAT));
            }
        }
        catch (Throwable t)
        {
            Logging.logger().finest(t.getMessage());
            canRead = false;
        }

        return canRead;
    }

    @Override
    protected DataRaster[] doRead(Object source, AVList params) throws IOException
    {
        File file = this.getFile(source);
        if (null == file)
        {
            String message = Logging.getMessage("generic.UnrecognizedSourceTypeOrUnavailableSource", source);
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        DataRaster raster = DTED.read(file);
        if( raster instanceof ByteBufferRaster)
            ElevationsUtil.rectify((ByteBufferRaster)raster);

        return new DataRaster[]{ raster };
    }

    @Override
    protected void doReadMetadata(Object source, AVList params) throws IOException
    {
        File file = this.getFile(source);
        if (null == file)
        {
            String message = Logging.getMessage("generic.UnrecognizedSourceTypeOrUnavailableSource", source);
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        AVList metadata = DTED.readMetadata(file);
        if (null != metadata && null != params)
        {
            params.setValues(metadata);
        }
    }

    protected File getFile(Object source)
    {
        if (null == source)
        {
            return null;
        }
        else if (source instanceof java.io.File)
        {
            return (File) source;
        }
        else if (source instanceof java.net.URL)
        {
            return WWIO.convertURLToFile((java.net.URL) source);
        }
        else
        {
            return null;
        }
    }
}
