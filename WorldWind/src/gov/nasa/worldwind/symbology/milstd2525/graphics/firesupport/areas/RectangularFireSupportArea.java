/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Implementation of rectangular Fire Support graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Free Fire Area (FFA), Rectangular (2.X.4.3.2.3.2)</li> <li>Restrictive Fire Area (RFA), Rectangular
 * (2.X.4.3.2.5.2)</li> <li>Airspace Coordination Area (ACA), Rectangular (2.X.4.3.2.2.2)</li> <li>Sensor Zone,
 * Rectangular</li> <li>Dead Space Area, Rectangular</li> <li>Zone of Responsibility, Rectangular</li> <li>Target
 * Build-up Area</li> <li>Target Value Area, Rectangular</li> <li>Artillery Target Intelligence Zone, Rectangular
 * (2.X.4.3.3.1.2)</li> <li>Call For Fire Zone, Rectangular (2.X.4.3.3.2.2)</li> <li>Censor Zone, Rectangular
 * (2.X.4.3.3.4.2)</li> <li>Critical Friendly Zone, Rectangular (2.X.4.3.3.6.2)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class RectangularFireSupportArea extends MilStd2525TacticalGraphic implements TacticalQuad, PreRenderable
{
    /** Function ID for the Fire Support Area graphic (2.X.4.3.2.1.2). */
    public final static String FUNCTION_ID_FSA = "ACSR--";
    /** Function ID for the Free Fire Area graphic (2.X.4.3.2.3.2). */
    public final static String FUNCTION_ID_FFA = "ACFR--";
    /** Function ID for the Restrictive Fire Area graphic (2.X.4.3.2.5.2). */
    public final static String FUNCTION_ID_RFA = "ACRR--";
    /** Function ID for the Airspace Coordination Area graphic (2.X.4.3.2.2.2). */
    public final static String FUNCTION_ID_ACA = "ACAR--";
    /** Function ID for the Sensor Zone graphic. */
    public final static String FUNCTION_ID_SENSOR_ZONE = "ACER--";
    /** Function ID for the Dead Space Area graphic. */
    public final static String FUNCTION_ID_DEAD_SPACE_AREA = "ACDR--";
    /** Function ID for the Zone of Responsibility graphic. */
    public final static String FUNCTION_ID_ZONE_OF_RESPONSIBILITY = "ACZR--";
    /** Function ID for the Target Build-up Area graphic. */
    public final static String FUNCTION_ID_TARGET_BUILDUP = "ACBR--";
    /** Function ID for the Target Value Area graphic. */
    public final static String FUNCTION_ID_TARGET_VALUE = "ACVR--";
    /** Function ID for the Artillery Target Intelligence Zone graphic (2.X.4.3.3.1.2). */
    public final static String FUNCTION_ID_ATI = "AZIR--";
    /** Function ID for the Call For Fire Zone graphic (2.X.4.3.3.2.2). */
    public final static String FUNCTION_ID_CFF = "AZXR--";
    /** Function ID for the Censor Zone graphic (2.X.4.3.3.4.2). */
    public final static String FUNCTION_ID_CENSOR_ZONE = "AZCR--";
    /** Function ID for the Critical Friendly Zone graphic (2.X.4.3.3.6.2). */
    public final static String FUNCTION_ID_CF = "AZFR--";

    /** Center text block on label position when the text is left aligned. */
    protected final static Offset LEFT_ALIGN_OFFSET = new Offset(-0.5d, -0.5d, AVKey.FRACTION, AVKey.FRACTION);

    protected Iterable<? extends Position> positions;
    protected SurfaceQuad quad;

    protected boolean shapeInvalid;

    /** Create a new target. */
    public RectangularFireSupportArea()
    {
        this.quad = this.createShape();
    }

    /**
     * Indicates the function IDs of rectangular Fire Support area graphics that display a date/time range as a separate
     * label at the left side of the rectangle. Whether or not a graphic supports this is determined by the graphic's
     * template in MIL-STD-2525C.
     *
     * @return A Set containing the function IDs of graphics that support a date/time label separate from the graphic's
     *         main label.
     */
    public static Set<String> getGraphicsWithTimeLabel()
    {
        return new HashSet<String>(Arrays.asList(
            FUNCTION_ID_FSA,
            FUNCTION_ID_SENSOR_ZONE,
            FUNCTION_ID_DEAD_SPACE_AREA,
            FUNCTION_ID_ZONE_OF_RESPONSIBILITY,
            FUNCTION_ID_TARGET_BUILDUP,
            FUNCTION_ID_TARGET_VALUE,
            FUNCTION_ID_ATI,
            FUNCTION_ID_CFF,
            FUNCTION_ID_CENSOR_ZONE,
            FUNCTION_ID_CF));
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /** {@inheritDoc} */
    public double getWidth()
    {
        return this.quad.getHeight();
    }

    /** {@inheritDoc} */
    public void setWidth(double width)
    {
        //noinspection SuspiciousNameCombination
        this.quad.setHeight(width);
    }

    /** {@inheritDoc} */
    public double getLength()
    {
        return this.quad.getWidth();
    }

    /** {@inheritDoc} */
    public void setLength(double length)
    {
        this.quad.setWidth(length);
    }

    /**
     * {@inheritDoc}
     *
     * @param positions Control points. This graphic uses only one control point, which determines the center of the
     *                  circle.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Iterator<? extends Position> iterator = positions.iterator();
        try
        {
            Position pos1 = iterator.next();
            Position pos2 = iterator.next();

            LatLon center = LatLon.interpolateGreatCircle(0.5, pos1, pos2);
            this.quad.setCenter(center);

            Angle heading = LatLon.greatCircleAzimuth(pos2, pos1);
            this.quad.setHeading(heading.subtract(Angle.POS90));

            this.positions = positions;
            this.shapeInvalid = true; // Need to recompute quad size
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
        {
            if (value instanceof Double)
            {
                this.setWidth((Double) value);
            }
            else if (value instanceof Iterable)
            {
                // Only use the first value of the iterable. This graphic uses two control points and a width.
                Iterator iterator = ((Iterable) value).iterator();
                this.setWidth((Double) iterator.next());
            }
        }
        else
        {
            super.setModifier(modifier, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
            return this.getWidth();
        else
            return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(new Position(this.quad.getCenter(), 0));
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.quad.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.quad.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.quad.moveTo(position);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        if (this.shapeInvalid)
        {
            this.computeQuadSize(dc);
            this.shapeInvalid = false;
        }

        this.determineActiveAttributes();
        this.quad.preRender(dc);
    }

    protected void computeQuadSize(DrawContext dc)
    {
        if (this.positions == null)
            return;

        Iterator<? extends Position> iterator = this.positions.iterator();

        Position pos1 = iterator.next();
        Position pos2 = iterator.next();

        Angle angularDistance = LatLon.greatCircleDistance(pos1, pos2);
        double length = angularDistance.radians * dc.getGlobe().getRadius();

        this.quad.setWidth(length);
    }

    /**
     * Render the quad.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.quad.render(dc);
    }

    /** Create labels for the graphic. */
    @Override
    protected void createLabels()
    {
        FireSupportTextBuilder textBuilder = new FireSupportTextBuilder();
        String[] allText = textBuilder.createText(this);

        String text = allText[0];
        if (!WWUtil.isEmpty(text))
        {
            Label mainLabel = this.addLabel(text);
            mainLabel.setTextAlign(this.getMainLabelTextAlign());
        }

        if (allText.length > 1 && !WWUtil.isEmpty(allText[1]))
        {
            Label timeLabel = this.addLabel(allText[1]);
            timeLabel.setTextAlign(AVKey.RIGHT);

            // Align the upper right corner of the time label with the upper right corner of the quad.
            timeLabel.setOffset(new Offset(0d, 0d, AVKey.FRACTION, AVKey.FRACTION));
        }
    }

    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        Position center = new Position(this.quad.getCenter(), 0);
        this.labels.get(0).setPosition(center);

        if (this.labels.size() > 1)
        {
            double hw = this.quad.getWidth() / 2.0;
            double hh = this.quad.getHeight() / 2.0;
            double globeRadius = dc.getGlobe().getRadiusAt(center.getLatitude(), center.getLongitude());
            double distance = Math.sqrt(hw * hw + hh * hh);
            double pathLength = distance / globeRadius;

            // Find the upper left corner (looking the quad such that Point 1 is on the left and Point 2 is on the right,
            // and the line between the two is horizontal, as the quad is pictured in the MIL-STD-2525C spec, pg. 652).
            double cornerAngle = Math.atan2(-hh, hw);
            double azimuth = (Math.PI / 2.0) - (cornerAngle - this.quad.getHeading().radians);

            LatLon corner = LatLon.greatCircleEndPosition(center, azimuth, pathLength);

            this.labels.get(1).setPosition(new Position(corner, 0));
        }
    }

    /**
     * Indicates the text alignment to apply to the main label of this graphic.
     *
     * @return Text alignment for the main label.
     */
    protected String getMainLabelTextAlign()
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

    protected SurfaceQuad createShape()
    {
        SurfaceQuad quad = new SurfaceQuad();
        quad.setDelegateOwner(this);
        quad.setAttributes(this.getActiveShapeAttributes());
        return quad;
    }
}