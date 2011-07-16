/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.exception;

/**
 * Thrown when a World Wind operation times out.
 *
 * @author tag
 * @version $Id$
 */
public class WWTimeoutException extends WWRuntimeException
{
    public WWTimeoutException(String message)
    {
        super(message);
    }
}
