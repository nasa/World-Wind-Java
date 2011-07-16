/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.utilities;

/**
 * @author garakl
 * @version $Id$
 */

class WaveletEncodingThread extends Thread
{
    protected WaveletCodec codec = null;
    protected float[] imageData = null;
    protected int width = 0;
    protected int height = 0;
    protected int band = 0;

    public WaveletEncodingThread(WaveletCodec codec, float[] imageData, int band, int width, int height)
    {
        this.codec = codec;
        this.imageData = imageData;
        this.width = width;
        this.height = height;
        this.band = band;
    }

    public void run()
    {
        // Perform the transformation...
        int level = 0;
        int xformXres = width;
        int xformYres = height;
        // We need some temporary work space the size of the image...
        float[] workspace = new float[width * height];

        while (true)
        {
            ++level;

            if (!(xformXres > 0 || xformYres > 0))
            {
                break;
            }
            int halfXformXres = xformXres / 2;
            int halfXformYres = xformYres / 2;

            // transform along the rows...
            for (int j = 0; j < xformYres; j++)
            {

                // IMPORTANT THAT THIS REFLECT SOURCE IMAGE, NOT THE CURRENT LEVEL!
                int offset = j * height;

                for (int i = 0; i < halfXformXres; i++)
                {
                    int indx1 = offset + i * 2;
                    int indx2 = offset + i * 2 + 1;

                    // horizontally...
                    float average = (imageData[indx1] + imageData[indx2]) / 2f;
                    float detail = imageData[indx1] - average;
                    workspace[offset + i] = average;
                    workspace[offset + i + halfXformXres] = detail;
                }
            }

            // copy transformed data from this iteration back into our source arrays...
            System.arraycopy(workspace, 0, imageData, 0, workspace.length);

            // now transform along columns...
            for (int j = 0; j < xformXres; j++)
            {
                for (int i = 0; i < halfXformYres; i++)
                {
                    int indx1 = j + (i * 2) * this.height;
                    int indx2 = j + (i * 2 + 1) * this.height;

                    // horizontally...
                    float average = (imageData[indx1] + imageData[indx2]) / 2f;
                    float detail = imageData[indx1] - average;
                    workspace[j + i * this.height] = average;
                    workspace[j + (i + halfXformYres) * this.height] = detail;
                }
            }

            xformXres /= 2;
            xformYres /= 2;

            // copy transformed data from this iteration back into our source arrays...
            System.arraycopy(workspace, 0, imageData, 0, workspace.length);
        }


        workspace = null;

        //
        // Rearrange in memory for optimal, hierarchical layout on disk, quantizing down to
        // byte values as we go.
        //

        // NOTE: the first byte of each channel is different; it represents the average color of the
        // overall image, and as such should be an unsigned quantity in the range 0..255.
        // All other values are signed coefficents, so the clamping boundaries are different.

        byte[] xform = codec.getBandData(this.band);

        xform[0] = (byte) Math.min(255, Math.max(0, Math.round(imageData[0])));

        int scale = 1;   // actually inverse of the magnification level...
        int next = 1;
        while (scale < this.width)
        {
            for (int subBlock = 0; subBlock < 3; subBlock++)
            {
                int colOffset = ((subBlock % 2) == 0) ? scale : 0;
                int rowOffset = (subBlock > 0) ? scale * this.width : 0;
                for (int j = 0; j < scale; j++)
                {
                    for (int i = 0; i < scale; i++, next++)
                    {
                        int indx = rowOffset + colOffset + j * this.width + i;

                        xform[next] = (byte) Math.max(Byte.MIN_VALUE,
                                Math.min(Byte.MAX_VALUE, Math.round(imageData[indx])));
                    }
                }
            }
            scale *= 2;
        }
    }
}