/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.util.Logging;

import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

/**
 * @author brownrigg
 * @version $Id$
 */

// TODO: This class in large measure is a subset of behavior/state embodied by WMSGetMapRequest.
public class WMSGetImageryListRequest extends WMSRequest 
{

    public WMSGetImageryListRequest(HTTPRequest req) throws WMSServiceException
    {
        super( req );

        String request = req.getParameter( WMS.Param.REQUEST.toString() );
        if( !WMS.Request.GetImageryList.equals( request ) )
        {
            String msg = Logging.getMessage("WMS.UnknownOrUnsupportedRequest", request );
            Logging.logger().severe(msg);
            throw new WMSServiceException( msg );
        }

        this.layers = req.getParameterValues( WMS.Param.LAYERS.toString() );
        if( null == this.layers || 0 == this.layers.length )
        {
            String msg = Logging.getMessage("WMS.MissingLayerParameters" );
            Logging.logger().severe(msg);
            throw new WMSServiceException( msg );
        }

        String[] bbox = req.getParameterValues( WMS.Param.BBOX.toString() );
        if( null == bbox || 4 != bbox.length )
        {
            String msg = Logging.getMessage("WMS.MissingGeographicBoundingBoxParameter" );
            Logging.logger().severe(msg);
            throw new WMSServiceException( msg );
        }
        else
        {
            this.bbox = new double[4];
            try
            {
                for (int i = 0; i < 4; i++)
                {
                    this.bbox[i] = Double.parseDouble( bbox[i] );
                }
            }
            catch (NumberFormatException ex)
            {
                String msg = Logging.getMessage("WMS.InvalidGeographicBoundingBoxParameter",
                        "{ " + bbox[0] + ", " + bbox[1] + ", " + bbox[2] + ", " + bbox[3] + " }" );
                Logging.logger().severe(msg);
                throw new WMSServiceException( msg );
            }

            if(     this.bbox[WMS.BBOX.XMIN] >= this.bbox[WMS.BBOX.XMAX]
                ||  this.bbox[WMS.BBOX.YMIN] >= this.bbox[WMS.BBOX.YMAX]
              )
            {
                String msg = Logging.getMessage("WMS.InvalidGeographicBoundingBoxParameter",
                        "{ " + bbox[0] + ", " + bbox[1] + ", " + bbox[2] + ", " + bbox[3] + " }" );
                Logging.logger().severe(msg);
                throw new WMSServiceException( msg );
            }
        }
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("GetImageryList: ");
        str.append(" layers(");
        for (int i = 0; i < this.layers.length; i++)
        {
            if (i > 0)
                str.append(", ");
            str.append(this.layers[i]);
        }
        str.append(") bbox(");
        for (int i = 0; i < this.bbox.length; i++)
        {
            if (i > 0)
                str.append(", ");
            str.append(this.bbox[i]);
        }
        str.append(")");
        return str.toString();
    }

    public String[] getLayers()
    {
        return this.layers;
    }

    public double[] getBBox()
    {
        return this.bbox;
    }

    public double getBBoxXMin()
    {
        return this.bbox[WMS.BBOX.XMIN];
    }

    public double getBBoxXMax()
    {
        return this.bbox[WMS.BBOX.XMAX];
    }

    public double getBBoxYMin()
    {
        return this.bbox[WMS.BBOX.YMIN];
    }

    public double getBBoxYMax()
    {
        return this.bbox[WMS.BBOX.YMAX];
    }

    private String[] layers = null;
    private double[] bbox = null;

    public String getFormat()
    {
        return null;
    }

    public void service(HTTPRequest httpreq, HTTPResponse resp) throws IOException, WMSServiceException
    {
        // do we know about this map layer?
        // TODO - for the moment ignore all but first one, until we've implemented map-compositing
        String[] layers = this.getLayers();

        MapRegistry registry = this.getApplicationContext().getMapSourceRegistry();

        Iterable<String> mapNames = registry.getMapNames();
        if (mapNames == null)
            throw new WMSServiceException("No registered map sources!");

        StringBuffer xmlResp = new StringBuffer();
        xmlResp.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlResp.append("<ImageryList bbox=\"");
        xmlResp.append(Double.toString(this.getBBoxXMin()));
        xmlResp.append(",");
        xmlResp.append(Double.toString(this.getBBoxYMin()));
        xmlResp.append(",");
        xmlResp.append(Double.toString(this.getBBoxXMax()));
        xmlResp.append(",");
        xmlResp.append(Double.toString(this.getBBoxYMax()));
        xmlResp.append("\">");

        for (String mapName : mapNames)
        {
            try
            {
                MapSource mapSource = registry.get(mapName);
                String mapLayer = mapSource.getName();
                for (String layer : this.getLayers())
                {
                    if (!"all".equalsIgnoreCase(layer) && !layer.equalsIgnoreCase(mapLayer))
                        continue;

                    MapGenerator mapGen = mapSource.getMapGenerator();
                    MapGenerator.ServiceInstance mapService = mapGen.getServiceInstance();
                    List<File> mapFiles = mapService.serviceRequest( this );
                    if (mapFiles == null)
                        continue;

                    for (File f : mapFiles)
                    {
                        xmlResp.append("<file path=\"");
                        xmlResp.append(f.getAbsolutePath());
                        xmlResp.append("\" layer=\"");
                        xmlResp.append(mapLayer);
                        xmlResp.append("\" filesize=\"");
                        xmlResp.append(Long.toString(f.length()));
                        xmlResp.append("\" />");
                    }
                }
            }
            catch (Exception ex)
            {
                throw new WMSServiceException("Failed to instance map-generator: " + ex.toString());
            }
        }

        xmlResp.append("</ImageryList>");
        byte[] bytes = xmlResp.toString().getBytes();
        resp.setStatus(HTTPResponse.OK);
        resp.setContentType("text/xml");
        resp.setContentLength(bytes.length);

        OutputStream os = resp.getOutputStream();
        os.write(bytes);
        resp.flushBuffer();
    }


}
