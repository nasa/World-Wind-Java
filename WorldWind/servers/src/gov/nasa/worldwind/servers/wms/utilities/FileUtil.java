/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.wms.utilities;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Lado Garakanidze
 * @version $
 */

public class FileUtil
{
    private FileUtil()
    {
    }

    /**
     * Delete a file;  This method DOES NOT throw any exception, any errors will be logged.
     *
     * @param src - a String with a filename or File object
     *
     * @return true, if file was deleted successfully or never existed;
     */
    public static boolean deleteFile(Object src)
    {
        boolean success = false;

        try
        {
            File f = WWIO.getFileForLocalAddress(src);
            if( null == f )
            {
                String msg = Logging.getMessage("generic.UnrecognizedSourceTypeOrUnavailableSource", src);
                Logging.logger().fine(msg);
            }
            else if (!f.exists())
            {
                success = true;
                String msg = Logging.getMessage("generic.FileDoesNotExists", src);
                Logging.logger().finest(msg);
            }
            else
            {
                success = f.delete();
                if (!success || f.exists())
                {

                    String msg = Logging.getMessage("generic.CannotDeleteFile", src);
                    Logging.logger().fine(msg);
                }
            }
        }
        catch (Throwable t)
        {
            String msg = Logging.getMessage("generic.CannotDeleteFile", src);
            Logging.logger().fine(msg);
            Logging.logger().log(Level.FINEST, WWUtil.extractExceptionReason(t), t);
        }

        return success;
    }

    /**
     * Quietly deletes an existing file;
     * In most cases you just want to cleanup temporary files and throwing exceptions is useless.
     * This method DOES NOT throw any exception, only significant errors will be logged
     * (example: if file after the delete still exists)
     *
     * @param file - File object, could be <code>null</code>
     *
     */
    public static void delete(File file)
    {
        if( null != file && file.exists() )
        {
            try
            {
                boolean success = file.delete();
                if ( !success || file.exists() )
                {
                    String msg = Logging.getMessage("generic.CannotDeleteFile", file.getAbsolutePath());
                    Logging.logger().finest(msg);
                }
            }
            catch (Throwable t)
            {
                String reason = WWUtil.extractExceptionReason(t);
                String msg = Logging.getMessage("generic.CannotDeleteFile", reason );
                Logging.logger().finest(msg);
            }
        }
    }

    /**
     * Move from one file to another; if OS dependant rename fails, trys a copy/delete method
     *
     * @param fromFilePath  source file
     * @param toFilePath    destination file
     * @param allowOverride allow override existing destination file
     *
     * @throws IOException              if fails to create a destination file or its parent directories
     * @throws IllegalArgumentException if any of the from/to files are null
     */
    public static void moveFile(String fromFilePath, String toFilePath, boolean allowOverride)
        throws IOException, IllegalArgumentException
    {
        if (fromFilePath == null)
        {
            String msg = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().finest(msg);
            throw new IllegalArgumentException(msg);
        }

        if (toFilePath == null)
        {
            String msg = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().finest(msg);
            throw new IllegalArgumentException(msg);
        }

        File fromFile = new File(fromFilePath);
        if (!fromFile.exists())
        {
            String msg = Logging.getMessage("generic.FileDoesNotExists", fromFilePath);
            Logging.logger().finest(msg);
            throw new IOException(msg);
        }

        File toFile = new File(toFilePath);
        if (toFile.exists())
        {
            if (!allowOverride)
            {
                String msg = Logging.getMessage("generic.FileAlreadyExists", toFilePath);
                Logging.logger().finest(msg);
                throw new IOException(msg);
            }

            deleteFile(toFilePath);
        }
        else
        {
            String parentPath = WWIO.getParentFilePath(toFilePath);
            if (null != parentPath)
            {
                File parentDir = new File(parentPath);
                if (!parentDir.exists())
                {
                    WWIO.makeParentDirs(toFilePath);
                }

                if (!parentDir.exists())
                {
                    String msg = Logging.getMessage("generic.CannotCreateDirectory", parentPath);
                    Logging.logger().finest(msg);
                    throw new IOException(msg);
                }
            }
        }

        boolean success;
        try
        {
            success = fromFile.renameTo(toFile);
        }
        catch (Exception e)
        {
            success = false;
            String msg = Logging.getMessage("generic.CannotMoveFile", fromFilePath, toFilePath);
            Logging.logger().log(Level.SEVERE, msg, e);
        }

        // if rename/move was not successful, let's try to copy and delete
        // but first we need to make sure that the destination was already removed
        // otherwise, we will never know if the target was created
        // TODO: we need to use CRC32 to make sure target was successfully copied
        if (!success && !toFile.exists())
        {
            try
            {
                WWIO.copyFile(fromFile, toFile);
                // reload toFile object, make sure it gets updated length
                toFile = new File(toFilePath);
                if (toFile.exists() && toFile.length() == fromFile.length())
                {
                    deleteFile(fromFilePath);
                    success = true;
                }
            }
            catch (Exception e)
            {
                success = false;
                String msg = Logging.getMessage("generic.CannotMoveFile", fromFilePath, toFilePath);
                Logging.logger().log(Level.SEVERE, msg, e);
            }
        }

        if (!success)
        {
            String msg = Logging.getMessage("generic.CannotMoveFile", fromFilePath, toFilePath);
            Logging.logger().severe(msg);
            throw new IOException(msg);
        }
    }

    /**
     * Locates a configuration file. If file is not found will try to find the file in the WEB-INF and "config"
     * sub-folders.
     *
     * @param filePath a file path or filename
     *
     * @return File object
     *
     * @throws IOException if path is null, or the file was not found
     */
    public static File locateConfigurationFile(String filePath) throws IOException
    {
        if (WWUtil.isEmpty(filePath))
        {
            throw new IOException(Logging.getMessage("nullValue.FilePathIsNull"));
        }

        File file = new File(filePath);
        if (file.exists())
        {
            return file;
        }

        String filename = file.getName();

        file = new File("WEB-INF" + File.separator + filename);
        if (file.exists())
        {
            return file;
        }

        file = new File("config" + File.separator + filename);
        if (file.exists())
        {
            return file;
        }

        throw new IOException(Logging.getMessage("generic.FileNotFound", filePath));
    }
}
