/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.FileTree;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVKey;

import java.io.IOException;
import java.io.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class CreateSingleFileImageLayers extends AbstractMapGenerator implements FileFilter
{
    protected static final String[] CRS = {"EPSG:4326"};

    @Override
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.mapSource = mapSource;

        File source = new File(this.mapSource.getRootDir());
        if (!source.exists())
        {
            String msg = Logging.getMessage("generic.FolderDoesNotExist", this.mapSource.getRootDir());
            Logging.logger().severe(msg);
            throw new FileNotFoundException(msg);
        }

        this.findImageFiles( source );
        
        return true;
    }

    @Override
    public ServiceInstance getServiceInstance()
    {
        return null;
    }

    @Override
    public Sector getBBox()
    {
        // TODO re-current ask children
        return null;
    }

    @Override
    public String[] getCRS()
    {
        return CRS;
    }

    @Override
    public String getDataType()
    {
        return "imagery";
    }

    @Override
    public double getPixelSize()
    {
        // TODO re-current ask children
        return 0;
    }

    @Override
    public boolean accept(File pathname)
    {
        String filename = null;
        if (    null != pathname
                && pathname.isFile()
                && null != (filename = pathname.getName())
                && (    filename.endsWith(".tiff")
                     || filename.endsWith(".tif")
                     || filename.endsWith(".gtif")
                     || filename.endsWith(".gtiff")
                   )
           )
        {
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();
                AVList params = gdal.info( this.mapSource.getName(), pathname );

                if( null != params
                    && params.hasKey( AVKey.SECTOR )
                    && params.hasKey( AVKey.WIDTH )
                    && params.hasKey( AVKey.HEIGHT )
                    && params.hasKey( AVKey.LAYER_NAME )
                  )
                {
                    params.setValue( AVKey.SERVICE_NAME, SingleFileImageLayer.class );

                    AVListMapSource ms = new AVListMapSource( this.mapSource, params );

                    this.mapSource.addChild( ms );

//                    WMSServer.getMapSourceRegistry().add( ms );

                    Logging.logger().finest("Added --> " + pathname.getAbsolutePath());
                }
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return false;
    }

    private void findImageFiles(File folder)
    {
        Logging.logger().info("Searching in the folder " + folder.getAbsolutePath());

        FileTree fileTree = new FileTree(folder);
        fileTree.setMode( FileTree.FILES_ONLY );
        fileTree.asList(this);
    }
}
