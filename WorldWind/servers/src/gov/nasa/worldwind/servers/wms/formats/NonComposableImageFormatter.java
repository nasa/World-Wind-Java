/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.formats;

import gov.nasa.worldwind.util.Logging;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.net.URL;

/**
 * @author dcollins
 * @version $Id$
 */
public class NonComposableImageFormatter extends ImageFormatter
{
    protected URL url;

    public NonComposableImageFormatter(URL url)
    {
        if (url == null)
        {
            String message = Logging.getMessage("nullValue.URLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.url = url;
    }

    public InputStream getStreamFromMimeType(String mimeType, Properties properties) throws IOException
    {
        return this.url.openStream();
    }

    public BufferedImage toIntermediateForm() throws IOException
    {
        throw new IOException("Image cannot be translsated");
    }
}
