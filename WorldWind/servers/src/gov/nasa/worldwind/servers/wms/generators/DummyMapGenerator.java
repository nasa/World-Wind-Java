package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.BILImageFormatter;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.Tile;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;

/* Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

/**
 * @author lado
 * @version $Id$
 */
public class DummyMapGenerator extends AbstractMapGenerator
{
    private static final String crsStr = "EPSG:4326";
    
    private Sector coverage = null;
    private double pixelSize = 0d;

    public DummyMapGenerator()
    {
        super();
    }

    public String getDataType()
    {
        return "imagery";
    }

    public MapGenerator.ServiceInstance getServiceInstance()
    {
        return new DummyServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...

        try
        {
            if(null == mapSource)
            {
                String msg = Logging.getMessage("nullValue.SourceIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.mapSource = mapSource;

            if( mapSource.getName() != null )
                Logging.logger().info("DummyMapGenerator should not be used for named layers like " + mapSource.getName() );

            Logging.logger().info( "DummyMapGenerator created for " + mapSource.getTitle() );
        }
        catch (Exception ex)
        {
            success = false;
            Logging.logger().severe("DummyMapGenerator:initialize(): status=Error, details=" + ex.getMessage() );
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
                Logging.logger().severe("DummyMapGenerator:getBBox: undefined coverage of child map source" + ms.getName());
            }
        }
        return this.coverage;
    }

    public String[] getCRS()
    {

        return new String[]{crsStr};
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

    public class DummyServiceInstance extends AbstractServiceInstance
    {
        private long threadId = 0;

        public void freeResources() {}

        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            throw new WMSServiceException( "Attempt to query a wrapper layer. Please try children layers instead." );
        }
    }
}
