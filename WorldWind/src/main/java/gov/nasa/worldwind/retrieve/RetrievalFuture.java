/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.retrieve;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface RetrievalFuture extends java.util.concurrent.Future<Retriever>
{
    public Retriever getRetriever();
}
