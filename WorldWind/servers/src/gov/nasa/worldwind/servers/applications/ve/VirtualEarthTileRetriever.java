/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.applications.ve;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.retrieve.*;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.*;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;

/**
 * @author Lado Garakanidze
 * @version $
 */
public class VirtualEarthTileRetriever 
{
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    public static final int DEFAULT_READ_TIMEOUT = 20000;
    public static final long DEFAULT_EXPIRY_TIME = 2592000000L;

    // Connect timeout in milliseconds
    protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    // Read timeout in milliseconds
    protected int readTimeout = DEFAULT_READ_TIMEOUT;
    // cached tile expiration time
    protected long expiryTime = DEFAULT_EXPIRY_TIME;
    
    protected VirtualEarthTile[] tiles = null;

    public void setTileExpiryTime(long milliseconds)
    {
        this.expiryTime = ( milliseconds > 0L ) ? milliseconds : this.expiryTime; 
    }

    public VirtualEarthTileRetriever(VirtualEarthTile[] tiles)
    {
        if (null == tiles || 0 == tiles.length)
        {
            String message = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.tiles = tiles;
    }

    public void downloadTiles()
    {
        for (VirtualEarthTile tile : this.tiles)
        {
            try
            {
                String tilePath = tile.getPath();
                File tileFile = new File( tilePath );
                if( tileFile.exists() )
                {
                    long tileAge = System.currentTimeMillis() - tileFile.lastModified();
                    if( tileAge < expiryTime )
                    {
                        String msg = Logging.getMessage("generic.FileAlreadyExists", tilePath );
                        Logging.logger().finest( msg );
                        continue;
                    }
                }

                final URL resourceURL = tile.getURL();

                String protocol = resourceURL.getProtocol();
                if (!"http".equalsIgnoreCase(protocol))
                {
                    String msg = Logging.getMessage("generic.UnrecognizedProtocol", resourceURL);
                    throw new WWRuntimeException(msg);
                }

                Retriever retriever = new HTTPRetriever(resourceURL, new HttpRetrievalPostProcessor(tile));
                retriever.setConnectTimeout(connectTimeout);
                retriever.setReadTimeout(readTimeout);
                retriever.call();
            }
            catch (Exception e)
            {
                Logging.logger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private class HttpRetrievalPostProcessor implements RetrievalPostProcessor
    {
        private VirtualEarthTile tile;

        public HttpRetrievalPostProcessor(VirtualEarthTile tile)
        {
            this.tile = tile;
        }

        public ByteBuffer run(Retriever retriever)
        {
            if (!retriever.getState().equals(Retriever.RETRIEVER_STATE_SUCCESSFUL))
                return null;

            HTTPRetriever htr = (HTTPRetriever) retriever;
            if (htr.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)
            {
                String msg = Logging.getMessage("HTTP.NoContentReceived", this.tile.getURL() );
                Logging.logger().severe(msg);
                return null;
            }

            int responseCode = htr.getResponseCode();
            if ( responseCode != HttpURLConnection.HTTP_OK )
            {
                String msg = Logging.getMessage("HTTP.ResponseCode", responseCode, this.tile.getURL() );
                Logging.logger().severe(msg);
                return null;
            }

            URLRetriever r = (URLRetriever) retriever;
            ByteBuffer buffer = r.getBuffer();

            String suffix = WWIO.makeSuffixForMimeType( htr.getContentType() );
            String expectedSuffix = tile.getLayer().getExt().toLowerCase();

            if( null == suffix || !expectedSuffix.endsWith(suffix.toLowerCase()) )
            {
                String msg = Logging.getMessage("HTTP.UnexpectedContentType", htr.getContentType(), expectedSuffix );
                Logging.logger().severe(msg);
                return null;
            }

            try
            {
                String tilePath = tile.getPath();
                String parentPath = WWIO.getParentFilePath( tilePath );
                if( null != parentPath && !new File(parentPath).exists() )
                    WWIO.makeParentDirs( tilePath );

                String tempTilePath = tilePath + "." + System.currentTimeMillis();
                WWIO.saveBuffer(buffer, new File( tempTilePath ) );

                FileUtil.moveFile( tempTilePath, tilePath, true );

                return buffer;
            }
            catch (IOException e)
            {
                Logging.logger().log(Level.SEVERE, e.getMessage(), e);
                return null;
            }
        }
    }
}
