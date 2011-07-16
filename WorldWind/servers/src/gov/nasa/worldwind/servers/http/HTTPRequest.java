/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.http;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.ogc.wss.WSS;
import gov.nasa.worldwind.servers.app.ApplicationServer;
import gov.nasa.worldwind.servers.app.ServerApplication;
import gov.nasa.worldwind.servers.wms.generators.TempFile;
import gov.nasa.worldwind.servers.wms.security.AllTrustedSecurityManager;
import gov.nasa.worldwind.servers.wms.security.WMSSecurity;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class HTTPRequest
{
    protected ServerApplication serverApp = null;

    protected StringBuffer stats = new StringBuffer();

    protected String method = "null";
    protected String url = "null";
    protected String query = null;
    protected String protocol = null;

    protected AVList headers = new AVListImpl();
    protected Map<String, String> parameters = null;
    protected AVList data = null;

    protected Socket clientSocket = null;

    private HTTPRequest()
    {
    }

    public HTTPRequest(String wms_query)
    {
        this.method = "GET";
        this.url = wms_query;
        this.query = HTTP.decodeEscapes(wms_query);
        this.url = "localhost";
        this.extractParameters();
    }

    /**
     * Creates a new instance of HTTPRequest
     */
    public HTTPRequest(Socket clientSocket, ServerApplication app) throws IOException, InterruptedException
    {
        this.clientSocket = clientSocket;
        this.serverApp = app;

        try
        {
            BufferedReadableByteChannel channel = new BufferedReadableByteChannel(this.clientSocket.getInputStream());

            // the first line is special...
            String line = channel.readLine();
            if (WWUtil.isEmpty(line))
            {
                String reason = Logging.getMessage("nullValue.RequestIsNull");
                String msg = Logging.getMessage("generic.MalformedRequest", reason);
                Logging.logger().severe(msg);
                throw new IOException(msg);
            }

            StringTokenizer parser = new StringTokenizer(line);

            if (parser.countTokens() < 3)
            {
                String msg = Logging.getMessage("generic.MalformedRequest", line);
                Logging.logger().severe(msg);
                throw new IOException(msg);
            }
            this.method = parser.nextToken();
            this.url = parser.nextToken();
            this.protocol = parser.nextToken();

            if ("GET".equalsIgnoreCase(this.method))
            {
                Boolean allowGet = AVListImpl.getBooleanValue(app, AVKey.HTTP_SERVER_ALLOW_GET, Boolean.TRUE);
                if (allowGet)
                {
                    this.doGet(channel);
                }
                else
                {
                    String msg = Logging.getMessage("HTTP.MethodNotAllowed", this.method);
                    Logging.logger().severe(msg);
                    throw new IOException(msg);
                }
            }
            else if ("POST".equalsIgnoreCase(this.method))
            {
                Boolean allowPost = AVListImpl.getBooleanValue(app, AVKey.HTTP_SERVER_ALLOW_POST, Boolean.FALSE);
                if (allowPost)
                {
                    this.doPost(channel);
                }
                else
                {
                    String msg = Logging.getMessage("HTTP.MethodNotAllowed", this.method);
                    Logging.logger().severe(msg);
                    throw new IOException(msg);
                }
            }
            else
            {
                String msg = Logging.getMessage("HTTP.UnknownMethod", this.method);
                Logging.logger().severe(msg);
                throw new IOException(msg);
            }

            if (!(WMSSecurity.getSecurityManager() instanceof AllTrustedSecurityManager))
            {
                this.extractClientIpAddress(this.clientSocket);
            }
        }
        finally
        {
            this.closeInputStreamOnly(this.clientSocket);
        }
    }

    protected void doGet(BufferedReadableByteChannel channel) throws IOException
    {
        // break apart request and the query-string...
        int i = this.url.indexOf('?');
        if (i >= 0 && (i + 1) != url.length())
        {
            this.query = HTTP.decodeEscapes(url.substring(i + 1));
            this.url = this.url.substring(0, i);
        }

        // Gather up the headers. For now, we'll ignore the body...
        while (true)
        {
            String line = channel.readLine();
            if (WWUtil.isEmpty(line))
            {
                break;
            }
            this.extractHeaderKeyValue(line);
        }
    }


    protected void extractHeaderKeyValue(String line)
    {
        int idx = (null != line) ? line.indexOf(':') : -1;
        if (idx > 1 && null != line)
        {
            String hdrName = line.substring(0, idx);
            String hdrValue = line.substring(idx + 1);

            if (null != hdrName && 0 < hdrName.trim().length() && null != hdrValue || 0 < hdrValue.trim().length())
            {
                hdrName = hdrName.trim().toUpperCase();
                hdrValue = hdrValue.trim();
                this.headers.setValue(hdrName, hdrValue);
            }
        }
    }

    protected void closeInputStreamOnly(Socket s)
    {
        if (null != s && !s.isInputShutdown())
        {
            try
            {
                s.shutdownInput();
            }
            catch (IOException e)
            {
                String message = Logging.getMessage("generic.ExceptionClosingStream", e.getMessage());
                Logging.logger().finest(message);
            }
        }
    }

    public ServerApplication getServerApplication()
    {
        return this.serverApp;
    }

    protected void extractClientIpAddress(Socket s)
    {
        String x_forwarded_for = null;

        if (this.headers.hasKey(HTTP.Header.X_FORWARDED_FOR))
        {
            x_forwarded_for = this.headers.getStringValue(HTTP.Header.X_FORWARDED_FOR);
        }

        if (this.headers.hasKey(HTTP.Header.HTTP_X_FORWARDED_FOR))
        {
            x_forwarded_for = this.headers.getStringValue(HTTP.Header.HTTP_X_FORWARDED_FOR);
        }

        else if (this.headers.hasKey(HTTP.Header.HTTP_CLIENT_IP))
        {
            x_forwarded_for = this.headers.getStringValue(HTTP.Header.HTTP_CLIENT_IP);
        }

        else if (this.headers.hasKey(HTTP.Header.REMOTE_ADDR))
        {
            x_forwarded_for = this.headers.getStringValue(HTTP.Header.REMOTE_ADDR);
        }

        else
        {
            if (null != s)
            {
                InetAddress address = s.getInetAddress();
                x_forwarded_for = (null != address) ? address.getHostAddress() : null;
            }
        }

        if (null != x_forwarded_for && x_forwarded_for.indexOf(",") > -1)
        {
            // sometimes SQUID has multiple IP addresses separated by comma
            // we need to use the most right IP address
            String[] ips = x_forwarded_for.split(",");
            if (null != ips && ips.length > 1)
            {
                String msg = Logging.getMessage("WMS.Security.MultipleIpAddressesDetected", x_forwarded_for);
                Logging.logger().finest(msg);

                for (int k = ips.length - 1; k >= 0; k--)
                {
                    String host = ips[k].trim();
                    if (null != host && 0 < host.length())
                    {
                        try
                        {
                            InetAddress address = InetAddress.getByName(host);
                            x_forwarded_for = (null != address) ? address.getHostAddress() : null;
                            break;
                        }
                        catch (Exception e)
                        {
                            Logging.logger().finest(e.getMessage());
                        }
                    }
                }
            }
        }

        if (null != x_forwarded_for)
        {
            this.headers.setValue(HTTP.Header.X_FORWARDED_FOR, x_forwarded_for);
        }
    }

    public String getHeader(String hdrName)
    {
        return (null != hdrName && 0 != hdrName.length()) ? this.headers.getStringValue(hdrName.toUpperCase()) : null;
    }

    public String getParameter(String name)
    {
        if (this.query == null || name == null || name.length() == 0)
        {
            return null;
        }

        if (this.parameters == null)
        {
            extractParameters();
        }

        // recall that the spec states parameter names shall be case insensitive...
        return this.parameters.get(name.toUpperCase());
    }

    public String[] getParameterValues(String name)
    {
        String param = getParameter(name);
        if (param == null)
        {
            return null;
        }
        if (param.equals(""))
        {
            return new String[1];
        }

        StringTokenizer parser = new StringTokenizer(param, ",");
        int numValues = parser.countTokens();
        String[] values = new String[numValues];
        int i = 0;
        while (parser.hasMoreTokens())
        {
            values[i++] = parser.nextToken();
        }
        return values;
    }

    public String getMethod()
    {
        return this.method;
    }

    public String getUrl()
    {
        return this.url;
    }

    /**
     * Gets an AVList with any HTTP data that was passed with the HTTP request
     * @return AVList that contains HTTP data passed with the HTTP requests; null if no data
     */
    public AVList getData()
    {
        return this.data;
    }

    public Enumeration getParameterNames()
    {
        if (this.parameters == null)
        {
            extractParameters();
        }
        return new WrappedIterator(this.parameters.keySet());
    }

    public String getQueryString()
    {
        return this.query;
    }

    /**
     * Convenience method to pull out all query parameters.
     */
    private void extractParameters()
    {
        this.parameters = new HashMap<String, String>();

        StringTokenizer parser = new StringTokenizer(this.query, "&");
        while (parser.hasMoreTokens())
        {
            String param = parser.nextToken();
            int i = param.indexOf('=');
            if (i < 0)
            {
                continue;  // silently ignore bogus stuff here (may be revealed as error later)
            }

            String value = param.substring(i + 1);
            param = param.substring(0, i).toUpperCase(); // spec says case-insensitive
            this.parameters.put(param, value);
        }
    }

    //
    // A utility class to bend an Iterator into an Enumeration.
    //

    private class WrappedIterator implements Enumeration<String>
    {
        private Iterator<String> iterator;

        public WrappedIterator(Set<String> s)
        {
            this.iterator = s.iterator();
        }

        public boolean hasMoreElements()
        {
            return this.iterator.hasNext();
        }

        public String nextElement()
        {
            return this.iterator.next();
        }
    }

    @Override
    public java.lang.String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("url=").append(this.url).append('\n');
        sb.append("method=").append(this.method).append('\n');
        sb.append("query=").append(this.query).append('\n');

        if (!WWUtil.isEmpty(this.parameters))
        {
            sb.append("parameters={");
            for (Map.Entry<String, String> pair : this.parameters.entrySet())
            {
                sb.append(pair.getKey()).append('=').append(pair.getValue()).append(", ");
            }
            sb.append("} \n");
        }

        if (!WWUtil.isEmpty(this.headers))
        {
            sb.append("headers={");
            for (Map.Entry<String, Object> e : this.headers.getEntries())
            {
                sb.append(e.getKey()).append("=").append(e.getValue()).append(", ");
            }
            sb.append("} \n");
        }

        return sb.toString();
    }

    public void addStats(Object o, long msec)
    {
        if (null != o && this.stats != null)
        {
            this.stats.append(o).append(":").append(msec).append("ms,");
        }
    }

    public String getStats()
    {
        if (this.stats == null)
        {
            return "";
        }

        if (this.stats.length() > 0 && this.stats.charAt(this.stats.length() - 1) == ',')
        {
            this.stats.deleteCharAt(this.stats.length() - 1);
        }

        return "{ " + this.stats.toString() + " }";
    }

    protected void doPost(BufferedReadableByteChannel channel) throws IOException, InterruptedException
    {
        String postBoundary = null, filename = null;

        for (; ;)
        {
            String line = channel.readLine();
//            System.out.println(line);

            if (line.indexOf("Content-Type: multipart/form-data") != -1)
            {
                Boolean allow = AVListImpl.getBooleanValue(this.serverApp, AVKey.HTTP_SERVER_ALLOW_FILE_UPLOAD, Boolean.FALSE);
                if (!allow)
                {
                    String msg = Logging.getMessage("HTTP.MethodNotAllowed", "HTTP_FILE_UPLOAD");
                    Logging.logger().severe(msg);
                    throw new IOException(msg);
                }

                this.data = new AVListImpl();

                postBoundary = line.split("boundary=")[1];
                // The POST boundary

                for (; ;)
                {
                    line = channel.readLine();
                    if (line.indexOf("Content-Length:") != -1)
                    {
                        Long contentLength = WWUtil.convertStringToLong(line.split(" ")[1]);
                        if (null == contentLength || contentLength <= 0L)
                        {
                            String msg = Logging.getMessage("generic.MissingRequiredParameter", "Content-Length");
                            Logging.logger().severe(msg);
                            throw new IOException(msg);
                        }

                        Long maxSize = AVListImpl.getLongValue(this.serverApp, AVKey.HTTP_SERVER_FILE_UPLOAD_MAX_SIZE, 0L);
                        if (maxSize != 0L && contentLength > maxSize)
                        {
                            String msg = Logging.getMessage("generic.MissingRequiredParameter", "Content-Length");
                            Logging.logger().severe(msg);
                            throw new IOException(msg);
                        }

                        break;
                    }
                }

                for (; ;)
                {
                    line = channel.readLine();
//                    System.out.println(line);
                    if (line.indexOf("--" + postBoundary) == -1)
                    {
                        continue;
                    }

                    line = channel.readLine();
                    if ("".equals(line))
                    {
                        continue;
                    }

                    if (line.startsWith("Content-Disposition: attachment; filename="))
                    {
                        filename = line.split("filename=")[1].replaceAll("\"", "");
                        // TODO check for both slashes - forward slash and backward slash
                        String[] filelist = filename.split("\\" + System.getProperty("file.separator"));
                        filename = filelist[filelist.length - 1];
                        filename = filename.trim();
                        this.data.setValue(AVKey.FILE_NAME, filename);
                        break;
                    }
                    else if (line.startsWith("Content-Disposition: form-data; name="))
                    {
                        String name = line.split("name=")[1].replaceAll("\"", "");
                        line = channel.readLine(); // read empty line (CRLF)
//                        Assert.that("".equals(line), "CRLF missing");
                        String value = channel.readLine(); // read value
                        if (!WWUtil.isEmpty(name) && !WWUtil.isEmpty(value))
                        {
                            this.data.setValue(name.trim(), value.trim());
                        }
                        continue;
                    }
                    else
                    {
                        Logging.logger().finest(line);
                    }
                }

                for (; ;)
                {
                    line = channel.readLine();
                    if (WWUtil.isEmpty(line))
                    {
                        // OK, the beginning of the binary file found
                        // TODO : assert(readLine(inFromClient).equals("")) : "Expected line in POST request is "" ";
                        break;
                    }

                    if (line.startsWith("Content-type:") || line.startsWith("Content-Type:"))
                    {
                        String fileContentType = null;
                        fileContentType = line.split(" ")[1];
//                        System.out.println("File content type = " + fileContentType);
                    }
                }

                // Check if server app is configured to require AuthToken
                String appAuthToken = AVListImpl.getStringValue(this.serverApp, AVKey.HTTP_FILE_UPLOAD_AUTH_TOKEN, "");
                if( !WWUtil.isEmpty(appAuthToken) )
                {
                    String reqAuthToken = AVListImpl.getStringValue(this.data, WSS.Param.AUTH_TOKEN, "" );
                    if( !appAuthToken.equals(reqAuthToken) )
                    {
                        String msg;
                        if(WWUtil.isEmpty(reqAuthToken))
                        {
                            msg = Logging.getMessage("generic.MissingRequiredParameter", WSS.Param.AUTH_TOKEN);
                        }
                        else
                        {
                            msg = Logging.getMessage("WMS.Security.AccessDenied", reqAuthToken, WSS.Param.AUTH_TOKEN );
                        }
                        Logging.logger().severe(msg);
                        throw new IOException(msg);
                    }
                }

                byte[] separator = ("\r\n--" + postBoundary + "--").getBytes();

                File tempFolder = new File(ApplicationServer.getTempDirectory());
                if( !tempFolder.exists() )
                    tempFolder.mkdirs();
                tempFolder.setWritable(true,false);

                File tempFile = TempFile.createTempFile(filename, ".tmp", tempFolder );
                tempFile.setWritable(true,false);

                FileOutputStream fos = null;
                try
                {
                    // write to a temp file first and
                    // let application define where to move the file for its final destination

                    fos = new FileOutputStream(tempFile);

                    long totalBytes = channel.readChannelAndWriteTo(new BufferedOutputStream(fos), separator);
                    WWIO.closeStream(fos, filename );

                    if (tempFile.exists() && tempFile.length() == totalBytes)
                    {
                        this.data.setValue(AVKey.FILE_NAME, filename);
                        this.data.setValue(AVKey.FILE, tempFile);
                        this.data.setValue(AVKey.FILE_SIZE, tempFile.length() );

                        String message  = Logging.getMessage("HTTP.FileUploadedOK", filename);
                        Logging.logger().finest(message);
                    }
                    else
                    {
                        String reason = Logging.getMessage("term.unknown");

                        if( null != tempFile )
                        {
                            if( tempFile.exists() )
                                FileUtil.deleteFile(tempFile);

                            if( tempFile.length() != totalBytes )
                                reason = "" + tempFile.length() + " != " + totalBytes ;
                        }

                        String message  = Logging.getMessage("HTTP.FileUploadFailed", filename, reason );
                        Logging.logger().finest(message);
                    }
                }
                finally
                {
                    WWIO.closeStream(fos, filename);
                }
                break;
            }
        }
    }
}
