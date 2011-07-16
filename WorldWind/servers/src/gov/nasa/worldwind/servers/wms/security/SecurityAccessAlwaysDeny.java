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

public class SecurityAccessAlwaysDeny extends AbstractSecurityAccess
{
    public SecurityAccessAlwaysDeny(Policy policy)
    {
        super(policy);
    }

    @Override
    public boolean matches(Object a)
    {
        return false;
    }
}
