/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.util.Logging;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SecurityAccessFactory
{
    private static final String XPATH_SECURITY_ACCESS_POLICY  = "@policy";
    private static final String XPATH_SECURITY_ACCESS_DOMAIN  = "@domain";
    private static final String XPATH_SECURITY_ACCESS_RANGE   = "@range";
    private static final String XPATH_SECURITY_ACCESS_USER    = "@user";
    private static final String XPATH_SECURITY_ACCESS_IP      = "@ip";

    public static SecurityAccess create(XPath xpath, Node n) throws SecurityException
    {
        String name = null;

        try
        {
            name = xpath.evaluate( XPATH_SECURITY_ACCESS_POLICY, n);
        }
        catch (XPathExpressionException ex)
        {
            Logging.logger().severe( ex.toString() );
        }

        if (null == name || 0 == name.length())
        {
            String msg = Logging.getMessage("WMS.Security.AccessRequiresPolicy" );
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }

        Policy policy = WMSSecurity.getPolicyManager().get( name );
        if (null == policy )
        {
            String msg = Logging.getMessage("WMS.Security.AccessRefersToNonExistingPolicy", name );
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }


        String s = null;

        try
        {
            s = xpath.evaluate( XPATH_SECURITY_ACCESS_DOMAIN, n);
        }
        catch (XPathExpressionException ex)
        {
            Logging.logger().severe( ex.toString() );
        }

        if( null != s && 0 < s.length() )
            return new SecurityAccessByDomain( policy, s );

        try
        {
            s = xpath.evaluate(XPATH_SECURITY_ACCESS_IP, n);
        }
        catch (XPathExpressionException ex)
        {
            Logging.logger().severe(ex.toString());
        }

        if (null != s && 0 < s.length())
        {
            return new SecurityAccessByIpAddress( policy, s );
        }


        try
        {
            s = xpath.evaluate(XPATH_SECURITY_ACCESS_USER, n);
        }
        catch (XPathExpressionException ex)
        {
            Logging.logger().severe(ex.toString());
        }

        if (null != s && 0 < s.length())
        {
            return new SecurityAccessByUsername( policy, s );
        }

        try
        {
            s = xpath.evaluate(XPATH_SECURITY_ACCESS_RANGE, n);
        }
        catch (XPathExpressionException ex)
        {
            Logging.logger().severe(ex.toString());
        }

        if (null != s && 0 < s.length())
        {
            String[] addresses = s.split("\\-");
            if( null != addresses && 2 == addresses.length )
                return new SecurityAccessByIpRange( policy, addresses[0], addresses[1] );
            else
            {
                String msg = Logging.getMessage("WMS.Security.InvalidIpAddressesRange", s );
                Logging.logger().severe(msg);
            }
        }

        return null;
    }
}
