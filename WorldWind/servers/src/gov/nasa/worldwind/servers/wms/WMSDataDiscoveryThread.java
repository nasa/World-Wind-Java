/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.cache.FileStoreFilter;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Acts as a surrogate for a container to service a WMS request.
 *
 * @author garakl
 * @version $Id$
 */
public class WMSDataDiscoveryThread extends Thread
{
    protected static class LayerEntry
    {
        private final Element configElement;
        private final AVList params;
        private final long lastModifiedTime;

        public LayerEntry(Element configElement, AVList params, long lastModifiedTime)
        {
            this.configElement = configElement;
            this.params = params;
            this.lastModifiedTime = lastModifiedTime;
        }

        public Element getConfigurationDocument()
        {
            return this.configElement;
        }

        public AVList getParams()
        {
            return this.params;
        }

        public long getLastModifiedTime()
        {
            return this.lastModifiedTime;
        }
    }

    private static final String DISCOVERY_TASK = "Discovery Task: ";
    private static final int WAKEUP_PERIOD = 60000; // 60 seconds (60,000 milli-sec)

    private FileStore dataFileStore = null;
    private FileStoreFilter fileStoreFilter = null;
    private final Map<String, LayerEntry> layers = Collections.synchronizedMap(new HashMap<String, LayerEntry>());
    private EventListenerList eventListeners = new EventListenerList();

    private WMSServerApplication app;

