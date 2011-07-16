/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.util.Logging;

import java.net.InetAddress;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class Test
{
    public static String getDomainNameByIP( String ip )
    {
        String domainName = ip;
        try
        {
            InetAddress ipAddr = InetAddress.getByName( ip );
            domainName = "CanonicalHostName:" + ipAddr.getCanonicalHostName();
            domainName += "; HostName:" + ipAddr.getHostName();
            domainName += "; HostAddress:" + ipAddr.getHostAddress();


        }
        catch(Exception e)
        {
            Logging.logger().finest( e.getMessage() );
        }
        return ( null == domainName || 0 == domainName.length() ) ? ip : domainName;
    }

    public static void main( String args[] )
    {
        System.out.println( "188.92.76.208 --> " + getDomainNameByIP( "188.92.76.208") );
        System.out.println( "71.164.23.176 --> " + getDomainNameByIP( "71.164.23.176") );

        for(String ip : args)
        {
            System.out.println( ip + " --> " + getDomainNameByIP( ip ) );    
        }
    }
}
