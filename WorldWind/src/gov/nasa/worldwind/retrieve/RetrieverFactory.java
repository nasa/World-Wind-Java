/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.retrieve;

import gov.nasa.worldwind.avlist.AVList;

/**
 * @author tag
 * @version $Id$
 */
public interface RetrieverFactory
{
    Retriever createRetriever(AVList params, RetrievalPostProcessor postProcessor);
}
