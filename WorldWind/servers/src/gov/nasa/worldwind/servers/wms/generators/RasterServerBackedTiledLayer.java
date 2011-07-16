/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.BasicRasterServer;
import gov.nasa.worldwind.servers.wms.MapSource;
import gov.nasa.worldwind.util.*;

import java.io.File;
import java.net.URL;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class RasterServerBackedTiledLayer extends WorldWindTiledLayer
{
    protected BasicRasterServer rasterServer = null;

    public RasterServerBackedTiledLayer()
    {
        super();
    }

    @Override
    protected void doInitialize(MapSource mapSource)
    {
        super.doInitialize(mapSource);

        String rasterServerFileName = DataConfigurationUtils.getDataConfigFilename(params, ".RasterServer.xml");
        URL url = this.ms.getFileStore().findFile(rasterServerFileName, false);

        File rasterServerConfigFile = WWIO.getFileForLocalAddress(url);
        if (null == rasterServerConfigFile)
        {
            String reason = Logging.getMessage("generic.FileNotFound", url);
            String msg = Logging.getMessage("generic.CannotCreateRasterServer", reason);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.rasterServer = new BasicRasterServer(url, this.params);
        if (!this.rasterServer.hasDataRasters())
        {
            String details = Logging.getMessage("generic.UnknownFileFormatOrMatchingReaderNotFound", url);
            String reason = Logging.getMessage("generic.RasterListIsEmpty", details);
            String msg = Logging.getMessage("generic.CannotCreateRasterServer", reason);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected TileCacheMosaicer doCreateMosaicer() throws Exception
    {
        AVList tileParams = null;

        if (this.isElevationModelLayer())
        {
            AVList mapSourceParams = this.params;

            tileParams = new AVListImpl();

            tileParams.setValue(AVKey.BYTE_ORDER, mapSourceParams.getValue(AVKey.BYTE_ORDER));
            tileParams.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);
            tileParams.setValue(AVKey.DATA_TYPE, mapSourceParams.getValue(AVKey.DATA_TYPE));
            // Legacy code expects the string "gov.nasa.worldwind.avkey.MissingDataValue", which now corresponds to
            // the key MISSING_DATA_REPLACEMENT.
            tileParams.setValue(AVKey.MISSING_DATA_REPLACEMENT, mapSourceParams.getValue(AVKey.MISSING_DATA_SIGNAL));
        }

        return new RasterServerBackedMosaicer(this.rasterServer, this.ms.getFileStore(), this.levels, tileParams,
            this.readers);
    }
}
