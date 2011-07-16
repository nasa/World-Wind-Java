/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

/**
 * @author garakl
 * @version $Id$
 */

public class Option // extends AVListImpl
{
    public static final Option Debug_ON  = new Option( "--debug", "ON" );
    public static final Option Debug_OFF = new Option( "--debug", "OFF" );

    private final String key  ;
    private final String value ;

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public Option(String key )
    {
        this.key = key;
        this.value = null;
    }
    public Option(String key, String value )
    {
        this.key = key;
        this.value = value;
    }

    public Option(String key, String name, String value )
    {
        this.key = key;
        this.value = "\"" + name + "=" + value + "\"" ;
    }

    public String toString()
    {
        return    (( null != this.key  && this.key.length() > 0) ? (" " + this.key) : "" )
                + (( null != this.value  && this.value.length() > 0) ? (" " + this.value) : "" );
    }


    public static class Warp extends Option
    {
        public static final Warp Quiet = new Warp( "-q" );
        public static final Warp Multi = new Warp( "-multi" );

        protected Warp( String key )
        {
            super( key, null );
        }

        protected Warp(String key, String value )
        {
            super(key, value );
        }

        public static class Resampling extends Warp
        {
            public static final Resampling NearNeighbour = new Resampling( "near"       );
            public static final Resampling Bilinear      = new Resampling( "bilinear"   );
            public static final Resampling Cubic         = new Resampling( "cubic"      );
            public static final Resampling CubicSpline   = new Resampling( "cubicspline");
            public static final Resampling Lanczos       = new Resampling( "lanczos"    );

            protected Resampling( String value )
            {
                super("-r", value );
            }
        }
    
    }


    public static class Translate extends Option
    {
        public static final Translate Quiet     = new Translate( "-quiet" );
        public static final Translate Strict    = new Translate( "-strict" );

        protected Translate( String key )
        {
            super( key, null );
        }

        protected Translate(String key, String value )
        {
            super(key, value );
        }
        
        public static Translate projWin( double ulx, double uly, double lrx, double lry )
        {
            return new Translate( "-projwin", ulx + " " + uly + " " + lrx + " " + lry );
        }

        public static Translate projWin( int ulx, int uly, int lrx, int lry )
        {
            return new Translate( "-projwin", ulx + " " + uly + " " + lrx + " " + lry );
        }


        public static Translate a_ullr( double ulx, double uly, double lrx, double lry )
        {
            return new Translate( "-a_ullr", ulx + " " + uly + " " + lrx + " " + lry );
        }

        public static Translate srcWin( int xOff, int yOff, int xSize, int ySize )
        {
            return new Translate( "-srcwin", xOff + " " + yOff + " " + xSize + " " + ySize );
        }

        public static Translate outSize( int xSize, int ySize )
        {
            return new Translate( "-outsize", xSize + " " + ySize );
        }

    }
}
