/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.examples.util;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class RandomShapeAttributes
{
    protected int attrIndex = 0;
    protected PointPlacemarkAttributes[] pointAttrs;
    protected ShapeAttributes[] polylineAttrs;
    protected ShapeAttributes[] polygonAttrs;

    public RandomShapeAttributes()
    {
        this.initialize();
    }

    protected void initialize()
    {
        this.pointAttrs = new PointPlacemarkAttributes[]
            {
                this.createPointAttributes(Color.YELLOW),
                this.createPointAttributes(Color.BLUE),
                this.createPointAttributes(Color.RED),
                this.createPointAttributes(Color.GREEN),
                this.createPointAttributes(Color.CYAN),
                this.createPointAttributes(Color.ORANGE),
                this.createPointAttributes(Color.MAGENTA),
            };

        this.polylineAttrs = new ShapeAttributes[]
            {
                this.createPolylineAttributes(Color.YELLOW),
                this.createPolylineAttributes(Color.BLUE),
                this.createPolylineAttributes(Color.RED),
                this.createPolylineAttributes(Color.GREEN),
                this.createPolylineAttributes(Color.CYAN),
                this.createPolylineAttributes(Color.ORANGE),
                this.createPolylineAttributes(Color.MAGENTA),
            };

        this.polygonAttrs = new ShapeAttributes[]
            {
                this.createPolygonAttributes(Color.YELLOW),
                this.createPolygonAttributes(Color.BLUE),
                this.createPolygonAttributes(Color.RED),
                this.createPolygonAttributes(Color.GREEN),
                this.createPolygonAttributes(Color.CYAN),
                this.createPolygonAttributes(Color.ORANGE),
                this.createPolygonAttributes(Color.MAGENTA),
            };
    }

    public PointPlacemarkAttributes nextPointAttributes()
    {
        return this.pointAttrs[this.attrIndex++ % this.pointAttrs.length];
    }

    public ShapeAttributes nextPolylineAttributes()
    {
        return this.polylineAttrs[this.attrIndex++ % this.polylineAttrs.length];
    }

    public ShapeAttributes nextPolygonAttributes()
    {
        return this.polygonAttrs[this.attrIndex++ % this.polygonAttrs.length];
    }

    protected PointPlacemarkAttributes createPointAttributes(Color color)
    {
        PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
        attrs.setUsePointAsDefaultImage(true);
        attrs.setLineMaterial(new Material(color));
        attrs.setScale(7d);
        return attrs;
    }

    protected ShapeAttributes createPolylineAttributes(Color color)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(new Material(color));
        attrs.setOutlineWidth(1.5);
        return attrs;
    }

    protected ShapeAttributes createPolygonAttributes(Color color)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(new Material(color));
        attrs.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
        attrs.setInteriorOpacity(0.5);
        attrs.setOutlineWidth(3);
        attrs.setDrawInterior(false);
        return attrs;
    }
}
