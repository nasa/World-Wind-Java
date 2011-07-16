/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.data.ByteBufferRaster;

import java.awt.image.*;
import java.io.*;
import java.nio.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class BILImageFormatter extends ImageFormatter
{
    private short nodata_value = 0;
    private boolean hasVoidAreas = true;

    public short getNoDataValue()
    {
        return this.nodata_value;    
    }

    public void setNoDataValue(short nodata_value)
    {
        this.nodata_value = nodata_value;
    }

    public boolean hasNoDataAreas()
    {
        return this.hasVoidAreas;
    }

    public BILImageFormatter( ByteBufferRaster raster )
    {
        if(null != raster)
        {
            this.xRes = raster.getWidth();
            this.yRes = raster.getHeight();
            this.data = BILImageFormatter.getBytes( raster.getByteBuffer() );

            // TODO what if we are getting Int32, Float32?? Only Int16 works here
            this.image = ByteBuffer.wrap(this.data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        }
    }

    public BILImageFormatter(int xRes, int yRes, ByteBuffer bb )
    {
        this.xRes = xRes;
        this.yRes = yRes;
        if(null != bb )
        {
            this.data = BILImageFormatter.getBytes( bb );
            this.image = ByteBuffer.wrap(this.data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        }
    }

    public static byte[] getBytes(ByteBuffer bb) {
        /*
         * This should never return a BufferOverflowException, as we're
         * careful to allocate just the right amount.
         */
        byte [] buf = null;

        /*
         * If it has a usable backing byte buffer, use it.  Use only
         * if the array exactly represents the current ByteBuffer.
         */
        if (bb.hasArray()) {
            byte [] tmp = bb.array();
            if ((tmp.length == bb.capacity()) &&
                    (tmp.length == bb.remaining())) {
                buf = tmp;
                bb.position(bb.limit());
            }
        }

        if (buf == null) {
            /*
             * This class doesn't have a concept of encode(buf, len, off),
             * so if we have a partial buffer, we must reallocate
             * space.
             */
            buf = new byte[bb.remaining()];

            /*
             * position() automatically updated
             */
            bb.get(buf);
        }

        return buf;
    }

    public BILImageFormatter(int xRes, int yRes)
    {
        this.xRes = xRes;
        this.yRes = yRes;
        this.data = new byte[xRes * yRes * 2];
        this.image = ByteBuffer.wrap(this.data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
    }


    private void init(int width, int height)
    {
        this.xRes = width;
        this.yRes = height;
        this.data = new byte[xRes * yRes * 2];
        this.image = ByteBuffer.wrap(this.data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
    }

    public BufferedImage toIntermediateForm() throws IOException
    {
        throw new UnsupportedOperationException("BILImageFormatter.toIntermediateForm() has not been implemented.");
    }
    
    public InputStream asBIL() throws IOException
    {
        return new ByteArrayInputStream(data);
    }
    

    public void drawImage(java.nio.ShortBuffer srcImage, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
        int sy2)
    {
        if( null == srcImage)
            return;
        
        if (    this.image == null
             || dx1 < 0 || dx2 < dx1 || dx2 >= this.xRes
             || dy1 < 0 || dy2 < dy1 || dy2 >= this.yRes
             || sx1 < 0 || sx2 < sx1
             || sy1 < 0 || sy2 < sy1
//             || sx2 >= srcImage.getWidth()
//             || sy2 >= srcImage.getHeight()
            )
        {
            throw new IllegalArgumentException("Either image is null or source/destination parameters bogus: " +
                "dst: " + dx1 + "," + dy1 + "," + dx2 + "," + dy2 + "," + "  src: " + sx1 + "," +
                sy1 + "," + sx2 + "," + sy2);
        }

        int srcWidth = (sx2 - sx1 + 1);
        double scaleX = (double) (sx2 - sx1 + 1) / (double) (dx2 - dx1 + 1);
        double scaleY = (double) (sy2 - sy1 + 1) / (double) (dy2 - dy1 + 1);

        srcImage.rewind();
        try
        {
            for (int row = dy1; row <= dy2; row++)
            {
                for (int col = dx1; col <= dx2; col++)
                {
                    int i = (int) (sx1 + (col - dx1) * scaleX + 0.5);
                    i = Math.min(i, sx2);

                    int j = (int) (sy1 + (row - dy1) * scaleY + 0.5);
                    j = Math.min(j, sy2);

                    this.image.put(row * this.xRes + col, (short) srcImage.get(j * srcWidth + i));
                }
            }
        }
        catch(Exception e)
        {
            Logging.logger().info("Debug: " + e.getMessage() + "--- remaining " + srcImage.remaining() );
        }
    }


    public void drawImage(BufferedImage srcImage, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        if (this.image == null ||
            dx1 < 0 || dx2 < dx1 || dx2 >= this.xRes ||
            dy1 < 0 || dy2 < dy1 || dy2 >= this.yRes ||
            sx1 < 0 || sx2 < sx1 || sx2 >= srcImage.getWidth() ||
            sy1 < 0 || sy2 < sy1 || sy2 >= srcImage.getHeight())
        {
            throw new IllegalArgumentException("Either image is null or source/destination parameters bogus: " +
                "dst: " + dx1 + "," + dy1 + "," + dx2 + "," + dy2 + "," + "  src: " + sx1 + "," +
                sy1 + "," + sx2 + "," + sy2);
        }

        DataBuffer srcBuff = srcImage.getRaster().getDataBuffer();
        int srcWidth = srcImage.getWidth();

        double scaleX = (double) (sx2 - sx1 + 1) / (double) (dx2 - dx1 + 1);
        double scaleY = (double) (sy2 - sy1 + 1) / (double) (dy2 - dy1 + 1);

        for (int row = dy1; row <= dy2; row++)
        {
            for (int col = dx1; col <= dx2; col++)
            {
                int i = (int) (sx1 + (col - dx1) * scaleX + 0.5);
                i = Math.min(i, sx2);

                int j = (int) (sy1 + (row - dy1) * scaleY + 0.5);
                j = Math.min(j, sy2);

                this.image.put(row * this.xRes + col, (short) srcBuff.getElem(j * srcWidth + i));
            }
        }
    }


    public void copyFrom(BufferedImage srcImage, short nodata_value )
    {
        if( null == srcImage )
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        try
        {
            this.hasVoidAreas = false;
            this.nodata_value = nodata_value;
            if(null == this.data || null == this.image || this.xRes != width || this.yRes != height )
            {
                this.init( width, height );
            }
            DataBuffer srcBuff = srcImage.getRaster().getDataBuffer();
            int size = srcBuff.getSize();
            short value = 0;

            int count_missing_pixels = 0;

            for(int i = 0; i < size; i++ )
            {
                value = (short)(srcBuff.getElem(i) & 0xFFFF);
                if(value == nodata_value)
                {
                    this.hasVoidAreas = true;
                    count_missing_pixels ++;
                }
                this.image.put(i, value );
            }
            if( this.hasVoidAreas )
                Logging.logger().finest("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! [DEBUG] found " + count_missing_pixels
                        + " missing pixels of " + nodata_value + ", total pixels = " + (width * height));
        }
        catch(Exception severe)
        {
            String message = severe.getCause().getMessage();
            Logging.logger().severe(message);
            throw new RuntimeException( message );
        }
    }

    public int getXResolution()
    {
        return this.xRes;
    }

    public int getYResolution()
    {
        return this.yRes;
    }

    public ShortBuffer getImage()
    {
        return this.image;
    }

    public void treatMissingData( short findColor, short replaceColor )
    {
        if( null != this.image )
        {
            this.hasVoidAreas = false;
            this.image.rewind();
            int size = this.image.remaining();
            for( int i = 0; i < size; i++ )
            {
                short value = this.image.get(i);
                if( value == findColor || value == Short.MIN_VALUE )
                {
                    this.hasVoidAreas = true;
                    this.image.put(i, replaceColor );
                }
                else
                    this.image.put(i, value );
            }
            this.nodata_value = replaceColor; 
        }
    }


    public void merge( BILImageFormatter fromImage, short missing_color, short default_missing_color )
    {

        if(    null != this.image
            && null != fromImage
            && null != fromImage.getImage()
          )
        {
            this.image.rewind();

            ShortBuffer src = fromImage.getImage();
            src.rewind();
            int size = src.remaining();


            if( size == this.image.remaining() )
            {
                short color;
                for( int i = 0; i < size; i++ )
                {
                    color = src.get(i);
                    if( color == missing_color || color == default_missing_color )
                        continue;
                    this.image.put( i, color );
                }
            }
        }
    }

    // add only pixels that are missing (with nodata color) in the source image
    public void merge( BILImageFormatter fromImage )
    {

        if(    null != this.image
            && null != fromImage
            && null != fromImage.getImage()
          )
        {
            this.image.rewind();
            this.hasVoidAreas = false;

            ShortBuffer from = fromImage.getImage();
            from.rewind();
            int size = from.remaining();

            short fromColor ;
            short srcMissing = this.getNoDataValue();
            short fromMissing = fromImage.getNoDataValue();

            if( size == this.image.remaining() )
            {
                for( int i = 0; i < size; i++ )
                {
                    if( srcMissing == this.image.get(i) )
                    {
                        fromColor = from.get(i);
                        if( fromColor != fromMissing )
                            this.image.put( i, fromColor );
                        else
                            this.hasVoidAreas= true;
                    }
                }
            }
        }
    }
    private int xRes;
    private int yRes;
    private byte[] data;
    private ShortBuffer image;
}
