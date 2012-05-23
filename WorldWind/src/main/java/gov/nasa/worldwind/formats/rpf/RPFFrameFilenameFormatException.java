/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.rpf;

/**
 * @author dcollins
 * @version $Id$
 */
public class RPFFrameFilenameFormatException extends IllegalArgumentException
{
    public RPFFrameFilenameFormatException()
    {
    }

    public RPFFrameFilenameFormatException(String message)
    {
        super(message);
    }

    public RPFFrameFilenameFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RPFFrameFilenameFormatException(Throwable cause)
    {
        super(cause);
    }
}
