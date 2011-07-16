/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools;

import gov.nasa.worldwind.servers.wms.utilities.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author brownrigg
 * @version $Id$
 */


/**
 *          <p/>
 *          WaveletEncoder
 *          <p/>
 *          This is a utility program to wavelet-encode an image. The image formats supported are precisely those that
 *          can be read by Java's ImageIO API. By default, the input image resampled to 1024 x 1024 before the encoding
 *          is applied.
 *          <p/>
 *          <p/>
 *          Two switches alter the default behaviors:
 *          <p/>
 *          -r xres yres
 *          <p/>
 *          Downsamples the image to the given resolution. xres and yres must be powers of two.
 *          <p/>
 *          -d outputDirectory
 *          <p/>
 *          Writes the encoded results rooted at the given directory, rather than along side of the original image.
 *          Directories are created below this directory that parallel those of the source directories. This may be
 *          useful in cases where the source images are kept on read-only media.
 */

public class WaveletEncoder
{

    public static void main(String[] args)
    {
        try
        {

            List<String> imagefiles = parseArgs(args);
            for (String imagefile : imagefiles)
            {
                GenerateWaveletFile(imagefile);
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }
    }

    private static void GenerateWaveletFile(String imagefile) throws Exception
    {
        System.out.print("\nEncoding: " + imagefile + "...");

        // Read the image...
        BufferedImage sourceImage = ImageIO.read(new File(imagefile));

        // Down sample the source image to our desired transform resolution...
        int imageType = (sourceImage.getType() == BufferedImage.TYPE_BYTE_GRAY) ?
            BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage scaledImage = new BufferedImage(xResolution, yResolution, imageType);

        Graphics2D g2d = (Graphics2D) scaledImage.getGraphics();
        g2d.scale((double) xResolution / (double) sourceImage.getWidth(),
            (double) yResolution / (double) sourceImage.getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        long begTime = System.currentTimeMillis();
        g2d.drawImage(sourceImage, 0, 0, null);

        // perform our wavelet transform...
        //
        long scaleTime = System.currentTimeMillis();
        WaveletCodec codec = WaveletCodec.encode(scaledImage);
        long xformTime = System.currentTimeMillis();

        // Write the transform...
        //
        File outFilename;
        if (outputRootDir != null)
        {
            outFilename = new File(outputRootDir + File.separator + imagefile + WaveletCodec.WVT_EXT);
            outFilename.getParentFile().mkdirs();
        }
        else
            outFilename = new File(imagefile + WaveletCodec.WVT_EXT);
        codec.save(outFilename);
        long saveTime = System.currentTimeMillis();

        System.out.println("scaling: " + (scaleTime - begTime) + "  xform: " + (xformTime - scaleTime) +
            "  save: " + (saveTime - xformTime) + "  (millisec.)");
    }

    private static List<String> parseArgs(String[] args)
    {
        ArrayList<String> list = new ArrayList<String>();

        int next = 0;
        while (next < args.length)
        {
            String arg = args[next++];
            if ("-r".equals(arg))
            {
                try
                {
                    xResolution = Integer.parseInt(args[next++]);
                    yResolution = Integer.parseInt(args[next++]);
                    if (!WaveletCodec.isPowerOfTwo(xResolution) || !WaveletCodec.isPowerOfTwo(yResolution))
                        throw new IllegalArgumentException();
                }
                catch (Exception ex)
                {
                    System.err.println("The pair of arguments following the \"-r\" switch specify the resolution ");
                    System.err.println("of the wavelet encodings, and must be powers of two.");
                    printUsage();
                    System.exit(1);
                }
            }
            else if ("-d".equals(arg))
            {
                outputRootDir = args[next++];
            }
            else
                list.add(arg);
        }

        if (list.size() == 0)
        {
            printUsage();
            System.exit(1);
        }

        return list;
    }

    private static void printUsage()
    {
        System.err.println("\nCreates wavelet encodings of image files:");
        System.err.println("Usage: " +
            WaveletEncoder.class.getName() +
            " {-r xres yres} {-d outputRootDirectory} imagefile ... ");
    }

    private static int xResolution = 1024;
    private static int yResolution = 1024;
    private static String outputRootDir = null;
}
