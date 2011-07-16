/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.utilities;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

/**
 * @author brownrigg
 * @version $Id$
 */

public class WaveletCodec
{
    private int resolutionX;
    private int resolutionY;
    private EncodingType imageType;
    private byte[][] xform;


    public byte[] getBandData( int band )
    {
        if( null != this.xform && this.xform.length > band )
            return this.xform[ band ];
        else
            return null;
    }

    /**
     * A suggested filename extension for wavelet-encodings.
     */
    public static final String WVT_EXT = ".wvt";

    /**
     * Loads a previously persisted wavelet encoding from the given file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static WaveletCodec loadFully(File file) throws IOException
    {

        DataInputStream inp = null;
        try
        {
            inp = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file)));

            WaveletCodec codec = new WaveletCodec();
            codec.resolutionX = inp.readInt();
            codec.resolutionY = inp.readInt();
            int imageType = inp.readInt();
            int numBands = inp.readInt();
            codec.xform = new byte[numBands][codec.resolutionX * codec.resolutionY];
            for (int k = 0; k < numBands; k++)
            {
                inp.readFully(codec.xform[k]);
            }

            return codec;
        }
        finally
        {
            if (inp != null)
                inp.close();
        }
    }

    /**
     * Partially loads a previously persisted wavelet encoding from the given file, upto the given resolution.
     *
     * @param file
     * @param resolution
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static WaveletCodec loadPartially(File file, int resolution) throws IOException, IllegalArgumentException
    {
        if (!WaveletCodec.isPowerOfTwo(resolution))
            throw new IllegalArgumentException("WaveletCodec.loadPartially(): input resolution not a power of two.");

        // NOTE: the try-finally clause was introduced because we had observed cases where, if an
        // exception was thrown during the read, the file would remain open, eventually leading
        // to the process exceeding its maximum open files.
        RandomAccessFile inp = null;
        try
        {
            inp = new RandomAccessFile(file, "r");

            WaveletCodec codec = new WaveletCodec();
            codec.resolutionX = inp.readInt();
            codec.resolutionY = inp.readInt();
            if (resolution > codec.resolutionX || resolution > codec.resolutionY)
                throw new IllegalArgumentException(
                        "WaveletCodec.loadPartially(): input resolution greater than encoded image");

            int imageType = inp.readInt();
            int numBands = inp.readInt();
            codec.xform = new byte[numBands][resolution * resolution];
            for (int k = 0; k < numBands; k++)
            {
                inp.seek(4 * (Integer.SIZE / Byte.SIZE) + k * (codec.resolutionX * codec.resolutionY));
                inp.readFully(codec.xform[k]);
            }

            return codec;
        }
        finally
        {
            if (inp != null)
                inp.close();
        }
    }

    /**
     * Creates a wavelet encoding from the given BufferedImage. The image must have dimensions that are a power of 2. If
     * the incoming image has at least 3 bands, the first three are assumed to be RGB channels. If only one-band, it is
     * assumed to be grayscale. The SampleModel component-type must be BYTE.
     *
     * @param image
     * @return
     * @throws IllegalArgumentException
     */
    public static WaveletCodec encode(BufferedImage image) throws IllegalArgumentException
    {

        if (image == null)
            throw new IllegalArgumentException("WaveletCodec.encode: null image");

        // Does image have the required resolution constraints?
        int xRes = image.getWidth();
        int yRes = image.getHeight();
        if (!isPowerOfTwo(xRes) || !isPowerOfTwo(yRes))
            throw new IllegalArgumentException("Image dimensions are not a power of 2");

        // Try to determine image type...
        SampleModel sampleModel = image.getSampleModel();
        int numBands = sampleModel.getNumBands();
        if (!(numBands == 1 || numBands == 3) || sampleModel.getDataType() != DataBuffer.TYPE_BYTE)
            throw new IllegalArgumentException(
                    "Image is not of BYTE type, or not recognized as grayscale or RGB (alpha-channel is not supported)");

        // Looks good to go;  grab the image data.  We'll need to make a copy, as we need some
        // temp working space and we don't want to corrupt the BufferedImage's data...

        int bandSize = xRes * yRes;
        int next = 0;
        Raster rast = image.getRaster();
        float[] dataElems = new float[numBands];
        float[][] imageData = new float[numBands][bandSize];

        for (int j = 0; j < yRes; j++)
        {
            for (int i = 0; i < xRes; i++)
            {
                rast.getPixel(i, j, dataElems);
                for (int k = 0; k < numBands; k++)
                {
                    imageData[k][next] = dataElems[k];
                }
                ++next;
            }
        }

        // Our return WaveletCodec...
        WaveletCodec codec = new WaveletCodec();
        codec.resolutionX = xRes;
        codec.resolutionY = yRes;
        codec.imageType = (numBands == 1) ? EncodingType.GRAY_SCALE : EncodingType.COLOR_RGB;
        codec.xform = new byte[numBands][bandSize];

        Thread[] threads = new WaveletEncodingThread[numBands];

        for (int i = 0; i < numBands; i++)
        {
            threads[i] = new WaveletEncodingThread( codec, imageData[i], i, xRes, yRes );
            threads[i].start();
        }

        for (Thread t : threads)
        {
            try
            {
                t.join();
            }
            catch (InterruptedException ignore)
            {
            }
        }

        // Done!
        return codec;
    }


