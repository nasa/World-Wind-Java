/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.avlist.AVListImpl;

import java.net.InetAddress;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

class VisitorRecord
{
    protected String ip = null;
    protected long numOfVisits = 0L;
    protected String hostName = null;
    protected long maxHits = 0L;
    protected long maxPeriodInMillis = 0L;
    protected long firstVisitTime = System.currentTimeMillis();
    protected long lastVisitTime = System.currentTimeMillis();
    protected SecurityAccess sa = null;

    public VisitorRecord(String ip, SecurityAccess sa) throws NullPointerException
    {
        if (null == ip || ip.length() == 0)
        {
            String msg = Logging.getMessage("nullValue.InternetAddress");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (null == sa)
        {
            String msg = Logging.getMessage("nullValue.SecurityAccessIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (null == sa.getPolicy())
        {
            String msg = Logging.getMessage("nullValue.SecurityPolicyIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.ip = ip;
        this.numOfVisits = 0;
        this.lastVisitTime = this.firstVisitTime = System.currentTimeMillis();

        this.sa = sa;

        Policy policy = sa.getPolicy();

        this.maxHits = AVListImpl.getIntegerValue(policy, "max", 0);

        this.maxPeriodInMillis = 1000 * AVListImpl.getIntegerValue(policy, "period", 0);

        this.hostName = getHostNameByAddress(ip);
    }

    public long getFirstVisitTime()
    {
        return this.firstVisitTime;
    }

    public long getLastVisitTime()
    {
        return this.lastVisitTime;
    }

    public Policy getPolicy()
    {
        return (null != this.sa) ? this.sa.getPolicy() : null;
    }

    public long getNumberOfVisits()
    {
        return this.numOfVisits;
    }

    synchronized public void logVisit()
    {
        this.numOfVisits++;
        this.lastVisitTime = System.currentTimeMillis();
    }

    synchronized public void reset()
    {
        this.numOfVisits = 0;
        this.lastVisitTime = this.firstVisitTime = System.currentTimeMillis();
    }

    public long getMaxHits()
    {
        return this.maxHits;
    }

    public long getMaxPeriodInMillis()
    {
        return this.maxPeriodInMillis;
    }

    public long getTimeSpanInMillis()
    {
        return System.currentTimeMillis() - this.firstVisitTime;
    }

    public String getHostName()
    {
        return this.hostName;
    }

    private static String getHostNameByAddress(String ip)
    {
        String name = ip;
        long start = System.currentTimeMillis();
        try
        {
            InetAddress ipAddr = InetAddress.getByName(ip);
            name = ipAddr.getCanonicalHostName();
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.getMessage());
        }

        name = (null == name || 0 == name.length()) ? ip : name;
        Logging.logger().finest(
            ip + " resolved to the hostname " + name + " in " + (System.currentTimeMillis() - start) + " msec");

        return name;
    }
}
