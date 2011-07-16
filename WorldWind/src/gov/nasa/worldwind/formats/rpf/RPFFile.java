/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.formats.rpf;

import gov.nasa.worldwind.formats.nitfs.*;

import java.io.*;

/**
 * @author lado
 * @version $Id: RpfFile Apr 8, 2007 8:53:48 AM
 */
public class RPFFile
{
    private NITFSMessage nitfsMsg;
    private     java.io.File        rpfFile;

    public File getFile()
    {
        return this.rpfFile;
    }

    public NITFSFileHeader getNITFSFileHeader()
    {
        return (null != nitfsMsg) ? nitfsMsg.getNITFSFileHeader() : null;
    }

    public NITFSSegment getNITFSSegment(NITFSSegmentType segmentType)
    {
        return (null != nitfsMsg) ? nitfsMsg.getSegment(segmentType) : null;
    }

    protected RPFFile(java.io.File rpfFile) throws IOException
    {
        this.rpfFile = rpfFile;
        this.nitfsMsg = NITFSMessage.load(rpfFile);
    }
}
