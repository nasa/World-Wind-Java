/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.database;

import org.w3c.dom.Element;
import gov.nasa.worldwind.avlist.AVList;

import java.sql.SQLException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

/**
 * A Factory to create <code>DatabaseConnectionPool</code> instances
 * from an XML element or <code>AVList</code>
 *
 */
public interface DatabaseConnectionPoolFactory
{
    /**
     * Create <code>DatabaseConnectionPool</code> from an <code>AVList</code>
     * @param list <code>AVList</code> instance, must not be null
     * @return <code>DatabaseConnectionPool</code> instance
     * @throws SQLException if a <code>AVList</code> does not have required parameters
     * @throws IllegalArgumentException if any of the required parameters are missing or invalid
     */
    public DatabaseConnectionPool create(AVList list) throws IllegalArgumentException, SQLException;

    /**
     * Create <code>DatabaseConnectionPool</code> from an <code>Element</code>
     * @param element <code>Element</code> instance, must not be null
     * @return <code>DatabaseConnectionPool</code> instance
     * @throws SQLException if a <code>AVList</code> does not have required parameters
     * @throws IllegalArgumentException if any of the required parameters are missing or invalid
     */
    public DatabaseConnectionPool create(Element element) throws IllegalArgumentException, SQLException;
}
