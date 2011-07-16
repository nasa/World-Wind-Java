/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.cache.AbstractFileStore;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.util.*;

import java.io.InputStream;
import java.net.URL;

/**
 * @author dcollins
 * @version $Id$
 */
public class WMSDataFileStore extends AbstractFileStore
{
    public WMSDataFileStore(String configFilePath)
    {
        if (configFilePath == null)
        {
            String message = Logging.getMessage("nullValue.FilePathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        InputStream inputStream = WWIO.openFileOrResourceStream(configFilePath, this.getClass());
        if (inputStream == null)
        {
            String message = Logging.getMessage("generic.CannotOpenFile", configFilePath);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.initialize(inputStream);
        this.disableReadLocationsMarkWhenUsed();
    }

    protected void disableReadLocationsMarkWhenUsed()
    {
        for (StoreLocation location : this.readLocations)
        {
            location.setMarkWhenUsed(false);
        }
    }

    @Override
    public String getContentType(String address)
    {
        // TODO garakl
        // this method must bee implemented in the AbstractFileStore class, not in the BasicDataFileStore
        throw new WWRuntimeException(Logging.getMessage("generic.UnsupportedOperation"));
    }

    @Override
    public void removeFile(String address)
    {
        // TODO garakl
        // this method must bee implemented in the AbstractFileStore class, not in the BasicDataFileStore
        throw new WWRuntimeException(Logging.getMessage("generic.UnsupportedOperation"));
    }

    @Override
    public URL requestFile(String address)
    {
        // TODO garakl
        // this method must bee implemented in the AbstractFileStore class, not in the BasicDataFileStore
        throw new WWRuntimeException(Logging.getMessage("generic.UnsupportedOperation"));
    }

    @Override
    public URL requestFile(String address, boolean cacheRemoteFile)
    {
        // TODO garakl
        // this method must bee implemented in the AbstractFileStore class, not in the BasicDataFileStore
        throw new WWRuntimeException(Logging.getMessage("generic.UnsupportedOperation"));
    }
}
