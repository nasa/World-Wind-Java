/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.applications.worldwindow.features.swinglayermanager;

import gov.nasa.worldwindx.applications.worldwindow.core.WMSLayerInfo;

/**
 * @author tag
 * @version $Id$
 */
public class LayerTreeGroupNode extends LayerTreeNode
{
    public LayerTreeGroupNode()
    {
    }

    public LayerTreeGroupNode(String title)
    {
        super(title);
    }

    public LayerTreeGroupNode(WMSLayerInfo layerInfo)
    {
        super(layerInfo);
    }

    public LayerTreeGroupNode(LayerTreeGroupNode layerNode)
    {
        super(layerNode);
    }
}
