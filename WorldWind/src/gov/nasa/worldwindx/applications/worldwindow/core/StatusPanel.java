/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.core;

import gov.nasa.worldwindx.applications.worldwindow.features.FeaturePanel;

/**
 * @author tag
 * @version $Id$
 */
public interface StatusPanel extends FeaturePanel
{
    String setStatusMessage(String message);
}
