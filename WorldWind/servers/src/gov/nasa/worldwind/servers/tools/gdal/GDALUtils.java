/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.app.ApplicationServer;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.ImageUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class GDALUtils
{
    private static GDALUtils INSTANCE = null;

    private static final int DEFAULT_MAX_CACHE_SIZE = 1024;
    private static final long DEFAULT_MAX_TIMEOUT = 60000L;

    private String gdalPath = "";
    private String gdalWarp = "";
    private String gdalTranslate = "";
    private String gdalInfo = "";

    private Boolean debug = false;
    private int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
    private long maxTimeOut = DEFAULT_MAX_TIMEOUT;

    public static GDALUtils getGDAL() throws IllegalArgumentException, IOException
    {
        if (null == INSTANCE)
        {
            INSTANCE = new GDALUtils();
        }
        return INSTANCE;
    }

    private GDALUtils() throws IllegalArgumentException, IOException
    {
        this.init();
    }

    private void init() throws IllegalArgumentException, IOException
    {
        AVList configuration = ApplicationServer.getConfiguration();

        Object path = configuration.getValue(AVKey.GDAL_PATH);
        if (WWUtil.isEmpty(path))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.GDAL_PATH);
            throw new IOException(message);
        }

        this.verifyPath(WWIO.getFileForLocalAddress(path));

        if (configuration.hasKey(AVKey.GDAL_CACHEMAX))
        {
            try
            {
                this.maxCacheSize = Integer.parseInt(configuration.getStringValue(AVKey.GDAL_CACHEMAX));
            }
            catch (Exception e)
            {
                this.maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
            }
        }
        else
        {
            this.maxCacheSize = DEFAULT_MAX_CACHE_SIZE;
        }

        if (configuration.hasKey(AVKey.GDAL_DEBUG))
        {
            try
            {
                this.debug = Boolean.parseBoolean(configuration.getStringValue(AVKey.GDAL_DEBUG));
            }
            catch (Exception e)
            {
                this.debug = false;
            }
        }
        else
        {
            this.debug = false;
        }

        if (configuration.hasKey(AVKey.GDAL_TIMEOUT))
        {
            try
            {
                this.maxTimeOut = Long.parseLong(configuration.getStringValue(AVKey.GDAL_TIMEOUT));
            }
            catch (Exception e)
            {
                this.maxTimeOut = DEFAULT_MAX_TIMEOUT;
            }
        }
        else
        {
            this.maxTimeOut = DEFAULT_MAX_TIMEOUT;
        }
    }

    protected void verifyPath(File gdalpath) throws IllegalArgumentException, IOException
    {
        if (null == gdalpath)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!gdalpath.exists() || !gdalpath.isDirectory())
        {
            String msg = Logging.getMessage("generic.FolderDoesNotExist", gdalpath.getAbsolutePath());
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }

        this.gdalPath = gdalpath.getAbsolutePath();

        String baseFolder = this.gdalPath;
        if (!baseFolder.endsWith(File.separator))
        {
            baseFolder += File.separator;
        }

        String gdal_warp = baseFolder + ((Configuration.isWindowsOS()) ? "gdalwarp.exe" : "gdalwarp");
        File gdalwarp = new File(gdal_warp);
        if (!gdalwarp.exists() || !gdalwarp.canRead())
        {
            String msg = Logging.getMessage("generic.FileNotFound", gdal_warp);
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }
        else
        {
            this.gdalWarp = gdalwarp.getAbsolutePath();
        }

        String gdal_translate = baseFolder + ((Configuration.isWindowsOS()) ? "gdal_translate.exe" : "gdal_translate");
        File gdaltranslate = new File(gdal_translate);
        if (!gdaltranslate.exists() || !gdaltranslate.canRead())
        {
            String msg = Logging.getMessage("generic.FileNotFound", gdal_translate);
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }
        else
        {
            this.gdalTranslate = gdaltranslate.getAbsolutePath();
        }

        String gdal_info = baseFolder + ((Configuration.isWindowsOS()) ? "gdalinfo.exe" : "gdalinfo");
        File gdalinfo = new File(gdal_info);
        if (!gdalinfo.exists() || !gdalinfo.canRead())
        {
            String msg = Logging.getMessage("generic.FileNotFound", gdal_info);
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }
        else
        {
            this.gdalInfo = gdalinfo.getAbsolutePath();
        }
    }

    protected String gdalCommandToString(List<String> cmds)
    {
        StringBuffer sb = new StringBuffer(" GDAL command: ");
        for (String cmd : cmds)
        {
            sb.append(cmd).append(" ");
        }
        return sb.toString();
    }

    public long getMaxTimeout()
    {
        return this.maxTimeOut;
    }

    public boolean isDebugEnabled()
    {
        return this.debug;
    }

    public int getMaxCacheSize()
    {
        return this.maxCacheSize;
    }

    public String exec_gdal(String threadId, List<String> cmds) throws RuntimeException
    {
        long startTime = System.currentTimeMillis();

        StringBuffer sb = new StringBuffer();

        String output = "";

        if (debug)
        {
            sb.append(this.gdalCommandToString(cmds));
        }

        // Call upon a GDAL utility to make our map...
        Process proc = null;
        ExecThread out = null;
        ExecThread err = null;

        try
        {
            long start = System.currentTimeMillis();

            proc = new ProcessBuilder(cmds).start();

            if (debug)
            {
                sb.append("\nGDAL process created in ").append(System.currentTimeMillis() - start).append(" msec");
            }
            start = System.currentTimeMillis();

            err = new ExecThread(proc.getErrorStream());
            out = new ExecThread(proc.getInputStream());

            err.start();
            out.start();

            Timer timer = new Timer(false);
            TimerTask watchdog = new Watchdog(proc, threadId, err, out);
            timer.schedule(watchdog, this.getMaxTimeout()); // 30sec

            if (debug)
            {
                sb.append("\nStream readers and watchdog created in ").append(
                    System.currentTimeMillis() - start).append(" msec");
            }

            proc.waitFor();

            if (debug)
            {
                sb.append("\nGDAL process duration =").append(System.currentTimeMillis() - start).append(" msec");
            }

            start = System.currentTimeMillis();

            watchdog.cancel();
            timer.cancel();

            err.join(Watchdog.MAX_WAIT_TIME);
            out.join(Watchdog.MAX_WAIT_TIME);

            output = err.getOutput();

            try
            {
                int exitCode = proc.exitValue();
                sb.append("\nExit code=").append(exitCode);
            }
            catch (Exception ex)
            {
                if (debug)
                {
                    sb.append("\nException while retrieving exit code: ").append(ex.getMessage());
                }
            }

            if (debug)
            {
                sb.append("\nerror stream: {").append("\n").append(err.getOutput()).append("\n} end of error stream");
                sb.append("\noutput stream: {").append("\n").append(output).append("\n} end of output stream");
                sb.append("\nStream readers and watchdog joined in =").append(
                    System.currentTimeMillis() - start).append(" msec");
            }
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("generic.Unknown", ex.getMessage());
            Logging.logger().log(Level.SEVERE, msg, ex);
            throw new RuntimeException(msg);
        }
        finally
        {
            long start = System.currentTimeMillis();

            if (null != proc)
            {
                WWIO.closeStream(proc.getInputStream(), "InputStream");
                WWIO.closeStream(proc.getErrorStream(), "ErrorStream");
                WWIO.closeStream(proc.getOutputStream(), "OutputStream");
            }

            if (out != null && out.isAlive())
            {
                try
                {
                    out.join(Watchdog.MAX_WAIT_TIME);
                }
                catch (Exception ignore)
                {
                }
            }

            if (err != null && err.isAlive())
            {
                try
                {
                    err.join(Watchdog.MAX_WAIT_TIME);
                }
                catch (Exception ignore)
                {
                }
            }

            if (debug)
            {
                sb.append("\ncleanup time in the finally block =").append(System.currentTimeMillis() - start).append(
                    " msec");
                sb.append("\ntotal time =").append(System.currentTimeMillis() - startTime).append(" msec");
                Logging.logger().finest(sb.toString());
            }
        }
        return output;
    }

    public void warp(String threadId,
        Option.Warp.Resampling r,
        String[] optionsBag,
        File[] srcFiles,
        Sector extent,
        int width, int height,
        ReadWriteFormat outputFormat,
        File dest
    ) throws RuntimeException, java.io.FileNotFoundException
    {
        if (null == srcFiles || srcFiles.length == 0)
        {
            String msg = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (null == extent)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if ((extent.getMinLatitude().degrees >= extent.getMaxLatitude().degrees)
            || (extent.getMinLongitude().degrees >= extent.getMaxLongitude().degrees)
            )
        {
            String msg = Logging.getMessage("generic.SectorSizeInvalid");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        FileUtil.delete(dest); // Note! GDAL will not override existing files.

        ArrayList<String> cmds = new ArrayList<String>();

        cmds.add(this.gdalWarp);

        if (null != optionsBag && optionsBag.length > 0)
            Collections.addAll(cmds, optionsBag);

        cmds.add("--debug");
        if (this.isDebugEnabled())
        {
            cmds.add("ON");
        }
        else
        {
            cmds.add("OFF");
            cmds.add("-q");
        }

        String cacheMax = "" + this.getMaxCacheSize();
        cmds.add("--config");
        cmds.add("GDAL_CACHEMAX");
        cmds.add(cacheMax);
        cmds.add("-wm");
        cmds.add(cacheMax);

        cmds.add("-of");
        cmds.add(outputFormat.getValue());

        cmds.add(r.getKey());
        cmds.add(r.getValue());

        cmds.add("-te");
        cmds.add(Double.toString(extent.getMinLongitude().degrees));
        cmds.add(Double.toString(extent.getMinLatitude().degrees));
        cmds.add(Double.toString(extent.getMaxLongitude().degrees));
        cmds.add(Double.toString(extent.getMaxLatitude().degrees));

        cmds.add("-ts");
        cmds.add(Integer.toString(width));
        cmds.add(Integer.toString(height));

        for (File src : srcFiles)
        {
            if (null == src || !src.exists() || !src.canRead())
            {
                String msg = Logging.getMessage("generic.FileNotFound", ((null == src) ? "" : src.getAbsolutePath()));
                Logging.logger().severe(msg);
                throw new java.io.FileNotFoundException(msg);
            }
            cmds.add(src.getAbsolutePath());
        }

        // append a destination file
        cmds.add(dest.getAbsolutePath());

        // this.exec_gdal(threadId, (String[])cmds.toArray(new String[cmds.size()]), srcFiles.length );
        this.exec_gdal(threadId, cmds);
    }

    public void translate(String threadId, String[] optionsBag, File src, File dest)
        throws RuntimeException, java.io.FileNotFoundException
    {
        if (null == src || !src.exists() || !src.canRead())
        {
            String msg = Logging.getMessage("generic.FileNotFound", ((null == src) ? "" : src.getAbsolutePath()));
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }

        FileUtil.delete(dest); // Note! GDAL will not override existing files.

        ArrayList<String> cmds = new ArrayList<String>();
        cmds.add(this.gdalTranslate);

        cmds.add("--debug");
        if (this.isDebugEnabled())
        {
            cmds.add("ON");
        }
        else
        {
            cmds.add("OFF");
            cmds.add("-quiet");
        }

        String cacheMax = "" + this.getMaxCacheSize();
        cmds.add("--config");
        cmds.add("GDAL_CACHEMAX");
        cmds.add(cacheMax);

        if (null != optionsBag && optionsBag.length > 0)
            Collections.addAll(cmds, optionsBag);

        cmds.add(src.getAbsolutePath());

        // append a destination file
        cmds.add(dest.getAbsolutePath());

        this.exec_gdal(threadId, cmds);
    }

    public AVList info(String threadId, File src) throws RuntimeException, java.io.FileNotFoundException
    {
        if (null == src)
        {
            String msg = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }

        if (!src.exists())
        {
            String msg = Logging.getMessage("generic.FileNotFound", src.getAbsolutePath());
            Logging.logger().severe(msg);
            throw new java.io.FileNotFoundException(msg);
        }

        if (!src.canRead())
        {
            String msg = Logging.getMessage("generic.FileNoReadPermission", src.getAbsolutePath());
            Logging.logger().severe(msg);
            throw new RuntimeException(msg);
        }

        AVList metadata = new AVListImpl();

        metadata.setValue(AVKey.FILE_NAME, src.getAbsolutePath());
        metadata.setValue(AVKey.LAYER_NAME, src.getName());
        metadata.setValue(AVKey.TITLE, src.getName());

        String imagefile = src.getAbsolutePath();
        if (imagefile.toLowerCase().endsWith(".tif") || imagefile.toLowerCase().endsWith(".tiff"))
        {

            try
            {
                GeotiffReader reader = new GeotiffReader(src);

                if (reader.isGeotiff(0))
                {
                    ImageUtil.readGeoKeys(reader, 0, metadata);

                    if (metadata.hasKey(AVKey.SECTOR) && metadata.hasKey(AVKey.WIDTH) && metadata.hasKey(AVKey.HEIGHT))
                    {
                        Sector sector = (Sector) metadata.getValue(AVKey.SECTOR);

                        if (!metadata.hasKey(AVKey.PIXEL_WIDTH))
                        {
                            int width = (Integer) metadata.getValue(AVKey.WIDTH);
                            if (0d != width)
                            {
                                metadata.setValue(AVKey.PIXEL_WIDTH, sector.getDeltaLonDegrees() / ((double) width));
                            }
                        }

                        if (!metadata.hasKey(AVKey.PIXEL_HEIGHT))
                        {
                            int height = (Integer) metadata.getValue(AVKey.HEIGHT);
                            if (0d != height)
                            {
                                metadata.setValue(AVKey.PIXEL_HEIGHT, sector.getDeltaLatDegrees() / ((double) height));
                            }
                        }

                        Logging.logger().info(sector.toString());

                        if (!metadata.hasKey(AVKey.MISSING_DATA_SIGNAL))
                        {
                            metadata.setValue(AVKey.MISSING_DATA_SIGNAL, 0d);
                        }

                        if (!metadata.hasKey(AVKey.MISSING_DATA_REPLACEMENT))
                        {
                            metadata.setValue(AVKey.MISSING_DATA_REPLACEMENT, 0d);
                        }

                        return metadata;
                    }
                }
            }
            catch (Exception e)
            {
                Logging.logger().log(Level.SEVERE, e.getMessage(), e);
            }
        }

        ArrayList<String> cmds = new ArrayList<String>();
        cmds.add(this.gdalInfo);
        cmds.add(src.getAbsolutePath());

        return this.parse_gdalinfo_output(this.exec_gdal(threadId, cmds));
    }

    protected AVList parse_gdalinfo_output(String output)
    {
        AVList metadata = new AVListImpl();

        List<LatLon> bbox = new ArrayList<LatLon>();

        if (null == output || 0 == output.trim().length())
        {
            return metadata;
        }

        for (String s : output.split("\\r\\n|[\\n\\r\\u0085\\u2028\\u2029]"))
        {
            if (s.startsWith("Driver:"))
            {
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}");
                sc.findInLine("Driver:");
                if (sc.hasNext())
                {
                    metadata.setValue(AVKey.FORMAT_SUFFIX, sc.next());
                }
            }
//            else if( s.startsWith("Files:"))
//            {
//                Scanner sc = new Scanner(s).useDelimiter( "\\p{javaWhitespace}" );
//                sc.findInLine("Files:");
//                if( sc.hasNext() )
//                {
//                    File file = new File( sc.next() );
//                    metadata.setValue( AVKey.FILE_NAME, file.getAbsolutePath() );
//                    metadata.setValue( AVKey.LAYER_NAME, file.getName() );
//
//                    metadata.setValue( AVKey.TITLE, file.getName() );
//
//                    String parent_path = file.getParent();
//                    int idx = parent_path.lastIndexOf( File.separator ) + 1;
//                    String parent = parent_path.substring( idx, parent_path.length());
//                    metadata.setValue( AVKey.PARENT_LAYER_NAME, parent );
//                }
//            }
            else if (s.startsWith("Size is"))
            {
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|, *");
                sc.findInLine("Size is");
                if (sc.hasNextInt())
                {
                    metadata.setValue(AVKey.WIDTH, sc.nextInt());
                }
                if (sc.hasNextInt())
                {
                    metadata.setValue(AVKey.HEIGHT, sc.nextInt());
                }
            }
            else if (s.startsWith("PROJCS"))
            {
                // Examples:
                // PROJCS["WGS 84 / UTM zone 18N",

            }
            else if (s.startsWith("Origin"))
            {
                double lat = 0d, lon = 0d;

                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");

                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                metadata.setValue(AVKey.ORIGIN, LatLon.fromDegrees(lat, lon));
            }
            else if (s.startsWith("Pixel Size"))
            {
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    metadata.setValue(AVKey.PIXEL_WIDTH, sc.nextDouble());
                }

//                sc.findInLine("\\,");
                if (sc.hasNextDouble())
                {
                    metadata.setValue(AVKey.PIXEL_HEIGHT, sc.nextDouble());
                }
            }
            else if (s.startsWith("Center"))
            {
                double lat = 0d, lon = 0d;
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                metadata.setValue(AVKey.CENTER, LatLon.fromDegrees(lat, lon));
            }
            else if (s.startsWith("Upper Left"))
            {
                double lat = 0d, lon = 0d;
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                bbox.add(LatLon.fromDegrees(lat, lon));
            }
            else if (s.startsWith("Lower Left"))
            {
                double lat = 0d, lon = 0d;
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                bbox.add(LatLon.fromDegrees(lat, lon));
            }
            else if (s.startsWith("Upper Right"))
            {
                double lat = 0d, lon = 0d;
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                bbox.add(LatLon.fromDegrees(lat, lon));
            }
            else if (s.startsWith("Lower Right"))
            {
                double lat = 0d, lon = 0d;
                Scanner sc = new Scanner(s).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\(");
                if (sc.hasNextDouble())
                {
                    lon = sc.nextDouble();
                }

                if (sc.hasNextDouble())
                {
                    lat = sc.nextDouble();
                }

                bbox.add(LatLon.fromDegrees(lat, lon));
            }
            else if (s.trim().startsWith("NoData"))
            {
                Scanner sc = new Scanner(s.trim()).useDelimiter("\\p{javaWhitespace}|\\(|\\)|\\=|\\, *");

                sc.findInLine("\\=");
                if (sc.hasNextDouble())
                {
                    metadata.setValue(AVKey.MISSING_DATA_SIGNAL, sc.nextDouble());
                }
            }
//            Logging.logger().info( s );
        }

        if (bbox.size() > 1)
        {
            metadata.setValue(AVKey.SECTOR, Sector.boundingSector(bbox));
        }

        if (!metadata.hasKey(AVKey.MISSING_DATA_SIGNAL))
        {
            metadata.setValue(AVKey.MISSING_DATA_SIGNAL, 0d);
        }

        if (!metadata.hasKey(AVKey.MISSING_DATA_REPLACEMENT))
        {
            metadata.setValue(AVKey.MISSING_DATA_REPLACEMENT, 0d);
        }

        return metadata;
    }

    protected class ExecThread extends Thread
    {
        InputStream is;
        StringBuffer sb = new StringBuffer();

        ExecThread(InputStream is)
        {
            this.is = is;
        }

        public void run()
        {
            try
            {
                this.sb.setLength(0);

                InputStreamReader isr = new InputStreamReader(this.is);
                BufferedReader br = new BufferedReader(isr);

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.length() > 0)
                    {
                        this.sb.append(line).append("\n");
                    }
                }
            }
            catch (IOException ignore)
            {
            }
        }

        public String getOutput()
        {
            return this.sb.toString();
        }
    }

    protected class Watchdog extends TimerTask
    {
        public static final long MAX_WAIT_TIME = 1L;

        protected String taskID = "[GDAL WATCHDOG]";
        protected Process process;
        protected String pid;
        protected ExecThread err, out;

        public Watchdog(Process p, String pid, ExecThread err, ExecThread out)
        {
            super();
            this.process = p;
            this.pid = pid;
            this.err = err;
            this.out = out;
            this.taskID = "[GDAL WATCHDOG for " + pid + "] ";
        }

        public void run()
        {
            Logging.logger().severe(this.taskID + ": killing process !!!!!!!!!!!!!!!!!!!!!");

            if (null != this.err)
            {
                try
                {
                    Logging.logger().severe(this.taskID + ": error stream: " + err.getOutput());
                    this.err.join(MAX_WAIT_TIME);
                }
                catch (Exception ignore)
                {
                }
            }

            if (null != this.out)
            {
                try
                {
                    Logging.logger().severe(this.taskID + ": output stream: " + out.getOutput());
                    this.out.join(MAX_WAIT_TIME);
                }
                catch (Exception ignore)
                {
                }
            }

            if (null != this.process)
            {
                WWIO.closeStream(this.process.getInputStream(), "InputStream");
                WWIO.closeStream(this.process.getErrorStream(), "ErrorStream");
                WWIO.closeStream(this.process.getOutputStream(), "OutputStream");

                if (Configuration.isWindowsOS())
                {
                    this.destroyWindowsProcess("gdalwarp.exe");
                    this.destroyWindowsProcess("gdal_translate.exe");
                }

                this.process.destroy();
                Logging.logger().finest(this.taskID + ":  killed. ");
            }
        }

        protected void destroyWindowsProcess(String processImageName)
        {
            if (Configuration.isWindowsOS() && !WWUtil.isEmpty(processImageName))
            {
                try
                {
                    String killCommand = "taskkill /F /T /IM " + processImageName;

                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(killCommand);
                }
                catch (Throwable t)
                {
                    Logging.logger().finest(WWUtil.extractExceptionReason(t));
                }
            }
        }
    }
}


