/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.retrieve.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class BasicTiledImageLayer extends TiledImageLayer
{
    protected static class RequestTask implements Runnable, Comparable<RequestTask>
    {
        protected GpuTextureTile tile;
        protected BasicTiledImageLayer layer;
        protected double priority;

        public RequestTask(GpuTextureTile tile, BasicTiledImageLayer layer, double priority)
        {
            if (tile == null)
            {
                String msg = Logging.getMessage("nullValue.TileIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            if (layer == null)
            {
                String msg = Logging.getMessage("nullValue.LayerIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tile = tile;
            this.layer = layer;
            this.priority = priority;
        }

        public void run()
        {
            if (Thread.currentThread().isInterrupted())
                return; // This task was cancelled because it's a duplicate or for some other reason.

            this.layer.loadTile(this.tile, this.priority);
        }

        public int compareTo(RequestTask that)
        {
            if (that == null)
                return -1;

            return this.priority < that.priority ? -1 : (this.priority > that.priority ? 1 : 0);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (!(o instanceof RequestTask))
                return false;

            RequestTask that = (RequestTask) o;
            return this.tile.equals(that.tile);
        }

        @Override
        public int hashCode()
        {
            return this.tile.hashCode();
        }

        @Override
        public String toString()
        {
            return this.tile.toString();
        }
    }

    protected static class DownloadPostProcessor extends AbstractRetrievalPostProcessor
    {
        protected GpuTextureTile tile;
        protected BasicTiledImageLayer layer;

        public DownloadPostProcessor(GpuTextureTile tile, BasicTiledImageLayer layer)
        {
            super(layer);

            this.tile = tile;
            this.layer = layer;
        }

        @Override
        protected void markResourceAbsent()
        {
            this.layer.getLevels().markTileAbsent(this.tile.getTileKey());
        }

        @Override
        protected Object getFileLock()
        {
            return this.layer.fileLock;
        }

        @Override
        protected File doGetOutputFile()
        {
            return layer.getDataFileStore().newFile(this.tile.getPath());
        }

        @Override
        protected ByteBuffer handleSuccessfulRetrieval()
        {
            ByteBuffer buffer = super.handleSuccessfulRetrieval();

            // Fire a property change to denote that the layer's backing data has changed.
            this.layer.firePropertyChange(AVKey.LAYER, null, this);

            return buffer;
        }

        @Override
        protected ByteBuffer handleTextContent() throws IOException
        {
            this.markResourceAbsent();

            return super.handleTextContent();
        }
    }

    protected static final int DEFAULT_REQUEST_QUEUE_SIZE = 200;

    protected Queue<Runnable> requestQ;
    protected final Object fileLock = new Object();
    protected TileUrlBuilder urlBuilder;

    public BasicTiledImageLayer(AVList params)
    {
        super(params);
    }

    public BasicTiledImageLayer(Element element)
    {
        super(element);
    }

    @Override
    protected void init()
    {
        super.init();

        this.requestQ = this.createRequestQueue();
    }

    /**
     * Create the queue that holds pending tile requests.
     *
     * @return new request queue.
     */
    protected Queue<Runnable> createRequestQueue()
    {
        return new PriorityQueue<Runnable>(DEFAULT_REQUEST_QUEUE_SIZE);
    }

    @Override
    protected void doRender(DrawContext dc)
    {
        super.doRender(dc);

        this.sendTileRequests();
        this.requestQ.clear(); // This request queue should be empty after sendTileRequests, but we clear it anyway.
    }

    @Override
    protected void requestTile(DrawContext dc, GpuTextureTile tile)
    {
        Runnable task = this.createRequestTask(dc, tile);
        if (task == null)
        {
            String msg = Logging.getMessage("nullValue.TaskIsNull");
            Logging.warning(msg);
            return;
        }

        this.requestQ.add(task);
    }

    /** Submit pending tile requests to the task service for background processing. */
    protected void sendTileRequests()
    {
        if (this.requestQ.isEmpty())
            return;

        TaskService service = WorldWind.getTaskService();

        Runnable task;
        while ((task = this.requestQ.poll()) != null)
        {
            if (!service.isFull())
                service.runTask(task);
        }
    }

    /**
     * Create a task to load a tile.
     *
     * @param dc   current draw context.
     * @param tile tile to load.
     *
     * @return new task.
     */
    protected Runnable createRequestTask(DrawContext dc, GpuTextureTile tile)
    {
        return new RequestTask(tile, this, this.computeTilePriority(dc, tile));
    }

    /**
     * Compute the priority of loading this tile, based on distance from the eye to the tile's center point. Tiles
     * closer to the eye have higher priority than those far from the eye.
     *
     * @param dc   current draw context.
     * @param tile tile for which to compute the priority.
     *
     * @return tile priority. A lower number indicates higher priority.
     */
    protected double computeTilePriority(DrawContext dc, GpuTextureTile tile)
    {
        // Tile priority is ordered from low (most priority) to high (least priority). Assign the tile priority based
        // on square distance form the eye point. Since we don't care about the actual distance this enables us to
        // avoid a square root computation. Tiles further from the eye point are loaded last.
        return dc.getView().getEyePoint().distanceToSquared3(tile.getExtent().getCenter());
    }

    /**
     * Load a tile. If the tile exists in the file cache, it will be loaded from the file cache. If not, it will be
     * requested from the network.
     *
     * @param tile     tile to load.
     * @param priority the priority of this tile. If the tile needs to be retrieved from the network, this value
     *                 determines the priority of the retrieval task.
     */
    protected void loadTile(GpuTextureTile tile, double priority)
    {
        URL textureURL = this.getDataFileStore().findFile(tile.getPath(), false);
        if (textureURL != null)
        {
            this.loadTileFromCache(tile, textureURL);
        }
        else
        {
            this.retrieveTexture(tile, this.createDownloadPostProcessor(tile), priority);
        }
    }

    /**
     * Load a tile from the file cache.
     *
     * @param tile       tile to load.
     * @param textureURL local URL to the cached resource.
     */
    protected void loadTileFromCache(GpuTextureTile tile, URL textureURL)
    {
        GpuTextureData textureData;

        synchronized (this.fileLock)
        {
            textureData = this.createTextureData(textureURL);
        }

        if (textureData != null)
        {
            tile.setTextureData(textureData);

            // The tile's size has changed, so update its size in the memory cache.
            MemoryCache cache = this.getTextureTileCache();
            if (cache.contains(tile.getTileKey()))
                cache.put(tile.getTileKey(), tile);

            // Mark the tile as not absent to ensure that it is used, and cause any World Windows containing this layer
            // to repaint themselves.
            this.levels.unmarkTileAbsent(tile.getTileKey());
            this.firePropertyChange(AVKey.LAYER, null, this);
        }
        else
        {
            // Assume that something is wrong with the file and delete it.
            this.getDataFileStore().removeFile(textureURL);
            String message = Logging.getMessage("generic.DeletedCorruptDataFile", textureURL);
            Logging.info(message);
        }
    }

    protected GpuTextureData createTextureData(URL textureURL)
    {
        return BasicGpuTextureFactory.createTextureData(AVKey.GPU_TEXTURE_FACTORY, textureURL, null);
    }

    /**
     * Retrieve a tile from the network. This method initiates an asynchronous retrieval task and then returns.
     *
     * @param tile          tile to download.
     * @param postProcessor post processor to handle the retrieval.
     * @param priority      priority of the retrieval task.
     */
    protected void retrieveTexture(GpuTextureTile tile, DownloadPostProcessor postProcessor, double priority)
    {
        if (!this.isNetworkRetrievalEnabled())
        {
            this.getLevels().markTileAbsent(tile.getTileKey());
            return;
        }

        if (!WorldWind.getRetrievalService().isAvailable())
            return;

        URL url = this.urlBuilder.getURL(tile, null);
        if (url == null)
            return;

        if (WorldWind.getNetworkStatus().isHostUnavailable(url))
        {
            this.getLevels().markTileAbsent(tile.getTileKey());
            return;
        }

        Retriever retriever = URLRetriever.createRetriever(url, postProcessor);
        if (retriever == null)
        {
            Logging.error(Logging.getMessage("layers.TextureLayer.UnknownRetrievalProtocol", url.toString()));
            return;
        }

        // Apply any overridden timeouts.
        Integer connectTimeout = AVListImpl.getIntegerValue(this, AVKey.URL_CONNECT_TIMEOUT);
        if (connectTimeout != null && connectTimeout > 0)
            retriever.setConnectTimeout(connectTimeout);

        Integer readTimeout = AVListImpl.getIntegerValue(this, AVKey.URL_READ_TIMEOUT);
        if (readTimeout != null && readTimeout > 0)
            retriever.setReadTimeout(readTimeout);

        Integer staleRequestLimit = AVListImpl.getIntegerValue(this, AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT);
        if (staleRequestLimit != null && staleRequestLimit > 0)
            retriever.setStaleRequestLimit(staleRequestLimit);

        WorldWind.getRetrievalService().runRetriever(retriever, priority);
    }

    /**
     * Create a post processor for a tile retrieval task.
     *
     * @param tile tile to create a post processor for.
     *
     * @return new post processor.
     */
    protected DownloadPostProcessor createDownloadPostProcessor(GpuTextureTile tile)
    {
        return new DownloadPostProcessor(tile, this);
    }
}
