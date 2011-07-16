/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessByUsername extends AbstractSecurityAccess
{
    private String username = null;

    public SecurityAccessByUsername(Policy policy, String username )
    {
        super(policy);

        this.username = username;
    }

    @Override
    public boolean matches(Object o)
    {
        if( 0 == "any".compareToIgnoreCase( this.username ) )
            return true;

        if( null == this.username || 0 == this.username.trim().length() )
            return false;

        if( o instanceof String )
            return ((String)o).matches( this.username );

        else if( o instanceof HTTPRequest)
        {
            HTTPRequest req = (HTTPRequest)o;


            String username = req.getParameter( "username" );
            // TODO check if hostname (not IP) or getDomainName
            return ( null != username ) ? username.matches( this.username ) : false;
        }

        else
        {
            String msg = Logging.getMessage("WMS.Security.UnknownOrUnsupportedIdentity", o.getClass().getName() );
            Logging.logger().severe(msg);
            throw new SecurityException( msg );
        }
    }
}
