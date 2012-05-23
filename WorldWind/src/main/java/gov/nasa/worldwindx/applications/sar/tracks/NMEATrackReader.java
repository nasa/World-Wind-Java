/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar.tracks;

import gov.nasa.worldwind.tracks.Track;
import gov.nasa.worldwind.formats.nmea.NmeaReader;

import java.io.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class NMEATrackReader extends AbstractTrackReader
{
    public NMEATrackReader()
    {
    }

    public String getDescription()
    {
        return "National Marine Electronics Association (*.nmea)";
    }

    protected Track[] doRead(InputStream inputStream) throws IOException
    {
        NmeaReader reader = new NmeaReader();
        reader.readStream(inputStream, null); // un-named stream
        return this.asArray(reader.getTracks());
    }

    protected boolean acceptFilePath(String filePath)
    {
        return filePath.toLowerCase().endsWith(".nmea");
    }
}
