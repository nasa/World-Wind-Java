/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.net.Socket;

/**
 * @author Lado Garakanidze
 * @version $
 */

class ServerWorkerThread implements Runnable
{
    // if the client connection is in the queue for 10 seconds, ignore it,
    // because WWJ client will disconnect after 10 seconds
    protected static final long MAX_WAIT_TIME = 10000;  // 10sec (10,000 milli-seconds)
    protected Socket socket;
    protected ServerApplication app;
    protected long submitTime;

    // TODO - Create a watchdog task

    public ServerWorkerThread(Socket socket, ServerApplication app)
    {
        if (null == app)
        {
            String message = Logging.getMessage("nullValue.ServerApplicationIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (null == socket)
        {
            String message = Logging.getMessage("nullValue.SocketIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.socket = socket;
        this.app = app;

        this.submitTime = System.currentTimeMillis();
    }

    /**
     * Calculates a time difference in milliseonds, checks that endTime is greater than startTime
     *
     * @param endTime   End time in milli-seconds
     * @param startTime Start time in milli-seconds
     *
     * @return time diff in milliseconds
     */
    protected long calcTimeDiff(long startTime, long endTime)
    {
        return ((endTime >= startTime) ? (endTime - startTime) : (startTime - startTime));
    }

    /**
     * Calculates a time difference between NOW and startTime
     *
     * @param startTime Start time in milli-seconds
     *
     * @return time diff in milliseconds
     */
    protected long calcTimeDiff(long startTime)
    {
        return calcTimeDiff(startTime, System.currentTimeMillis());
    }

    /**
     * Attempts to read a value of the SERVER_APPLICATION_POOL_CONNECTION_TIMEOUT of the Server Application If the
     * SERVER_APPLICATION_POOL_CONNECTION_TIMEOUT property is not defined, returns a default value
     *
     * @return milli-seconds
     */
    protected long getMaxWaitingTime()
    {
        long maxWaitTime = MAX_WAIT_TIME;

        if (this.app.hasKey(AVKey.SERVER_APPLICATION_POOL_CONNECTION_TIMEOUT))
        {
            try
            {
                String s = this.app.getStringValue(AVKey.SERVER_APPLICATION_POOL_CONNECTION_TIMEOUT);
                maxWaitTime = (int) (Long.parseLong(s));
            }
            catch (Throwable t)
            {
                Logging.logger().log(java.util.logging.Level.FINEST, t.getMessage(), t);
            }
        }

        return maxWaitTime;
    }

    @Override
    public void run()
    {
        long execStartTime = System.currentTimeMillis();

        long waitingTime = this.calcTimeDiff(this.submitTime, execStartTime);

        StringBuffer sb = new StringBuffer();
        sb.append("Thread [").append(Thread.currentThread().getId()).append("]: ");

        try
        {
            if (!this.socket.isConnected())
            {
                String message = Logging.getMessage("WMS.Server.ClientClosedConnection");
                Logging.logger().finest(message);
                throw new RuntimeException(message);
            }
            else if (waitingTime > this.getMaxWaitingTime())
            {
                String message = Logging.getMessage("WMS.Server.Busy");
                Logging.logger().finest(message);
                throw new RuntimeException(message);
            }
            else
            {
                sb.append("Server App=").append(app.getName()).append(", ");

                this.app.service(this.socket);
            }
        }
        catch (Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            String message = Logging.getMessage("WMS.Server.InternalError", reason);
            Logging.logger().severe(message);
        }
        finally
        {
            this.shutdownSocket();

            sb.append(" { waiting time: ").append(waitingTime).append(" msec;");
            sb.append(" execution time=").append(this.calcTimeDiff(execStartTime)).append(" msec; ");
            sb.append(" total time=").append(this.calcTimeDiff(this.submitTime)).append(" msec; }");

            Logging.logger().fine(sb.toString());
        }
    }

    protected void shutdownSocket()
    {
        if (this.socket != null)
        {
            if (this.socket.isConnected())
            {
                if (!this.socket.isInputShutdown())
                {
                    try
                    {
                        this.socket.shutdownInput();
                    }
                    catch (Throwable t)
                    {
                        Logging.logger().finest(t.getMessage());
                    }
                }

                if (!this.socket.isOutputShutdown())
                {
                    try
                    {
                        this.socket.shutdownOutput();
                    }
                    catch (Throwable t)
                    {
                        Logging.logger().finest(t.getMessage());
                    }
                }
            }

            try
            {
                this.socket.close();
            }
            catch (Throwable t)
            {
                Logging.logger().finest(t.getMessage());
            }
        }
    }
}