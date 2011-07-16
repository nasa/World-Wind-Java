/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.wss;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class WSS
{
    public class Param
    {
        public static final String REQUEST = "REQUEST";
        public static final String SERVICE = "SERVICE";
        public static final String VERSION = "VERSION";
        public static final String TYPE_NAMES = "typeNames";
        public static final String RESOURCE_ID = "resourceID";
        public static final String AUTH_TOKEN = "AuthToken";
    }

    public class Request
    {
        public static final String GetFeature = "GetFeature";
        public static final String GetCapabilities = "GetCapabilities";
    }

    public class Service
    {
        public static final String WFS = "WFS";
    }

    public class Version
    {
        public static final String V2_0_0 = "2.0.0";
    }

    public class TypeNames
    {
        public static final String ALL = "All";
    }
}
