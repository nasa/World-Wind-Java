/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.wms.utilities.StringUtil;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessByIpAddress extends AbstractSecurityAccess
{
    private String ip = null;
    private long ipValue = 0L;

    public SecurityAccessByIpAddress(Policy policy, String ip ) throws SecurityException
    {
        super(policy);

        if( 0 == "any".compareToIgnoreCase( ip ) )
        {
            this.ip = ip;
            this.ipValue = 0L;
        }
        else if( !StringUtil.isValidIpAddress( ip ) )
        {
            String msg = Logging.getMessage("WMS.Security.InvalidIpAddress", ip );
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }
        else
        {
            this.ip = ip;
            this.ipValue = StringUtil.valueOfIpAddress( ip );
        }
    }

    @Override
    public boolean matches(Object o)
    {
        if( 0 == "any".compareToIgnoreCase( this.ip ) )
            return true;

        String ip = null;

        if( o instanceof String )
            ip = ((String)o);

        else if( o instanceof HTTPRequest)
        {
            HTTPRequest req = (HTTPRequest)o;
            ip = req.getHeader( HTTP.Header.X_FORWARDED_FOR );
        }
        else
        {
            String msg = Logging.getMessage("WMS.Security.UnknownOrUnsupportedIdentity", o.getClass().getName() );
            Logging.logger().severe(msg);
            throw new SecurityException( msg );
        }

        if( !StringUtil.isValidIpAddress(ip) )
            return false;

        return (this.ipValue == StringUtil.valueOfIpAddress( ip ));
    }
}
