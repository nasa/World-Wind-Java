/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.retrieve;

import gov.nasa.worldwind.util.Logging;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public class HTTPRetriever extends URLRetriever
{
    private int responseCode;
    private String responseMessage;

    public HTTPRetriever(URL url, RetrievalPostProcessor postProcessor)
    {
        super(url, postProcessor);
    }

    public int getResponseCode()
    {
        return this.responseCode;
    }

    public String getResponseMessage()
    {
        return this.responseMessage;
    }

    protected ByteBuffer doRead(URLConnection connection) throws Exception
    {
        if (connection == null)
        {
            String msg = Logging.getMessage("nullValue.ConnectionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        HttpURLConnection htpc = (HttpURLConnection) connection;
        this.responseCode = htpc.getResponseCode();
        this.responseMessage = htpc.getResponseMessage();
        String contentType = connection.getContentType();

        Logging.logger().log(Level.FINE, "HTTPRetriever.ResponseInfo", new Object[] {this.responseCode,
            connection.getContentLength(), contentType != null ? contentType : "content type not returned",
            connection.getURL()});

        if (this.responseCode == HttpURLConnection.HTTP_OK)
            return super.doRead(connection);

        return null;
    }
}
