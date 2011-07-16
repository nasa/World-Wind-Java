/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.database;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.util.*;

import java.sql.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * A class for pre-allocating, recycling, and managing database connections
 *
 * @author Lado Garakanidze
 * @version $Id$
 */

public class BasicDatabaseConnectionPool implements DatabaseConnectionPool
{
    public static final int DEFAULT_POLICY_INITIAL_CONNECTIONS = 1;
    public static final int DEFAULT_POLICY_MAXIMUM_CONNECTIONS = 5;
    public static final boolean DEFAULT_POLICY_WAIT_IF_BUSY = true;
    public static final long DEFAULT_POLICY_CONNECTION_WAIT_TIMEOUT = 5L;

    protected String driverClassName = null;
    protected String connectionString = null;
    protected String username = null;
    protected String password = null;
    protected int initialConnections = DEFAULT_POLICY_INITIAL_CONNECTIONS;
    protected int maximumConnections = DEFAULT_POLICY_MAXIMUM_CONNECTIONS;
    protected boolean waitIfBusy = DEFAULT_POLICY_WAIT_IF_BUSY;

    protected CopyOnWriteArrayList<Connection> availableConnections = new CopyOnWriteArrayList<Connection>();
    protected CopyOnWriteArrayList<Connection> loanedConnections = new CopyOnWriteArrayList<Connection>();

    /**
     * This is a default constructor if the connection pool instance was created by instantiating using its class name.
     * <p/>
     * It is required to invoke any of the <code>initialize()</code> methods to actually initialize the connection pool
     * instance.
     * <p/>
     * Minimal expected parameters are: <code>AVKey.DATABASE_DRIVER_CLASS_NAME</code>
     * <code>AVKey.DATABASE_CONNECTION_STRING
     */
    public BasicDatabaseConnectionPool()
    {

    }

    /**
     * Constructs a ConnectionPool based on the calling arguments
     *
     * @param driverClassName    The database driver class name
     * @param connectionString   The database connection string or URL
     * @param username           The optional username, could be <code>null</code>
     * @param password           The optional password, could be <code>null</code>
     * @param initialConnections The minimum number of connections possible
     * @param maximumConnections The maximum number of connections possible
     * @param waitIfBusy         Boolean flag that determines whether to wait if there are no more available
     *                           connections. If set to true, it will wait until a connection becomes available. If set
     *                           to false, it will throw SQLException when a connection is requested and there are no
     *                           more available connections.
     *
     * @throws IllegalArgumentException if any of the calling arguments are illegal
     * @throws SQLException             if a connection could not be created
     */
    public BasicDatabaseConnectionPool(String driverClassName, String connectionString,
        String username, String password,
        int initialConnections, int maximumConnections, boolean waitIfBusy)
        throws IllegalArgumentException, SQLException
    {
        this.initialize(driverClassName, connectionString, username, password, initialConnections, maximumConnections,
            waitIfBusy);
    }

    /**
     * This is an alternative way to initialize a connection pool, if the connection pool instance was created with a
     * default constructor or instantiated using class name.
     * <p/>
     * Minimal expected parameters are: <code>AVKey.DATABASE_DRIVER_CLASS_NAME</code>
     * <code>AVKey.DATABASE_CONNECTION_STRING</code>
     * <p/>
     * Optional parameters are: <code>DATABASE_INITIAL_CONNECTIONS</code> <code>DATABASE_MAXIMUM_CONNECTIONS</code>
     * <code>DATABASE_USERNAME</code> <code>DATABASE_PASSWORD</code>
     *
     * @param list <code>AVList</code> with connection pool parameters
     */
    public void initialize(AVList list) throws IllegalArgumentException, java.sql.SQLException
    {
        if (null == list)
        {
            String message = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String driverClassName = AVListImpl.getStringValue(list, AVKey.DATABASE_DRIVER_CLASS_NAME, null);
        if (WWUtil.isEmpty(driverClassName))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.DATABASE_DRIVER_CLASS_NAME);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String connectionString = AVListImpl.getStringValue(list, AVKey.DATABASE_CONNECTION_STRING, null);
        if (WWUtil.isEmpty(connectionString))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.DATABASE_CONNECTION_STRING);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int initialConnections = AVListImpl.getIntegerValue(list,
            AVKey.DATABASE_POLICY_INITIAL_CONNECTIONS, DEFAULT_POLICY_INITIAL_CONNECTIONS);

        int maximumConnections = AVListImpl.getIntegerValue(list,
            AVKey.DATABASE_POLICY_MAXIMUM_CONNECTIONS, DEFAULT_POLICY_MAXIMUM_CONNECTIONS);

        boolean waitIfBusy = AVListImpl.getBooleanValue(list,
            AVKey.DATABASE_POLICY_WAIT_IF_BUSY, DEFAULT_POLICY_WAIT_IF_BUSY);

        String username = AVListImpl.getStringValue(list, AVKey.DATABASE_USERNAME, null);
        String password = AVListImpl.getStringValue(list, AVKey.DATABASE_PASSWORD, null);

