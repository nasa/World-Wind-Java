/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.formats.nitfs;

import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id: NitfsRuntimeException Mar 31, 2007 7:41:31 AM
 */
public final class NITFSRuntimeException extends java.lang.RuntimeException
{
    public NITFSRuntimeException()
    {
        super();
    }

    public NITFSRuntimeException(String messageID)
    {
        super(Logging.getMessage(messageID));
        log(this.getMessage());
    }

    public NITFSRuntimeException(String messageID, String params)
    {
        super(Logging.getMessage(messageID) + params);
        log(this.getMessage());
    }

    public NITFSRuntimeException(Throwable throwable)
    {
        super(throwable);
        log(this.getMessage());
    }

    public NITFSRuntimeException(String messageID, Throwable throwable)
    {
        super(Logging.getMessage(messageID), throwable);
        log(this.getMessage());
    }

    public NITFSRuntimeException(String messageID, String params, Throwable throwable)
    {
        super(Logging.getMessage(messageID) + params, throwable);
        log(this.getMessage());
    }

    // TODO: Calling the logger from here causes the wrong method to be listed in the log record. Must call the
    // logger from the site with the problem and generating the exception.
    private void log(String s)
    {
        Logging.logger().fine(s);
    }
}