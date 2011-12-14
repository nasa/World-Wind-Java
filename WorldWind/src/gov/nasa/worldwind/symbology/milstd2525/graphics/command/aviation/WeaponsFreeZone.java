/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;

import java.awt.*;
import java.awt.geom.*;

/**
 * Implementation of the Weapons Free Zone graphic (2.X.2.2.3.5).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class WeaponsFreeZone extends AviationZone
{
    /** Path to the image used for the polygon fill pattern. */
    protected static final String DIAGONAL_FILL_PATH = "images/diagonal-fill-16x16.png";

    /** Function ID for Weapons Free Zone (2.X.2.2.3.5). */
    public final static String FUNCTION_ID = "AAW---";

    /** Annotation used to draw the label. */
    protected GlobeAnnotation annotation;

    @Override
    protected void doRenderGraphic(DrawContext dc)
    {
        super.doRenderGraphic(dc);
        this.annotation.render(dc);
    }

    /** {@inheritDoc} */
    @Override
    protected String getGraphicLabel()
    {
        return "WFZ";
    }

    @Override
    protected void createLabels()
    {
        // Use an annotation to draw the label on a solid background. Unframed text is difficult to read against
        // the fill pattern.
        this.annotation = new GlobeAnnotation(this.createLabelText(), this.getReferencePosition());

        this.annotation.setAttributes(this.createAnnotationAttributes());
        this.annotation.setDelegateOwner(this);
    }

    protected AnnotationAttributes createAnnotationAttributes()
    {
        AnnotationAttributes attrs = new AnnotationAttributes();

        attrs.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        attrs.setFrameShape(AVKey.SHAPE_RECTANGLE);
        attrs.setLeader(AVKey.SHAPE_NONE);
        attrs.setCornerRadius(0);
        attrs.setTextAlign(AVKey.LEFT);
        attrs.setInsets(new Insets(5, 5, 5, 5));
        attrs.setDrawOffset(new Point(0, 0));

        return attrs;
    }

    @Override
    protected void applyLabelAttributes()
    {
        Material labelMaterial = this.getLabelMaterial();

        Font font = this.activeOverrides.getTextModifierFont();
        if (font == null)
            font = gov.nasa.worldwind.symbology.milstd2525.Label.DEFAULT_FONT;

        AnnotationAttributes attributes = this.annotation.getAttributes();

        Color color = labelMaterial.getDiffuse();
        attributes.setTextColor(color);
        attributes.setFont(font);

        Color backgroundColor = this.computeBackgroundColor(color);
        attributes.setBackgroundColor(backgroundColor);
    }

    /** {@inheritDoc} */
    @Override
    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        super.applyDefaultAttributes(attributes);

        // Enable the polygon interior and set the image source to draw a fill pattern of diagonal lines.
        attributes.setDrawInterior(true);
        attributes.setImageSource(this.getImageSource());
    }

    /**
     * Determine the appropriate position for the graphic's labels.
     *
     * @param dc Current draw context.
     */
    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        Position mainLabelPosition = this.determineMainLabelPosition(dc);
        this.annotation.setPosition(mainLabelPosition);

        Rectangle bounds = this.annotation.getBounds(dc);

        Offset offset = this.getLabelOffset();
        if (offset == null)
            offset = this.getDefaultLabelOffset();

        Point2D offsetPoint = offset.computeOffset(bounds.width, bounds.height, null, null);
        this.annotation.getAttributes().setDrawOffset(new Point((int) offsetPoint.getX(), (int) offsetPoint.getY()));
    }

    @Override
    protected Offset getDefaultLabelOffset()
    {
        return gov.nasa.worldwind.symbology.milstd2525.Label.DEFAULT_OFFSET;
    }

    /**
     * {@inheritDoc} Overridden to not include the altitude modifier in the label. This graphic does not support the
     * altitude modifier.
     */
    @Override
    protected String createLabelText()
    {
        return this.doCreateLabelText(false);
    }

    /**
     * Indicates the source of the image that provides the polygon fill pattern.
     *
     * @return The source of the polygon fill pattern.
     */
    protected Object getImageSource()
    {
        return DIAGONAL_FILL_PATH;
    }

    /**
     * Compute a contrasting background color to draw the label's outline.
     *
     * @param color Label color.
     *
     * @return A color that contrasts with {@code color}.
     */
    protected Color computeBackgroundColor(Color color)
    {
        float[] colorArray = new float[4];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), colorArray);

        if (colorArray[2] > 0.5)
            return new Color(0, 0, 0, 0.7f);
        else
            return new Color(1, 1, 1, 0.7f);
    }
}
