/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessByDomain extends AbstractSecurityAccess
{
    private String regex = null;

    public SecurityAccessByDomain(Policy policy, String regex )
    {
        super(policy);

        this.regex = regex;
    }

    @Override
    public boolean matches(Object o)
    {
        if( 0 == "any".compareToIgnoreCase( this.regex ) )
            return true;

        if( null == this.regex || 0 == this.regex.trim().length() )
            return false;

        if( o instanceof String )
            return ((String)o).matches( this.regex );

        else if( o instanceof HTTPRequest)
        {
            HTTPRequest req = (HTTPRequest)o;

            String clientID = req.getHeader( HTTP.Header.X_FORWARDED_FOR );
            // TODO check if hostname (not IP) or getDomainName
            return ( null != clientID ) ? clientID.matches( this.regex ) : false;
        }

        else
        {
            String msg = Logging.getMessage("WMS.Security.UnknownOrUnsupportedIdentity", o.getClass().getName() );
            Logging.logger().severe(msg);
            throw new SecurityException( msg );
        }
    }
}
