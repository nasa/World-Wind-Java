/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.applications.ve;

import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $
 */

public class VirtualEarthLayer
{
    public static final VirtualEarthLayer AERIAL = new VirtualEarthLayer( "Aerial", "a", ".jpg" );
    public static final VirtualEarthLayer HYBRID = new VirtualEarthLayer( "Hybrid", "h", ".jpg" );
    public static final VirtualEarthLayer ROADS  = new VirtualEarthLayer( "Roads" , "r", ".png" );

    protected String name;
    protected String letter;
    protected String ext;

    protected VirtualEarthLayer(String name, String letter, String ext )
    {
        this.name = name;
        this.letter = letter;
        this.ext = ext;
    }

    public String getName()
    {
        return name;
    }

    public String getLetter()
    {
        return letter;
    }

    public String getExt()
    {
        return ext;
    }

    public static final VirtualEarthLayer parse(String layerName)
    {
        if( null == layerName )
        {
            String message = Logging.getMessage("nullValue.LayerNamesIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (AERIAL.letter.equalsIgnoreCase( layerName ))
            return AERIAL;
        else if (HYBRID.letter.equalsIgnoreCase( layerName ))
            return HYBRID;
        else if (ROADS.letter.equalsIgnoreCase( layerName ))
            return ROADS;
        else
        {
            String message = Logging.getMessage("generic.UnrecognizedLayer", layerName );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }
}
