/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class KMLSurfacePolygonImpl extends SurfacePolygon implements KMLRenderable
{
    protected final KMLAbstractFeature parent;
    protected boolean highlightAttributesResolved = false;
    protected boolean normalAttributesResolved = false;

    /**
     * Create an instance.
     *
     * @param tc        the current {@link KMLTraversalContext}.
     * @param placemark the <i>Placemark</i> element containing the <i>LineString</i>.
     * @param geom      the {@link gov.nasa.worldwind.ogc.kml.KMLPolygon} geometry.
     *
     * @throws NullPointerException     if the geometry is null.
     * @throws IllegalArgumentException if the parent placemark or the traversal context is null.
     */
    public KMLSurfacePolygonImpl(KMLTraversalContext tc, KMLPlacemark placemark, KMLAbstractGeometry geom)
    {
        if (tc == null)
        {
            String msg = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (placemark == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.parent = placemark;

        KMLPolygon polygon = (KMLPolygon) geom;

        // KMLPolygon's use linear interpolation between corners by definition. Configure the World Wind SurfacePolygon
        // to use the appropriate path type for linear interpolation in geographic coordinates.
        this.setPathType(AVKey.LINEAR);

        // Note: SurfacePolygon implies altitude mode "clampToGround", therefore KMLSurfacePolygonImpl ignores the
        // KMLPolygon's altitude mode property.

        KMLLinearRing outerBoundary = polygon.getOuterBoundary();
        if (outerBoundary != null)
        {
            Position.PositionList coords = outerBoundary.getCoordinates();
            if (coords != null && coords.list != null)
                this.setOuterBoundary(outerBoundary.getCoordinates().list);
        }

        Iterable<? extends KMLLinearRing> innerBoundaries = polygon.getInnerBoundaries();
        if (innerBoundaries != null)
        {
            for (KMLLinearRing ring : innerBoundaries)
            {
                Position.PositionList coords = ring.getCoordinates();
                if (coords != null && coords.list != null)
                    this.addInnerBoundary(ring.getCoordinates().list);
            }
        }

        if (placemark.getName() != null)
            this.setValue(AVKey.DISPLAY_NAME, placemark.getName());

        if (placemark.getDescription() != null)
            this.setValue(AVKey.DESCRIPTION, placemark.getDescription());

        if (placemark.getSnippetText() != null)
            this.setValue(AVKey.SHORT_DESCRIPTION, placemark.getSnippetText());

        this.setValue(AVKey.CONTEXT, this.parent);
    }

    /**
     * Create a surface polygon from a KML GroundOverlay.
     *
     * @param tc      the current {@link KMLTraversalContext}.
     * @param overlay the {@link gov.nasa.worldwind.ogc.kml.KMLGroundOverlay} to render as a polygon.
     *
     * @throws NullPointerException     if the geometry is null.
     * @throws IllegalArgumentException if the parent placemark or the traversal context is null.
     */
    public KMLSurfacePolygonImpl(KMLTraversalContext tc, KMLGroundOverlay overlay)
    {
        if (tc == null)
        {
            String msg = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (overlay == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.parent = overlay;

        // Positions are specified either as a kml:LatLonBox or a gx:LatLonQuad
        Position.PositionList corners = overlay.getPositions();
        this.setOuterBoundary(corners.list);

        if (overlay.getName() != null)
            this.setValue(AVKey.DISPLAY_NAME, overlay.getName());

        if (overlay.getDescription() != null)
            this.setValue(AVKey.BALLOON_TEXT, overlay.getDescription());

        if (overlay.getSnippetText() != null)
            this.setValue(AVKey.SHORT_DESCRIPTION, overlay.getSnippetText());

        String colorStr = overlay.getColor();
        if (!WWUtil.isEmpty(colorStr))
        {
            Color color = WWUtil.decodeColorABGR(colorStr);

            ShapeAttributes attributes = new BasicShapeAttributes();
            attributes.setDrawInterior(true);
            attributes.setInteriorMaterial(new Material(color));
            this.setAttributes(attributes);
        }
    }


    public void preRender(KMLTraversalContext tc, DrawContext dc)
    {
        // If the attributes are not inline or internal then they might not be resolved until the external KML
        // document is resolved. Therefore check to see if resolution has occurred.

        if (this.isHighlighted())
        {
            if (!this.highlightAttributesResolved)
            {
                ShapeAttributes a = this.getHighlightAttributes();
                if (a == null || a.isUnresolved())
                {
                    a = this.makeAttributesCurrent(KMLConstants.HIGHLIGHT);
                    if (a != null)
                    {
                        this.setHighlightAttributes(a);
                        if (!a.isUnresolved())
                            this.highlightAttributesResolved = true;
                    }
                }
            }
        }
        else
        {
            if (!this.normalAttributesResolved)
            {
                ShapeAttributes a = this.getAttributes();
                if (a == null || a.isUnresolved())
                {
                    a = this.makeAttributesCurrent(KMLConstants.NORMAL);
                    if (a != null)
                    {
                        this.setAttributes(a);
                        if (!a.isUnresolved())
                            this.normalAttributesResolved = true;
                    }
                }
            }
        }

        this.preRender(dc);
    }

    public void render(KMLTraversalContext tc, DrawContext dc)
    {
        // We've already resolved the SurfacePolygon's attributes during the preRender pass. During the render pass we
        // simply draw the SurfacePolygon.
        this.render(dc);
    }

    /** {@inheritDoc} */
    @Override
    protected PickedObject createPickedObject(DrawContext dc, Color pickColor)
    {
        PickedObject po = super.createPickedObject(dc, pickColor);

        // Add the KMLPlacemark to the picked object as the context of the picked object.
        po.setValue(AVKey.CONTEXT, this.parent);
        return po;
    }
    
    /**
     * Determine and set the {@link Path} highlight attributes from the KML <i>Feature</i> fields.
     *
     * @param attrType the type of attributes, either {@link KMLConstants#NORMAL} or {@link KMLConstants#HIGHLIGHT}.
     *
     * @return the new attributes.
     */
    protected ShapeAttributes makeAttributesCurrent(String attrType)
    {
        ShapeAttributes attrs = this.getInitialAttributes(
            this.isHighlighted() ? KMLConstants.HIGHLIGHT : KMLConstants.NORMAL);

        // Get the KML sub-style for Line attributes. Map them to Shape attributes.

        KMLAbstractSubStyle lineSubStyle = this.parent.getSubStyle(new KMLLineStyle(null), attrType);
        if (!this.isHighlighted() || KMLUtil.isHighlightStyleState(lineSubStyle))
        {
            KMLUtil.assembleLineAttributes(attrs, (KMLLineStyle) lineSubStyle);
            if (lineSubStyle.hasField(AVKey.UNRESOLVED))
                attrs.setUnresolved(true);
        }

        // Get the KML sub-style for interior attributes. Map them to Shape attributes.

        KMLAbstractSubStyle fillSubStyle = this.parent.getSubStyle(new KMLPolyStyle(null), attrType);
        if (!this.isHighlighted() || KMLUtil.isHighlightStyleState(lineSubStyle))
        {
            KMLUtil.assembleInteriorAttributes(attrs, (KMLPolyStyle) fillSubStyle);
            if (fillSubStyle.hasField(AVKey.UNRESOLVED))
                attrs.setUnresolved(true);
        }

        attrs.setDrawInterior(((KMLPolyStyle) fillSubStyle).isFill());
        attrs.setDrawOutline(((KMLPolyStyle) fillSubStyle).isOutline());

        return attrs;
    }

    protected ShapeAttributes getInitialAttributes(String attrType)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();

        if (KMLConstants.HIGHLIGHT.equals(attrType))
        {
            attrs.setOutlineMaterial(Material.RED);
            attrs.setInteriorMaterial(Material.PINK);
        }
        else
        {
            attrs.setOutlineMaterial(Material.WHITE);
            attrs.setInteriorMaterial(Material.LIGHT_GRAY);
        }

        return attrs;
    }
}
