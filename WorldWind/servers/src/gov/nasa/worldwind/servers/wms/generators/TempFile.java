/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.app.ApplicationServer;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.util.Logging;

import java.io.*;
import java.util.Formatter;
import java.util.Locale;

/**
 * This class wraps a File object with a finalizer that ensures the file gets deleted (if it has not already) when this
 * object is reclaimed.
 *
 * @author brownrigg
 * @version $Id$
 */

public class TempFile extends File
{
     private static int unique_index = 0;
     private static final Object lock = new Object();

    /*
     * Factory for getting named TempFiles.
     */
    public static TempFile getTempFile()
    {
        StringBuilder tmp = new StringBuilder();
        tmp.append( ApplicationServer.getTempDirectory() ).
            append(File.separator).append("WMStmp").
            append(Integer.toString((int) (Math.random() * 100000.)));
        return new TempFile(tmp.toString());
    }

    private synchronized static int get_unique_index()
    {
        synchronized (lock)
        {
            return (++TempFile.unique_index);
        }
    }



    public static TempFile getTempFile(double lon, double lat, String ext)
    {
        TempFile file = null;

        try
        {
            StringBuilder source = new StringBuilder(200);
            Formatter formatter = new Formatter(source, Locale.US);

            formatter.format( "%s%stmp_%s%02d%s%03d_%08d.%s",
                    ApplicationServer.getTempDirectory(),
                    File.separator,
                    ((lat >= 0d) ? "N" : "S"), Math.abs((int)lat),
                    ((lon <= 0d) ? "W" : "E"), Math.abs((int)lon),
                    TempFile.get_unique_index(),
                    ext
            );
            file = new TempFile(source.toString());
        }
        catch(Exception e)
        {
            Logging.logger().severe("TempFile->getTempFile: " + e.getMessage());
        }

        if(null == file)
            file = TempFile.getTempFile();
        
        return file;
    }



    public TempFile(String name)
    {
        super(name);
    }

    protected void finalize() throws Throwable
    {
        try
        {
            if (this.exists())
                this.delete();
        }
        catch (Exception ex)
        {
            Logging.logger().severe("TempFile->finalize() Attempt to delete file failed: " + ex.getMessage()); 
        }
        super.finalize();
    }
}
