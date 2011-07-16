/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools;

import gov.nasa.worldwind.formats.rpf.*;
import gov.nasa.worldwind.servers.wms.utilities.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.*;

/**
 * @author brownrigg
 * @version $Id$
 *          <p/>
 *          WaveletEncodeRPFs
 *          <p/>
 *          This is a utility program to wavelet-encode a collection of RPF files in bulk. One or more command-line
 *          arguments are presumed to be directories that are the root of sets of RPF files. All framefiles encountered
 *          in these directories are encoded, with the results stored along side the framefile in a like-named file with
 *          a ".wvt" extention. The framefiles are downsampled by 1/3 before encoding.
 *          <p/>
 *          Two optional switches alter the default behaviors:
 *          <p/>
 *          -r xres yres
 *          <p/>
 *          Downsamples the framefile to the given resolution. xres and yres must be powers of two.
 *          <p/>
 *          -d outputDirectory
 *          <p/>
 *          Writes the encoded results rooted at the given directory, rather than along side of the original frame
 *          files. Directories are created below this directory that parallel those of the source directories,
 *          preserving the RPF structure. This may be useful in cases where the RPFs are kept on read-only media.
 */

public class WaveletEncodeRPFs
{

    public static void main(String[] args)
    {
        try
        {
            WaveletEncodeRPFs This = new WaveletEncodeRPFs();
            WaveletEncoderListener listener = new WaveletEncoderListener()
            {
                public void status(String msg)
                {
                    System.out.println(msg);
                }
            };
            This.addListener(listener);
            List<String> dirs = This.parseArgs(args);
            for (String dir : dirs)
            {
                This.generateWaveletFiles(dir);
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.toString());
        }
    }

    public WaveletEncodeRPFs()
    {
        dataseries = null;
    }

    public WaveletEncodeRPFs(RPFDataSeries series)
    {
        dataseries = series;
    }

    public void generateWaveletFiles(String rpfRoot) throws Exception
    {
        generateWaveletFiles(rpfRoot, outputRootDir, xResolution, yResolution);
    }

    public void generateWaveletFiles(String rpfRoot, String outputDir) throws Exception
    {
        generateWaveletFiles(rpfRoot, outputDir, xResolution, yResolution);
    }

    public void generateWaveletFiles(String rpfRoot, String outputRootDir, int xResolution, int yResolution)
        throws Exception
    {

        reportProgress("\nWorking on root: " + rpfRoot + "...");

        // load up all the RPF-TOCs we can find...
        DataSeriesGrouper grouper = new DataSeriesGrouper();
        RPFCrawler crawler = new RPFCrawler();
        crawler.invoke(new File(rpfRoot), grouper, false);
        File[] frameFiles = grouper.getFileCollection();
        if (!(frameFiles.length > 0))
        {
            reportProgress("no framefiles found!");
            reportDone();
            return;
        }

        reportProgress(frameFiles.length + " framefiles found");

        // Generate a wavelet-encoding for each framefile...
        for (int i = 0; i < frameFiles.length; i++)
        {

            if (done.get())
                break;
            reportProgress("Encoding " + (i + 1) + " of " + frameFiles.length + " files...");

            File outFilename;
            if (outputRootDir != null)
            {
                String frameFile = frameFiles[i].getAbsolutePath();
                int pos = frameFile.indexOf(rpfRoot);
                String partialPath = frameFile.substring(pos + rpfRoot.length());
                outFilename = new File(outputRootDir + File.separator + partialPath + WaveletCodec.WVT_EXT);
                //DEBUG: System.out.println(outFilename);
                outFilename.getParentFile().mkdirs();
            }
            else
                outFilename = new File(frameFiles[i].getAbsolutePath() + WaveletCodec.WVT_EXT);

            // skip any unnecessary work...
            if (!forceEncoding && outFilename.exists() && (outFilename.lastModified() > frameFiles[i].lastModified()))
                continue;

            // Load the next framefile and get its contents as a BufferedImage...
            BufferedImage sourceImage;
            try
            {  // we'll ignore any issues loading frame files...

                // We need a FrameTransform; reuse these where ever possible...
                RPFFrameFilename ffile = RPFFrameFilename.parseFilename(frameFiles[i].getName().toUpperCase());
                RPFDataSeries dataSeries = RPFDataSeries.dataSeriesFor(ffile.getDataSeriesCode());
                String key = ffile.getZoneCode() + dataSeries.rpfDataType + Double.toString(dataSeries.scaleOrGSD);
                RPFFrameTransform transf = this.frameTransforms.get(key);
                if (transf == null) {
                    transf = RPFFrameTransform.createFrameTransform(ffile.getZoneCode(),
                        dataSeries.rpfDataType, dataSeries.scaleOrGSD);
                    this.frameTransforms.put(key, transf);
                }

                RPFImageFile sourceFile = RPFImageFile.load(frameFiles[i]);
                sourceImage = sourceFile.getBufferedImage();
                RPFFrameTransform.RPFImage[] images = transf.deproject(ffile.getFrameNumber(), sourceImage);
                sourceImage = compositeImages(images);
            }
            catch (Exception ex)
            {
                reportProgress("Failed to load frame file " + frameFiles[i].getCanonicalPath() + ": " + ex.toString());
                continue;
            }

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
            codec.save(outFilename);
            long saveTime = System.currentTimeMillis();

            //System.out.println("scaling: " + (scaleTime - begTime) + "  xform: " + (xformTime - scaleTime) +
            //        "  save: " + (saveTime - xformTime) + "  (millisec.)");

        }

        reportDone();
    }

