/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.database;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class BasicDatabaseConnectionPoolFactory implements DatabaseConnectionPoolFactory
{
    /**
     * Create <code>DatabaseConnectionPool</code> from an <code>AVList</code>
     *
     * @param list <code>AVList</code> instance, must not be null
     *
     * @return <code>DatabaseConnectionPool</code> instance
     *
     * @throws SQLException if a <code>AVList</code> does not have required parameters
     */
    public DatabaseConnectionPool create(AVList list) throws IllegalArgumentException, SQLException
    {
        return this.doCreate(list);
    }

    /**
     * Create <code>DatabaseConnectionPool</code> from an <code>Element</code>
     *
     * @param element <code>Element</code> instance, must not be null
     *
     * @return <code>DatabaseConnectionPool</code> instance
     *
     * @throws SQLException if a <code>Element</code> does not have required parameters
     */
    public DatabaseConnectionPool create(Element element) throws IllegalArgumentException, SQLException
    {
        return this.doCreate(element);
    }

    protected DatabaseConnectionPool doCreate(AVList list) throws IllegalArgumentException, SQLException
    {
        if (null == list)
        {
            String message = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String connectionPoolClassName = AVListImpl.getStringValue(list, AVKey.DATABASE_CONNECTION_POOL_CLASS_NAME,
            "gov.nasa.worldwind.database.BasicDatabaseConnectionPool");
        if (WWUtil.isEmpty(connectionPoolClassName))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter",
                AVKey.DATABASE_CONNECTION_POOL_CLASS_NAME);

            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Object o = WorldWind.createComponent(connectionPoolClassName);
        if (null != o && o instanceof DatabaseConnectionPool)
        {
            DatabaseConnectionPool connectionPool = (DatabaseConnectionPool) o;
            connectionPool.initialize(list);
            return connectionPool;
        }

        String message = Logging.getMessage("generic.CannotCreateObject", connectionPoolClassName);
        Logging.logger().severe(message);
        throw new IllegalArgumentException(message);
    }

    protected DatabaseConnectionPool doCreate(Element element) throws IllegalArgumentException, SQLException
    {
        String message = Logging.getMessage("generic.FeatureNotImplemented",
            "BasicDatabaseConnectionPoolFactory::create(Element element);");
        Logging.logger().severe(message);
        throw new SQLException(message);
    }
}
