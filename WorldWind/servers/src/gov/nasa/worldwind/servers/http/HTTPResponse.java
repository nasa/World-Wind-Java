/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.http;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author brownrigg
 * @version $Id$
 */
public class HTTPResponse
{
    /**
     * Creates a new instance of HTTPResponse
     */
    public HTTPResponse(Socket s) throws IOException
    {
        super();

        if (null == s)
        {
            String message = Logging.getMessage("nullValue.SocketIsNull");
            Logging.logger().severe(message);
            throw new IOException(message);
        }
        if (s.isClosed())
        {
            String message = Logging.getMessage("generic.SocketIsClosed");
            Logging.logger().fine(message);
            throw new IOException(message);
        }
        this.theSocket = s;

        this.theOutputStream = new HttpOutputStream(s.getOutputStream());
//        this.theOutputStream = new HttpOutputStream(new BufferedOutputStream(s.getOutputStream(), HTTP.MAXIMUM_BUF_SIZE));

        this.headers = new HashMap<String, String>();
    }

    public void setStatus(int status)
    {
        this.httpStatus = status;
    }

    public void setContentLength(int length)
    {
        this.contentLength = length;
    }

    public void setContentType(String type)
    {
        this.contentType = type;
    }

    // Note: overwrites any existing header of this name...

    public void addHeader(String header, String value)
    {
        this.headers.put(header, value);
    }

    public OutputStream getOutputStream() throws IOException
    {
        return this.theOutputStream;
    }

    public PrintWriter getWriter() throws IOException
    {
        return null;
    }

    public boolean isCommitted()
    {
        return this.committed;
    }

    public void flushBuffer() throws IOException
    {
        getOutputStream().flush();
    }


    public void setSendBufferSize(int size)
    {
        synchronized (this)
        {
            try
            {
                if (size > 0)
                {
                    theSocket.setSendBufferSize(size);
                }
            }
            catch (Throwable t)
            {
                Logging.logger().finest(WWUtil.extractExceptionReason(t));
            }
        }
    }

    public int getSendBufferSize()
    {
        synchronized (this)
        {
            try
            {
                return this.theSocket.getSendBufferSize();
            }
            catch (Throwable t)
            {
                Logging.logger().finest(WWUtil.extractExceptionReason(t));
                return 0;
            }
        }
    }

    public void write(InputStream in) throws IOException
    {
        if (null != in)
        {
            OutputStream out = null;

            try
            {
                out = this.getOutputStream();
                int contentLength = in.available();
                this.setContentLength(contentLength);

                int sendBufferSize = this.getSendBufferSize();
                int bufferSize = (sendBufferSize <= 0) ? HTTP.DEFAULT_BUFFER_SIZE : sendBufferSize;
                bufferSize = (bufferSize > HTTP.MAXIMUM_BUF_SIZE) ? HTTP.MAXIMUM_BUF_SIZE : bufferSize;
                bufferSize = (bufferSize > contentLength) ? contentLength : bufferSize;

                if (sendBufferSize != bufferSize)
                {
                    this.setSendBufferSize(bufferSize);
                    bufferSize = this.getSendBufferSize();
                }

                byte[] buffer = new byte[bufferSize];
                int numRead;

                while ((numRead = in.read(buffer, 0, bufferSize)) != -1)
                {
                    out.write(buffer, 0, numRead);
                }
            }
            finally
            {
                if (null != out)
                {
                    out.flush();
                }
            }
        }
    }

    private void writeHttpResponseHead() throws IOException
    {
        OutputStreamWriter out = new OutputStreamWriter(this.theSocket.getOutputStream());
        String statusText = statusMsgs.get(this.httpStatus);
        if (statusText == null)
        {
            statusText = statusMsgs.get(UNKNOWN_STATUS);
        }
        String status = "HTTP/1.1 " + Integer.toString(httpStatus) + " " + statusText + "\r\n";
        out.write(status);

        // include these "special" headers and write all of them...
        addHeader("Content-Type", this.contentType);
        // omit contentLength if unknown...
        if (this.contentLength > 0)
        {
            addHeader("Content-Length", Integer.toString(this.contentLength));
        }

        Date now = new Date(System.currentTimeMillis());
        String nowStr = getTimeStamp(now);
        addHeader("Date", nowStr);

        addHeader("Server", SERVER_STRING);

        Set<String> keys = this.headers.keySet();
        for (String key : keys)
        {
            String val = this.headers.get(key);
            out.write(key + ": " + val + "\r\n");
        }
        out.write("\r\n");
        out.flush();
        this.committed = true;
    }

