/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

/**
 * @author dcollins
 * @version $Id$
 */
public interface WFSConstants
{
    /**
     * The name of the OGC Web Feature Service (WFS) <code>GetFeature</code> operation. The <code>GetFeature</code>
     * operation returns a selection of features from the WFS data store. <code>GetFeature</code> is valid value for the
     * <code>request</code> parameter.
     * <p/>
     * Used by all versions of WFS.
     */
    final String GET_FEATURE = "GetFeature";
    /**
     * The name of the OGC Web Feature Service (WFS) <code>OperationNotSupported</code> exception code. Indicates that a
     * request is badly formed and failed to be parsed by the web service. Web service exceptions with this code are
     * typically followed by one or more exception codes that indicate how the request is badly formed.
     * <p/>
     * Used by all versions of WFS.
     */
    final String OPERATION_PARSING_FAILED = "OperationParsingFailed";
    /**
     * The name of the OGC Web Feature Service (WFS) <code>OperationProcessingFailed</code> exception code. Indicates
     * that the web service encountered an error while processing a request. This is typically used to indicate an
     * internal error in the web service.
     * <p/>
     * Used by all versions of WFS.
     */
    final String OPERATION_PROCESSING_FAILED = "OperationProcessingFailed";
    /**
     * The name of the OGC Web Feature Service <code>resourceID</code> parameter. The associated value must be a
     * persistent unique resource identifier assigned by the service to a feature. When both the <code>resourceID</code>
     * and <code>typeNames</code> parameters are present in a WFS request, the feature must be of a type specified in
     * <code>typeNames</code>, otherwise the server returns an exception.
     * <p/>
     * Used by WFS version <code>2.0.0</code>.
     */
    final String RESOURCE_ID = "resourceID";
    /**
     * The name of the OGC Web Feature Service <code>typeNames</code> parameter. The associated value must be the name
     * of one or more feature types published in a WFS server's Capabilities document. Multiple feature types are
     * delimited by a comma.
     * <p/>
     * Used by WFS version <code>2.0.0</code>.
     */
    final String TYPE_NAMES = "typeNames";
    /** The OGC Web Feature Service (WFS) namespace URI, version 2.0. */
    final String WFS_2dot0_NAMESPACE = "http://www.opengis.net/wfs/2.0";
    /** Version string for the OGC Web Feature Service (WFS), version 2.0 */
    final String WFS_2dot0_VERSION = "2.0.0";
    /**
     * The abbreviated name for an OGC Web Feature Service (WFS). <code>WFS_SERVICE_NAME</code> is valid value for the
     * <code>service</code> parameter. Used by all versions of WFS.
     */
    final String WFS_SERVICE_NAME = "WFS";
}
