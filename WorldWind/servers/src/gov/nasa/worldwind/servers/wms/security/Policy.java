/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class Policy extends AVListImpl
{
    public static final String NAME      = "gov.nasa.worldwind.avkey.PolicyName";
    public static final String MAX_HITS  = "gov.nasa.worldwind.avkey.PolicyMaxHits";
    public static final String TIME_SPAN = "gov.nasa.worldwind.avkey.PolicyTimeSpan";

    public Policy( String name )
    {
        super();

        if( null == name || 0 == name.trim().length() )
        {
            String message = Logging.getMessage("nullValue.NameIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.setValue( NAME, name );
    }

    public String getName()
    {
        return this.getStringValue( NAME );
    }
}
