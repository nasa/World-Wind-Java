/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;

/**
 * A container for common attributes applied to renderable shapes.
 *
 * @author tag
 * @version $Id$
 */
public class BasicAirspaceAttributes implements AirspaceAttributes
{
    private boolean drawInterior = true;
    private boolean drawOutline = false;
    private Material material = Material.WHITE;
    private Material outlineMaterial = Material.BLACK;
    private double opacity = 1.0;
    private double outlineOpacity = 1.0;
    private double outlineWidth = 1.0;

    public BasicAirspaceAttributes(Material material, double opacity)
    {
        if (material == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (opacity < 0.0 || opacity > 1.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "opacity=" + opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.material = material;
        this.opacity = opacity;
    }

    public BasicAirspaceAttributes(AirspaceAttributes that)
    {
        if (that == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.drawInterior = that.isDrawInterior();
        this.drawOutline = that.isDrawOutline();
        this.material = that.getMaterial();
        this.outlineMaterial = that.getOutlineMaterial();
        this.opacity = that.getOpacity();
        this.outlineOpacity = that.getOutlineOpacity();
        this.outlineWidth = that.getOutlineWidth();
    }

    public BasicAirspaceAttributes(ShapeAttributes shapeAttrs)
    {
        if (shapeAttrs == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // TODO: This is a temporary measure to convert ShapeAttributes to AirspaceAttributes.
        // TODO: Modify airspaces to use Attributes.

        this.drawInterior = shapeAttrs.isDrawInterior();
        this.drawOutline = shapeAttrs.isDrawOutline();
        this.material = shapeAttrs.getInteriorMaterial();
        this.outlineMaterial = shapeAttrs.getOutlineMaterial();
        this.opacity = shapeAttrs.getInteriorOpacity();
        this.outlineOpacity = shapeAttrs.getOutlineOpacity();
        this.outlineWidth = shapeAttrs.getOutlineWidth();
    }

    public BasicAirspaceAttributes()
    {
    }

    /**
     * Determines whether the shape interior or volume is being drawn.
     *
     * @return <code>true</code> if the shape interior or volume is being drawn.
     */
    public boolean isDrawInterior()
    {
        return this.drawInterior;
    }

    /**
     * Sets whether the shape interior or volume should be drawn.
     *
     * @param state <code>true</code> if the shape interior or volume should be drawn.
     */
    public void setDrawInterior(boolean state)
    {
        this.drawInterior = state;
    }

    /**
     * Determines whether the shape border or outline is being drawn.
     *
     * @return <code>true</code> if the shape border or outline is being drawn.
     */
    public boolean isDrawOutline()
    {
        return this.drawOutline;
    }

    /**
     * Sets whether the shape border or outline should be drawn.
     *
     * @param state <code>true</code> if the shape border or outline should be drawn.
     */
    public void setDrawOutline(boolean state)
    {
        this.drawOutline = state;
    }

    /**
     * Get the <code>Material</code> used to draw the shape interior or volume.
     *
     * @return the <code>Material</code> used to draw the shape interior or volume.
     */
    public Material getMaterial()
    {
        return this.material;
    }

    /**
     * Sets the <code>Material</code> used to draw the shape interior or volume.
     *
     * @param material the <code>Material</code> used to draw the shape interior or volume.
     */
    public void setMaterial(Material material)
    {
        if (material == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.material = material;
    }

    /**
     * Get the <code>Material</code> used to draw the shape border or outline.
     *
     * @return the <code>Material</code> used to draw the shape border or outline.
     */
    public Material getOutlineMaterial()
    {
        return this.outlineMaterial;
    }

    /**
     * Sets the <code>Material</code> used to draw the shape border or outline.
     *
     * @param materal the <code>Material</code> used to draw the shape border or outline.
     */
    public void setOutlineMaterial(Material materal)
    {
        if (materal == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineMaterial = materal;
    }

    /**
     * Returns the shape's opacity.
     *
     * @return the shape's opacity in the range [0, 1], where 0 indicates full transparency and 1 indicates full
     *         opacity.
     */
    public double getOpacity()
    {
        return this.opacity;
    }

    /**
     * Set the shape's opacity.
     *
     * @param opacity the shape's opacity in the range [0, 1], where 0 indicates full transparency and 1 indicates full
     *                opacity.
     */
    public void setOpacity(double opacity)
    {
        if (opacity < 0.0 || opacity > 1.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "opacity=" + opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.opacity = opacity;
    }

    /**
     * Returns the shape's outline or border opacity.
     *
     * @return the shape's outline or borderopacity in the range [0, 1], where 0 indicates full transparency and 1
     *         indicates full opacity.
     */
    public double getOutlineOpacity()
    {
        return this.outlineOpacity;
    }

    /**
     * Set the shape's outline or border opacity.
     *
     * @param opacity the shape's outline or border opacity in the range [0, 1], where 0 indicates full transparency and
     *                1 indicates full opacity.
     */
    public void setOutlineOpacity(double opacity)
    {
        if (opacity < 0.0 || opacity > 1.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "opacity=" + opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineOpacity = opacity;
    }

    /**
     * Get the shape border or outline width in pixels.
     *
     * @return the shape border or outline width in pixels.
     */
    public double getOutlineWidth()
    {
        return this.outlineWidth;
    }

    /**
     * Sets the shape border or outline width in pixels.
     *
     * @param width the shape border or outline width in pixels.
     */
    public void setOutlineWidth(double width)
    {
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width=" + width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineWidth = width;
    }

    public void applyInterior(DrawContext dc, boolean enableMaterial)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        this.applyMaterial(dc, this.getMaterial(), this.getOpacity(), enableMaterial);
    }

    public void applyOutline(DrawContext dc, boolean enableMaterial)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        this.applyMaterial(dc, this.getOutlineMaterial(), this.getOutlineOpacity(), enableMaterial);

        GL gl = dc.getGL();
        gl.glLineWidth((float) this.getOutlineWidth());
    }

    public void getRestorableState(RestorableSupport rs, RestorableSupport.StateObject so)
    {
        rs.addStateValueAsBoolean(so, "drawInterior", this.isDrawInterior());

        rs.addStateValueAsBoolean(so, "drawOutline", this.isDrawOutline());

        this.getMaterial().getRestorableState(rs, rs.addStateObject(so, "material"));

        this.getOutlineMaterial().getRestorableState(rs, rs.addStateObject(so, "outlineMaterial"));

        rs.addStateValueAsDouble(so, "opacity", this.getOpacity());

        rs.addStateValueAsDouble(so, "outlineOpacity", this.getOutlineOpacity());

        rs.addStateValueAsDouble(so, "outlineWidth", this.getOutlineWidth());
    }

    public void restoreState(RestorableSupport rs, RestorableSupport.StateObject so)
    {
        Boolean b = rs.getStateValueAsBoolean(so, "drawInterior");
        if (b != null)
            this.setDrawInterior(b);

        b = rs.getStateValueAsBoolean(so, "drawOutline");
        if (b != null)
            this.setDrawOutline(b);

        RestorableSupport.StateObject mo = rs.getStateObject(so, "material");
        if (mo != null)
            this.setMaterial(this.getMaterial().restoreState(rs, mo));

        mo = rs.getStateObject(so, "outlineMaterial");
        if (mo != null)
            this.setOutlineMaterial(this.getOutlineMaterial().restoreState(rs, mo));

        Double d = rs.getStateValueAsDouble(so, "opacity");
        if (d != null)
            this.setOpacity(d);

        d = rs.getStateValueAsDouble(so, "outlineOpacity");
        if (d != null)
            this.setOutlineOpacity(d);

        d = rs.getStateValueAsDouble(so, "outlineWidth");
        if (d != null)
            this.setOutlineWidth(d);
    }

    protected void applyMaterial(DrawContext dc, Material material, double opacity, boolean enableMaterial)
    {
        GL gl = dc.getGL();

        if (material != null)
        {
            if (enableMaterial)
            {
                material.apply(gl, GL.GL_FRONT_AND_BACK, (float) opacity);
            }
            else
            {
                float[] compArray = new float[4];
                material.getDiffuse().getRGBComponents(compArray);
                compArray[3] = (float) opacity;
                gl.glColor4fv(compArray, 0);
            }
        }
    }
}
