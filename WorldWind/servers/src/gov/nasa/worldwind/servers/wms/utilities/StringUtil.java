/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.utilities;

import gov.nasa.worldwind.util.Logging;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class StringUtil
{
    // removes prefix "|" (pipe) 
    public static String[] removePipePrefix(String[] values)
    {
        if (null != values && values.length > 0)
        {
            int size = values.length;
            String[] newValues = new String[size];
            for (int i = 0; i < size; i++)
            {
                String s = values[i];
                if (null != s && s.length() > 0 && s.startsWith("|"))
                {
                    newValues[i] = s.substring(1, s.length());
                }
                else
                {
                    newValues[i] = s;
                }

            }
            return newValues;
        }
        return values;
    }

    public static boolean isValidIpAddress(String ip)
    {
        try
        {
            InetAddress addr = InetAddress.getByName(ip);
            if (null != addr)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.getMessage());
        }
        return false;
//        if( null != ip && 0 < ip.length())
//        {
//            return ip.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b" );
//        }
//        else
//            return false;
    }


    public static long valueOfIpAddress(String ip)
    {
        long value = 0L;
        try
        {
            InetAddress ipAddress = InetAddress.getByName(ip);
            byte[] dot_address = ipAddress.getAddress();
            for (byte n : dot_address)
            {
                value = value * 256L + (long) (n & 0xFF);

            }
        }
        catch (UnknownHostException e)
        {
            Logging.logger().fine(e.getMessage());
        }

        return value;
    }
}
