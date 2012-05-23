/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.WorldWind;

/**
 * Operates completely locally, drawing data only from local caches.
 *
 * @author tag
 * @version $Id$
 */
public class LocalDataOnly extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            // Tell the status bar not to check for network activity
            this.getStatusBar().setShowNetworkStatus(false);
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("Local Data Only", AppFrame.class);

        // Force World Wind not to use the network
        WorldWind.getNetworkStatus().setOfflineMode(true);
    }
}