    public WMSDataDiscoveryThread(WMSServerApplication app, FileStore dataFileStore, FileStoreFilter filter)
    {
        if (dataFileStore == null)
        {
            String message = Logging.getMessage("nullValue.FileStoreIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (filter == null)
        {
            String message = Logging.getMessage("nullValue.FilterIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == app)
        {
            String msg = Logging.getMessage("WMS.Server.ApplicationNotFound");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.app = app;
        this.dataFileStore = dataFileStore;
        this.fileStoreFilter = filter;
    }

    public WMSLayerEventListener[] getWMSLayerEventListeners()
    {
        return this.eventListeners.getListeners(WMSLayerEventListener.class);
    }

    public void addWMSLayerEventListener(WMSLayerEventListener listener)
    {
        this.eventListeners.add(WMSLayerEventListener.class, listener);
    }

    public void removeWMSLayerEventListener(WMSLayerEventListener listener)
    {
        this.eventListeners.remove(WMSLayerEventListener.class, listener);
    }

    protected void fireLayerAdded(WMSLayerEvent e)
    {
        // Iterate over the listener list in reverse order. This has the effect of notifying the listeners in the
        // order they were added.
        WMSLayerEventListener[] listeners = this.eventListeners.getListeners(WMSLayerEventListener.class);
        for (int i = listeners.length - 1; i >= 0; i--)
        {
            try
            {
                listeners[i].layerAdded(e);
            }
            catch (Exception ex)
            {
                Logging.logger().severe(ex.getMessage());
            }
        }
    }

    protected void fireLayerRemoved(WMSLayerEvent e)
    {
        // Iterate over the listener list in reverse order. This has the effect of notifying the listeners in the
        // order they were added.
        WMSLayerEventListener[] listeners = this.eventListeners.getListeners(WMSLayerEventListener.class);
        for (int i = listeners.length - 1; i >= 0; i--)
        {
            try
            {
                listeners[i].layerRemoved(e);
            }
            catch (Exception ex)
            {
                Logging.logger().severe(ex.getMessage());
            }
        }
    }

    protected void fireLayerChanged(WMSLayerEvent e)
    {
        // Iterate over the listener list in reverse order. This has the effect of notifying the listeners in the
        // order they were added.
        WMSLayerEventListener[] listeners = this.eventListeners.getListeners(WMSLayerEventListener.class);
        for (int i = listeners.length - 1; i >= 0; i--)
        {
            try
            {
                listeners[i].layerChanged(e);
            }
            catch (Exception ex)
            {
                Logging.logger().severe(ex.getMessage());
            }
        }
    }

    public void run()
    {
        try
        {
            if (null == this.app)
            {
                String msg = Logging.getMessage("WMS.Server.ApplicationIsNull");
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            Configuration config = this.app.getConfiguration();
            if (WWUtil.isEmpty(config))
            {
                String msg = Logging.getMessage("nullValue.ConfigurationIsNull");
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            while (!Thread.currentThread().isInterrupted())
            {
                this.doRun();

                int wakeup_period = config.getAutoDiscoveryPeriod();
                if (wakeup_period > 0)
                {
                    Thread.sleep(wakeup_period);
                }
                else
                {
                    break;
                }
            }
        }
        catch (WWRuntimeException wwe)
        {
            Thread.currentThread().interrupt();
            String msg = Logging.getMessage("generic.TaskIsInterrupted", DISCOVERY_TASK, wwe.getMessage());
            Logging.logger().severe(msg);
        }
        catch (InterruptedException exit)
        {
            Thread.currentThread().interrupt();
            String msg = Logging.getMessage("generic.TaskIsInterrupted", DISCOVERY_TASK, exit.getMessage());
            Logging.logger().fine(msg);
        }
        catch (Exception e)
        {
            Thread.currentThread().interrupt();
            String msg = Logging.getMessage("generic.TaskIsInterrupted", DISCOVERY_TASK, e.getMessage());
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
        }
    }

    protected void doRun()
    {
        Logging.logger().fine(DISCOVERY_TASK + "Looking for added/removed layers... ");

        // The missingLayers map initially will contain all known (already configured) layers. During discovery if we
        // find a layer we already know about, we will remove it from the knownLayers map, but not from our internal
        // this.layers map. At the end, we will check if there are any layers left in the knownLayers. This means that
        // these layers were either NOT found or have changed and must be removed.
        Map<String, LayerEntry> missingLayers = new HashMap<String, LayerEntry>(this.layers);

        String[] filenames = this.listConfigFileNames(this.dataFileStore);
        if (null != filenames && filenames.length > 0)
        {
            for (String filename : filenames)
            {
                if (null == filename)
                {
                    continue;
                }

                long lastModifiedTime = this.getLastModifiedTime(this.dataFileStore, filename);

                // If the configuration filename exists in the list of known layers, then we remove it from the list of
                // potentially missing filenames, and check if the configuration has changed since we last encountered it.
                if (this.layers.containsKey(filename))
                {
                    Logging.logger().finest(DISCOVERY_TASK + "Existing layer configuration found: " + filename);

                    // This configuration filename exists, and it is not missing.
                    missingLayers.remove(filename);

                    // Compare the configuration file's last modified time against our entries last modified time. If our
                    // entry is out of date, then create a new entry with the new modified time, and fire a layer changed
                    // event.
                    LayerEntry entry = this.layers.get(filename);
                    if (null != entry && entry.getLastModifiedTime() < lastModifiedTime)
                    {
                        Logging.logger().fine(DISCOVERY_TASK + "Existing layer configuration changed: " + filename);

                        Element configElement = entry.getConfigurationDocument();
                        AVList params = entry.getParams();

                        this.layers.put(filename, new LayerEntry(configElement, params, lastModifiedTime));
                        this.fireLayerChanged(new WMSLayerEvent(this, filename, configElement, params));
                    }
                }
                // Otherwise the configuration file is new. Create an entry for the configuration file, and fire a layer
                else
                {
                    Document config = this.openDataConfig(this.dataFileStore, filename);
                    if (null != config)
                    {
                        Logging.logger().fine(DISCOVERY_TASK + "New layer configuration found: " + filename);

                        AVList params = new AVListImpl();
                        this.getDataConfigParams(this.dataFileStore, filename, config.getDocumentElement(), params);

                        this.layers.put(filename, new LayerEntry(config.getDocumentElement(), params, lastModifiedTime));
                        this.fireLayerAdded(new WMSLayerEvent(this, filename, config.getDocumentElement(), params));
                    }
                }
            }
        }
        // If the missingLayers map has any entries, then some of the known configuration files were not discovered,
        // which means they are either unavailable or were intentionally removed. Remove the entries for those layers
        // and fire a layer remove event.
        if (missingLayers.size() > 0)
        {
            Logging.logger().fine(DISCOVERY_TASK + "Existing layer configurations removed.");
            for (String filename : missingLayers.keySet())
            {
                LayerEntry entry = this.layers.get(filename);
                if (null != entry)
                {
                    Logging.logger().fine(DISCOVERY_TASK + "Existing layer configuration removed: " + filename);

                    this.layers.remove(filename);
                    this.fireLayerRemoved(
                            new WMSLayerEvent(this, filename, entry.getConfigurationDocument(), entry.getParams()));
                }
            }
            missingLayers.clear();
        }
        // All know configuration files were discovered again.
        else
        {
            Logging.logger().fine(DISCOVERY_TASK + "No existing layer configurations removed.");
        }
    }

    protected String[] listConfigFileNames(FileStore fileStore)
    {
        return fileStore.listTopFileNames(null, this.fileStoreFilter);
    }

    protected Document openDataConfig(FileStore fileStore, String filename)
    {
        // Search the file store for the data configuration file with the specified name, but don't search the class
        // path.
        URL url = fileStore.findFile(filename, false);
        if (null == url)
        {
            Logging.logger().warning(DISCOVERY_TASK + "No URL for layer configuration filename: " + filename);
            return null;
        }

        try
        {
            // Open the data configuration URL as an XML document.
            Document doc = WWXML.openDocument(url);
            // If the data configiuration document is a DataDescriptor or a World Wind .NET LayerSet, then convert it to
            // a standard Layer or ElevationModel configuration document.
            doc = DataConfigurationUtils.convertToStandardDataConfigDocument(doc);
            return doc;
        }
        catch (Exception e)
        {
            Logging.logger().severe(DISCOVERY_TASK + "Cannot create layer configuration for filename: " + filename);
        }

        return null;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void getDataConfigParams(FileStore fileStore, String filename, Element configElement, AVList params)
    {
        DataConfigurationUtils.getDataConfigCacheName(filename, params);
    }

    protected long getLastModifiedTime(FileStore fileStore, String filename)
    {
        URL url = fileStore.findFile(filename, false);
        if (null == url)
        {
            return -1;
        }

        File file = WWIO.convertURLToFile(url);
        if (null == file)
        {
            return -1;
        }

        return file.lastModified();
    }
}
