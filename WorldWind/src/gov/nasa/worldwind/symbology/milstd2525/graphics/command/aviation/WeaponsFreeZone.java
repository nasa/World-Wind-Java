/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.render.*;

/**
 * Implementation of the Weapons Free Zone graphic (2.X.2.2.3.5).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class WeaponsFreeZone extends AviationZone
{
    /** Path to the image used for the polygon fill pattern. */
    protected static final String DIAGONAL_FILL_PATH = "images/diagonal-fill-128x128.png";

    /** Function ID for Weapons Free Zone (2.X.2.2.3.5). */
    public final static String FUNCTION_ID = "AAW---";

    /** {@inheritDoc} */
    @Override
    protected String getGraphicLabel()
    {
        return "WFZ";
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
     * {@inheritDoc} Overridden to not include the altitude modifier in the label. This graphic does not support the
     * altitude modifier.
     */
    @Override
    protected String createLabelText()
    {
        return doCreateLabelText(false);
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
}
