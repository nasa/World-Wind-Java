/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc;

/**
 * Defines constants used in the OGC package and sub-packages.
 *
 * @author tag
 * @version $Id$
 */
public interface OGCConstants
{
    /**
     * The name of the OGC Web Service <code>GetCapabilities</code> operation. The <code>GetCapabilities</code>
     * operation returns metadata about the operations and data provided by an OGC Web Service.
     * <code>GetCapabilities</code> is valid value for the <code>request</code> parameter. Used by all versions of all
     * OGC web services.
     */
    final String GET_CAPABILITIES = "GetCapabilities";
    /**
     * The name of the OGC Web Service <code>request</code> parameter. The associated value must be the name of an
     * operation to execute (for example, <code>GetCapabilities</code>). Used by all versions of all OGC web services.
     */
    final String REQUEST = "request";
    /**
     * The name of the OGC Web Service <code>service</code> parameter. The associated value must be the abbreviated OGC
     * Web Service name (for example, <code>WMS</code>). Used by all versions of all OGC web services.
     */
    final String SERVICE = "service";
    /**
     * The name of the OGC Web Service <code>version</code> parameter. The associated value must be the version of the
     * OGC Web Service protocol to use. The version must be formatted as <code>x.y.z</code>, where <code>x, y</code> and
     * <code>z</code> are integers in the range 0-99. Used by all versions of all OGC web services.
     */
    final String VERSION = "version";

    public static final String WMS_SERVICE_NAME = "OGC:WMS";
    public static final String WMS_NAMESPACE_URI = "http://www.opengis.net/wms";
}