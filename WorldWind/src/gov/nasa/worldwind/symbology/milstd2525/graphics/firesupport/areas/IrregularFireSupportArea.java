/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.BasicArea;

/**
 * Implementation of the irregular Fire Support area graphics. This class implements the following graphics:
 *
 * <ul>
 *    <li>Area Target (2.X.4.3.1)</li>
 *    <li>Bomb (2.X.4.3.1.5)</li>
 *    <li>Airspace Coordination Area (ACA), Irregular (2.X.4.3.2.2.1)</li>
 *    <li>Free Fire Area (FFA), Irregular (2.X.4.3.2.3.1)</li>
 *    <li>Restrictive Fire Area (RFA), Irregular (2.X.4.3.2.5.1)</li>
 *    <li>Terminally Guided Munitions Footprint</li>
 * </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class IrregularFireSupportArea extends BasicArea
{
    /** Function ID of the Bomb graphic (2.X.4.3.1.5). */
    public final static String FUNCTION_ID_BOMB = "ATB---";
    /** Function ID of the Area Target graphic (2.X.4.3.1). */
    public final static String FUNCTION_ID_TARGET = "AT----";
    /** Function ID for the Free Fire Area graphic (2.X.4.3.2.3.1). */
    public final static String FUNCTION_ID_FFA = "ACFI--";
    /** Function ID for the Restrictive Fire Area graphic (2.X.4.3.2.5.1). */
    public final static String FUNCTION_ID_RFA = "ACRI--";
    /** Function ID of the Airspace Coordination Area graphic (2.X.4.3.2.2.1). */
    public final static String FUNCTION_ID_ACA = "ACAI--";
    /** Function ID of the Terminally Guided Munitions Footprint graphic. */
    public final static String FUNCTION_ID_TERMINALLY_GUIDED_MUNITIONS_FOOTPRINT = "ACT---";

    /** Center text block on label position when the text is left aligned. */
    protected final static Offset LEFT_ALIGN_OFFSET = new Offset(-0.5d, -0.5d, AVKey.FRACTION, AVKey.FRACTION);

    /** Create the area graphic. */
    public IrregularFireSupportArea()
    {
        this.setShowHostileIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    @Override
    protected String createLabelText()
    {
        FireSupportTextBuilder textBuilder = new FireSupportTextBuilder();
        return textBuilder.createText(this)[0];
    }

    /**
     * Indicates the alignment of the graphic's main label.
     *
     * @return Alignment for the main label. One of AVKey.CENTER, AVKey.LEFT, or AVKey.RIGHT.
     */
    @Override
    protected String getLabelAlignment()
    {
        boolean isACA = FUNCTION_ID_ACA.equals(this.getFunctionId());

        // Airspace Coordination Area labels are left aligned. All others are center aligned.
        if (isACA)
            return AVKey.LEFT;
        else
            return AVKey.CENTER;
    }

    /**
     * Indicates the default offset applied to the graphic's main label. This offset may be overridden by the graphic
     * attributes.
     *
     * @return Offset to apply to the main label.
     */
    @Override
    protected Offset getDefaultLabelOffset()
    {
        boolean isACA = FUNCTION_ID_ACA.equals(this.getFunctionId());

        // Airspace Coordination Area labels are left aligned. Adjust the offset to center the left aligned label
        // in the circle. (This is not necessary with a center aligned label because centering the text automatically
        // centers the label in the circle).
        if (isACA)
            return LEFT_ALIGN_OFFSET;
        else
            return super.getDefaultLabelOffset();
    }
}