        this.initialize(driverClassName, connectionString, username, password, initialConnections, maximumConnections,
            waitIfBusy);
    }

    protected void initialize(String driverClassName, String connectionString, String username, String password,
        int initialConnections, int maximumConnections, boolean waitIfBusy)
        throws IllegalArgumentException, SQLException
    {
        if (WWUtil.isEmpty(driverClassName))
        {
            String reason = Logging.getMessage("nullValue.ClassNameIsNull");
            Logging.logger().severe(reason);
            throw new IllegalArgumentException(reason);
        }

        if (WWUtil.isEmpty(connectionString))
        {
            String reason = Logging.getMessage("nullValue.ConnectionStringIsNull");
            Logging.logger().severe(reason);
            throw new IllegalArgumentException(reason);
        }

        if (initialConnections < 0)
        {
            String reason = Logging.getMessage("generic.UnexpectedLesserValue", initialConnections, 0);
            Logging.logger().severe(reason);
            throw new IllegalArgumentException(reason);
        }

        if (maximumConnections < initialConnections)
        {
            String reason = Logging.getMessage("generic.UnexpectedLesserValue", maximumConnections, initialConnections);
            Logging.logger().severe(reason);
            throw new IllegalArgumentException(reason);
        }

        this.driverClassName = driverClassName;
        this.connectionString = connectionString;
        this.initialConnections = initialConnections;
        this.maximumConnections = maximumConnections;
        this.waitIfBusy = waitIfBusy;

        this.username = username;
        this.password = password;

        this.loadDriver();

        while (this.availableConnections.size() < this.initialConnections)
        {
            this.createConnection();
        }
    }

    protected void loadDriver() throws SQLException
    {
        WorldWind.createComponent(this.driverClassName);
    }

    protected void createConnection() throws SQLException
    {
        synchronized (this)
        {
            if (!this.maximumConnectionsLimitReached())
            {
                try
                {
                    Connection connection =
                        WWUtil.isEmpty(this.username) ? DriverManager.getConnection(this.connectionString)
                            : DriverManager.getConnection(this.connectionString, this.username, this.password);

                    if (null != connection)
                    {
                        this.availableConnections.add(connection);
                    }
                }
                catch (Throwable t)
                {
                    Logging.logger().finest(WWUtil.extractExceptionReason(t));
                }
            }
        }
    }

    public Connection getConnection() throws SQLException
    {
        try
        {
            for (; ;)
            {
                Connection connection = (this.availableConnections.isEmpty())
                    ? null : this.grabFirstConnection(this.availableConnections);

                if (null != connection)
                {
                    this.loanedConnections.add(connection);
                    return connection;
                }

                if (!this.waitIfBusy)
                {
                    break;
                }

                this.createConnection();

                if (this.availableConnections.isEmpty())
                {
                    Thread.sleep(DEFAULT_POLICY_CONNECTION_WAIT_TIMEOUT);
                }
            }
        }
        catch (InterruptedException ie)
        {
            Thread.currentThread().interrupt();
        }

        String reason = Logging.getMessage("generic.ConnectionIsNotAvailable");
        Logging.logger().severe(reason);
        throw new SQLException(reason);
    }

    protected Connection grabFirstConnection(CopyOnWriteArrayList<? extends Connection> list)
    {
        try
        {
            return list.remove(0);
        }
        catch (Throwable t)
        {
            Logging.logger().finest(WWUtil.extractExceptionReason(t));
        }

        return null;
    }

    public void releaseConnection(Connection connection)
    {
        if (WWUtil.isEmpty(connection))
        {
            String reason = Logging.getMessage("nullValue.ConnectionIsNull");
            Logging.logger().severe(reason);
            return;
        }

        try
        {
            this.loanedConnections.remove(connection);
        }
        catch (Throwable t)
        {
            Logging.logger().finest(WWUtil.extractExceptionReason(t));
        }

        try
        {
            if (!connection.isClosed())
            {
                if (this.maximumConnectionsLimitReached())
                {
                    connection.close();
                }
                else
                {
                    this.availableConnections.add(connection);
                }
            }
        }
        catch (Throwable t)
        {
            Logging.logger().finest(WWUtil.extractExceptionReason(t));
        }
    }

    public void closeAllConnections()
    {
        this.closeAllConnections(this.availableConnections);
        this.closeAllConnections(this.loanedConnections);
    }

    protected void closeAllConnections(CopyOnWriteArrayList<? extends Connection> list)
    {
        while (null != list && !list.isEmpty())
        {
            try
            {
                Connection connection = this.grabFirstConnection(list);
                if (null != connection && !connection.isClosed())
                {
                    connection.close();
                }
            }
            catch (Throwable t)
            {
                Logging.logger().finest(WWUtil.extractExceptionReason(t));
            }
        }
    }

    protected boolean maximumConnectionsLimitReached()
    {
        return (this.maximumConnections != 0
            && ((this.availableConnections.size() + this.loanedConnections.size()) >= this.maximumConnections));
    }

    public static void main(String[] args)
    {
        DatabaseConnectionPool connectionPool = null;

        try
        {
            // JavaDB (aka "Derby")
            connectionPool = new BasicDatabaseConnectionPool(
                "org.apache.derby.jdbc.EmbeddedDriver",
                "jdbc:derby:/depot/WorldWindJ/myDatabase;create=true", null, null, 1, 2, true);

            // H2 database
            // @See http://www.h2database.com/html/features.html for more connection strings, etc

//            connectionPool = new BasicDatabaseConnectionPool(
//                "org.h2.Driver",
//                "jdbc:h2:mem:~/test;CIPHER=AES;create=true", "sa", "h2password", 1, 2, true);
        }
        catch (Exception e)
        {
            Logging.logger().log(Level.FINEST, WWUtil.extractExceptionReason(e), e);
        }
        finally
        {
            if (connectionPool != null)
            {
                connectionPool.closeAllConnections();
            }
        }
    }
}
