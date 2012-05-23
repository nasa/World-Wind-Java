/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

/**
 * @author dcollins
 * @version $Id$
 */
public interface OWSConstants
{
    /**
     * The name of the OGC Web Service Common (OWS) <code>AcceptVersions</code> parameter. The associated value must be
     * a prioritized sequence of one or more versions accepted by the client, with preferred versions listed first.
     * Versions are comma delimited and formatted as <code>x.y.z</code>, where <code>x, y</code> and <code>z</code> are
     * integers in the range 0-99.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String ACCEPT_VERSIONS = "AcceptVersions";

    /**
     * The name of the OGC Web Service Common (OWS) <code>InvalidParameterValue</code> exception code. Indicates that a
     * request contains a parameter with an invalid value.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String INVALID_PARAMETER_VALUE = "InvalidParameterValue";
    /**
     * The name of the OGC Web Service Common (OWS) <code>MissingParameterValue</code> exception code. Indicates that a
     * request does not include a required parameter value.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String MISSING_PARAMETER_VALUE = "MissingParameterValue";
    /**
     * The name of the OGC Web Service Common (OWS) <code>NoApplicableCode</code> exception code. Indicates that no
     * other exception code used by the web service applies to the exception.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String NO_APPLICABLE_CODE = "NoApplicableCode";
    /**
     * The name of the OGC Web Service Common (OWS) <code>OperationNotSupported</code> exception code. Indicates that
     * the requested operation is not supported by the web service.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String OPERATION_NOT_SUPPORTED = "OperationNotSupported";
    /**
     * The name of the OGC Web Service Common (OWS) <code>OptionNotSupported</code> exception code. Indicates that a
     * request contains a parameter with a value that the web service does not support.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String OPTION_NOT_SUPPORTED = "OptionNotSupported";
    /** The OGC Web Service Common (OWS) namespace URI, version 1.1. */
    final String OWS_1dot1_NAMESPACE = "http://www.opengis.net/ows/1.1";
    /**
     * The mime type indicating an OGC Web Service Common (OWS) exception report document.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String SERVICE_EXCEPTION_MIME_TYPE = "application/vnd.ogc.se+xml";
    /**
     * The name of the OGC Web Service Common (OWS) <code>VersionNegotiationFailed</code> exception code. Indicates that
     * the list of versions specified by the <code>AcceptVersions</code> parameter does not contain any versions
     * supported by the web service.
     * <p/>
     * Used by WFS versions <code>1.1.0</code> and <code>2.0.0</code>. Not used by WFS version <code>1.0.0</code>, WMS
     * version <code>1.1.1</code>, and WMS version <code>1.3.0</code>.
     */
    final String VERSION_NEGOTIATION_FAILED = "VersionNegotiationFailed";
}
