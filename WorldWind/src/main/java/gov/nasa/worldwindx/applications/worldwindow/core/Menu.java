/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.core;

import javax.swing.*;

/**
 * @author tag
 * @version $Id$
 */
public interface Menu extends Initializable
{
    JMenu getJMenu();

    void addMenu(String featureID);
}
