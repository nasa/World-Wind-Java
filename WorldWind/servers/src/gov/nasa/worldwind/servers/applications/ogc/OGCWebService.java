/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.ogc;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.ows.OWSConstants;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;
import gov.nasa.worldwind.servers.app.BasicHttpServerApplication;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Level;

/**
 * @author dcollins
 * @version $Id$
 */
public class OGCWebService extends BasicHttpServerApplication
{
    protected static final String[] CommonParams = new String[]
        {
            OGCConstants.SERVICE,
            OGCConstants.REQUEST,
            OGCConstants.VERSION
        };

    protected static final String[] GetCapabilitiesParams = new String[]
        {
            OGCConstants.SERVICE,
            OGCConstants.REQUEST,
        };

    public OGCWebService()
    {
    }

    public OGCWebService(AVList config)
    {
        super(config);
    }


    @Override
    protected void doPost(HTTPRequest request, HTTPResponse response)
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

        try
        {
            OGCWebServiceOperation op = this.createOperation(request);
            if( null != op )
            {
                op.service(request, response);
            }
            else
            {
                // if no one handles the POST request,
                // send ACK to the client and cleanup data (uploaded files)
                response.setStatus(HTTPResponse.OK);
                response.setContentType("text");
                response.flushBuffer();
            }
        }
        catch(Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            Logging.logger().severe(reason);
        }
        finally
        {
            AVList data = request.getData();
            if( data != null && data.hasKey(AVKey.FILE) )
            {
                File uploadedTempFile = WWIO.getFileForLocalAddress(data.getValue(AVKey.FILE));
                if( uploadedTempFile != null && uploadedTempFile.exists() )
                    FileUtil.deleteFile(uploadedTempFile);
            }
        }
    }
    @Override
    protected void doGet(HTTPRequest request, HTTPResponse response)
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

        // Note: This method and the operations it calls must be thread safe. The ApplicationServer may call this method
        // from multiple threads to service simultaneous requests.

        OWSExceptionReport exceptionReport = new OWSExceptionReport();
        this.validateRequest(request, exceptionReport);

        if (exceptionReport.getExceptions().size() > 0)
        {
            this.respondWithExceptionReport(response, exceptionReport);
            return;
        }

        try
        {
            OGCWebServiceOperation op = this.createOperation(request);
            op.service(request, response);
        }
        catch (OGCRuntimeException e)
        {
            this.respondWithExceptionReport(response, e.getExceptionReport());
        }
    }

    protected OGCWebServiceOperation createOperation(HTTPRequest request)
    {
        if (OGCConstants.GET_CAPABILITIES.equals(request.getParameter(OGCConstants.REQUEST)))
        {
            return this.createGetCapabilitiesOperation(request);
        }
        else
        {
            return null;
        }
    }

    protected OGCWebServiceOperation createGetCapabilitiesOperation(HTTPRequest request)
    {
        return null;
    }

    protected void validateRequest(HTTPRequest request, OWSExceptionReport report)
    {
        if (OGCConstants.GET_CAPABILITIES.equals(request.getParameter(OGCConstants.REQUEST)))
        {
            OGCUtil.reportMissingParameters(request, GetCapabilitiesParams, report);
        }
        else
        {
            OGCUtil.reportMissingParameters(request, CommonParams, report);
        }
    }

    protected void respondWithExceptionReport(HTTPResponse response, OWSExceptionReport report)
    {
        try
        {
            StringWriter writer = new StringWriter();
            report.export(writer);
            byte[] bytes = writer.toString().getBytes();

            if (report.getExceptions().size() > 0)
                response.setStatus(OGCUtil.getStatusCodeForExceptionReport(report.getExceptions().get(0)));

            response.setContentType(OWSConstants.SERVICE_EXCEPTION_MIME_TYPE);
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();
        }
        catch (Exception e)
        {
            Logging.logger().log(Level.SEVERE, Logging.getMessage("WMS.Server.ExceptionSendingResponse", response), e);
        }
    }
}
