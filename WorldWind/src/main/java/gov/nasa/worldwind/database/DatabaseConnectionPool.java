/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.database;

import gov.nasa.worldwind.avlist.AVList;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public interface DatabaseConnectionPool
{
    /**
     * This is an alternative way to initialize a connection pool, if the connection pool instance was created with a
     * default constructor or instantiated using class name Minimal expected parameters are:
     * <code>AVKey.DATABASE_DRIVER_CLASS_NAME</code> <code>AVKey.DATABASE_CONNECTION_STRING</code>
     * <p/>
     * Optional parameters are: <code>DATABASE_INITIAL_CONNECTIONS</code> <code>DATABASE_MAXIMUM_CONNECTIONS</code>
     * <code>DATABASE_USERNAME</code> <code>DATABASE_PASSWORD</code>
     *
     * @param list <code>AVList</code> with connection pool parameters
     *
     * @throws IllegalArgumentException if any of the required parameter is missing
     * @throws java.sql.SQLException    if there is any database connectivity problem
     */
    public void initialize(AVList list) throws IllegalArgumentException, java.sql.SQLException;

    /**
     * Gets the next available connection
     *
     * @return java.sql.Connection the next available connection
     *
     * @throws java.sql.SQLException if no connections available
     */
    public java.sql.Connection getConnection() throws java.sql.SQLException;

    /**
     * Releases the specified connection and returns it to the active pool
     *
     * @param connection The SQL connection
     */
    public void releaseConnection(java.sql.Connection connection);

    /** Closes all the connections */
    public void closeAllConnections();
}
