/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class AVListMapSource extends MapSource
{
    public AVListMapSource( MapSource parent, AVList params )
    {
        super( parent, ( null != params ) ? params : new AVListImpl() );
    }

    @Override
    public void setScaleHint(double min, double max)
    {
        this.setValue( AVKey.SCALE_HINT_MIN, min );
        this.setValue( AVKey.SCALE_HINT_MAX, max );
        super.setScaleHint( min, max );
    }

    @Override
    public double getScaleHintMax()
    {
        try
        {
            if( this.hasValue( AVKey.SCALE_HINT_MAX ))
            {
                return (Double)this.getValue( AVKey.SCALE_HINT_MAX );
            }
            else if( this.hasValue( AVKey.PIXEL_HEIGHT ))
            {
                return (Double)this.getValue( AVKey.PIXEL_HEIGHT );
            }
            else if( this.hasValue( AVKey.HEIGHT ) && this.hasValue(AVKey.SECTOR) )
            {
                Double height = (Double)this.getValue( AVKey.HEIGHT );
                if( 0d != height )
                    return ((Sector)this.getValue(AVKey.SECTOR)).getDeltaLatDegrees() / height;
                    
            }
        }
        catch(Exception e)
        {
            Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
        }

        return 0d;
    }

    @Override
    public String getMissingDataSignal()
    {
        return ( this.hasValue( AVKey.MISSING_DATA_SIGNAL )) ?
                this.getValueAsString( AVKey.MISSING_DATA_SIGNAL ) : null ;
    }

    @Override
    public double getScaleHintMin()
    {
        if( this.hasValue( AVKey.SCALE_HINT_MIN))
        {
            return (Double)this.getValue( AVKey.SCALE_HINT_MIN );
        }
        else
            return 1d;
    }

    @Override
    public String getName()
    {
        return this.getValueAsString( AVKey.LAYER_NAME );
    }

    @Override
    public String getTitle()
    {
        return this.getValueAsString( AVKey.TITLE );
    }

    @Override
    public Class getServiceClass()
    {
        return (Class)params.getValue( AVKey.SERVICE_NAME );
    }
}
