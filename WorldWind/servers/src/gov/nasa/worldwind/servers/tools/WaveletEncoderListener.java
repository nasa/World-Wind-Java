/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools;

/**
 * @author brownrigg
 * @version $Id$
 */

// -----------------------------------------------------------------------
// A listener interface for client apps to get progress feedback.
//
public abstract class WaveletEncoderListener
{
    public abstract void status(String status);

    public final boolean isDone()
    {
        return done;
    }

    // package access 
    boolean done = false;
}

