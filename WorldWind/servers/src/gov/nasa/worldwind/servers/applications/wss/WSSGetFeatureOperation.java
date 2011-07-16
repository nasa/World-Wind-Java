/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.wss;

import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.ows.OWSException;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;
import gov.nasa.worldwind.ogc.wfs.WFSConstants;
import gov.nasa.worldwind.servers.applications.ogc.OGCRuntimeException;
import gov.nasa.worldwind.servers.applications.ogc.OGCUtil;
import gov.nasa.worldwind.servers.applications.ogc.OGCWebServiceOperation;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author dcollins
 * @version $Id$
 */
public class WSSGetFeatureOperation implements OGCWebServiceOperation
{
    protected static final String[] RequiredParameters = new String[]
            {
                    OGCConstants.VERSION,
                    WFSConstants.TYPE_NAMES,
                    WFSConstants.RESOURCE_ID
            };

    public WSSGetFeatureOperation()
    {
    }

    @Override
    public void service(HTTPRequest request, HTTPResponse response)
    {
        if (request == null)
        {
            String message = Logging.getMessage("nullValue.RequestIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (response == null)
        {
            String message = Logging.getMessage("nullValue.ResponseIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        OWSExceptionReport report = this.validateRequest(request);
        if (report != null && report.getExceptions().size() > 0)
        {
            throw new OGCRuntimeException(report);
        }

        try
        {
            this.doService(request, response);
        }
        catch (IOException e)
        {
            report = this.createProcessingFailedReport(e.getMessage());
            throw new OGCRuntimeException(report);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void doService(HTTPRequest request, HTTPResponse response) throws IOException
    {
        // Throws an OGCRuntimeException if the request is invalid.
        URL featureURL = this.getFeatureResourceURL(request);

        response.setStatus(HTTPResponse.OK);
        response.setContentType(KMLConstants.KMZ_MIME_TYPE);

        InputStream in = null;
        try
        {
            in = featureURL.openStream();
            response.write(in);
        }
        finally
        {
            WWIO.closeStream(in, null);
        }
    }

    protected URL getFeatureResourceURL(HTTPRequest request)
    {
        WebShapeService wssApp = (WebShapeService) request.getServerApplication();

        WSSFeatureType featureType = wssApp.getFeatureType(request.getParameter(WFSConstants.TYPE_NAMES));
        if (featureType == null) // TODO: parse multiple comma delimited typeNames.
        {
            OWSExceptionReport report = this.createProcessingFailedReport(
                    Logging.getMessage("generic.UnrecognizedFeature", request.getParameter(WFSConstants.TYPE_NAMES)));
            throw new OGCRuntimeException(report);
        }

        URL resourceURL = featureType.getFeatureResource(request.getParameter(WFSConstants.RESOURCE_ID));
        if (resourceURL == null)
        {
            OWSExceptionReport report = this.createProcessingFailedReport(
                    Logging.getMessage("generic.UnrecognizedFeature", request.getParameter(WFSConstants.RESOURCE_ID)));
            throw new OGCRuntimeException(report);
        }

        return resourceURL;
    }

    protected OWSExceptionReport validateRequest(HTTPRequest request)
    {
        OWSExceptionReport report = new OWSExceptionReport();
        report.setVersion(WFSConstants.WFS_2dot0_VERSION);

        OGCUtil.reportMissingParameters(request, RequiredParameters, report);
        OGCUtil.reportInvalidParameter(request, OGCConstants.VERSION, new String[]{WFSConstants.WFS_2dot0_VERSION},
                report);

        if (report.getExceptions().isEmpty())
        {
            return null;
        }

        return report;
    }

    protected OWSExceptionReport createProcessingFailedReport(String message)
    {
        OWSExceptionReport report = new OWSExceptionReport();
        report.setVersion(WFSConstants.WFS_2dot0_VERSION);

        OWSException ex = new OWSException();
        ex.setExceptionCode(WFSConstants.OPERATION_PROCESSING_FAILED);
        ex.setLocator(WFSConstants.GET_FEATURE);
        ex.addExceptionText(Logging.getMessage("WMS.Server.InternalError", message));
        report.addException(ex);

        return report;
    }
}
