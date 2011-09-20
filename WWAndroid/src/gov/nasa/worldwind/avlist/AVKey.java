/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.avlist;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface AVKey
{
    final String DATA_CACHE_NAME = "gov.nasa.worldwind.avkey.DataCacheName";
    final String DATA_FILE_STORE_CLASS_NAME = "gov.nasa.worldwind.avkey.DataFileStoreClassName";
    final String DATA_FILE_STORE_CONFIGURATION_FILE_NAME
        = "gov.nasa.worldwind.avkey.DataFileStoreConfigurationFileName";
    final String DELETE_CACHE_ON_EXIT = "gov.nasa.worldwind.avkey.DeleteCacheOnExit";
    final String DETAIL_HINT = "gov.nasa.worldwind.avkey.DetailHint";
    final String DISPLAY_NAME = "gov.nasa.worldwind.avkey.DisplayName";

    final String EARTH_ELEVATION_MODEL_CONFIG_FILE = "gov.nasa.worldwind.avkey.EarthElevationModelConfigFile";
    final String ELEVATION_MAX = "gov.nasa.worldwind.avkey.ElevationMax";
    final String ELEVATION_MIN = "gov.nasa.worldwind.avkey.ElevationMin";
    final String ELEVATION_MODEL = "gov.nasa.worldwind.avkey.ElevationModel";
    final String ELEVATION_MODEL_FACTORY = "gov.nasa.worldwind.avkey.ElevationModelFactory";
    final String ELEVATION_TILE_CACHE_SIZE = "gov.nasa.worldwind.avkey.ElevationTileCacheSize";

    final String FILE_NAME = "gov.nasa.worldwind.avkey.FileName";
    final String FILE_STORE_LOCATION = "gov.nasa.worldwind.avkey.FileStoreLocation";
    final String FORMAT_SUFFIX = "gov.nasa.worldwind.avkey.FormatSuffixKey";

    final String GENERATE_MIPMAP = "gov.nasa.worldwind.avkey.GenerateMipmap";
    final String GLOBE = "gov.nasa.worldwind.avkey.Globe";
    final String GLOBE_CLASS_NAME = "gov.nasa.worldwind.avkey.GlobeClassName";
    final String GPU_RESOURCE_CACHE_SIZE = "gov.nasa.worldwind.avkey.GpuResourceCacheSize";
    final String GPU_TEXTURE_FACTORY = "gov.nasa.worldwind.avkey.GpuTextureFactory";
    final String GPU_TEXTURE_TILE_CACHE_SIZE = "gov.nasa.worldwind.avkey.GpuTextureTileCacheSize";

    final String IMAGE_FORMAT = "gov.nasa.worldwind.avkey.ImageFormat";
    final String INITIAL_LATITUDE = "gov.nasa.worldwind.avkey.InitialLatitude";
    final String INITIAL_LONGITUDE = "gov.nasa.worldwind.avkey.InitialLongitude";
    final String INITIAL_ALTITUDE = "gov.nasa.worldwind.avkey.InitialAltitude";
    final String INPUT_HANDLER_CLASS_NAME = "gov.nasa.worldwind.avkey.InputHandlerClassName";
    final String INSTALLED = "gov.nasa.worldwind.avkey.Installed";

    final String LAYER = "gov.nasa.worldwind.avkey.Layer";
    final String LAYERS = "gov.nasa.worldwind.avkey.Layers";
    final String LAYER_FACTORY = "gov.nasa.worldwind.avkey.LayerFactory";
    final String LAYER_NAMES = "gov.nasa.worldwind.avkey.LayerNames";
    final String LEVEL_NAME = "gov.nasa.worldwind.avkey.LevelName";
    final String LEVEL_NUMBER = "gov.nasa.worldwind.avkey.LevelNumber";
    final String LEVEL_ZERO_TILE_DELTA = "gov.nasa.worldwind.LevelZeroTileDelta";
    final String LOGCAT_TAG = "gov.nasa.worldwind.avkey.LogcatTag";

    final String MAX_ACTIVE_ALTITUDE = "gov.nasa.worldwind.avkey.MaxActiveAltitude";
    final String MAX_MESSAGE_REPEAT = "gov.nasa.worldwind.avkey.MaxMessageRepeat";
    final String MIN_ACTIVE_ALTITUDE = "gov.nasa.worldwind.avkey.MinActiveAltitude";
    final String MEMORY_CACHE_SET_CLASS_NAME = "gov.nasa.worldwind.avkey.MemoryCacheSetClassName";
    final String MODEL = "gov.nasa.worldwind.avkey.Model";
    final String MODEL_CLASS_NAME = "gov.nasa.worldwind.avkey.ModelClassName";

    final String NETWORK_STATUS_CLASS_NAME = "gov.nasa.worldwind.avkey.NetworkStatusClassName";
    final String NUM_EMPTY_LEVELS = "gov.nasa.worldwind.avkey.NumEmptyLevels";
    final String NUM_LEVELS = "gov.nasa.worldwind.avkey.NumLevels";

    final String OFFLINE_MODE = "gov.nasa.worldwind.avkey.OfflineMode";

    final String PICKED_OBJECT_PARENT_LAYER = "gov.nasa.worldwind.avkey.PickedObject.ParentLayer";
    final String POSITION = "gov.nasa.worldwind.avkey.Position";

    final String RETRIEVAL_POOL_SIZE = "gov.nasa.worldwind.avkey.RetrievalPoolSize";
    final String RETRIEVAL_QUEUE_SIZE = "gov.nasa.worldwind.avkey.RetrievalQueueSize";
    final String RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT = "gov.nasa.worldwind.avkey.RetrievalStaleRequestLimit";
    final String RETRIEVAL_SERVICE_CLASS_NAME = "gov.nasa.worldwind.avkey.RetrievalServiceClassName";
    final String RETRIEVER_STATE = "gov.nasa.worldwind.avkey.RetrieverState";

    final String SCENE_CONTROLLER_CLASS_NAME = "gov.nasa.worldwind.avkey.SceneControllerClassName";
    final String SECTOR = "gov.nasa.worldwind.avkey.Sector";
    final String SECTOR_GEOMETRY_CACHE_SIZE = "gov.nasa.worldwind.avkey.SectorGeometryCacheSize";
    final String SECTOR_GEOMETRY_TILE_CACHE_SIZE = "gov.nasa.worldwind.avkey.SectorGeometryTileCacheSize";
    final String SERVICE = "gov.nasa.worldwind.avkey.ServiceURLKey";
    final String STYLE_NAMES = "gov.nasa.worldwind.avkey.StyleNames";

    final String TESSELLATOR_FACTORY = "gov.nasa.worldwind.avkey.TessellatorFactory";
    final String TESSELLATOR_CONFIG_FILE = "gov.nasa.worldwind.avkey.TessellatorConfigFile";
    final String TASK_SERVICE_CLASS_NAME = "gov.nasa.worldwind.avkey.TaskServiceClassName";
    final String TASK_SERVICE_POOL_SIZE = "gov.nasa.worldwind.avkey.TaskServicePoolSize";
    final String TASK_SERVICE_QUEUE_SIZE = "gov.nasa.worldwind.avkey.TaskServiceQueueSize";
    final String TILE_DELTA = "gov.nasa.worldwind.avkey.TileDelta";
    final String TILE_HEIGHT = "gov.nasa.worldwind.avkey.TileHeight";
    final String TILE_URL_BUILDER = "gov.nasa.worldwind.avkey.TileURLBuilder";
    final String TILE_WIDTH = "gov.nasa.worldwind.avkey.TileWidth";

    final String URL_CONNECT_TIMEOUT = "gov.nasa.worldwind.avkey.URLConnectTimeout";
    final String URL_PROXY_HOST = "gov.nasa.worldwind.avkey.UrlProxyHost";
    final String URL_PROXY_PORT = "gov.nasa.worldwind.avkey.UrlProxyPort";
    final String URL_PROXY_TYPE = "gov.nasa.worldwind.avkey.UrlProxyType";
    final String URL_READ_TIMEOUT = "gov.nasa.worldwind.avkey.URLReadTimeout";

    final String VERTICAL_EXAGGERATION = "gov.nasa.worldwind.avkey.VerticalExaggeration";
    final String VIEW = "gov.nasa.worldwind.avkey.View";
    final String VIEW_CLASS_NAME = "gov.nasa.worldwind.avkey.ViewClassName";
}
