/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.BufferedImageFormatter;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;

import java.io.IOException;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * @author garakl
 * @version $Id$
 */

public class CompoundImageryGenerator extends AbstractMapGenerator
{
    public static final Integer DEFAULT_MISSING_DATA_COLOR = 0;

    private double pixelSize = 0d;
    private static final String crsStr = "EPSG:4326";
    private Sector coverage = null;

    // Configuration property keys...

    public String getDataType()
    {
        return "imagery";
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

        Logging.logger().info("CompoundImageryGenerator:initialize(): started... ");
        try
        {
            if (null == mapSource)
            {
                String msg = Logging.getMessage("nullValue.SourceIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.mapSource = mapSource;

            success = true;
            Logging.logger().info("CompoundImageryGenerator:initialize( " + mapSource.getName() + "): status=Done!" );
        }
        catch (Exception ex)
        {
            success = false;
            Logging.logger().severe("CompoundImageryGenerator:initialize(): Error! " + ex.getMessage());
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
                Logging.logger().severe("CompoundImageryGenerator:getBBox: undefined coverage of child map source" + ms.getName());
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
        private long threadId = 0;

        public void freeResources()
        {
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            int reqWidth = 512;
            int reqHeight = 512;

            this.threadId = Thread.currentThread().getId();

            MapSource myMapSource = CompoundImageryGenerator.this.mapSource;
            Logging.logger().info("START CompoundServiceInstance :: serviceRequest( " + myMapSource.getName() + ")");
            try
            {
                Integer bgColor = CompoundImageryGenerator.DEFAULT_MISSING_DATA_COLOR;
                String bgColorStr = req.getBGColor();
                if (bgColorStr != null && 0 < bgColorStr.length())
                {
                    try
                    {
                        bgColor = Integer.parseInt( bgColorStr, 16 );
                    }
                    catch (NumberFormatException ex)
                    {
                        Logging.logger().severe("Unable to parse BGCOLOR in get imagery request: " + ex.getMessage());
                        bgColor = CompoundImageryGenerator.DEFAULT_MISSING_DATA_COLOR;
                    }
                }
                else
                    Logging.logger().severe("BGCOLOR was not specified in the getImagery request: using default " + bgColor );

                req.setBGColor( bgColor.toString() );

                reqWidth = (req.getWidth() > 0) ? req.getWidth() : 512;
                req.setWidth( reqWidth );

                reqHeight = (req.getHeight() > 0) ? req.getHeight() : 512;
                req.setHeight( reqHeight );

                Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(), req.getBBoxXMax());

                double reqPixelSize = reqSector.getDeltaLatDegrees() / reqHeight;
                MapSource  reqMapSource = null;

                for (Iterator<MapSource> iterator = myMapSource.getChildren(); iterator.hasNext();)
                {
                    MapSource ms = iterator.next();
                    MapGenerator gen = (null != ms) ? ms.getMapGenerator() : null;
                    if( null == gen )
                    {
                        Logging.logger().severe("child mapSource `" + ms.getName() + "` has no associated map generator!" );
                        continue;
                    }
                    Sector bbox = gen.getBBox();
                    Logging.logger().info("found child mapSource `" + ms.getName() + "` with generator: " + gen.toString() );
                    if (!reqSector.intersects(bbox))
                    {
                        Logging.logger().info("child mapSource`" + ms.getName() + "`: out of coverage, skipping" + bbox.toString());
                        continue;
                    }

                    double genPixelSize = gen.getPixelSize();
                    reqMapSource = ms; // assumes layers are configured in order according to resolutions from low to high
                    Logging.logger().info("Comparing texel sizes: req = " + reqPixelSize + " , layer's = " + gen.getPixelSize() );
                    if( reqPixelSize >= genPixelSize )
                    {
                        // satisfactory generator found
                        break;
                    }
                }

                ImageFormatter fmt = null;
                if( null != reqMapSource )
                {
                    try
                    {
                        MapGenerator gen = reqMapSource.getMapGenerator();
                        Logging.logger().info("`" + reqMapSource.getName() + "` is executing request; req texel size = "
                                    + reqPixelSize + " where layer's texel size = " + gen.getPixelSize() );
                        fmt = gen.getServiceInstance().serviceRequest( req );
                    }
                    catch(Exception ex)
                    {
                        Logging.logger().severe("Error while accessing a child mapSource: " + ex.getMessage());
                    }
                }

                if( null == fmt )
                {
                    Logging.logger().info("CompoundImageryGenerator will return an empty transparent image" );
                    fmt = createEmptyImage( req.getWidth(), req.getHeight(), new Color(bgColor) );
                }
                return fmt;
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage( "WMS.RequestFailed", ex.getMessage() );
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
                throw new WMSServiceException( msg );
            }
        }

        private BufferedImageFormatter createEmptyImage(int width, int height, Color bgColor )
        {
            BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
            Graphics2D g2d = image.createGraphics();
            g2d.setColor( bgColor );
            g2d.fillRect( 0, 0, width, height );
            g2d.dispose();
            return new BufferedImageFormatter( image );
        }
    }
}
