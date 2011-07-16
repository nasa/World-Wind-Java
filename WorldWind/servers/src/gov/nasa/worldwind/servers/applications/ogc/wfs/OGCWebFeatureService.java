/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.ogc.wfs;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.ogc.OGCConstants;
import gov.nasa.worldwind.ogc.ows.OWSException;
import gov.nasa.worldwind.ogc.ows.OWSExceptionReport;
import gov.nasa.worldwind.ogc.wfs.WFSConstants;
import gov.nasa.worldwind.servers.applications.ogc.OGCUtil;
import gov.nasa.worldwind.servers.applications.ogc.OGCWebService;
import gov.nasa.worldwind.servers.applications.ogc.OGCWebServiceOperation;
import gov.nasa.worldwind.servers.http.HTTP;
import gov.nasa.worldwind.servers.http.HTTPRequest;

/**
 * @author dcollins
 * @version $Id$
 */
public class OGCWebFeatureService extends OGCWebService
{
    protected static final String[] RequestValues = new String[]
        {
            OGCConstants.GET_CAPABILITIES,
            WFSConstants.GET_FEATURE
        };

    protected static final String[] ServiceNames = new String[]
        {
            WFSConstants.WFS_SERVICE_NAME
        };

    public OGCWebFeatureService()
    {
    }

    public OGCWebFeatureService(AVList config)
    {
        super(config);
    }

    @Override
    protected OGCWebServiceOperation createOperation(HTTPRequest request)
    {
        if( HTTP.POST.equalsIgnoreCase(request.getMethod()) )
        {
            return this.createFeatureUploadOperation(request);
        }
        else if (WFSConstants.GET_FEATURE.equals(request.getParameter(OGCConstants.REQUEST)))
        {
            return this.createGetFeatureOperation(request);
        }
        else
        {
            return super.createOperation(request);
        }
    }

    protected OGCWebServiceOperation createGetFeatureOperation(HTTPRequest request)
    {
        return null;
    }

    protected OGCWebServiceOperation createFeatureUploadOperation(HTTPRequest request)
    {
        return null;
    }

    @Override
    protected void validateRequest(HTTPRequest request, OWSExceptionReport report)
    {
        super.validateRequest(request, report);

        report.setVersion(WFSConstants.WFS_2dot0_VERSION);
        OGCUtil.reportInvalidParameter(request, OGCConstants.SERVICE, ServiceNames, report);

        if (!report.getExceptions().isEmpty())
        {
            OWSException ex = new OWSException();
            ex.setExceptionCode(WFSConstants.OPERATION_PARSING_FAILED);
            ex.setLocator(request.getParameter(OGCConstants.REQUEST));
            report.getExceptions().add(0, ex); // Make this the first exception in the exception report.
            return;
        }

        OGCUtil.reportUnsupportedOperation(request, RequestValues, report);
    }
}
