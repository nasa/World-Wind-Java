/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

/**
 * @author tag
 * @version $Id$
 */

public class Version
{
    private static final String MAJOR_VALUE = "1";
    private static final String MINOR_VALUE = "2";
    private static final String DOT_VALUE = "0";
    private static final String versionNumber = MAJOR_VALUE + "." + MINOR_VALUE + "." + DOT_VALUE;
    private static final String versionName = "NASA World Wind Java 1.2";

    public static String getVersion()
    {
        return versionName + " " + versionNumber;
    }

    public static String getVersionName()
    {
        return versionName;
    }

    public static String getVersionNumber()
    {
        return versionNumber;
    }

    public static String getVersionMajorNumber()
    {
        return MAJOR_VALUE;
    }

    public static String getVersionMinorNumber()
    {
        return MINOR_VALUE;
    }

    public static String getVersionDotNumber()
    {
        return DOT_VALUE;
    }
}
