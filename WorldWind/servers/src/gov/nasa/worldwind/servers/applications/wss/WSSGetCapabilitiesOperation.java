/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.wss;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.ows.OWSConstants;
import gov.nasa.worldwind.ogc.ows.OWSException;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;
import gov.nasa.worldwind.ogc.wfs.WFSConstants;
import gov.nasa.worldwind.servers.app.ServerApplication;
import gov.nasa.worldwind.servers.applications.ogc.OGCRuntimeException;
import gov.nasa.worldwind.servers.applications.ogc.OGCWebServiceOperation;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 * @author dcollins
 * @version $Id$
 */
public class WSSGetCapabilitiesOperation implements OGCWebServiceOperation
{
    protected static final String DEFAULT_WSS_CAPABILITIES_TEMPLATE = "WEB-INF/WSSCapabilitiesTemplate.xml";

    public WSSGetCapabilitiesOperation()
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

    protected void doService(HTTPRequest request, HTTPResponse response) throws IOException
    {
        response.setStatus(HTTPResponse.OK);
        response.setContentType("text/xml");

        String capabilitiesXML = this.readCapabilitiesTemplateXML(request);
        String featureTypeListXML = this.createFeatureTypeListXML(request);
        capabilitiesXML = capabilitiesXML.replace("<FeatureTypeList/>", featureTypeListXML);

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(capabilitiesXML);
        writer.flush();
    }

    protected String readCapabilitiesTemplateXML(HTTPRequest request)
    {
        ServerApplication app = request.getServerApplication();
        File capFile = null;

        if (app.hasKey(AVKey.CAPABILITIES_TEMPLATE_PATH))
        {
            try
            {
                String customCapFileName = app.getStringValue(AVKey.CAPABILITIES_TEMPLATE_PATH);
                capFile = FileUtil.locateConfigurationFile(customCapFileName);
            }
            catch (Exception e)
            {
                String reason = WWUtil.extractExceptionReason(e);
                String message = Logging.getMessage("generic.DefaultWillBeUsed", DEFAULT_WSS_CAPABILITIES_TEMPLATE, reason);
                Logging.logger().info(message);
            }
        }

        if (null == capFile)
        {
            try
            {
                capFile = FileUtil.locateConfigurationFile(DEFAULT_WSS_CAPABILITIES_TEMPLATE);
            }
            catch (Exception e)
            {
                String message = WWUtil.extractExceptionReason(e);
                Logging.logger().severe(message);
                throw new WWRuntimeException(message, e);
            }
        }

        return WWIO.readTextFile(capFile);
    }

    protected String createFeatureTypeListXML(HTTPRequest request)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<FeatureTypeList>");

        WebShapeService wssApp = (WebShapeService) request.getServerApplication();
        for (WSSFeatureType featureType : wssApp.getAllFeatureTypes())
        {
            sb.append("<FeatureType>");
            sb.append("<Name>").append(featureType.getName()).append("</Name>");

            sb.append("<OutputFormats>");
            for (String format : featureType.getOutputFormats())
            {
                sb.append("<Format>").append(format).append("</Format>");
            }
            sb.append("</OutputFormats>");

            sb.append("<NoCRS/>");

            sb.append("<NamedFeatures>");
            for (String name : featureType.getFeatureResourceIDs())
            {
                sb.append("<Feature>").append(name).append("</Feature>");
            }
            sb.append("</NamedFeatures>");

            sb.append("</FeatureType>");
        }

        sb.append("</FeatureTypeList>");

        return sb.toString();
    }

    protected OWSExceptionReport validateRequest(HTTPRequest request)
    {
        OWSExceptionReport report = new OWSExceptionReport();
        report.setVersion(WFSConstants.WFS_2dot0_VERSION);

        String[] versions = request.getParameterValues(OWSConstants.ACCEPT_VERSIONS);
        if (versions != null && versions.length > 0
            && !Arrays.asList(versions).contains(WFSConstants.WFS_2dot0_VERSION))
        {
            OWSException ex = new OWSException();
            ex.setExceptionCode(OWSConstants.VERSION_NEGOTIATION_FAILED);
            ex.setLocator(OWSConstants.ACCEPT_VERSIONS);
            ex.addExceptionText(Logging.getMessage("WMS.InvalidParameterValue", OWSConstants.ACCEPT_VERSIONS,
                    Arrays.toString(versions)));

        }

        return report;
    }

    protected OWSExceptionReport createProcessingFailedReport(String message)
    {
        OWSExceptionReport report = new OWSExceptionReport();
        report.setVersion(WFSConstants.WFS_2dot0_VERSION);

        OWSException ex = new OWSException();
        ex.setExceptionCode(WFSConstants.OPERATION_PROCESSING_FAILED);
        ex.setLocator(OGCConstants.GET_CAPABILITIES);
        ex.addExceptionText(Logging.getMessage("WMS.Server.InternalError", message));
        report.addException(ex);

        return report;
    }
}
