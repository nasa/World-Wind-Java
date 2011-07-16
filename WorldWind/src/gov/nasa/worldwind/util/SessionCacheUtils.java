/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.cache.SessionCache;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.retrieve.*;

import java.beans.PropertyChangeListener;

/**
 * A collection of utility methods for retrieving and managing data in the {@link SessionCache}.
 *
 * @author dcollins
 * @version $Id$
 */
public class SessionCacheUtils
{
    /**
     * Asynchronously retrieves the contents of a specified {@link java.net.URL}. If successful, this places the URL
     * contents in a specified session cache with a specified key. This either marks the resource as available or
     * missing, depending on whether the retrieval succeeds or fails. Finally, this optionally notifies the caller that
     * the retrieval has succeeded by firing a property change event. If either the property listener or property name
     * are null, that functionality is disabled.
     *
     * @param url                the URL contents to retrieve.
     * @param cache              the cache which receives the retrieved data.
     * @param cacheKey           the cache key which identifies where the retrieved data is placed in the session
     *                           cache.
     * @param absentResourceList the absent resource list to update.
     * @param resourceID         the resource ID to use in the absent resource list.
     * @param propertyListener   the property change listener which is fired when the retrieved data is available.
     * @param propertyName       the property name to fire when retrieved data is available.
     *
     * @throws IllegalArgumentException if any of the url, retrieval service, cache, or cache key are null.
     */
    public static void retrieveSessionData(java.net.URL url, SessionCache cache, Object cacheKey,
        AbsentResourceList absentResourceList, long resourceID, PropertyChangeListener propertyListener,
        String propertyName)
    {
        if (url == null)
        {
            String message = Logging.getMessage("nullValue.URLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cache == null)
        {
            String message = Logging.getMessage("nullValue.CacheIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cacheKey == null)
        {
            String message = Logging.getMessage("nullValue.CacheKeyIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (WorldWind.getNetworkStatus().isHostUnavailable(url))
        {
            absentResourceList.markResourceAbsent(resourceID);
            return;
        }

        SessionCacheRetrievalPostProcessor postProcessor = new SessionCacheRetrievalPostProcessor(cache, cacheKey,
            absentResourceList, resourceID, propertyListener, propertyName);
        postProcessor.setName(url.toString());

        Retriever retriever = URLRetriever.createRetriever(url, postProcessor);
        WorldWind.getRetrievalService().runRetriever(retriever);
    }

    /**
     * Checks a session cache for a specified key, and if present attempts to interpret the cache entry as a {@link
     * WMSCapabilities} document. If the key does not exist in the cache, or the cache entry cannot be interpreted as a
     * Capabilities document, this returns null. If the entry exists, but must be converted to a Capabilities document,
     * this overrides the previous cache entry with the the newly converted Capabilities.
     *
     * @param cache    the session cache.
     * @param cacheKey the key to identify the object in the session cache.
     * @param name     the name to use in logging messages.
     *
     * @return the Capabilities document in the session cache, or null if either the key does not match an entry in the
     *         cache, or that entry cannot be interpreted as a Capabilities document.
     *
     * @throws IllegalArgumentException if either the cache or cache key are null.
     */
    public static WMSCapabilities getSessionCapabilities(SessionCache cache, Object cacheKey, String name)
    {
        if (cache == null)
        {
            String message = Logging.getMessage("nullValue.CacheIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cacheKey == null)
        {
            String message = Logging.getMessage("nullValue.CacheKeyIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Object o = cache.get(cacheKey);
        if (o == null)
            return null;

        // The cache entry exists, and is already a Capabilities document.
        if (o instanceof WMSCapabilities)
            return (WMSCapabilities) o;

        // The cache entry exists, but is not a Capabilities document. Attempt to parse the Capabilities docuemnt,
        // by treating the current cache entry as a source.
        WMSCapabilities caps = parseCapabilities(o, name);
        if (caps == null)
            return null;

        // If the parsing succeeded, then overwrite the existing cache entry with the newly created Capabilities.
        cache.put(cacheKey, caps);
        return caps;
    }

    /**
     * Checks a session cache for a specified key, and if present attempts to interpret the cache entry as a {@link
     * WMSCapabilities} document. If the key does not map to a Capabilities document for any reason, this attempts to
     * asynchronously retrieve the Capabilities from a specified URL, and returns null.
     *
     * @param url                the URL contents to retrieve.
     * @param cache              the session cache.
     * @param cacheKey           the key to identify the object in the session cache.
     * @param absentResourceList the absent resource list to update.
     * @param resourceID         the resource ID to use in the absent resource list.
     * @param propertyListener   the property change listener which is fired when the retrieved data is available.
     * @param propertyName       the property name to fire when retrieved data is available.
     *
     * @return the Capabilities document in the session cache, or null if the document is not in the cache.
     *
     * @throws IllegalArgumentException if either the url, retrieval service, cache or cache key are null.
     */
    public static WMSCapabilities getOrRetrieveSessionCapabilities(java.net.URL url, SessionCache cache,
        Object cacheKey, AbsentResourceList absentResourceList, long resourceID,
        PropertyChangeListener propertyListener, String propertyName)
    {
        if (url == null)
        {
            String message = Logging.getMessage("nullValue.URLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cache == null)
        {
            String message = Logging.getMessage("nullValue.CacheIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (cacheKey == null)
        {
            String message = Logging.getMessage("nullValue.CacheKeyIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        WMSCapabilities caps = getSessionCapabilities(cache, cacheKey, url.toString());
        if (caps != null)
            return caps;

        retrieveSessionData(url, cache, cacheKey, absentResourceList, resourceID, propertyListener, propertyName);

        return null;
    }

    protected static WMSCapabilities parseCapabilities(Object source, String name)
    {
        if (source == null)
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        java.io.InputStream inputStream = null;
        try
        {
            WMSCapabilities caps = new WMSCapabilities(source);
            return caps.parse();
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.CannotParseCapabilities", name);
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
        }
        finally
        {
            WWIO.closeStream(inputStream, name);
        }

        return null;
    }
}
