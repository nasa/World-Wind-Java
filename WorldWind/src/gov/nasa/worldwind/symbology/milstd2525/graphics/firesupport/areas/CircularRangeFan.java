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
 * Implementation of the Circular Weapon/Sensor Range Fan graphic (2.X.4.3.4.1).
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO: add support for a symbol at the center of the range fan.
public class CircularRangeFan extends MilStd2525TacticalGraphic implements PreRenderable
{
    /** Function ID for the Circular Weapon/Sensor Range Fan graphic. */
    public final static String FUNCTION_ID = "AXC---";

    protected final static Offset LABEL_OFFSET = new Offset(0d, 0d, AVKey.FRACTION, AVKey.FRACTION);

    /** Position of the center of the range fan. */
    protected Position position;
    /** Rings that make up the range fan. */
    protected List<SurfaceCircle> rings;

    /** Create the range fan. */
    public CircularRangeFan()
    {
        this.rings = new ArrayList<SurfaceCircle>();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
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

        this.position = iterator.next();

        for (SurfaceCircle ring : this.rings)
        {
            ring.setCenter(this.position);
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public void setModifier(String modifier, Object value)
    {
        if (AVKey.RADIUS.equals(modifier))
        {
            if (value instanceof Iterable)
            {
                //noinspection unchecked
                this.setRadii((Iterable) value);
            }
            else if (value instanceof Double)
            {
                this.setRadii(Arrays.asList((Double) value));
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
        if (AVKey.RADIUS.equals(modifier))
        {
            return this.getRadii();
        }
        else
        {
            return super.getModifier(modifier);
        }
    }

    /**
     * Indicates the radii of the rings that make up the range fan.
     *
     * @return List of radii, in meters. If there are no rings this returns an empty list.
     */
    public List<Double> getRadii()
    {
        List<Double> radii = new ArrayList<Double>();
        for (SurfaceCircle ring : this.rings)
        {
            radii.add(ring.getRadius());
        }
        return radii;
    }

    /**
     * Specifies the radii of the rings that make up the range fan.
     *
     * @param radii List of radii, in meters. A circle will be created for each radius.
     */
    public void setRadii(Iterable<Double> radii)
    {
        this.rings.clear();

        for (Double d : radii)
        {
            if (d != null)
            {
                SurfaceCircle ring = this.createCircle();
                ring.setRadius(d);
                if (this.position != null)
                {
                    ring.setCenter(this.position);
                }

                this.rings.add(ring);
            }
        }
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(this.position);
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.position;
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Position referencePosition = this.getReferencePosition();
        if (referencePosition == null)
            return;

        this.position = referencePosition.add(position);

        for (SurfaceCircle ring : this.rings)
        {
            ring.move(position);
        }
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        for (SurfaceCircle ring : this.rings)
        {
            ring.moveTo(position);
        }
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

        for (SurfaceCircle ring : this.rings)
        {
            ring.preRender(dc);
        }
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        for (SurfaceCircle ring : this.rings)
        {
            ring.render(dc);
        }
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        Iterator altIterator = null;

        // See if the altitude modifier is set. If so, use it's value to construct altitude labels.
        Object modifier = this.getModifier(AVKey.ALTITUDE);
        if (modifier instanceof Iterable)
        {
            altIterator = ((Iterable) modifier).iterator();
        }
        else if (modifier != null)
        {
            // Use the modifier as the altitude of the first ring
            altIterator = Arrays.asList(modifier).iterator();
        }

        // Create a label for each ring
        for (int i = 0; i < this.rings.size(); i++)
        {
            SurfaceCircle ring = this.rings.get(i);
            StringBuilder sb = new StringBuilder();

            if (i == 0)
            {
                sb.append("MIN RG ");
            }
            else
            {
                sb.append("MAX RG(");
                sb.append(i);
                sb.append(") ");
            }
            sb.append(ring.getRadius());

            // Append the altitude, if available
            if (altIterator != null && altIterator.hasNext())
            {
                Object alt = altIterator.next();
                sb.append("\n");
                sb.append("ALT ");
                sb.append(alt);
            }

            Label label = this.addLabel(sb.toString());
            label.setOffset(LABEL_OFFSET);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        double dueSouth = Angle.POS180.radians;
        double globeRadius = dc.getGlobe().getRadius();

        int i = 0;
        for (SurfaceCircle ring : this.rings)
        {
            double radius = ring.getRadius();

            // Position the label at the Southern edge of the ring
            LatLon ll = LatLon.greatCircleEndPosition(this.position, dueSouth, radius / globeRadius);

            this.labels.get(i).setPosition(new Position(ll, 0));
            i += 1;
        }
    }

    /**
     * Create a circle for a range ring.
     *
     * @return New circle.
     */
    protected SurfaceCircle createCircle()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setDelegateOwner(this);
        circle.setAttributes(this.activeShapeAttributes);
        return circle;
    }
}