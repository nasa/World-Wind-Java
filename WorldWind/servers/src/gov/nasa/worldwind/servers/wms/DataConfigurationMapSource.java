/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWXML;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;
import java.util.Vector;

/**
 * @author garakl
 * @version $Id$
 */
public class DataConfigurationMapSource extends MapSource
{
    protected FileStore fileStore = null;
    protected Element configElement = null;

    public DataConfigurationMapSource(FileStore fileStore, Element configElement, AVList params, Class<?> serviceClass)
    {
        if (null == fileStore)
        {
            String message = Logging.getMessage("nullValue.FileStoreIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == configElement)
        {
            String message = Logging.getMessage("nullValue.ElementIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == params)
        {
            String message = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == serviceClass)
        {
            String message = Logging.getMessage("nullValue.ClassIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.fileStore = fileStore;
        this.configElement = configElement;
        this.params = params;
        this.serviceClass = serviceClass;

        this.init();
    }

    public FileStore getFileStore()
    {
        return this.fileStore;
    }

    public Element getConfigurationDocument()
    {
        return this.configElement;
    }

    protected void init()
    {
        this.name = this.params.getStringValue(AVKey.DATASET_NAME);
        if (null == this.name || this.name.length() == 0)
        {
            String message = Logging.getMessage("nullValue.DataSetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.name = name.replace(",", "_");  // commas trip up "Layers=" clause in GetMap/Feature requests...
        
        this.title = this.params.getStringValue(AVKey.DISPLAY_NAME);
        if (null == this.title || this.title.length() == 0)
            this.title = WWXML.getText(this.configElement, "DisplayName");
        if (null == this.title || this.title.length() == 0)
            this.title = this.name;

        this.description = this.title;
        this.keywords = this.params.getStringValue( AVKey.LAYER_NAMES );

        String storePath = this.params.getStringValue( AVKey.DATA_CACHE_NAME );
        if (null == storePath || storePath.length() == 0)
        {
            String message = Logging.getMessage( "generic.FolderDoesNotExist", storePath );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        URL storeURL = this.fileStore.findFile( storePath, false );
        if (null == storeURL)
        {
            String message = Logging.getMessage( "generic.FolderDoesNotExist", storePath );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        File storeFile = WWIO.convertURLToFile( storeURL );
        this.rootDir = (storeFile != null) ? storeFile.getPath() : null;

        this.lastUpdate = this.parseLastUpdate( this.params.getStringValue( AVKey.LAST_UPDATE ) );
        if( null == this.lastUpdate && null != this.params.getValue( AVKey.EXPIRY_TIME ) )
            this.lastUpdate = this.params.getValue( AVKey.EXPIRY_TIME ).toString();

        // TODO: calculate scaleHInt based on
        // AVKey.NUM_LEVELS, AVKey.NUM_EMPTY_LEVELS, AVKey.INACTIVE_LEVELS, AVKey.SECTOR,
        // AVKey.SECTOR_RESOLUTION_LIMITS, AVKey.TILE_WIDTH, AVKey.TILE_HEIGHT,
        // and AVKey.LEVEL_ZERO_TILE_DELTA
//        double scaleHintMin,
//        double scaleHintMax,

        this.nestedMapSources = new Vector<MapSource>(1);
    }
}
