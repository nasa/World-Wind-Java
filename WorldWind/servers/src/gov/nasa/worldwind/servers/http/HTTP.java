/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.http;

import gov.nasa.worldwind.servers.wms.formats.ImageTypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class HTTP
{
    public static final String GET = "GET";
    public static final String POST = "POST";

    private HTTP()
    {
    }

    // empirically determined size for typical DDS requests.
    public static final int MAXIMUM_BUF_SIZE = 146808;
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static boolean isGET(String requestType)
    {
        return HTTP.GET.equalsIgnoreCase(requestType);
    }

    public static boolean isPOST(String requestType)
    {
        return HTTP.POST.equalsIgnoreCase(requestType);
    }

    public class Header
    {
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String VARY = "Vary";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String COMPRESSED_ENCODING = "compress";
        public static final String GZIPPED_ENCODING = "gzip";

        public static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
        public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
        public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
        public static final String REMOTE_ADDR = "REMOTE_ADDR";
    }

    // Returns an appropriate InputStream representing the response payload bytes.
    // Either returns the given InputStream or one that contains a compressed encoding
    // of the original.
    // As a heuristic, we don't bother to compress PNGs nor JPEGs, as they don't compress well,
    // and not worth the computational expense.
    //

    public static InputStream encodePayload(InputStream payload,
                                            String mimeType,
                                            HTTPRequest req,
                                            HTTPResponse resp)
            throws IOException
    {
        if (ImageTypes.PNG.mimeType.equalsIgnoreCase(mimeType)
            || ImageTypes.JPEG.mimeType.equalsIgnoreCase(mimeType)
                )
        {
            return payload;
        }

        if (null == req || null == resp)
        {
            return payload;
        }

        String hdr = req.getHeader(HTTP.Header.ACCEPT_ENCODING);
        if (hdr == null)
        {
            return payload;
        }

        // For now, we ignore the presence of any q-values;
        // just pick whichever compression scheme we happen to find first...
        DeflaterOutputStream out = null;
        ByteArrayOutputStream bytes = null;
        if (hdr.contains(HTTP.Header.COMPRESSED_ENCODING))
        {
            bytes = new ByteArrayOutputStream(MAXIMUM_BUF_SIZE);
            out = new DeflaterOutputStream(bytes);
            resp.addHeader(HTTP.Header.CONTENT_ENCODING, HTTP.Header.COMPRESSED_ENCODING);
        }
        else if (hdr.contains(HTTP.Header.GZIPPED_ENCODING))
        {
            bytes = new ByteArrayOutputStream(MAXIMUM_BUF_SIZE);
            out = new GZIPOutputStream(bytes);
            resp.addHeader(HTTP.Header.CONTENT_ENCODING, HTTP.Header.GZIPPED_ENCODING);
        }

        if (out == null)
        {
            return payload;
        }

        int bufSize = resp.getSendBufferSize();
        bufSize = (bufSize > MAXIMUM_BUF_SIZE) ? MAXIMUM_BUF_SIZE : ((bufSize <= 0) ? DEFAULT_BUFFER_SIZE : bufSize);
        if (bufSize != resp.getSendBufferSize())
        {
            resp.setSendBufferSize(bufSize);
        }

        // otherwise, copy (filtered) bytes...
        byte[] buff = new byte[bufSize];
        int len;
        while ((len = payload.read(buff, 0, bufSize)) != -1)
        {
            out.write(buff, 0, len);
        }
        out.close();

        return new ByteArrayInputStream(bytes.toByteArray());
    }

    /**
     * Decodes escape sequences in the query string.  NOTE that although there is a class java.net.URLDecoder that can
     * do this, it appears to adhere to strict rules about what are legal characters in a URL. Evidently our clients may
     * not always generate such URLs -- in particular a WMS request might specify a format as "image/png" rather than
     * the properly escaped "image%2Fpng". URLDecode will throw an exception in such a case;  we loosen the restrictions
     * here.
     */
    private static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                      'a', 'b', 'c', 'd', 'e', 'f'};

    public static String decodeEscapes(String url)
    {
        char[] hexStr = new char[3];  // need these two bookkeeping arrays below
        byte[] b = new byte[1];

        StringBuilder str = new StringBuilder(url);
        int i = -1;
        while (++i < str.length())
        {
            hexStr[0] = str.charAt(i);
            if (hexStr[0] == '%')
            {
                if ((i + 2) >= str.length())
                {
                    continue;
                }
                hexStr[1] = Character.toLowerCase(str.charAt(i + 1));
                hexStr[2] = Character.toLowerCase(str.charAt(i + 2));
                int hiVal = -1;
                int loVal = -1;
                for (int j = 0; j < hexChars.length && (hiVal == -1 || loVal == -1); j++)
                {
                    if (hexStr[1] == hexChars[j])
                    {
                        hiVal = j;
                    }
                    if (hexStr[2] == hexChars[j])
                    {
                        loVal = j;
                    }
                }
                if (hiVal != -1 && loVal != -1)
                {
                    hexStr[0] = '#';  // replace % by # for proper Java hex notation...
                    b[0] = Byte.decode(new String(hexStr));
                    str.replace(i, i + 3, new String(b));
                }
            }
        }

        return str.toString();
    }
}
