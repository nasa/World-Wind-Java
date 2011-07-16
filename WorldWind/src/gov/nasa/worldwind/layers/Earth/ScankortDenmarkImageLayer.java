/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.layers.Earth;

import gov.nasa.worldwind.util.WWXML;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import gov.nasa.worldwind.avlist.AVKey;
import org.w3c.dom.Document;

/**
 * @author tag
 * @version $Id$
 */
public class ScankortDenmarkImageLayer extends WMSTiledImageLayer
{
    public ScankortDenmarkImageLayer()
    {
        super(getConfigurationDocument(), null);
    }

    private static Document getConfigurationDocument()
    {
        return WWXML.openDocumentFile("config/Earth/ScankortDenmarkImageLayer.xml", null);
    }

    public String toString()
    {
        Object o = this.getValue(AVKey.DISPLAY_NAME);
        return o != null ? (String) o : "Scankort Denmark Imagery";
    }
}
