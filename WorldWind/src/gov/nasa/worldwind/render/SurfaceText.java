/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import com.sun.opengl.util.j2d.TextRenderer;
import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Renders a string of text on the surface of the globe. The text will appear draped over terrain. Surface text is drawn
 * at a constant geographic size: it will appear larger when the view zooms in on the text and smaller when the view
 * zooms out.
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO: add support for heading
public class SurfaceText extends AbstractSurfaceObject implements GeographicText, Movable
{
    /** Default text size. */
    public final static double DEFAULT_TEXT_SIZE_IN_METERS = 1000;
    /** Default font. */
    public static final Font DEFAULT_FONT = Font.decode("Arial-BOLD-24");
    /** Default text color. */
    public static final Color DEFAULT_COLOR = Color.WHITE;

    /** The text to draw. */
    protected CharSequence text;
    /** Location at which to draw the text. */
    protected Position location;
    /** The height of the text in meters. */
    protected double textSizeInMeters = DEFAULT_TEXT_SIZE_IN_METERS;

    /** Font to use to draw the text. Defaults to {@link #DEFAULT_FONT}. */
    protected Font font = DEFAULT_FONT;
    /** Color to use to draw the text. Defaults to {@link #DEFAULT_COLOR}. */
    protected Color color = DEFAULT_COLOR;
    /** Background color for the text. By default color will be generated to contrast with the text color. */
    protected Color bgColor;
    /** Text priority. Can be used to implement text culling. */
    protected double priority;

    // Computed each time text is rendered
    /** Bounds of the text in pixels. */
    protected Rectangle2D textBounds;
    /** Geographic size of a pixel. */
    protected double pixelSizeInMeters;
    /** Scaling factor applied to the text to maintain a constant geographic size. */
    protected double scale;

    /**
     * Create a new surface text object.
     *
     * @param text     Text to draw.
     * @param position Geographic location at which to draw the text.
     */
    public SurfaceText(String text, Position position)
    {
        this.setText(text);
        this.setPosition(position);
    }

    /**
     * Create a new surface text object.
     *
     * @param text     Text to draw.
     * @param position Geographic location at which to draw the text.
     * @param font     Font to use when drawing text.
     * @param color    Color to use when drawing text.
     */
    public SurfaceText(String text, Position position, Font font, Color color)
    {
        this.setText(text);
        this.setPosition(position);
        this.setFont(font);
        this.setColor(color);
    }

    /** {@inheritDoc} */
    public CharSequence getText()
    {
        return this.text;
    }

