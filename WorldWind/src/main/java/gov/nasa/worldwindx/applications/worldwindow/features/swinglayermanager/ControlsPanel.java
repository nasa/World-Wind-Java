/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.features.swinglayermanager;

import gov.nasa.worldwindx.applications.worldwindow.core.*;

import javax.swing.*;

/**
 * @author tag
 * @version $Id$
 */
public interface ControlsPanel extends Initializable, WWOPanel
{
    JPanel getJPanel();
}
