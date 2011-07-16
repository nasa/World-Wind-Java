/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public abstract class AbstractSecurityAccess implements SecurityAccess
{
    protected Policy policy = null;

    protected AbstractSecurityAccess(Policy policy)
    {
        if( null == policy )
        {
            String msg = Logging.getMessage( "nullValue.SecurityPolicyIsNull" );
            Logging.logger().severe(msg);
            throw new IllegalArgumentException( msg );
        }
        
        this.policy = policy;
    }

    @Override
    public Policy getPolicy()
    {
        return this.policy;
    }
}
