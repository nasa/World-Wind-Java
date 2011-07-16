/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessManager
{
    private static final SecurityAccess alwaysDeny = new SecurityAccessAlwaysDeny( new Policy("AlwaysDeny") );
    private final List<SecurityAccess> list = new ArrayList<SecurityAccess>();

    public SecurityAccessManager()
    {
    }

    public void add(SecurityAccess sa)
    {
        if( null != sa && !this.list.contains(sa) )
            this.list.add(sa);
    }

    public SecurityAccess match( String ip )
    {
        for(SecurityAccess sa : this.list )
        {
            if( sa.matches( ip) )
                return sa;
        }
        return alwaysDeny;        
    }

    public boolean hasAccessRestrictions()
    {
        return ( 0 < this.list.size() );
    }
}
