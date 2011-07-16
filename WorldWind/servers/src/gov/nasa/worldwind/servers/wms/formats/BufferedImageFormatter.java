/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import java.awt.image.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class BufferedImageFormatter extends ImageFormatter
{


    public BufferedImageFormatter(BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage toIntermediateForm()
    {
        return image;
    }

    private BufferedImage image;

    public BufferedImage getImage()
    {
        return this.image; 
    }
}
