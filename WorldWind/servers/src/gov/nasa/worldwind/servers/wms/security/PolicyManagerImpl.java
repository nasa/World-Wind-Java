/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;

import java.util.Hashtable;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class PolicyManagerImpl implements PolicyManager
{
    private final Hashtable<String, Policy> policies = new Hashtable<String, Policy>();

    public PolicyManagerImpl()
    {
    }

    @Override
    public void add(Policy policy) throws SecurityException
    {
        if( null != policy )
        {
            String key = policy.getName();
            if( this.policies.containsKey( key ))
            {
                String msg = Logging.getMessage("WMS.Security.PolicyAlreadExists", key );
                Logging.logger().severe(msg);
                throw new SecurityException( msg );
            }

            this.policies.put( key, policy );
        }
    }

    @Override
    public Policy get(String policyName)
    {
        if( null != policyName && 0 < policyName.trim().length() && this.policies.containsKey( policyName ))
            return this.policies.get( policyName );
        return null;
    }

    @Override
    public boolean hasPolicies()
    {
        return ( 0 < this.policies.size());
    }
}
