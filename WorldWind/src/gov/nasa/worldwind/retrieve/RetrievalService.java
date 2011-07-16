/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.retrieve;

import gov.nasa.worldwind.WWObject;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface RetrievalService extends WWObject
{
    RetrievalFuture runRetriever(Retriever retriever);

    RetrievalFuture runRetriever(Retriever retriever, double priority);

    void setRetrieverPoolSize(int poolSize);

    int getRetrieverPoolSize();

    boolean hasActiveTasks();

    boolean isAvailable();

    boolean contains(Retriever retriever);

    int getNumRetrieversPending();

    void shutdown(boolean immediately);
}
