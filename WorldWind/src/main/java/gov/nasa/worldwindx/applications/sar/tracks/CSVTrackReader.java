/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar.tracks;

import gov.nasa.worldwind.tracks.Track;
import gov.nasa.worldwind.formats.csv.CSVReader;

import java.io.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class CSVTrackReader extends AbstractTrackReader
{
    public CSVTrackReader()
    {
    }

    public String getDescription()
    {
        return "Comma Separated Value (*.csv)";
    }

    protected Track[] doRead(InputStream inputStream) throws IOException
    {
        CSVReader reader = new CSVReader();
        reader.readStream(inputStream, null); // un-named stream
        return this.asArray(reader.getTracks());
    }

    protected boolean acceptFilePath(String filePath)
    {
        return filePath.toLowerCase().endsWith(".csv");
    }
}
