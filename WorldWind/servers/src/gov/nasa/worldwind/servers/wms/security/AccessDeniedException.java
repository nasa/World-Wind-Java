/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.security;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class AccessDeniedException extends SecurityException
{
    private String address;
    /**
     * Constructs a <code>AccessDeniedException</code> with the specified
     * detail message.
     *
     * @param   address        the IP address of the denied client
     *
     * @param   message   the detail message.
     */
    public AccessDeniedException(String address, String message)
    {
	    super(message);
        this.address = address;
    }

    public String getAddress()
    {
        return this.address;
    }
}