    /**
     * Reconstructs an image from this wavelet encoding at the given resolution. The specified resolution must be a
     * power of two, and must be less than or equal to the resolution of the encoding.
     * <p/>
     * This reconstruction algorithm was hinted at in:
     * <p/>
     * "Principles of Digital Image Synthesis" Andrew Glassner 1995, pp. 296
     *
     * @param resolution
     * @return reconstructed image.
     * @throws IllegalArgumentException
     */
    public BufferedImage reconstruct(int resolution) throws IllegalArgumentException
    {

        if (!isPowerOfTwo(resolution))
            throw new IllegalArgumentException("Image dimensions are not a power of 2");


        // Allocate memory for the BufferedImage
        int numBands = this.xform.length;
        byte[][] imageBytes = new byte[numBands][this.resolutionX * this.resolutionY];

        Thread[] threads = new ReconstructionThread[numBands];

        for (int i = 0; i < numBands; i++)
        {
            threads[i] = new ReconstructionThread(xform[i], imageBytes[i], resolution);
            threads[i].start();
        }


        for (Thread t : threads)
        {
            try
            {
                t.join();
            }
            catch (InterruptedException ignore)
            {
            }
        }


        // Finally, construct a BufferedImage...
        BandedSampleModel sm = new BandedSampleModel(DataBuffer.TYPE_BYTE, resolution, resolution, numBands);
        DataBufferByte dataBuff = new DataBufferByte(imageBytes, imageBytes[0].length);
        WritableRaster rast = Raster.createWritableRaster(sm, dataBuff, new Point(0, 0));
        int imageType = (numBands == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_RGB;
        BufferedImage image = new BufferedImage(resolution, resolution, imageType);
        image.getRaster().setRect(rast);

        return image;
    }

    /**
     * Saves this wavelet encoding to the given File.
     *
     * @param file
     * @throws IOException
     */
    public void save(File file) throws IOException
    {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeInt(this.resolutionX);
        out.writeInt(this.resolutionY);
        out.writeInt(this.imageType.getTag());
        out.writeInt(this.xform.length);
        for (int k = 0; k < this.xform.length; k++)
        {
            out.write(this.xform[k]);
        }
        out.close();
    }

    /**
     * Returns the resolution of this wavelet encoding.
     *
     * @return resolution
     */
    public int getResolutionX()
    {
        return this.resolutionX;
    }

    /**
     * Returns the resolution of this wavelet encoding.
     *
     * @return resolution
     */
    public int getResolutionY()
    {
        return this.resolutionY;
    }

    /**
     * Convenience method for testing is a value is a power of two.
     *
     * @param value
     * @return
     */
    public static boolean isPowerOfTwo(int value)
    {
        return (value == nearestPowerOfTwo(value)) ? true : false;
    }

    /**
     * Returns a resolution value that is the nearest power of 2 greater than or equal to the given value.
     *
     * @param resolution
     * @return power of two resolution
     */
    public static int nearestPowerOfTwo(int resolution)
    {
        int power = (int) Math.ceil(Math.log(resolution) / Math.log(2.));
        return (int) Math.pow(2., power);
    }

    /**
     * Convenience method to compute the log-2 of a value.
     *
     * @param value
     * @return
     */
    public static double logBase2(double value)
    {
        return Math.log(value) / Math.log(2.);
    }

    public enum EncodingType
    {
        GRAY_SCALE(0x67726179),  // ascii "gray"
        COLOR_RGB(0x72676220);   // ascii "rgb "

        private EncodingType(int tag)
        {
            this.tag = tag;
        }

        public int getTag()
        {
            return this.tag;
        }

        private int tag;
    }


    public class ReconstructionThread extends Thread
    {
        int resolution;
        byte[] xsrc;
        byte[] dest;


        public ReconstructionThread(byte[] xsrc, byte[] dest, int resolution)
        {
            this.resolution = resolution;
            this.xsrc = xsrc;
            this.dest = dest;

        }


        public void run()
        {
            int scale = 1;
            int offset = 1;
            int size = this.resolution * this.resolution;

            int[] imageData = new int[size];

            // we need working buffers as large as 1/2 the output resolution...
            // Note how these are named after Glassner's convention...
            int qsize = (resolution / 2) * (resolution / 2);
            int[] A = new int[qsize];
            int[] D = new int[qsize];
            int[] V = new int[qsize];
            int[] H = new int[qsize];


            // Prime the process. Recall that the first byte of each channel is a color value, not
            // signed coefficients. So treat it as an unsigned value.
            imageData[0] = 0x000000ff & this.xsrc[0];

            do
            {
                // load up our A,D,V,H component arrays...
                int numVals = scale * scale;
                if (numVals >= resolution * resolution)
                    break;

                int next = 0;
                for (int j = 0; j < scale; j++)
                {
                    for (int i = 0; i < scale; i++, next++)
                    {
                        A[next] = imageData[j * resolution + i];
                    }
                }
                for (int i = 0; i < numVals; i++, offset++)
                {
                    H[i] = this.xsrc[offset];
                }
                for (int i = 0; i < numVals; i++, offset++)
                {
                    V[i] = this.xsrc[offset];
                }
                for (int i = 0; i < numVals; i++, offset++)
                {
                    D[i] = this.xsrc[offset];
                }

                next = 0;
                for (int j = 0; j < scale; j++)
                {
                    int jj = 2 * j * resolution;

                    for (int i = 0; i < scale; i++, next++)
                    {
                        int idx = jj + (i * 2);

//                        int a = A[next] + H[next] + V[next] + D[next];
//                        int b = A[next] - H[next] + V[next] - D[next];
//                        int c = A[next] + H[next] - V[next] - D[next];
//                        int d = A[next] - H[next] - V[next] + D[next];
                        imageData[idx] = A[next] + H[next] + V[next] + D[next];
                        imageData[idx + 1] = A[next] - H[next] + V[next] - D[next];
                        imageData[idx + resolution] = A[next] + H[next] - V[next] - D[next];
                        imageData[idx + resolution + 1] = A[next] - H[next] - V[next] + D[next];
                    }
                }

                scale *= 2;
            }
            while (scale < resolution);

            // Copy to bytes and clamp to byte-range...
            for (int i = 0; i < size; i++)
            {
                int v = imageData[i];
                this.dest[i] = (byte) ((v > 255) ? 255 : ((v < 0) ? 0 : v));

                // To enable Auto Contrast
//                float d = imageData[i] / 255f;
//                d = 255f * (d * (3 * d - 2 * d * d ));
//                this.imageBytes[i] = (byte) (( d > 255f ) ? 255 : ((d < 0f) ? 0 : ((byte)d & 0xFF)));
            }
        }
    }
}
