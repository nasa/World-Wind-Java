/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.servers.http.HTTP;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.wms.utilities.StringUtil;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessByIpRange extends AbstractSecurityAccess
{
    private String minIP, maxIP ;
    private long valueMinIP, valueMaxIP;

    public SecurityAccessByIpRange(Policy policy, String minIP, String maxIP ) throws SecurityException
    {
        super(policy);

        if( !StringUtil.isValidIpAddress( minIP ))
        {
            String msg = Logging.getMessage("WMS.Security.InvalidIpAddress", minIP );
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }

        if( !StringUtil.isValidIpAddress( maxIP ))
        {
            String msg = Logging.getMessage("WMS.Security.InvalidIpAddress", maxIP );
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }
        
        this.minIP = minIP;
        this.maxIP = maxIP;

        this.valueMinIP = StringUtil.valueOfIpAddress( this.minIP );
        this.valueMaxIP = StringUtil.valueOfIpAddress( this.maxIP );
    }

    @Override
    public boolean matches(Object o)
    {
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

        long value = StringUtil.valueOfIpAddress( ip );

        return ( value >= this.valueMinIP && value <= this.valueMaxIP );
    }
}
