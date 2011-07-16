/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.app.ServerApplication;
import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author brownrigg
 * @version $Id$
 */
public class WMSGetCapabilitiesRequest extends WMSRequest
{
    protected static final String DEFAULT_WMS_CAPABILITIES_TEMPLATE = "WEB-INF/capabilities_template.xml";

    private String version = null;
    private String format = null;
    private String updateSequence = null;

    public WMSGetCapabilitiesRequest(HTTPRequest req) throws WMSServiceException
    {
        super(req);

        String request = req.getParameter(WMS.Param.REQUEST.name());
        if (!WMS.Request.GetCapabilities.equals(request))
        {
            String msg = Logging.getMessage("WMS.UnknownOrUnsupportedRequest", request);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        String service = req.getParameter(WMS.Param.SERVICE.name());
        if (!"WMS".equalsIgnoreCase(service))
        {
            String msg = Logging.getMessage("generic.MissingRequiredParameter", WMS.Param.SERVICE.name());
            Logging.logger().finest(msg);
//            throw new WMSServiceException( msg );
        }

        this.version = req.getParameter(WMS.Param.VERSION.toString());

        this.format = req.getParameter(WMS.Param.FORMAT.toString());

        this.updateSequence = req.getParameter(WMS.Param.UPDATESEQUENCE.toString());
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("GetCapabilities: version(");
        str.append(this.version);
        str.append(") format(");
        str.append(this.format);
        str.append(") updateSeq(");
        str.append(this.updateSequence);
        str.append(")");
        return str.toString();
    }

    // gettors...
    public String getVersion()
    {
        return this.version;
    }

    public String getFormat()
    {
        return this.format;
    }

    public String getUpdateSequence()
    {
        return this.updateSequence;
    }

    public void service(HTTPRequest req, HTTPResponse resp) throws IOException, WMSServiceException
    {
        ServerApplication app = (null != req) ? req.getServerApplication() : null;

        String caps = this.getCapabilitiesString(app);
        if (WWUtil.isEmpty(caps))
        {
            String message = Logging.getMessage("nullValue.CapabilitiesIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        byte[] bytes = (null != caps) ? caps.getBytes() : new byte[0];
        resp.setContentLength(bytes.length);
        resp.setContentType("text/xml");
        resp.setStatus(HTTPResponse.OK);

        OutputStream os = resp.getOutputStream();
        os.write(bytes);
        resp.flushBuffer();
    }

    protected String getCapabilitiesString(ServerApplication app) throws WMSServiceException
    {
        String capabilitiesXML = null;

        HTTPRequest httpReq = this.getHttpRequest();

        capabilitiesXML = this.loadCapabilitiesTemplate(app);

        String requestedType = (httpReq.getUrl().toLowerCase().endsWith("/elev")) ? "elevation" : "imagery";

        String supportedFormats = ("imagery".equals(requestedType))
            ? "<Format>image/dds</Format><Format>image/png</Format><Format>image/jpeg</Format><Format>image/tiff</Format>"
            : "<Format>image/bil</Format><Format>application/bil</Format><Format>application/bil16</Format><Format>application/bil32</Format>";

        capabilitiesXML = capabilitiesXML.replaceFirst("<Insert_SupportedFormats/>", supportedFormats);

        MapRegistry msRegistry = null;
        String url = null;

        if (null != app && app instanceof WMSServerApplication)
        {
            WMSServerApplication wmsApp = (WMSServerApplication) app;

            msRegistry = wmsApp.getMapSourceRegistry();
            url = wmsApp.getOnlineResource();
        }

        if (null != url && url.length() > 0 && capabilitiesXML.indexOf("<Insert_OnlineResource/>") > 0)
        {
            if ("elevation".equals(requestedType))
            {
                url = url.replaceAll("/wms\\?", "/elev\\?");
            }

            String xmlOnlineResource =
                "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"simple\" xlink:href=\"" + url
                    + "\"/>";

            capabilitiesXML = capabilitiesXML.replaceAll("<Insert_OnlineResource/>", xmlOnlineResource);
        }
        else
        {
            if ("elevation".equals(requestedType))
            {
                capabilitiesXML = capabilitiesXML.replaceAll("/wms\\?", "/elev\\?");
            }
        }

        //insert dynamic layers here
        StringBuffer sb = new StringBuffer();
        try
        {
            if (null != msRegistry)
            {
                for (String mapName : msRegistry.getMapNames())
                {
                    try
                    {
                        // Get the MapSource registered with the specified map name.
                        // If the MapSource corresponding to that name does not exist,
                        // or if that MapSource has a parent, then ignore it.
                        // At this level we only write the the capabilities XML for the top level MapSources.
                        MapSource mapSrc = msRegistry.get(mapName);
                        if (null == mapSrc || null != mapSrc.getParent())
                        {
                            continue;
                        }

                        MapGenerator gen = mapSrc.getMapGenerator();
                        if (null == gen)
                        {
                            continue;
                        }

                        if (requestedType.equals(gen.getDataType()))
                        {
                            String xml = mapSrc.toXML();
                            Logging.logger().finest(xml);
                            sb.append(xml);
                        }
                    }
                    catch (Exception e)
                    {
                        Logging.logger().finest(e.getMessage());
                    }
                }
            }
        }
        catch (Exception ex)
        {
            WMSServiceException ex2 = new WMSServiceException(
                "Error retrieving layer information fulfilling getCapabilities request: " + ex.toString());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }

        capabilitiesXML = capabilitiesXML.replaceFirst("<Insert></Insert>", sb.toString());

        Logging.logger().finest(capabilitiesXML);

        return capabilitiesXML;
    }

    protected String loadCapabilitiesTemplate(ServerApplication app) throws WMSServiceException
    {
        File capFile = null;

        if (null != app && app.hasKey(AVKey.CAPABILITIES_TEMPLATE_PATH))
        {
            try
            {
                String customCapFileName = app.getStringValue(AVKey.CAPABILITIES_TEMPLATE_PATH);
                capFile = FileUtil.locateConfigurationFile(customCapFileName);
            }
            catch (Exception e)
            {
                String reason = WWUtil.extractExceptionReason(e);
                Logging.logger().info(
                    Logging.getMessage("generic.DefaultWillBeUsed", DEFAULT_WMS_CAPABILITIES_TEMPLATE, reason));
            }
        }

        if (null == capFile)
        {
            try
            {
                capFile = FileUtil.locateConfigurationFile(DEFAULT_WMS_CAPABILITIES_TEMPLATE);
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
}
