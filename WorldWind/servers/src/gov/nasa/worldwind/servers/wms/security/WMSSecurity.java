/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class WMSSecurity
{
    private static SecurityManager securityManager = new AllTrustedSecurityManager();
    private static final PolicyManager policyManager = new PolicyManagerImpl();
    private static final SecurityAccessManager accessManager = new SecurityAccessManager();

    private WMSSecurity()
    {
    }
    
    public static void setSecurityManager( SecurityManager mgr )
    {
        if( null != mgr && securityManager != mgr )
            securityManager = mgr;
    }

    public static SecurityManager getSecurityManager()
    {
        return securityManager;
    }

    public static PolicyManager getPolicyManager()
    {
        return policyManager;
    }

    public static SecurityAccessManager getSecurityAccessManager()
    {
        return accessManager;
    }
}
