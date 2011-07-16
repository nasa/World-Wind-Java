/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.exception;

/**
 * @author tag
 * @version $Id$
 */
public class WWAbsentRequirementException extends WWRuntimeException
{
    public WWAbsentRequirementException()
    {
    }

    public WWAbsentRequirementException(String s)
    {
        super(s);
    }

    public WWAbsentRequirementException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public WWAbsentRequirementException(Throwable throwable)
    {
        super(throwable);
    }
}
