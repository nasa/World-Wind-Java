/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import gov.nasa.worldwind.exception.WWRuntimeException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class NativeLibraryLoader
{
    public static void loadLibrary(String libName) throws WWRuntimeException, IllegalArgumentException
    {
        if (WWUtil.isEmpty(libName))
        {
            String message = Logging.getMessage("nullValue.LibraryIsNull");
            throw new IllegalArgumentException(message);
        }

        try
        {
            System.loadLibrary(libName);
        }
        catch (java.lang.UnsatisfiedLinkError ule)
        {
            String message = Logging.getMessage("generic.LibraryNotLoaded", libName, ule.getMessage());
            throw new WWRuntimeException(message);
        }
        catch (Throwable t)
        {
            String message = Logging.getMessage("generic.LibraryNotLoaded", libName, t.getMessage());
            throw new WWRuntimeException(message);
        }
    }

    protected static String makeFullLibName(String libName)
    {
        if (WWUtil.isEmpty(libName))
            return null;

        if (gov.nasa.worldwind.Configuration.isWindowsOS())
        {
            if (!libName.toLowerCase().endsWith(".dll"))
                return libName + ".dll";
        }
        else if (gov.nasa.worldwind.Configuration.isMacOS())
        {
            if (!libName.toLowerCase().endsWith(".jnilib") && !libName.toLowerCase().startsWith("lib"))
                return "lib" + libName + ".jnilib";
        }
        else if (gov.nasa.worldwind.Configuration.isUnixOS())  // covers Solaris and Linux
        {
            if (!libName.toLowerCase().endsWith(".so") && !libName.toLowerCase().startsWith("lib"))
                return "lib" + libName + ".so";
        }
        return libName;
    }
}
