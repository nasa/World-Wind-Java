/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

/**
 * @author dcollins
 * @version $Id$
 */
public interface TaskService
{
    void runTask(Runnable task);

    boolean contains(Runnable task);

    boolean isFull();
}
