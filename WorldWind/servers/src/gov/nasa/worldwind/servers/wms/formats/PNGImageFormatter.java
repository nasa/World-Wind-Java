/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class PNGImageFormatter extends ImageFormatter
{

    public PNGImageFormatter(File f)
    {
        this.sourceFile = f;
    }

    public InputStream asPng() throws IOException
    {
        BufferedInputStream inp = new BufferedInputStream(new FileInputStream(this.sourceFile));
        return inp;
    }

    public BufferedImage toIntermediateForm() throws IOException
    {
        return ImageIO.read(this.sourceFile);
    }

    private File sourceFile;
}