    private String getTimeStamp(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    /*
    * This inner class wraps the socket's OutputStream. We override the write()
    * methods so that we can guarantee that the HTTP headers get written before
    * any other output.
    */

    private class HttpOutputStream extends OutputStream
    {
        public HttpOutputStream(OutputStream o)
        {
            this.out = o;
        }

        public void write(byte[] b) throws IOException
        {
            if (!isCommitted())
            {
                writeHttpResponseHead();
            }
            this.out.write(b);
        }

        public void write(byte b[], int off, int len) throws IOException
        {
            if (!isCommitted())
            {
                writeHttpResponseHead();
            }
            this.out.write(b, off, len);
        }

        public void write(int b) throws IOException
        {
            if (!isCommitted())
            {
                writeHttpResponseHead();
            }
            this.out.write(b);
        }

        public void flush() throws IOException
        {
            if (!isCommitted())
            {
                writeHttpResponseHead();
            }
            this.out.flush();
        }

        private OutputStream out;
    }

    private Socket theSocket = null;
    private String contentType = "text/html";
    private int contentLength = 0;
    private int httpStatus = 0;
    private boolean committed = false;
    private Map<String, String> headers;
    private HttpOutputStream theOutputStream;

    // HTTP status codes...
    static public final int UNKNOWN_STATUS = -1;
    static public final int OK = 200;
    static public final int NO_CONTENT = 204;

    static public final int PERM_REDIRECT = 301;
    static public final int TEMP_REDIRECT = 302;

    static public final int BAD_REQUEST = 400;
    static public final int NOT_FOUND = 404;
    static public final int SERVER_ERROR = 500;
    static public final int SERVER_ERROR_NOT_IMPLEMENTED = 501;
    static public final int SERVER_ERROR_BAD_GATEWAY = 502;
    static public final int SERVER_ERROR_SERVICE_UNAVAILABLE = 503;
    static public final int SERVER_ERROR_GATEWAY_TIMEOUT = 504;
    static public final int SERVER_ERROR_HTTP_VERSION_UNSUPPORTED = 505;
    static public final int SERVER_ERROR_INSUFFICIENT_STORAGE = 507;
    static public final int SERVER_ERROR_BANDWIDTH_LIMIT_EXCEEDED = 509;
    static public final int SERVER_ERROR_NOT_EXTENDED = 510;

    static private Map<Integer, String> statusMsgs = new HashMap<Integer, String>();

    static
    {
        statusMsgs.put(OK, "OK");
        statusMsgs.put(NO_CONTENT, "No Content");
        statusMsgs.put(BAD_REQUEST, "Bad Request");
        statusMsgs.put(NOT_FOUND, "Not Found");
        statusMsgs.put(SERVER_ERROR, "Internal Server Error");

        statusMsgs.put(SERVER_ERROR_NOT_IMPLEMENTED, "Not Implemented");
        statusMsgs.put(SERVER_ERROR_BAD_GATEWAY, "Bad Gateway");
        statusMsgs.put(SERVER_ERROR_SERVICE_UNAVAILABLE, "Service Unavailable");
        statusMsgs.put(SERVER_ERROR_GATEWAY_TIMEOUT, "Gateway Timeout");
        statusMsgs.put(SERVER_ERROR_HTTP_VERSION_UNSUPPORTED, "HTTP Version Not Supported");
        statusMsgs.put(SERVER_ERROR_INSUFFICIENT_STORAGE, "Insufficient Storage");
        statusMsgs.put(SERVER_ERROR_BANDWIDTH_LIMIT_EXCEEDED, "Bandwidth Limit Exceeded");
        statusMsgs.put(SERVER_ERROR_NOT_EXTENDED, "Not Extended");

        statusMsgs.put(PERM_REDIRECT, "Moved Permanently");
        statusMsgs.put(TEMP_REDIRECT, "Found");

        statusMsgs.put(UNKNOWN_STATUS, "WMSResponse");   // for unknown status codes...
    }

    static private final String SERVER_STRING = "NASA WorldWind Application Server";

    // hide this...

    private HTTPResponse()
    {
    }
}
