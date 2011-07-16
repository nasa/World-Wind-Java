/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.data.DataRaster;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class ElevationUSGSNED extends AbstractElevationGenerator
{
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...

        try
        {
            File rootDir = new File(mapSource.getRootDir());

            // The NED files have a regular naming scheme, but are irregular in extent. March through the
            // rootDir and build up a spatial index.
            //
            // A pattern to identify and parse NED filenames. These darned regular expressions are
            // write-only (difficult to read).  NOTE:  IF THIS IS MODIFIED FOR ANY REASON, BE CERTAIN TO
            // ADJUST THE CAPTURE-GROUP NUMBERS ACCORDINGLY!  (done by counting number of opening parens)
            final Pattern pattern = Pattern.compile(
                "(-?\\d+(\\.\\d+)?)_(-?\\d+(\\.\\d+)?)_(-?\\d+(\\.\\d+)?)_(-?\\d+(\\.\\d+)?)\\.tif");
            final int MIN_LON_GROUP = 1;
            final int MIN_LAT_GROUP = 3;
            final int MAX_LON_GROUP = 5;
            final int MAX_LAT_GROUP = 7;

            File[] files = rootDir.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    Matcher m = pattern.matcher(name);
                    return m.matches();
                }
            });

            if (files == null || files.length == 0)
            {
                Logging.logger().severe("Zero NED files found in " + rootDir.getAbsolutePath());
                return false;
            }

            this.spatialIndex = new NEDFile[files.length];
            int i = 0;
            double globalMinLon = Double.MAX_VALUE;
            double globalMinLat = Double.MAX_VALUE;
            double globalMaxLon = -Double.MAX_VALUE;
            double globalMaxLat = -Double.MAX_VALUE;

            for (File f : files)
            {
                Matcher m = pattern.matcher(f.getName());
                m.matches();
                double minLon = Double.parseDouble(m.group(MIN_LON_GROUP));
                double minLat = Double.parseDouble(m.group(MIN_LAT_GROUP));
                double maxLon = Double.parseDouble(m.group(MAX_LON_GROUP));
                double maxLat = Double.parseDouble(m.group(MAX_LAT_GROUP));
                this.spatialIndex[i++] = new NEDFile(f, Sector.fromDegrees(minLat, maxLat, minLon, maxLon));

                // Find the global extent...
                if (minLon < globalMinLon)
                    globalMinLon = minLon;
                if (minLat < globalMinLat)
                    globalMinLat = minLat;
                if (maxLon > globalMaxLon)
                    globalMaxLon = maxLon;
                if (maxLat > globalMaxLat)
                    globalMaxLat = maxLat;
            }

            this.boundingSector = Sector.fromDegrees(globalMinLat, globalMaxLat, globalMinLon, globalMaxLon);

            Logging.logger().fine(files.length + " NED Files found" + ";  bounding sector: " + this.boundingSector.toString());
        }
        catch (Exception ex)
        {
            success = false;
            String msg = Logging.getMessage( "WMS.MapGenerator.CannotInstantiate", ex.getMessage() );
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            // throw new WMSServiceException( msg );
        }

        return success;
    }

    public ServiceInstance getServiceInstance()
    {
        return new USGSNED30mServiceInstance();
    }

    public Sector getBBox()
    {
        return this.boundingSector;
    }

    public boolean hasCoverage(Sector sector)
    {
        // check first bigger bounding box
        if(null != sector && sector.intersects( this.getBBox() ))
        {
            // more fine check, becasue USGS NED does not have continuous coverage within its bounding box
            for (NEDFile ned : ElevationUSGSNED.this.spatialIndex)
            {
                if (sector.intersects(ned.sector))
                    return true;
            }
        }
        return false;
    }

    public double getPixelSize()
    {
        //  Original pixel size 0.000277777 (1/3600)
        double pixelSize = this.mapSource.getScaleHintMax();
        return ( pixelSize != 0d ) ? pixelSize : (1d/3600d);
    }

    public String[] getCRS()
    {
        return CRS;
    }


    public class USGSNED30mServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "USGS_NED_30m";

        public String getThreadID()
        {
            return this.threadID;
        }

        public USGSNED30mServiceInstance ()
        {
            super();
            this.threadID = new StringBuffer("USGS_NED_30m").append(" (").append(Thread.currentThread().getId()).append("): ").toString();
        }

        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = this.doServiceRequest( req );
            if( null == raster )
                raster = Mosaicer.createCompatibleDataRaster( req );
            return new DataRasterFormatter( raster );
        }

        private DataRaster doServiceRequest( IMapRequest req ) throws IOException, WMSServiceException
        {
            DataRaster raster = null;
            long begTime = System.currentTimeMillis();

            Logging.logger().finest( ElevationUSGSNED.this.getThreadId() + "processing service request ...");
            
            try
            {
                // Determine which NED files overlap the request...
                // Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(), req.getBBoxXMax());
                // TODO: zz: garakl: debug: attempt to include neighboor elevation pixels
                Sector reqSector = req.getExtentForElevationRequest();

                Double bgColor = req.getBGColorAsDouble();
                short missingColor = (short)((double)bgColor);

                ArrayList<File> tileFiles = new ArrayList<File>();
                for (NEDFile ned : ElevationUSGSNED.this.spatialIndex)
                {
                    if (reqSector.intersects(ned.sector))
                        tileFiles.add( ned.filename );
                }

                if( tileFiles.size() > 0 )
                {
                    File[] sourceFiles = new File[ tileFiles.size() ];
                    sourceFiles = (File[])(tileFiles.toArray( sourceFiles ));

                    raster = Mosaicer.mosaicElevations( this.getThreadID(),
                            sourceFiles, reqSector,
                            req.getWidth(), req.getHeight(),
                            ElevationUSGSNED.NED_MISSING_DATA_FLAG,
                            (short)0, // missingColor,
                            ( "application/bil32".equals(req.getFormat()))  ? "Float32" : "Int16"
                    );

//                    targetImage.copyFrom( sourceImage, missingColor );
                }

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report( "USGS.NED.30m", tileFiles.size(), ellapsed );
                Logging.logger().fine( ElevationUSGSNED.this.getThreadId() + "DONE " + tileFiles.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats("USGS.NED.30m"));
            }
            catch (Exception ex)
            {
                String s = ElevationUSGSNED.this.getThreadId() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                throw new WMSServiceException( s );
            }
            
            return raster;
        }

        public void freeResources()
        {
            // NO-OP
        }
    }

    // Elements of our spatial index of NED files.
    //
    private class NEDFile
    {
        File filename;
        Sector sector;

        NEDFile(File filename, Sector sector)
        {
            this.filename = filename;
            this.sector = sector;
        }
    }

    private NEDFile[] spatialIndex;
    private Sector boundingSector;

    private static final String[] CRS = {"EPSG:4326"};
    private static final short NED_MISSING_DATA_FLAG = -9999;  // http://seamless.usgs.gov/faq/ned_faq.php#ten
}