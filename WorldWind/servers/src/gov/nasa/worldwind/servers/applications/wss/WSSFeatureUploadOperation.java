/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.applications.wss;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.ogc.ows.*;
import gov.nasa.worldwind.ogc.wfs.WFSConstants;
import gov.nasa.worldwind.servers.applications.ogc.*;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.*;

import java.io.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class WSSFeatureUploadOperation implements OGCWebServiceOperation
{
    protected static final String[] RequiredParameters = new String[]
        {
            AVKey.FILE,
            AVKey.FILE_NAME,
            AVKey.FILE_SIZE
        };

    public WSSFeatureUploadOperation()
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
//            report = this.createProcessingFailedReport(e.getMessage());
            throw new OGCRuntimeException(report);
        }
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected void doService(HTTPRequest request, HTTPResponse response) throws IOException
    {
        WebShapeService wssApp = (WebShapeService) request.getServerApplication();

        try
        {
            AVList data = request.getData();

            FileStore fs = wssApp.getFileStore();
            File destFolder = fs.getWriteLocation();

            String filename = data.getStringValue(AVKey.FILE_NAME);
            File tempFile = (File) data.getValue(AVKey.FILE);

            File dest = new File(this.makeUniqueFilename(filename, fs));

            FileUtil.moveFile(tempFile.getAbsolutePath(), dest.getAbsolutePath(), false);
            if (fs.containsFile(dest.getName())
                && dest.exists()
                && dest.length() == (Long) data.getValue(AVKey.FILE_SIZE)
                )
            {
                response.setStatus(HTTPResponse.OK);

                String featureFilename = dest.getName();
                String featureName = WWIO.replaceSuffix(featureFilename, "");

                wssApp.addFeature( featureName, featureFilename );
                wssApp.loadFeatureTypes();
            }
            else
            {
                response.setStatus(HTTPResponse.SERVER_ERROR);
            }
        }
        catch (Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            Logging.logger().severe(reason);

            response.setStatus(HTTPResponse.SERVER_ERROR);
        }
        finally
        {
            response.setContentType("text");
            response.flushBuffer();
        }
    }

    protected OWSExceptionReport validateRequest(HTTPRequest request)
    {
        OWSExceptionReport report = new OWSExceptionReport();
        report.setVersion(WFSConstants.WFS_2dot0_VERSION);

        AVList data = request.getData();
        for (String paramKey : RequiredParameters)
        {
            if (WWUtil.isEmpty(data.getValue(paramKey)))
            {
                OWSException ex = new OWSException();
                ex.setExceptionCode(OWSConstants.MISSING_PARAMETER_VALUE);
                ex.setLocator(paramKey);
                ex.addExceptionText(Logging.getMessage("generic.MissingRequiredParameter", paramKey));

                report.addException(ex);
            }
        }

        if (report.getExceptions().isEmpty())
        {
            return null;
        }

        return report;
    }

    protected String makeUniqueFilename(String filename, FileStore fs)
    {
        File destFolder = fs.getWriteLocation();

        String destFilename = filename;
        File dest = new File(destFolder + File.separator + destFilename);

        int number = 0;
        while (fs.containsFile(destFilename) || dest.exists())
        {
            String ext = WWIO.getSuffix(filename);
            if (WWUtil.isEmpty(ext))
            {
                destFilename = filename + "(" + (++number) + ")";
            }
            else
            {
                int pos = filename.lastIndexOf(ext);
                String name = (pos > 0) ? filename.substring(0, pos - 1) : filename;
                destFilename = name + "(" + (++number) + ")" + ".kmz"; // + ext;
            }

            dest = new File(destFolder + File.separator + destFilename);
        }

        return dest.getAbsolutePath();
    }
}
