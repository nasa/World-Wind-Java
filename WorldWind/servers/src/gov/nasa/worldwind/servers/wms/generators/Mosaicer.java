/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.gdal.*;
import gov.nasa.worldwind.servers.wms.IMapRequest;
import gov.nasa.worldwind.util.*;

import java.io.File;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class Mosaicer
{
    public static DataRaster mosaicElevations(
        String threadId,
        File[] sourceFiles,
        Sector extent,
        int width, int height,
        short srcNoData,
        short destNoData,
        String outType /* "Float32" or "Int16" */,
        Option.Warp.Resampling resampling
    )
    {
        DataRaster raster = null;

        File tmpBaseFile = TempFile.getTempFile();
        File resultFile = new File(tmpBaseFile.getAbsoluteFile() + ".tif");

        GeotiffReader reader = null;

        try
        {
            GDALUtils gdal = GDALUtils.getGDAL();

            gdal.warp(threadId,
                resampling,
                new String[] {
                    "-wt", outType,
                    "-ot", outType,
                    "-srcnodata", String.valueOf(srcNoData),
                    "-dstnodata", String.valueOf(destNoData)
                },
                sourceFiles,
                extent,
                width, height,
                ReadWriteFormat.GTiff,
                resultFile
            );

            if (resultFile.exists())
            {
                reader = new GeotiffReader(resultFile);
                if (null != reader)
                {
                    raster = reader.readDataRaster(0);
                }
            }
        }
        catch (Throwable t)
        {
            String reason = "[" + threadId + "] " + WWUtil.extractExceptionReason(t);
            String message = Logging.getMessage("WMS.Server.InternalError", reason);
            Logging.logger().severe(message);
        }
        finally
        {
            if (null != reader)
            {
                reader.close();
            }

            // delete temporary file and .HDR, .PRJ, .AUX.XML files
            if (!WWUtil.isEmpty(resultFile))
            {
                Mosaicer.deleteFile(resultFile);
                Mosaicer.deleteFile(new File(resultFile.getAbsolutePath() + ".aux.xml"));
            }

            if (!WWUtil.isEmpty(tmpBaseFile))
            {
                String tmpBase = tmpBaseFile.getAbsolutePath();
                Mosaicer.deleteFile(new File(tmpBase + ".hdr"));
                Mosaicer.deleteFile(new File(tmpBase + ".prj"));
            }
        }
        return raster;
    }

    public static DataRaster mosaicElevations(String threadId,
        File[] sourceFiles,
        Sector extent,
        int width, int height,
        short srcNoData,
        short destNoData,
        String outType /* "Float32" or "Int16" */
    )
    {
        return Mosaicer.mosaicElevations(threadId, sourceFiles, extent,
            width, height, srcNoData, destNoData, outType, Option.Warp.Resampling.Cubic);
    }

    public static DataRaster createCompatibleDataRaster(IMapRequest req)
    {
        return Mosaicer.createDataRaster(
            req.getWidth(), req.getHeight(),
            req.getExtentForElevationRequest(),
            ("application/bil32".equals(req.getFormat())) ? AVKey.FLOAT32 : AVKey.INT16,
            req.getBGColorAsDouble()
        );
    }

    public static DataRaster createDataRaster(int width, int height, Sector sector, String dataType,
        Double missingDataValue)
    {
        // Create a BIL elevation raster to hold the tile's data.
        AVList bufferParams = new AVListImpl();

        bufferParams.setValue(AVKey.BYTE_ORDER, AVKey.LITTLE_ENDIAN);
        bufferParams.setValue(AVKey.TILE_WIDTH, width);
        bufferParams.setValue(AVKey.TILE_HEIGHT, height);
        bufferParams.setValue(AVKey.DATA_TYPE, dataType);
        bufferParams.setValue(AVKey.SECTOR, sector);
        bufferParams.setValue(AVKey.MISSING_DATA_REPLACEMENT, missingDataValue);

        ByteBufferRaster bufferRaster = new ByteBufferRaster(width, height, sector, bufferParams);

        // Clear the raster with the missing data replacement.
        // This code expects the string "gov.nasa.worldwind.avkey.MissingDataValue",
        // which now corresponds to the key MISSING_DATA_REPLACEMENT.
        if (null != missingDataValue)
        {
            bufferRaster.fill(missingDataValue);
            bufferRaster.setTransparentValue(missingDataValue);
        }

        return bufferRaster;
    }

    private static void deleteFile(File f)
    {
        try
        {
            if (null != f && f.exists())
            {
                f.delete();
            }
        }
        catch (Throwable t)
        {
            Logging.logger().finest(WWUtil.extractExceptionReason(t));
        }
    }
}
