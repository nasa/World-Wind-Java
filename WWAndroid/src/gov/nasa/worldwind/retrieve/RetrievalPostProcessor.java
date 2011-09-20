/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.retrieve;

import java.nio.ByteBuffer;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface RetrievalPostProcessor
{
    ByteBuffer run(Retriever retriever);
}
