/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import java.util.EventListener;

/**
 * @author garakl
 * @version $Id$
 */
public interface WMSLayerEventListener extends EventListener
{
    void layerAdded( WMSLayerEvent e );

    void layerRemoved( WMSLayerEvent e );

    void layerChanged( WMSLayerEvent e );
}
