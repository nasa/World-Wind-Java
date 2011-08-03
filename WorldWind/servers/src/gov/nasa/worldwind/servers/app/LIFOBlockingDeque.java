/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.servers.app;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class LIFOBlockingDeque<T> extends LinkedBlockingDeque<T>
{
    @Override
    public boolean offer(T t)
    {
        return super.offerLast(t);
    }

    @Override
    public T remove()
    {
        return super.removeLast();
    }
}