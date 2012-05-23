/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.gdal;

import java.io.File;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
class GDALDataFinder extends GDALAbstractFileFilter
{
    public GDALDataFinder()
    {
        super("gdal_datum.csv");
    }

    public boolean accept(File pathname)
    {
        String filename;
        String dir;
        if (null != pathname
            && !isHidden(pathname.getAbsolutePath())
            && null != (dir = pathname.getParent())
            && !this.listFolders.contains(dir)                  // skip already discovered
            && null != (filename = pathname.getName())          // get folder name
            && searchPattern.equalsIgnoreCase(filename))
        {
            this.listFolders.add(dir);
            return true;
        }
        Thread.yield();
        return false;
    }
}
