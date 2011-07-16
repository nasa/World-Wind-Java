/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author brownrigg
 * @version $Id$
 */
public class BasicMapRegistry implements MapRegistry, WMSLayerEventListener
{
    private Map<String, MapSource> mapSources = new ConcurrentHashMap<String, MapSource>();
    private Map<Object, MapSource> dataConfigMapSources = new ConcurrentHashMap<Object, MapSource>();

    protected WMSServerApplication app = null;

    public BasicMapRegistry(WMSServerApplication app)
    {
        this.app = app;
    }

    public Iterable<String> getMapNames()
    {
        // The map's keyset is backed by the map itself, so changes to the keyset are reflected in the map. This
        // getter method should not modify the registry, therefore we defensively wrap the keyset in an unmodifiable
        // set.
        return Collections.unmodifiableSet(this.mapSources.keySet());
    }

    public MapSource get(String mapName)
    {
        if (mapName == null)
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.mapSources.get(mapName);
    }

    public boolean contains(String mapName)
    {
        if (mapName == null)
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.mapSources.get(mapName) != null;
    }

    public void add(MapSource source)
    {
        if (source == null)
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.doAdd(source);
    }

    public void remove(MapSource source)
    {
        if (source == null)
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.doRemove(source);
    }

    public void layerAdded(WMSLayerEvent e)
    {
        if (e == null)
        {
            return;
        }

        try
        {
            this.doLayerAdded(e);
        }
        catch (Exception ex)
        {
            Logging.logger().severe(Logging.getMessage("generic.CannotCreateLayer", ex.getMessage()));
        }
    }

    public void layerRemoved(WMSLayerEvent e)
    {
        if (e == null)
        {
            return;
        }

        try
        {
            this.doLayerRemoved(e);
        }
        catch (Exception ex)
        {
            Logging.logger().severe(Logging.getMessage("generic.CannotRemoveLayer", ex.getMessage()));
        }
    }

    public void layerChanged(WMSLayerEvent e)
    {
        if (e == null)
        {
            return;
        }

        try
        {
            this.doLayerChanged(e);
        }
        catch (Exception ex)
        {
            Logging.logger().severe(Logging.getMessage("generic.CannotChangeLayer", ex.getMessage()));
        }
    }

    protected void doLayerAdded(WMSLayerEvent e)
    {
        Object source = e.getConfigurationSource();
        if (WWUtil.isEmpty(source))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        if (this.dataConfigMapSources.containsKey(source))
        {
            String message = Logging.getMessage("generic.DuplicateLayerFound", source);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        MapSource mapSource = this.createDataConfigurationMapSource(e);
        if (null == mapSource)
        {
            String message = Logging.getMessage("generic.UnrecognizedSourceType", source);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        this.add(mapSource);
        this.dataConfigMapSources.put(e.getConfigurationSource(), mapSource);

        Logging.logger().info(Logging.getMessage("generic.LayerAdded", source));
    }

    protected void doLayerRemoved(WMSLayerEvent e)
    {
        Object source = e.getConfigurationSource();
        if (WWUtil.isEmpty(source))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        MapSource mapSource = this.dataConfigMapSources.get(source);
        if (null == mapSource)
        {
            String message = Logging.getMessage("generic.LayerNotFound", source);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        this.remove(mapSource);
        this.dataConfigMapSources.remove(source);

        Logging.logger().info(Logging.getMessage("generic.LayerRemoved", source));
    }

    protected void doLayerChanged(WMSLayerEvent e)
    {
        // Remove the existing MapSource which is mapped to the layer's configuration source.
        this.doLayerRemoved(e);
        // Add a new MapSource constructed from the updated configuration.
        this.doLayerAdded(e);
    }

    protected void doAdd(MapSource mapSrc)
    {
        try
        {
            // initialize each named MapSource's MapGenerator; add to registry only if successful...
            MapGenerator mapGen = mapSrc.getMapGenerator();
            if (!mapGen.isInitialized())
            {
                Logging.logger().info(Logging.getMessage("WMS.MapSource.Initializing", mapSrc.getName()));

                mapGen.setApplicationContext(this.app);

                mapGen.setMapSource(mapSrc);
                if (mapGen.initialize(mapSrc))
                {
                    mapGen.markAsInitialized();

//                    WMSServer.logMemoryUsage( mapSrc.getName() );

                    this.mapSources.put(mapSrc.getName(), mapSrc);

                    Logging.logger().info(Logging.getMessage("WMS.MapSource.InitSuccess", mapSrc.getName()));
                }
                else
                {
                    Logging.logger().severe(Logging.getMessage("WMS.MapSource.InitFailedAndDisabled", mapSrc.getName()));
                }
            }

            // iterate over any children...
            Iterator<MapSource> iter = mapSrc.getChildren();
            while (iter.hasNext())
            {
                this.doAdd(iter.next());
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.MapGenerator.CannotInstantiate", mapSrc.getName());
            Logging.logger().log(Level.SEVERE, msg, ex);
        }
    }

    protected void doRemove(MapSource mapSrc)
    {
        try
        {
            Logging.logger().info("Deregistering MapSource " + mapSrc.getName() + "...");

            this.mapSources.remove(mapSrc.getName());

            // iterate over any children...
            Iterator<MapSource> iter = mapSrc.getChildren();
            while (iter.hasNext())
            {
                this.doRemove(iter.next());
            }
        }
        catch (Exception ex)
        {
            Logging.logger().severe("Could not deregister MapSource: " + ex.toString());
        }
    }

    protected MapSource createDataConfigurationMapSource(WMSLayerEvent e)
    {
        AVList params = (e.getParameters() != null) ? e.getParameters().copy() : null;


        Factory factory = this.app.getMapSourceFactory();
        return (MapSource) factory.createFromConfigSource(e.getConfigurationDocument(), params);
    }
}
