/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.util.Logging;

import java.io.IOException;
import java.util.Properties;
import java.util.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class CompoundElevationsGenerator extends AbstractElevationGenerator
{
    private static final String crsStr = "EPSG:4326";

    private Sector coverage = null;
    private double pixelSize = 0d;

    // Configuration property keys...

    public String getDataType()
    {
        return "elevation";
    }

    public double getPixelSize()
    {
        // default is 0 after initialization,
        // so if it is not 0, that means it was already calculated
        if( 0d == this.pixelSize && null != this.mapSource && null != this.mapSource.getChildren() )
        {
            try
            {
                for (Iterator<MapSource> iterator = this.mapSource.getChildren(); iterator.hasNext();)
                {
                    MapGenerator gen = null;
                    MapSource ms = iterator.next();
                    if(null != ms && null != (gen = ms.getMapGenerator()))
                    {
                        double d = gen.getPixelSize();
                        if( 0d == this.pixelSize || d < this.pixelSize )
                            this.pixelSize = d;
                    }
                }
            }
            catch(Exception e)
            {
                Logging.logger().fine( e.getMessage() );
            }
        }
        return this.pixelSize;
    }

    public ServiceInstance getServiceInstance()
    {
        return new CompoundServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...

        Logging.logger().info("CompoundElevationsGenerator:initialize(): started... ");
        try
        {
            if (null == mapSource)
            {
                String msg = Logging.getMessage("nullValue.SourceIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.mapSource = mapSource;

            // Get these optional properties...
            Properties props = mapSource.getProperties();
            String datasetName = mapSource.getName();

            success = true;
            Logging.logger().info("CompoundElevationsGenerator:initialize( " + datasetName + "): status=Done!" );
        }
        catch (Exception ex)
        {
            success = false;
            Logging.logger().severe("CompoundElevationsGenerator:initialize(): Error! " + ex.getMessage());
        }
        return success;
    }

    public Sector getBBox()
    {
        if( null != this.coverage )
            return this.coverage;

        this.coverage = Sector.EMPTY_SECTOR;
        MapSource myMapSource = this.mapSource;
        for (Iterator<MapSource> iterator = myMapSource.getChildren(); iterator.hasNext();)
        {
            MapSource ms = iterator.next();
            try
            {
                MapGenerator gen = (null != ms) ? ms.getMapGenerator() : null;
                if( null != gen )
                {
                    Sector bbox = gen.getBBox();
                    if(null != bbox )
                        this.coverage = Sector.union(this.coverage, bbox);
                }
            }
            catch(Exception e)
            {
                Logging.logger().severe("CompoundElevationsGenerator:getBBox: undefined coverage of child map source" + ms.getName());
            }
        }
        return this.coverage;
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    // --------------------------------------------
    // class BILServiceInstance
    //
    // Used to manage per-request state.
    //
    public class CompoundServiceInstance extends AbstractServiceInstance
    {
        public void freeResources()
        {
        }

        private String threadID = "CompoundElevations";

        public String getThreadID()
        {
            return this.threadID;
        }

        public CompoundServiceInstance()
        {
            super();
            this.threadID = new StringBuffer(CompoundElevationsGenerator.this.mapSource.getName())
                    .append(" (").append(Thread.currentThread().getId())
                    .append("): ").toString();
        }
        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            return this.doServiceRequest( req );
        }

        private DataRasterFormatter doServiceRequest( IMapRequest req ) throws IOException, WMSServiceException
        {
            DataRaster targetRaster = Mosaicer.createCompatibleDataRaster( req );
            DataRasterFormatter targetFormatter = new DataRasterFormatter( targetRaster );

            long begTime = System.currentTimeMillis();

            Logging.logger().finest( this.getThreadID() + "processing service request ...");

            int reqWidth = 150;
            int reqHeight = 150;

            MapSource myMapSource = CompoundElevationsGenerator.this.getMapSource();

            try
            {
                reqWidth = (req.getWidth() > 0) ? req.getWidth() : reqWidth;
                reqHeight = (req.getHeight() > 0) ? req.getHeight() : reqHeight;

                req.setWidth( reqWidth );
                req.setHeight( reqHeight );

                // Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(), req.getBBoxXMax());
                // include neighboor elevation pixels
                Sector reqSector = req.getExtentForElevationRequest();

                double reqPixelSize = reqSector.getDeltaLatDegrees() / reqHeight;

                Stack<MapSource> stack = new Stack<MapSource>();
                for (Iterator<MapSource> iterator = myMapSource.getChildren(); iterator.hasNext();)
                {
                    MapSource ms = iterator.next();
                    MapGenerator gen = (null != ms) ? ms.getMapGenerator() : null;
                    if( null == gen )
                    {
                        Logging.logger().severe( this.getThreadID()
                                + "child mapSource `" + ms.getName() + "` has no associated map generator!" );
                        continue;
                    }

                    if( !gen.hasCoverage(reqSector))
                    {
                        Logging.logger().finest( this.getThreadID()
                                + "child mapSource`" + ms.getName() + "`: out of coverage, skipping" );
                        continue;
                    }

                    double minResolution = ms.getScaleHintMin();
                    double maxResolution = ms.getScaleHintMax();

                    // we are NOT looking for the first one that fits the pixel size
                    // next layer with better resolution could overlap previous layer with lower resolution
                    if( reqPixelSize > minResolution && reqPixelSize > maxResolution )
                        break;

                    stack.push( ms );
                }

                while( !stack.empty() )
                {
                    MapSource ms = stack.pop();
                    MapGenerator gen = ms.getMapGenerator();
                    MapGenerator.ServiceInstance svc = gen.getServiceInstance();

                    String details = "MapSource '"+ms.getName()+"', thread("+Thread.currentThread().getId()+"): ";

                    Logging.logger().finest( details + "executing request for req texel size = " + reqPixelSize );
                    try
                    {
                        DataRasterFormatter formatter = (DataRasterFormatter)svc.serviceRequest( req );

                        if( null != formatter && null != formatter.getRaster() )
                            targetFormatter.merge( formatter.getRaster() );

                        // why to check if target has nodata areas if it was a last layer in the stack?
                        if( !stack.empty() && targetFormatter.hasNoDataAreas() )
                        {
                            Logging.logger().finest( details + "======= DEBUG: there are still missing pixels =======");
                            continue;
                        }
                        else
                            break; // return targetFormatter;
                    }
                    catch(Exception ex)
                    {
                        String msg = Logging.getMessage( "WMS.Server.InternalError", ex.getMessage() );
                        Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
                    }
                }

                return targetFormatter;
            }
            catch (WMSServiceException wmsex)
            {
                Logging.logger().severe(wmsex.getMessage());
                throw new WMSServiceException(wmsex);
            }
            catch (Exception ex)
            {
                String msg = this.getThreadID() + ex.getMessage();
                Logging.logger().severe( msg );
                throw new WMSServiceException( msg );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report( CompoundElevationsGenerator.this.mapSource.getName(), 1, ellapsed );
                Logging.logger().info( this.getThreadID()
                        + "DONE " + " in " + ellapsed + " msec. "
                        + Stats.getStats(CompoundElevationsGenerator.this.mapSource.getName()));
            }
        }
    }
}
