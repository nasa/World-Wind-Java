/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import gov.nasa.worldwind.formats.dds.*;
import gov.nasa.worldwind.formats.tiff.*;
import gov.nasa.worldwind.servers.wms.generators.*;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.Properties;

/**
 * @author brownrigg
 * @version $Id$
 */

abstract public class ImageFormatter
{
    abstract public BufferedImage toIntermediateForm() throws IOException;

    public InputStream asPng() throws IOException
    {
        return intermediateToPng(toIntermediateForm());
    }

    public InputStream asDDS(Properties properties) throws IOException
    {
        return intermediateToDDS(toIntermediateForm(), properties);
    }

    public InputStream asJPEG() throws IOException
    {
        return intermediateToJPEG(toIntermediateForm());
    }

    public InputStream asBIL() throws IOException
    {
        return intermediateToBIL(toIntermediateForm());
    }

    public InputStream asTiff() throws IOException
    {
        return intermediateToTiff(toIntermediateForm());
    }

    protected InputStream intermediateToPng(BufferedImage image) throws IOException
    {
        return convertToImageIOType(image, "png");
    }

    protected InputStream intermediateToJPEG(BufferedImage image) throws IOException
    {
        return convertToImageIOType(image, "jpeg");
    }

    protected InputStream intermediateToDDS(BufferedImage image, Properties properties) throws IOException
    {
        ByteBuffer ddsBuffer = DDSCompressor.compressImage(image);

        // Copy the DDS file buffer into a standard Java byte array.
        ddsBuffer.rewind();
        byte[] imageBytes = new byte[ddsBuffer.remaining()];
        ddsBuffer.get(imageBytes);
        return new ByteArrayInputStream(imageBytes);
    }

    protected InputStream intermediateToBIL(BufferedImage image) throws IOException
    {
        Raster raster = image.getRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int numBands = raster.getNumBands();

        ByteBuffer buff = ByteBuffer.allocate(width * height * 2);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        int[] rowBuff = new int[width];
        for (int row = 0; row < height; row++)
        {
            for (int band = 0; band < numBands; band++)
            {
                raster.getSamples(0, row, width, 1, band, rowBuff);
                for (int i = 0; i < width; i++)
                {
                    buff.putShort((short) (rowBuff[i] & 0xFFFF));
                }
            }
        }

        /*******
         // TODO: there's undoubtably a better/faster way to do this; it seems to make little
         // use of "bulk" data movement and requires lots of memory allocations.
         // However, this is a first pass...  --RLB
         ByteArrayOutputStream imageBytes = new ByteArrayOutputStream(100000);
         DataOutputStream bil = new DataOutputStream(imageBytes);
         int[] rowBuff = new int[width];
         for (int row=0; row<height; row++) {
         for (int band=0; band<numBands; band++) {
         raster.getSamples(0, row, width, 1, band, rowBuff);
         for (int i=0; i<width; i++)
         bil.writeShort(rowBuff[i]&0xFFFF);
         }
         }

         return new ByteArrayInputStream(imageBytes.toByteArray());
         ****/

        return new ByteArrayInputStream(buff.array());
    }

    protected InputStream intermediateToTiff(BufferedImage image) throws IOException
    {
        TempFile tmpFile = TempFile.getTempFile();
        GeotiffWriter writer = new GeotiffWriter(tmpFile.getAbsoluteFile());
        writer.write(image);
        writer.close();
        return new FileInputStream(tmpFile);
    }

    public InputStream getStreamFromMimeType(String mimeType, Properties properties) throws IOException
    {
        if (SupportedFormats.IMAGE_PNG.equals(mimeType))
            return asPng();
        if (SupportedFormats.IMAGE_DDS.equals(mimeType))
            return asDDS(properties);
        if (SupportedFormats.IMAGE_JPEG.equals(mimeType))
            return asJPEG();
        if (SupportedFormats.IMAGE_BIL.equals(mimeType))
            return asBIL();
        if (SupportedFormats.APPLICATION_BIL.equals(mimeType))
            return asBIL();
        if (SupportedFormats.APPLICATION_BIL16.equals(mimeType))
            return asBIL();
        if (SupportedFormats.APPLICATION_BIL32.equals(mimeType))
            return asBIL();
        if (SupportedFormats.IMAGE_TIFF.equals(mimeType))
            return asTiff();
        throw new IOException("ImageFormatter: unsupport image type: " + mimeType);
    }

    private InputStream convertToImageIOType(BufferedImage image, String imageIOType) throws IOException
    {
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
        ImageOutputStream ios = new MemoryCacheImageOutputStream(imageBytes);

        if("jpeg".equalsIgnoreCase(imageIOType))
        {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setSourceBands( new int[] {0,1,2} );
            ColorModel cm = new DirectColorModel(24, /*Red*/0x00ff0000, /*Green*/0x0000ff00, /*Blue*/ 0x000000ff,  /*Alpha*/0x0);
            param.setDestinationType( new ImageTypeSpecifier( cm, cm.createCompatibleSampleModel(1,1)));

            writer.setOutput( ios );
            writer.write( null, new IIOImage(image,null,null),param);
            writer.dispose();
        }
        else
        {
            ImageIO.write(image, imageIOType, ios);
        }
        ios.close();
        return new ByteArrayInputStream( imageBytes.toByteArray() );
    }

    static public ImageFormatter getFormatterFromMimeType(String mimeType, File sourceFile)
    {
        if (SupportedFormats.IMAGE_PNG.equals(mimeType))
            return new PNGImageFormatter(sourceFile);
        return null;
    }

    // ----------------- Supported Output Formats ----------------------------

    private enum SupportedFormats
    {
        IMAGE_PNG("image/png"),
        IMAGE_DDS("image/dds"),
        IMAGE_JPEG("image/jpeg"),
        IMAGE_BIL("image/bil"),
        IMAGE_TIFF("image/tiff"),
        APPLICATION_BIL("application/bil"),
        APPLICATION_BIL16("application/bil16"),
        APPLICATION_BIL32("application/bil32");

        SupportedFormats(String mimeType)
        {
            this.mimeType = mimeType;
        }

        public boolean equals(String that)
        {
            return this.mimeType.equalsIgnoreCase(that);
        }

        String mimeType;
    }

    static public boolean isSupportedType(String mimeType)
    {
        for (SupportedFormats f : formats)
        {
            if (f.equals(mimeType))
                return true;
        }
        return false;
    }

    static private SupportedFormats[] formats = SupportedFormats.class.getEnumConstants();
}
