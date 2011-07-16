/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar.actions;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwindx.examples.util.ScreenShotAction;

import javax.swing.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class SARScreenShotAction extends ScreenShotAction
{
    public SARScreenShotAction(WorldWindow wwd, Icon icon)
    {
        super(wwd);
        this.putValue(Action.NAME, "Screen Shot...");
        this.putValue(Action.SHORT_DESCRIPTION, "Save a screen shot");
        this.putValue(Action.SMALL_ICON, icon);
    }
}
