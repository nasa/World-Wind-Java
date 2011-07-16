/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.Sector;

import java.io.File;

/**
 * @author garakl
 * @version $Id$
 */
class TileFile
{
    protected File file = null;
    protected Sector sector = null;

    public File getFile()
    {
        return this.file;
    }

    public Sector getSector()
    {
        return this.sector;
    }

    public TileFile(File file, Sector sector)
    {
        this.file = file;
        this.sector = sector;
    }
}
