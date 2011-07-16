/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.cache.FileStoreFilter;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.util.DataConfigurationFilter;
import gov.nasa.worldwind.util.Logging;

/**
 * @author dcollins
 * @version $Id$
 */
public class WMSDataConfigurationFilter implements FileStoreFilter
{
    protected DataConfigurationFilter dataConfigFilter;

    public WMSDataConfigurationFilter()
    {
        this.dataConfigFilter = new DataConfigurationFilter();
    }

    public boolean accept(FileStore fileStore, String fileName)
    {
        if (fileStore == null)
        {
            String msg = Logging.getMessage("nullValue.FileStoreIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (fileName == null)
        {
            String message = Logging.getMessage("nullValue.FilePathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.doAccept(fileStore, fileName);
    }

    protected boolean doAccept(FileStore fileStore, String fileName)
    {
        // TODO: accept these configuration file types
        // 1. BMNG
        // 2. DTED
        // 3. SRTM3
        // 4. SRTM30
        // 5. SRTM3v4
        // 6. USGS NED
        // 7. Esat
        // 8. NAIP
        // 9. RPF
        // 10. Skankort (optional)
        // * They'll likely be the same or similar XML structure

        return this.dataConfigFilter.accept(fileStore, fileName);
    }
}
