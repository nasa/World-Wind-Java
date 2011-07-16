/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.ogc;

import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.ows.OWSConstants;
import gov.nasa.worldwind.ogc.ows.OWSException;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.util.Arrays;

/**
 * @author dcollins
 * @version $Id$
 */
public class OGCUtil
{
    public static int getStatusCodeForExceptionReport(OWSException e)
    {
        if (OWSConstants.OPERATION_NOT_SUPPORTED.equals(e.getExceptionCode()))
        {
            return HTTPResponse.SERVER_ERROR_NOT_IMPLEMENTED;
        }
        else if (OWSConstants.MISSING_PARAMETER_VALUE.equals(e.getExceptionCode()))
        {
            return HTTPResponse.BAD_REQUEST;
        }
        else if (OWSConstants.INVALID_PARAMETER_VALUE.equals(e.getExceptionCode()))
        {
            return HTTPResponse.BAD_REQUEST;
        }
        else if (OWSConstants.VERSION_NEGOTIATION_FAILED.equals(e.getExceptionCode()))
        {
            return HTTPResponse.BAD_REQUEST;
        }
        else if (OWSConstants.OPTION_NOT_SUPPORTED.equals(e.getExceptionCode()))
        {
            return HTTPResponse.SERVER_ERROR_NOT_IMPLEMENTED;
        }
        else // OGCConstants.NO_APPLICABLE_CODE
        {
            return HTTPResponse.SERVER_ERROR;
        }
    }

    public static void reportMissingParameters(HTTPRequest request, String[] requiredParams, OWSExceptionReport report)
    {
        for (String s : requiredParams)
        {
            if (WWUtil.isEmpty(request.getParameter(s)))
            {
                OWSException ex = new OWSException();
                ex.setExceptionCode(OWSConstants.MISSING_PARAMETER_VALUE);
                ex.setLocator(s);
                ex.addExceptionText(Logging.getMessage("WMS.MissingRequiredParameter", s));

                report.addException(ex);
            }
        }
    }

    public static void reportInvalidParameter(HTTPRequest request, String paramName, String[] validValues,
                                              OWSExceptionReport report)
    {
        String s = request.getParameter(paramName);
        if (!WWUtil.isEmpty(s) && !Arrays.asList(validValues).contains(s))
        {
            OWSException ex = new OWSException();
            ex.setExceptionCode(OWSConstants.INVALID_PARAMETER_VALUE);
            ex.setLocator(paramName + "=" + s);
            ex.addExceptionText(Logging.getMessage("WMS.InvalidParameterValue", OGCConstants.SERVICE, s));

            report.addException(ex);
        }
    }

    public static void reportUnsupportedOperation(HTTPRequest request, String[] validValues, OWSExceptionReport report)
    {
        String s = request.getParameter(OGCConstants.REQUEST);
        if (WWUtil.isEmpty(s) || !Arrays.asList(validValues).contains(s))
        {
            OWSException ex = new OWSException();
            ex.setExceptionCode(OWSConstants.OPERATION_NOT_SUPPORTED);
            ex.setLocator(request.getParameter(OGCConstants.REQUEST));
            ex.addExceptionText(Logging.getMessage("WMS.UnknownOrUnsupportedRequest",
                request.getParameter(OGCConstants.REQUEST)));

            report.addException(ex);
        }
    }
}
