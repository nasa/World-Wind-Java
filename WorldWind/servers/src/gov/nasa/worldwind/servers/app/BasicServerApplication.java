/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Logging;

import java.net.Socket;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class BasicServerApplication extends AVListImpl implements ServerApplication
{
    public enum ApplicationState
    {
        Initializing,
        Initialized,
        Starting,
        Started,
        Stopping,
        Stopped
    }

    private volatile ApplicationState state = ApplicationState.Initializing;

    public BasicServerApplication()
    {
        super();
        this.state = ApplicationState.Initialized;
    }

    public BasicServerApplication(AVList params)
    {
        super();
        this.setValues(params);
        this.setApplicationState(ApplicationState.Initialized);
    }

    public ApplicationState getApplicationState()
    {
        return this.state;
    }

    public void setApplicationState(ApplicationState newState)
    {
        synchronized (this)
        {
            this.state = newState;
        }
    }

    @Override
    public int getPort()
    {
        try
        {
            if (this.hasKey(AVKey.SERVER_PORT))
            {
                String s = "" + this.getValue(AVKey.SERVER_PORT);
                return Integer.parseInt(s);
            }
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.getMessage());
        }
        return 0;
    }

    @Override
    public String getName()
    {
        try
        {
            if (this.hasKey(AVKey.NAME))
            {
                return this.getStringValue(AVKey.NAME);
            }
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.getMessage());
        }
        return null;
    }

    @Override
    public String getProtocol()
    {
        try
        {
            if (this.hasKey(AVKey.PROTOCOL))
            {
                String s = "" + this.getValue(AVKey.PROTOCOL);

                if (s.equalsIgnoreCase("HTTP") || s.equalsIgnoreCase(AVKey.PROTOCOL_HTTP))
                {
                    return AVKey.PROTOCOL_HTTP;
                }

                else if (s.equalsIgnoreCase("HTTPS") || s.equalsIgnoreCase(AVKey.PROTOCOL_HTTPS))
                {
                    return AVKey.PROTOCOL_HTTPS;
                }
            }
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.getMessage());
        }
        return null;
    }

    @Override
    public String getVirtualDirectory()
    {
        try
        {
            if (this.hasKey(AVKey.SERVER_VIRTUAL_DIRECTORY))
            {
                return this.getStringValue(AVKey.SERVER_VIRTUAL_DIRECTORY);
            }
        }
        catch (Exception e)
        {
            Logging.logger().log(java.util.logging.Level.FINEST, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void service(Socket socket)
    {
        this.doService(socket);
    }

    protected void doService(Socket socket)
    {
    }

    @Override
    public final void start()
    {
        this.setApplicationState(ApplicationState.Starting);
        this.doStart();
    }

    protected void doStart()
    {
        this.setApplicationState(ApplicationState.Started);
    }

    @Override
    public final void stop()
    {
        this.setApplicationState(ApplicationState.Stopping);
        this.doStop();
    }

    protected void doStop()
    {
        this.setApplicationState(ApplicationState.Stopped);
    }
}
