/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.retrieve.RetrievalService;
import gov.nasa.worldwind.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class WorldWind
{
    protected static WorldWind instance = new WorldWind();
    protected MemoryCacheSet memoryCacheSet;
    protected RetrievalService retrievalService;
    protected NetworkStatus networkStatus;
    protected FileStore dataFileStore;
    protected TaskService taskService;

    // Singleton, prevent public instantiation.
    protected WorldWind()
    {
        this.initialize();
    }

    protected void initialize()
    {
        this.retrievalService = (RetrievalService) createConfigurationComponent(AVKey.RETRIEVAL_SERVICE_CLASS_NAME);
        this.dataFileStore = (FileStore) createConfigurationComponent(AVKey.DATA_FILE_STORE_CLASS_NAME);
        this.memoryCacheSet = (MemoryCacheSet) createConfigurationComponent(AVKey.MEMORY_CACHE_SET_CLASS_NAME);
        this.networkStatus = (NetworkStatus) createConfigurationComponent(AVKey.NETWORK_STATUS_CLASS_NAME);
        this.taskService = (TaskService) createConfigurationComponent(AVKey.TASK_SERVICE_CLASS_NAME);
    }

    public static RetrievalService getRetrievalService()
    {
        return instance.retrievalService;
    }

    public static MemoryCacheSet getMemoryCacheSet()
    {
        return instance.memoryCacheSet;
    }

    public static NetworkStatus getNetworkStatus()
    {
        return instance.networkStatus;
    }

    public static TaskService getTaskService()
    {
        return instance.taskService;
    }

    public static FileStore getDataFileStore()
    {
        return instance.dataFileStore;
    }

    /**
     * @param className the full name, including package names, of the component to create
     *
     * @return the new component
     *
     * @throws gov.nasa.worldwind.exception.WWRuntimeException
     *                                  if the <code>Object</code> could not be created
     * @throws IllegalArgumentException if <code>className</code> is null or zero length
     */
    public static Object createComponent(String className)
    {
        if (WWUtil.isEmpty(className))
        {
            String msg = Logging.getMessage("nullValue.ClassNameIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            Class c = Class.forName(className.trim());
            return c.newInstance();
        }
        catch (Throwable t)
        {
            String msg = Logging.getMessage("WorldWind.UnableToCreateClass", className);
            Logging.error(msg, t);
            throw new WWRuntimeException(msg, t);
        }
    }

    /**
     * @param classNameKey the key identifying the component
     *
     * @return the new component
     *
     * @throws IllegalStateException    if no name could be found which corresponds to <code>classNameKey</code>
     * @throws IllegalArgumentException if <code>classNameKey<code> is null
     * @throws WWRuntimeException       if the component could not be created
     */
    public static Object createConfigurationComponent(String classNameKey)
    {
        if (WWUtil.isEmpty(classNameKey))
        {
            String msg = Logging.getMessage("nullValue.ClassNameKeyIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String name = Configuration.getStringValue(classNameKey);
        if (name == null)
        {
            String msg = Logging.getMessage("WorldWind.NoClassNameInConfigurationForKey", classNameKey);
            Logging.error(msg);
            throw new WWRuntimeException(msg);
        }

        return WorldWind.createComponent(name);
    }
}
