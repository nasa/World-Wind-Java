/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.layers.LatLonGraticuleLayer;

/**
 * Displays the globe with a latitude and longitude graticule (latitude and longitude grid). The graticule is its own
 * layer and can be turned on and off independent of other layers. As the view zooms in, the graticule adjusts to
 * display a finer grid.
 *
 * @author Patrick Murris
 * @version $Id$
 */
public class Graticule extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            // Add the graticule layer
            insertBeforePlacenames(getWwd(), new LatLonGraticuleLayer());

            // Update layer panel
            this.getLayerPanel().update(this.getWwd());
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Lat-Lon Graticule", AppFrame.class);
    }
}