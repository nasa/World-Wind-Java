/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.http;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a helper class used by HTTP Post and HTTP File Upload classes.
 * The basics of HTTP POST require to read input streams as lines, but when a binary file is transferred,
 * it is required to read input stream as a binary, but the file separator is text line again.
 * However, it is not possible to seek inside input stream, therefore we need to have a buffer
 * that will read input stream and will allow to mix reading as a text line or as a binary data.
 *
 * @author Lado Garakanidze
 * @version $Id$
 */

class BufferedReadableByteChannel implements ReadableByteChannel
{
    protected final int DEFAULT_BUFFER_SIZE = 1024 * 64; // 64KB is a default read buffer size
    protected final char CR = '\r';
    protected final char LF = '\n';

    protected java.nio.channels.ReadableByteChannel channel = null;
    protected ByteBuffer buffer = null;
    private final ReentrantLock lockChannel = new ReentrantLock();

    public BufferedReadableByteChannel(java.io.InputStream is) throws java.io.IOException
    {
        if (null == is)
        {
            String message = Logging.getMessage("nullValue.InputStreamIsNull");
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        this.channel = Channels.newChannel(is);
    }

    public BufferedReadableByteChannel(java.nio.channels.ReadableByteChannel channel) throws java.io.IOException
    {
        if (null == channel)
        {
            String message = Logging.getMessage("nullValue.InputStreamIsNull");
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        this.channel = channel;
    }

    protected int readFromChannel() throws java.io.IOException
    {
        int availableBytes = -1;
        if (this.isOpen() && this.lockChannel.tryLock())
        {
            try
            {
                if (this.buffer == null)
                {
                    this.buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
                }
                else
                {
                    this.buffer.clear();
                }

                availableBytes = this.channel.read(this.buffer);
                if (availableBytes < 0)
                {
                    this.close();
                }
                else
                {
                    this.buffer.flip();
                }
            }
            finally
            {
                this.lockChannel.unlock();
            }
        }

        return availableBytes;
    }

    /**
     * Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'),
     * a carriage return ('\r'), or a carriage return followed immediately by a linefeed ('\r\n').
     *
     * @return A String containing the contents of the line, not including any line-termination characters,
     *         or null if the end of the stream has been reached
     * @throws java.io.IOException If an I/O error occurs
     */
    public java.lang.String readLine() throws java.io.IOException
    {
        synchronized (this)
        {
            StringBuffer s = new StringBuffer();

            char prev = 0;

            for (; ;)
            {
                while (null != this.buffer && this.buffer.hasRemaining())
                {
                    char c = (char) (0xFF & this.buffer.get());
                    if (c == LF)
                    {
                        return s.toString();
                    }
                    else if (prev == CR)
                    {
                        return s.toString();
                    }
                    else if (c == CR)
                    {
                        prev = CR;
                    }
                    else
                    {
                        s.append(c);
                        prev = c;
                    }
                }

                if (this.isOpen())
                {
                    this.readFromChannel();
                }
                else
                {
                    break;
                }
            }

            return (s.length() > 0) ? s.toString() : ( this.isOpen() ? "" : null );
        }
    }

    public long readChannelAndWriteTo(java.io.OutputStream dest, byte[] separator)
            throws java.io.IOException, InterruptedException
    {
        if( null == dest )
        {
            String message = Logging.getMessage("nullValue.DestinationIsNull");
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        if( null == separator || separator.length < 4 )
        {
            String message = Logging.getMessage("generic.LengthIsInvalid", (null == separator) ? 0 : separator.length );
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        long totalBytes = 0;

        int matchPos = 0;
        for(;;)
        {
            while (null != this.buffer && this.buffer.hasRemaining() )
            {
                byte b = this.buffer.get();
                if( b == separator[matchPos] )
                {
                    matchPos++;
                    if( matchPos >= separator.length )
                    {
                        dest.flush();
                        return totalBytes;
                    }

                    continue;
                }
                else if( matchPos > 0 )
                {
                    // write a partial match
                    dest.write( separator, 0, matchPos );
                    totalBytes += matchPos;
                }

                dest.write( b );
                totalBytes++;
                matchPos = 0;
            }

            if( this.isOpen() && this.readFromChannel() >= 0 )
            {
                Thread.sleep(20L);
                continue;
            }
            else
                break;
        }

        if( totalBytes > 0 )
            dest.flush();

        return totalBytes;
    }

    /**
     * Tells whether or not this channel is open.
     *
     * @return true if, and only if, this channel is open
     */
    @Override
    public boolean isOpen()
    {
        return (this.channel != null && this.channel.isOpen());
    }

    @Override
    public void close() throws IOException
    {
        WWIO.closeStream(this.channel, null);
    }

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

