/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import gov.nasa.worldwind.formats.tiff.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class TiffImageFormatter extends ImageFormatter
{

    public TiffImageFormatter(File f)
    {
        this.sourceFile = f;
    }

    public InputStream asTiff() throws IOException
    {
        BufferedInputStream inp = new BufferedInputStream(new FileInputStream(this.sourceFile));
        return inp;
    }

    public BufferedImage toIntermediateForm() throws IOException
    {
        GeotiffReader reader = new GeotiffReader(this.sourceFile);
        return reader.read();
    }

    private File sourceFile;
}