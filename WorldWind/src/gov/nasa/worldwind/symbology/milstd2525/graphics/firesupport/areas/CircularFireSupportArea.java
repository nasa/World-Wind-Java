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
 * Implementation of circular Fire Support graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Circular Target (2.X.4.3.1.2)</li> <li>Fire Support Area, Circular (2.X.4.3.2.1.3)</li> <li>Free Fire Area
 * (FFA), Circular (2.X.4.3.2.3.3)</li> <li>Restrictive Fire Area (RFA), Circular (2.X.4.3.2.5.3)</li> <li>Airspace
 * Coordination Area (ACA), Circular (2.X.4.3.2.2.3)</li> <li>Sensor Zone, Circular</li> <li>Dead Space Area,
 * Circular</li> <li>Zone of Responsibility, Circular</li> <li>Target Build-up Area, Circular</li> <li>Target Value
 * Area, Circular</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class CircularFireSupportArea extends MilStd2525TacticalGraphic implements TacticalCircle, PreRenderable
{
    /** Function ID for the Circular Target graphic (2.X.4.3.1.2). */
    public final static String FUNCTION_ID_TARGET = "ATC---";
    /** Function ID for the Free Fire Area graphic (2.X.4.3.2.3.3). */
    public final static String FUNCTION_ID_FFA = "ACFC--";
    /** Function ID for the Restrictive Fire Area graphic (2.X.4.3.2.5.3). */
    public final static String FUNCTION_ID_RFA = "ACRC--";
    /** Function ID for the Fire Support Area graphic (2.X.4.3.2.1.3). */
    public final static String FUNCTION_ID_FSA = "ACSC--";
    /** Function ID for the Airspace Coordination Area graphic. */
    public final static String FUNCTION_ID_ACA = "ACAC--";
    /** Function ID for the Sensor Zone graphic. */
    public final static String FUNCTION_ID_SENSOR_ZONE = "ACEC--";
    /** Function ID for the Dead Space Area graphic. */
    public final static String FUNCTION_ID_DEAD_SPACE_AREA = "ACDC--";
    /** Function ID for the Zone of Responsibility graphic. */
    public final static String FUNCTION_ID_ZONE_OF_RESPONSIBILITY = "ACZC--";
    /** Function ID for the Target Build-up Area graphic. */
    public final static String FUNCTION_ID_TARGET_BUILDUP = "ACBC--";
    /** Function ID for the Target Value Area graphic. */
    public final static String FUNCTION_ID_TARGET_VALUE = "ACVC--";

    /** Center text block on label position when the text is left aligned. */
    protected final static Offset LEFT_ALIGN_OFFSET = new Offset(-0.5d, -0.5d, AVKey.FRACTION, AVKey.FRACTION);

    protected SurfaceCircle circle;
    protected Object delegateOwner;

    /** Create a new circular area. */
    public CircularFireSupportArea()
    {
        this.circle = this.createShape();
    }

    /**
     * Indicates the function IDs of circular Fire Support area graphics that display a date/time range as a separate
     * label at the left side of the circle. Whether or not a graphic supports this is determined by the graphic's
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
            FUNCTION_ID_TARGET_VALUE));
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /** {@inheritDoc} */
    public double getRadius()
    {
        return this.circle.getRadius();
    }

    /** {@inheritDoc} */
    public void setRadius(double radius)
    {
        this.circle.setRadius(radius);
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        this.move(position);
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
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
        if (!iterator.hasNext())
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.circle.setCenter(iterator.next());
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier) && (value instanceof Double))
            this.setRadius((Double) value);
        else
            super.setModifier(modifier, value);
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
            return this.getRadius();
        else
            return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(new Position(this.circle.getCenter(), 0));
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.circle.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.circle.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.circle.moveTo(position);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

        this.circle.preRender(dc);
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.circle.render(dc);
    }

    /** {@inheritDoc} Overridden to apply the delegate owner to shapes used to draw the route point. */
    @Override
    protected void determineActiveAttributes()
    {
        super.determineActiveAttributes();

        // Apply the delegate owner to the circle, if an owner has been set. If no owner is set, make this graphic the
        // circle's owner.
        Object owner = this.getDelegateOwner();
        if (owner != null)
            this.circle.setDelegateOwner(owner);
        else
            this.circle.setDelegateOwner(this);
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        FireSupportTextBuilder textBuilder = this.createTextBuilder();
        String[] allText = textBuilder.createText(this);

        String text = allText[0];
        if (!WWUtil.isEmpty(text))
        {
            Label mainLabel = this.addLabel(text);
            mainLabel.setTextAlign(this.getMainLabelTextAlign());
        }

        if (allText.length > 1)
        {
            String timeText = allText[1];

            if (!WWUtil.isEmpty(timeText))
            {
                Label timeLabel = this.addLabel(timeText);
                timeLabel.setTextAlign(AVKey.RIGHT);
            }
        }
    }

    protected FireSupportTextBuilder createTextBuilder()
    {
        return new FireSupportTextBuilder();
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

    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        this.labels.get(0).setPosition(new Position(this.circle.getCenter(), 0));

        Position center = new Position(this.circle.getCenter(), 0);
        double radiusRadians = this.circle.getRadius() / dc.getGlobe().getRadius();

        if (this.labels.size() > 1)
        {
            LatLon westEdge = LatLon.greatCircleEndPosition(center, Angle.NEG90 /* Due West */,
                Angle.fromRadians(radiusRadians));
            this.labels.get(1).setPosition(new Position(westEdge, 0));
        }
    }

    protected SurfaceCircle createShape()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setDelegateOwner(this);
        circle.setAttributes(this.activeShapeAttributes);
        return circle;
    }
}
