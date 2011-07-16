/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.Restorable;
import gov.nasa.worldwind.avlist.*;

import java.net.Socket;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public interface ServerApplication extends AVList
{
    public int      getPort();
    public String   getName();
    public String   getProtocol();
    public String   getVirtualDirectory();

    public void     service(Socket socket);
    public void     start();
    public void     stop();
}
