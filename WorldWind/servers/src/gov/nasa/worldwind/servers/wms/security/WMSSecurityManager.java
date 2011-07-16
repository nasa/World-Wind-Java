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

import java.util.Hashtable;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class WMSSecurityManager implements SecurityManager
{
    private static final long ONE_DAY = 86400000L; // 24hrs * 60mins * 60secs * 1000 millis
    private long visitorsLogCreatedOn = System.currentTimeMillis();
    private Hashtable<String, VisitorRecord> visitorsLog = new Hashtable<String, VisitorRecord>();

    public WMSSecurityManager()
    {
    }

    public synchronized void clearVisitorsLog()
    {
        if (0 != this.visitorsLog.size())
        {
            this.visitorsLog.clear();
            this.visitorsLogCreatedOn = System.currentTimeMillis();
            Logging.logger().info("Visitors log cleared.");
        }
    }

    @Override
    public boolean allow(Object o) throws SecurityException
    {
        if (System.currentTimeMillis() - this.visitorsLogCreatedOn > ONE_DAY)
            this.clearVisitorsLog();

        String ip = null;

        if (o instanceof String)
            ip = ((String) o);

        else if (o instanceof HTTPRequest)
        {
            HTTPRequest req = (HTTPRequest) o;
            ip = req.getHeader(HTTP.Header.X_FORWARDED_FOR);
        }
        else
        {
            String msg = Logging.getMessage("WMS.Security.UnknownOrUnsupportedIdentity", o.getClass().getName());
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }

        if (!StringUtil.isValidIpAddress(ip))
        {
            String msg = Logging.getMessage("WMS.Security.InvalidIpAddress", ip);
            Logging.logger().severe(msg);
            throw new SecurityException(msg);
        }

        VisitorRecord rec = null;

        if (!this.visitorsLog.containsKey(ip))
        {
            // This is a first visit
            SecurityAccess sa = WMSSecurity.getSecurityAccessManager().match(ip);

            rec = new VisitorRecord(ip, sa);
            this.visitorsLog.put(ip, rec);
        }
        else
        {
            rec = this.visitorsLog.get(ip);
        }

        if (rec.getTimeSpanInMillis() > rec.getTimeSpanInMillis())
            rec.reset();

        rec.logVisit();

        if (rec.getNumberOfVisits() <= rec.getMaxHits())
        {
            String msg = Logging.getMessage("WMS.Security.AccessAllowed", ip, rec.getPolicy().getName());
            Logging.logger().info(msg);
            return true;
        }

        String msg = Logging.getMessage("WMS.Security.AccessDenied", ip, rec.getPolicy().getName());
        Logging.logger().severe(msg);
        throw new AccessDeniedException(ip, msg);
    }
}
