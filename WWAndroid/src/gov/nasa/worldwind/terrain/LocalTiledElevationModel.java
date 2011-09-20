/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.MemoryCache;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import java.io.*;
import java.nio.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class LocalTiledElevationModel extends TiledElevationModel
{
    protected static class RequestTask implements Runnable
    {
        protected TileKey key;
        protected LocalTiledElevationModel elevationModel;

        public RequestTask(TileKey key, LocalTiledElevationModel elevationModel)
        {
            if (key == null)
            {
                String msg = Logging.getMessage("nullValue.KeyIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            if (elevationModel == null)
            {
                String msg = Logging.getMessage("nullValue.ElevationModelIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.key = key;
            this.elevationModel = elevationModel;
        }

        public void run()
        {
            if (Thread.currentThread().isInterrupted())
                return; // This task was cancelled because it's a duplicate or for some other reason.

            this.elevationModel.loadTile(this.key);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (!(o instanceof RequestTask))
                return false;

            RequestTask that = (RequestTask) o;
            return this.key.equals(that.key);
        }

        @Override
        public int hashCode()
        {
            return this.key.hashCode();
        }

        @Override
        public String toString()
        {
            return this.key.toString();
        }
    }

    protected String imageFormat;

    public LocalTiledElevationModel(AVList params)
    {
        super(params);
    }

    public LocalTiledElevationModel(Element element)
    {
        super(element);
    }

    @Override
    protected void initWithParams(AVList params)
    {
        super.initWithParams(params);

        Object o = params.getValue(AVKey.IMAGE_FORMAT);
        if (o != null && o instanceof String && !WWUtil.isEmpty(o))
            this.imageFormat = (String) o;
    }

    @Override
    protected void initWithConfigDoc(Element element)
    {
        super.initWithConfigDoc(element);

        XPath xpath = WWXML.makeXPath();

        String s = WWXML.getText(element, "ImageFormat", xpath);
        if (!WWUtil.isEmpty(s))
            this.imageFormat = s;
    }

    @Override
    protected void requestTile(TileKey key)
    {
        // Ignore requests for absent tiles.
        if (this.levels.isTileAbsent(key))
            return;

        TaskService service = WorldWind.getTaskService();
        if (service.isFull())
            return;

        Runnable task = this.createRequestTask(key);
        if (task == null)
        {
            String msg = Logging.getMessage("nullValue.TaskIsNull");
            Logging.warning(msg);
            return;
        }

        service.runTask(task);
    }

    protected Runnable createRequestTask(TileKey key)
    {
        return new RequestTask(key, this);
    }

    protected void loadTile(TileKey key)
    {
        MemoryCache cache = this.getElevationCache();
        if (cache.contains(key))
            return;

        ElevationTile tile = this.createElevations(key);
        if (tile != null)
        {
            // Put the elevations tile in the elevation tile cache. The tessellator or application finds the elevations
            // in memory on the next call to getElevations.
            cache.put(key, tile);

            // Mark the tile as not absent to ensure that it is used, and cause any World Windows containing this
            // elevation model to repaint themselves.
            this.levels.unmarkTileAbsent(key);
            this.firePropertyChange(AVKey.ELEVATION_MODEL, null, tile);
        }
        else
        {
            // Mark the tile as absent if its elevations cannot be loaded for any reason. Since this is a local
            // elevation model, there is no possibility that the data can be downloaded or retrieved from any other
            // source.
            this.levels.markTileAbsent(key);
        }
    }

    protected ElevationTile createElevations(TileKey key)
    {
        // Use the tile's key as a path to a class path resource.
        String path = this.getTileCacheKey(key);
        ByteBuffer buffer = null;

        InputStream stream = WWIO.openStream(path);
        try
        {
            if (stream != null)
            {
                if (!(stream instanceof BufferedInputStream))
                    stream = new BufferedInputStream(stream);

                buffer = WWIO.readStreamToBuffer(stream);
            }
        }
        catch (IOException e)
        {
            String msg = Logging.getMessage("generic.UnableToOpenPath", path);
            Logging.error(msg);
        }
        finally
        {
            WWIO.closeStream(stream, path);
        }

        if (buffer == null)
            return null;

        Level level = this.levels.getLevel(key.getLevelNumber());
        Angle latDelta = level.getTileDelta().latitude;
        Angle lonDelta = level.getTileDelta().longitude;
        Angle minLat = new Angle();
        Angle minLon = new Angle();
        Tile.computeRowLatitude(latDelta, key.getRow(), minLat);
        Tile.computeColumnLongitude(lonDelta, key.getColumn(), minLon);

        Sector sector = Sector.fromDegrees(minLat.degrees, minLat.degrees + latDelta.degrees, minLon.degrees,
            minLon.degrees + lonDelta.degrees);

        // ShortBuffer accesses on Android are slow; convert the buffer to an array and use the array to access the
        // individual elevations.
        ShortBuffer shortBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] elevations = new short[shortBuffer.remaining()];
        shortBuffer.get(elevations);

        return new ElevationTile(sector, level, key.getRow(), key.getColumn(), elevations);
    }

    protected String getTileCacheKey(TileKey key)
    {
        // Determine the cache path for the specified tile. In order to maximize its reuse, the Tile utility class does
        // not have the concept of a format suffix in its cache path. It is the layer's responsibility notion of image
        // format that defines what suffix is appended to the tile's cache path.
        StringBuilder sb = new StringBuilder();
        sb.append(key.getTileCacheKey());
        sb.append(WWIO.makeSuffixForMimeType(this.imageFormat));

        return sb.toString();
    }
}