    private BufferedImage compositeImages(RPFFrameTransform.RPFImage[] images)
    {
        if (images.length == 1)
            return images[0].getImage();

        // NOTE we are using explicit knowledge of the order of the two images produced in the deprojection step...
        BufferedImage westImage = images[0].getImage();
        BufferedImage eastImage = images[1].getImage();
        BufferedImage outImage = new BufferedImage(westImage.getWidth()+eastImage.getWidth(), westImage.getHeight(),
            BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = (Graphics2D) outImage.getGraphics();
        g2d.drawImage(westImage, 0, 0, null);
        g2d.drawImage(eastImage, westImage.getWidth(), 0, null);
        return outImage;
    }

    public void stopEncoding()
    {
        done.set(true);
    }

    private List<String> parseArgs(String[] args)
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

    private void printUsage()
    {
        System.err.println("\nCreates wavelet encodings of RPF files:");
        System.err.println("Usage: " +
            WaveletEncodeRPFs.class.getName() +
            " {-r xres yres} {-d outputRootDirectory} dirname ... ");
    }

    // -----------------------------------------------------------------------
    // An RPFGrouper immplementation that selects only framefiles in the
    // current DataSeries.  Selects all frames files if dataseries is null.
    //
    private class DataSeriesGrouper extends RPFCrawler.RPFGrouper
    {

        public DataSeriesGrouper()
        {
            super(RPFFrameProperty.DATA_SERIES);
            frameFiles = new ArrayList<File>(1000);
        }

        public void addToGroup(Object groupKey, File rpfFile, RPFFrameFilename rpfFrameFilename)
        {
            if (dataseries != null)
            {
                if (dataseries.seriesCode.equals((String) groupKey))
                {
                    frameFiles.add(rpfFile);
//??                    postProgress("Found " + frameFiles.size() + " files...");
                }
            }
            else
            {
                frameFiles.add(rpfFile);
//??                reportProgress("Found " + frameFiles.size() + " files...");
            }
        }

        public File[] getFileCollection()
        {
            File[] files = new File[frameFiles.size()];
            return frameFiles.toArray(files);
        }

        private ArrayList<File> frameFiles;
    }

    public void addListener(WaveletEncoderListener l)
    {
        if (l != null)
            listeners.add(l);
    }

    public void removeListener(WaveletEncoderListener l)
    {
        if (l != null)
            listeners.remove(l);
    }

    private void reportProgress(String msg)
    {
        for (WaveletEncoderListener l : listeners)
        {
            l.status(msg);
        }
    }

    private void reportDone()
    {
        for (WaveletEncoderListener l : listeners)
        {
            l.done = true;
        }
    }

    private int xResolution = 512;
    private int yResolution = 512;
    private String outputRootDir = null;
    private RPFDataSeries dataseries = null;
    private boolean forceEncoding = false;
    private List<WaveletEncoderListener> listeners = new ArrayList<WaveletEncoderListener>(2);
    private AtomicBoolean done = new AtomicBoolean(false);
    private HashMap<String, RPFFrameTransform> frameTransforms = new HashMap<String, RPFFrameTransform>(20);
}
