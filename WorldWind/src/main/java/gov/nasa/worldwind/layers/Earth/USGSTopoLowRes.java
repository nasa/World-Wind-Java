/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.layers.Earth;

import gov.nasa.worldwind.util.WWXML;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import org.w3c.dom.Document;

/**
 * @author tag
 * @version $Id$
 */
public class USGSTopoLowRes extends WMSTiledImageLayer
{
    public USGSTopoLowRes()
    {
        super(getConfigurationDocument(), null);
    }

    protected static Document getConfigurationDocument()
    {
        return WWXML.openDocumentFile("config/Earth/USGSTopoLowResLayer.xml", null);
    }
}
