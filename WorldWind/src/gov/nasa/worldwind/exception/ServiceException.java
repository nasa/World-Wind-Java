/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.exception;

/**
 * Thrown to indicate a service has failed.
 *
 * @author tag
 * @version $Id$
 */
public class ServiceException extends WWRuntimeException
{
    public ServiceException(String message)
    {
        super(message);
    }
}
