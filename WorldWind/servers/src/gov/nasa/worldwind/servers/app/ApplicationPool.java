/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.app;

import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author Lado Garakanidze
 * @version $
 */

public class ApplicationPool
{
    protected static final int DEFAULT_THREAD_POOL_SIZE = 8;

    private ExecutorService threadPool = null;

    public ApplicationPool(int size)
    {
        this.setThreadPoolSize(size);
    }

    public void setThreadPoolSize(int size)
    {
        try
        {
            if( this.threadPool != null )
                this.threadPool.shutdown();
        }
        finally
        {
            this.threadPool = new ThreadPoolExecutor( size, size, 0, TimeUnit.SECONDS, new LIFOBlockingDeque<Runnable>());
        }
    }

    public ApplicationPool()
    {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    public void execute(Socket socket, ServerApplication app)
    {
        if (socket != null && socket.isConnected())
            this.threadPool.execute(new ServerWorkerThread(socket, app));
    }
}