    /** {@inheritDoc} */
    public void setText(CharSequence text)
    {
        if (text == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.text = text;
        this.textBounds = null; // Need to recompute bounds
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.location;
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.location = position;
    }

    /** {@inheritDoc} */
    public Font getFont()
    {
        return this.font;
    }

    /** {@inheritDoc} */
    public void setFont(Font font)
    {
        if (font == null)
        {
            String message = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.font = font;
        this.textBounds = null; // Need to recompute bounds
    }

    /** {@inheritDoc} */
    public Color getColor()
    {
        return this.color;
    }

    /** {@inheritDoc} */
    public void setColor(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.color = color;
    }

    /** {@inheritDoc} */
    public Color getBackgroundColor()
    {
        return this.bgColor;
    }

    /** {@inheritDoc} */
    public void setBackgroundColor(Color background)
    {
        if (this.bgColor == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.bgColor = background;
    }

    /** {@inheritDoc} */
    public void setPriority(double priority)
    {
        this.priority = priority;
    }

    /** {@inheritDoc} */
    public double getPriority()
    {
        return this.priority;
    }

    /** {@inheritDoc} */
    @Override
    public void preRender(DrawContext dc)
    {
        if (this.textBounds == null)
        {
            this.updateTextBounds(dc);
        }

        super.preRender(dc);
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return new Position(this.location, 0);
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        Position refPos = this.getReferencePosition();
        if (refPos == null)
            return;

        this.moveTo(refPos.add(position));
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.setPosition(position);
    }

    /** {@inheritDoc} */
    public java.util.List<Sector> getSectors(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return Arrays.asList(this.computeSector(dc));
    }

    /** {@inheritDoc} */
    protected void drawGeographic(DrawContext dc, SurfaceTileDrawContext sdc)
    {
        GL gl = dc.getGL();
        OGLStackHandler ogsh = new OGLStackHandler();
        ogsh.pushAttrib(gl,
            GL.GL_CURRENT_BIT       // For current color (used by JOGL TextRenderer).
                | GL.GL_TRANSFORM_BIT); // For matrix mode.
        ogsh.pushModelview(gl);
        try
        {
            this.computeGeometry(dc, sdc);

            if (this.isSmall())
                return;

            this.applyDrawTransform(dc, sdc);
            this.drawText(dc);
        }
        finally
        {
            ogsh.pop(gl);
        }
    }

    /**
     * Draw the text.
     *
     * @param dc Current draw context.
     */
    protected void drawText(DrawContext dc)
    {
        TextRenderer tr = this.getTextRenderer(dc);

        int x = (int) (-this.textBounds.getWidth() / 2d);
        int y = 0;

        try
        {
            tr.begin3DRendering();

            Color bgColor = this.determineBackgroundColor(this.color);
            CharSequence text = this.getText();

            tr.setColor(bgColor);
            tr.draw(text, x + 1, y - 1);
            tr.setColor(this.getColor());
            tr.draw(text, x, y);
        }
        finally
        {
            tr.end3DRendering();
        }
    }

    /**
     * Compute the text size and position.
     *
     * @param dc  Current draw context.
     * @param sdc Current surface tile draw context.
     */
    protected void computeGeometry(DrawContext dc, SurfaceTileDrawContext sdc)
    {
        // Determine the geographic size of a pixel in the tile
        this.pixelSizeInMeters = this.computePixelSize(dc, sdc);

        // Determine how big the text would be without scaling
        double fullHeightInMeters = this.pixelSizeInMeters * this.textBounds.getHeight();

        // Calculate a scale to make the text the size we want (a constant geographic size)
        this.scale = this.textSizeInMeters / fullHeightInMeters;
    }

    /**
     * Apply a transform to the GL state to draw the text at the proper location and scale.
     *
     * @param dc  Current draw context.
     * @param sdc Current surface tile draw context.
     */
    protected void applyDrawTransform(DrawContext dc, SurfaceTileDrawContext sdc)
    {
        Vec4 point = new Vec4(this.location.getLongitude().degrees, this.location.getLatitude().degrees, 1);
        point = point.transformBy4(sdc.getModelviewMatrix());

        GL gl = dc.getGL();

        // Translate to location point
        gl.glTranslated(point.x(), point.y(), point.z());

        // Apply the scaling factor to draw the text at the correct geographic size
        gl.glScaled(this.scale, this.scale, 1d);
    }

    /**
     * Determine if the text is too small to draw.
     *
     * @return {@code true} if the height of the text is less than one pixel.
     */
    protected boolean isSmall()
    {
        return this.scale * this.textSizeInMeters < this.pixelSizeInMeters;
    }

    /**
     * Compute the size of a pixel in the surface tile.
     *
     * @param dc  Current draw context.
     * @param sdc Current surface tile draw context.
     *
     * @return The size of a tile pixel in meters.
     */
    protected double computePixelSize(DrawContext dc, SurfaceTileDrawContext sdc)
    {
        return dc.getGlobe().getRadius() * sdc.getSector().getDeltaLatRadians() / sdc.getViewport().height;
    }

    /**
     * Determine the text background color. This method returns the user specified background color, or a computed
     * default color if the user has not set a background color.
     *
     * @param color text color.
     *
     * @return the user specified background color, or a default color that contrasts with the text color.
     */
    protected Color determineBackgroundColor(Color color)
    {
        // If the app specified a background color, use that.
        Color bgColor = this.getBackgroundColor();
        if (bgColor != null)
            return bgColor;

        // Otherwise compute a color that contrasts with the text color.
        return this.computeBackgroundColor(color);
    }

    /**
     * Compute a background color that contrasts with the text color.
     *
     * @param color text color.
     *
     * @return a color that contrasts with the text color.
     */
    protected Color computeBackgroundColor(Color color)
    {
        // Otherwise compute a color that contrasts with the text color.
        float[] colorArray = new float[4];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), colorArray);

        if (colorArray[2] > 0.5)
            return new Color(0, 0, 0, 0.7f);
        else
            return new Color(1, 1, 1, 0.7f);
    }

    /**
     * Compute the sector covered by this surface text.
     *
     * @param dc Current draw context.
     *
     * @return The sector covered by the surface text.
     */
    protected Sector computeSector(DrawContext dc)
    {
        // Compute text extent depending on distance from eye
        Globe globe = dc.getGlobe();

        double heightInMeters = this.textSizeInMeters;
        double widthInMeters = heightInMeters * (this.textBounds.getWidth() / this.textBounds.getHeight());

        double heightInRadians = heightInMeters / globe.getRadius();
        double halfWidthInRadians = widthInMeters / globe.getRadius() / 2d;

        return new Sector(
            this.location.latitude,
            this.location.latitude.addRadians(heightInRadians),
            this.location.longitude.subtractRadians(halfWidthInRadians),
            this.location.longitude.addRadians(halfWidthInRadians)
        );
    }

    /**
     * Get the text renderer to use to draw text.
     *
     * @param dc Current draw context.
     *
     * @return The text renderer that will be used to draw the surface text.
     */
    protected TextRenderer getTextRenderer(DrawContext dc)
    {
        return OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), this.getFont(), true, false, false);
    }

    /**
     * Determine the text bounds.
     *
     * @param dc Current draw context.
     */
    protected void updateTextBounds(DrawContext dc)
    {
        this.textBounds = this.getTextRenderer(dc).getBounds(this.text);
    }
}
