/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.core;

import gov.nasa.worldwindx.applications.worldwindow.features.Feature;

import javax.swing.*;

/**
 * @author tag
 * @version $Id$
 */
public interface MenuBar extends Feature
{
    JMenuBar getJMenuBar();

    void addMenu(Menu menu);
}
