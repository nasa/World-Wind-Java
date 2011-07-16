/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.geom.Vec4;

import javax.media.opengl.GL;

/**
 * A collection of OpenGL utility methods, all static.
 *
 * @author dcollins
 * @version $Id$
 */
public class OGLUtil
{
    public final static int DEFAULT_TEX_ENV_MODE = GL.GL_MODULATE;
    public final static int DEFAULT_TEXTURE_GEN_MODE = GL.GL_EYE_LINEAR;
    public final static double[] DEFAULT_TEXTURE_GEN_S_OBJECT_PLANE = new double[] {1, 0, 0, 0};
    public final static double[] DEFAULT_TEXTURE_GEN_T_OBJECT_PLANE = new double[] {0, 1, 0, 0};

    public final static int DEFAULT_SRC0_RGB = GL.GL_TEXTURE;
    public final static int DEFAULT_SRC1_RGB = GL.GL_PREVIOUS;
    public final static int DEFAULT_SRC2_RGB = GL.GL_CONSTANT;
    public final static int DEFAULT_SRC0_ALPHA = GL.GL_TEXTURE;
    public final static int DEFAULT_SRC1_ALPHA = GL.GL_PREVIOUS;
    public final static int DEFAULT_SRC2_ALPHA = GL.GL_CONSTANT;
    public final static int DEFAULT_COMBINE_ALPHA = GL.GL_MODULATE;
    public final static int DEFAULT_COMBINE_RGB = GL.GL_MODULATE;

    protected static final String GL_EXT_BLEND_FUNC_SEPARATE = "GL_EXT_blend_func_separate";

    protected static final Vec4 DEFAULT_LIGHT_DIRECTION = new Vec4(0, 0, -1, 0);

    /**
     * Sets the GL blending state according to the specified color mode. If <code>havePremultipliedColors</code> is
     * true, this applies a blending function appropriate for colors premultiplied by the alpha component. Otherwise,
     * this applies a blending function appropriate for non-premultiplied colors.
     *
     * @param gl                      the GL context.
     * @param havePremultipliedColors true to configure blending for colors premultiplied by the alpha components, and
     *                                false to configure blending for non-premultiplied colors.
     *
     * @throws IllegalArgumentException if the GL is null.
     */
    public static void applyBlending(GL gl, boolean havePremultipliedColors)
    {
        if (gl == null)
        {
            String message = Logging.getMessage("nullValue.GLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0.0f);

        if (havePremultipliedColors)
        {
            gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
        }
        else
        {
            // The separate blend function correctly handles regular (non-premultiplied) colors. We want
            //     Cd = Cs*As + Cf*(1-As)
            //     Ad = As    + Af*(1-As)
            // So we use GL_EXT_blend_func_separate to specify different blending factors for source color and source
            // alpha.

            boolean haveExtBlendFuncSeparate = gl.isExtensionAvailable(GL_EXT_BLEND_FUNC_SEPARATE);
            if (haveExtBlendFuncSeparate)
            {
                gl.glBlendFuncSeparate(
                    GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, // rgb   blending factors
                    GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);      // alpha blending factors
            }
            else
            {
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            }
        }
    }

    /**
     * Sets the GL color state to the specified {@link java.awt.Color} and opacity, and with the specified color mode.
     * If <code>premultiplyColors</code> is true, this premultipies the Red, Green, and Blue color values by the opacity
     * value. Otherwise, this does not modify the Red, Green, and Blue color values.
     *
     * @param gl                the GL context.
     * @param color             the Red, Green, and Blue values to set.
     * @param opacity           the opacity to set.
     * @param premultiplyColors true to premultiply the Red, Green, and Blue color values by the opacity value, false to
     *                          leave the Red, Green, and Blue values unmodified.
     *
     * @throws IllegalArgumentException if the GL is null, if the Color is null, if the opacity is less than 0, or if
     *                                  the opacity is greater than 1.
     */
    public static void applyColor(GL gl, java.awt.Color color, double opacity, boolean premultiplyColors)
    {
        if (gl == null)
        {
            String message = Logging.getMessage("nullValue.GLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (opacity < 0d || opacity > 1d)
        {
            String message = Logging.getMessage("generic.OpacityOutOfRange", opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float[] compArray = new float[4];
        color.getRGBComponents(compArray);
        compArray[3] = (float) opacity;

        if (premultiplyColors)
        {
            compArray[0] *= compArray[3];
            compArray[1] *= compArray[3];
            compArray[2] *= compArray[3];
        }

        gl.glColor4fv(compArray, 0);
    }

    /**
     * Sets the GL color state to the specified {@link java.awt.Color}, and with the specified color mode. If
     * <code>premultiplyColors</code> is true, this premultipies the Red, Green, and Blue color values by the Alpha
     * value. Otherwise, this does not modify the Red, Green, and Blue color values.
     *
     * @param gl                the GL context.
     * @param color             the Red, Green, Blue, and Alpha values to set.
     * @param premultiplyColors true to premultiply the Red, Green and Blue color values by the Alpha value, false to
     *                          leave the Red, Green, and Blue values unmodified.
     *
     * @throws IllegalArgumentException if the GL is null, if the Color is null, if the opacity is less than 0, or if
     *                                  the opacity is greater than 1.
     */
    public static void applyColor(GL gl, java.awt.Color color, boolean premultiplyColors)
    {
        if (gl == null)
        {
            String message = Logging.getMessage("nullValue.GLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        float[] compArray = new float[4];
        color.getRGBComponents(compArray);

        if (premultiplyColors)
        {
            compArray[0] *= compArray[3];
            compArray[1] *= compArray[3];
            compArray[2] *= compArray[3];
        }

        gl.glColor4fv(compArray, 0);
    }

    /**
     * Sets the GL lighting state to a white light originating from the eye position and pointed in the specified
     * direction, in model coordinates. The light direction is always relative to the current eye point and viewer
     * direction. If the direction is null, this the light direction defaults to (0, 0, -1), which points directly along
     * the forward vector form the eye point
     *
     * @param gl        the GL context.
     * @param light     the GL light name to set.
     * @param direction the light direction in model coordinates, may be null.
     *
     * @throws IllegalArgumentException if the GL is null.
     */
    public static void applyLightingDirectionalFromViewer(GL gl, int light, Vec4 direction)
    {
        if (gl == null)
        {
            String message = Logging.getMessage("nullValue.GLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (direction == null)
            direction = DEFAULT_LIGHT_DIRECTION;

        float[] ambient = {1f, 1f, 1f, 0f};
        float[] diffuse = {1f, 1f, 1f, 0f};
        float[] specular = {1f, 1f, 1f, 0f};
        float[] position = {(float) direction.x, (float) direction.y, (float) direction.z, 0.0f};

        gl.glLightfv(light, GL.GL_AMBIENT, ambient, 0);
        gl.glLightfv(light, GL.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(light, GL.GL_SPECULAR, specular, 0);

        OGLStackHandler ogsh = new OGLStackHandler();
        ogsh.pushModelviewIdentity(gl);
        try
        {
            gl.glLightfv(light, GL.GL_POSITION, position, 0);
        }
        finally
        {
            ogsh.pop(gl);
        }
    }

    /**
     * Returns an OpenGL pixel format corresponding to the specified texture internal format. This maps internal format
     * to pixel format as follows: <code> <table> <tr><th>Internal Format</th><th>Pixel Format</th></tr>
     * <tr><td>GL.GL_ALPHA</td><td>GL.GL_ALPHA</td></tr> <tr><td>GL.GL_ALPHA4</td><td>GL.GL_ALPHA</td></tr>
     * <tr><td>GL.GL_ALPHA8</td><td>GL.GL_ALPHA</td></tr> <tr><td>GL.GL_ALPHA12</td><td>GL.GL_ALPHA</td></tr>
     * <tr><td>GL.GL_ALPHA16</td><td>GL.GL_ALPHA</td></tr> <tr><td>GL.GL_COMPRESSED_ALPHA</td><td>GL.GL_ALPHA</td></tr>
     * <tr><td>GL.GL_COMPRESSED_LUMINANCE</td><td>GL.GL_LUMINANCE</td></tr> <tr><td>GL.GL_COMPRESSED_LUMINANCE_ALPHA</td><td>GL.GL_LUMINANCE_ALPHA</td></tr>
     * <tr><td>GL.GL_COMPRESSED_INTENSITY</td><td>GL.GL_RED</td></tr> <tr><td>GL.GL_COMPRESSED_RGB</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_COMPRESSED_RGBA</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_DEPTH_COMPONENT</td><td>GL.GL_RED</td></tr>
     * <tr><td>GL.GL_DEPTH_COMPONENT16</td><td>GL.GL_RED</td></tr> <tr><td>GL.GL_DEPTH_COMPONENT24</td><td>GL.GL_RED</td></tr>
     * <tr><td>GL.GL_DEPTH_COMPONENT32</td><td>GL.GL_RED</td></tr> <tr><td>GL.GL_LUMINANCE</td><td>GL.GL_LUMINANCE</td></tr>
     * <tr><td>GL.GL_LUMINANCE4</td><td>GL.GL_LUMINANCE</td></tr> <tr><td>GL.GL_LUMINANCE8</td><td>GL.GL_LUMINANCE</td></tr>
     * <tr><td>GL.GL_LUMINANCE12</td><td>GL.GL_LUMINANCE</td></tr> <tr><td>GL.GL_LUMINANCE16</td><td>GL.GL_LUMINANCE</td></tr>
     * <tr><td>GL.GL_LUMINANCE_ALPHA</td><td>GL.GL_LUMINANCE_ALPHA</td></tr> <tr><td>GL.GL_LUMINANCE4_ALPHA4</td><td>GL.GL_LUMINANCE_ALPHA</td></tr>
     * <tr><td>GL.GL_LUMINANCE6_ALPHA2</td><td>GL.GL_LUMINANCE_ALPHA</td></tr> <tr><td>GL.GL_LUMINANCE8_ALPHA8</td><td>GL.GL_LUMINANCE_ALPHA</td></tr>
     * <tr><td>GL.GL_LUMINANCE12_ALPHA4</td><td>GL.GL_LUMINANCE_ALPHA</td></tr> <tr><td>GL.GL_LUMINANCE12_ALPHA12</td><td>GL.GL_LUMINANCE_ALPHA</td></tr>
     * <tr><td>GL.GL_LUMINANCE16_ALPHA16</td><td>GL.GL_LUMINANCE_ALPHA</td></tr> <tr><td>GL.GL_INTENSITY</td><td>GL.GL_RED</td></tr>
     * <tr><td>GL.GL_INTENSITY4</td><td>GL.GL_RED</td></tr> <tr><td>GL.GL_INTENSITY8</td><td>GL.GL_RED</td></tr>
     * <tr><td>GL.GL_INTENSITY12</td><td>GL.GL_RED</td></tr> <tr><td>GL.GL_INTENSITY16</td><td>GL.GL_RED</td></tr>
     * <tr><td>GL.GL_R3_G3_B2</td><td>GL.GL_RGB</td></tr> <tr><td>GL.GL_RGB</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_RGB4</td><td>GL.GL_RGB</td></tr> <tr><td>GL.GL_RGB5</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_RGB8</td><td>GL.GL_RGB</td></tr> <tr><td>GL.GL_RGB10</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_RGB12</td><td>GL.GL_RGB</td></tr> <tr><td>GL.GL_RGB16</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_RGBA</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_RGBA2</td><td>GL.GL_RGBA</td></tr>
     * <tr><td>GL.GL_RGBA4</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_RGB5_A1</td><td>GL.GL_RGBA</td></tr>
     * <tr><td>GL.GL_RGBA8</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_RGB10_A2</td><td>GL.GL_RGBA</td></tr>
     * <tr><td>GL.GL_RGBA12</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_RGBA16</td><td>GL.GL_RGBA</td></tr>
     * <tr><td>GL.GL_SLUMINANCE</td><td>GL.GL_LUMINANCE</td></tr> <tr><td>GL.GL_SLUMINANCE8</td><td>GL.GL_LUMINANCE</td></tr>
     * <tr><td>GL.GL_SLUMINANCE_ALPHA</td><td>GL.GL_LUMINANCE_ALPHA</td></tr> <tr><td>GL.GL_SLUMINANCE8_ALPHA8</td><td>GL.GL_LUMINANCE_ALPHA<td></tr>
     * <tr><td>GL.GL_SRGB</td><td>GL.GL_RGB</td></tr> <tr><td>GL.GL_SRGB8</td><td>GL.GL_RGB</td></tr>
     * <tr><td>GL.GL_SRGB_ALPHA</td><td>GL.GL_RGBA</td></tr> <tr><td>GL.GL_SRGB8_ALPHA8</td><td>GL.GL_RGBA</td></tr>
     * </code>
     * <p/>
     * This returns 0 if the internal format is not one of the recognized types.
     *
     * @param internalFormat the OpenGL texture internal format.
     *
     * @return a pixel format corresponding to the texture internal format, or 0 if the internal format is not
     *         recognized.
     */
    public static int computeTexturePixelFormat(int internalFormat)
    {
        switch (internalFormat)
        {
            // Alpha pixel format.
            case GL.GL_ALPHA:
            case GL.GL_ALPHA4:
            case GL.GL_ALPHA8:
            case GL.GL_ALPHA12:
            case GL.GL_ALPHA16:
            case GL.GL_COMPRESSED_ALPHA:
                return GL.GL_ALPHA;
            // Luminance pixel format.
            case GL.GL_COMPRESSED_LUMINANCE:
            case GL.GL_LUMINANCE:
            case GL.GL_LUMINANCE4:
            case GL.GL_LUMINANCE8:
            case GL.GL_LUMINANCE12:
            case GL.GL_LUMINANCE16:
            case GL.GL_SLUMINANCE:
            case GL.GL_SLUMINANCE8:
                return GL.GL_LUMINANCE;
            // Luminance-alpha pixel format.
            case GL.GL_COMPRESSED_LUMINANCE_ALPHA:
            case GL.GL_LUMINANCE_ALPHA:
            case GL.GL_LUMINANCE4_ALPHA4:
            case GL.GL_LUMINANCE6_ALPHA2:
            case GL.GL_LUMINANCE8_ALPHA8:
            case GL.GL_LUMINANCE12_ALPHA4:
            case GL.GL_LUMINANCE12_ALPHA12:
            case GL.GL_LUMINANCE16_ALPHA16:
            case GL.GL_SLUMINANCE_ALPHA:
            case GL.GL_SLUMINANCE8_ALPHA8:
                return GL.GL_LUMINANCE_ALPHA;
            // Unspecified single component (red) pixel format.
            case GL.GL_COMPRESSED_INTENSITY:
            case GL.GL_DEPTH_COMPONENT:
            case GL.GL_DEPTH_COMPONENT16:
            case GL.GL_DEPTH_COMPONENT24:
            case GL.GL_DEPTH_COMPONENT32:
            case GL.GL_INTENSITY:
            case GL.GL_INTENSITY4:
            case GL.GL_INTENSITY8:
            case GL.GL_INTENSITY12:
            case GL.GL_INTENSITY16:
                return GL.GL_RED;
            // RGB pixel format.
            case GL.GL_COMPRESSED_RGB:
            case GL.GL_R3_G3_B2:
            case GL.GL_RGB:
            case GL.GL_RGB4:
            case GL.GL_RGB5:
            case GL.GL_RGB8:
            case GL.GL_RGB10:
            case GL.GL_RGB12:
            case GL.GL_RGB16:
            case GL.GL_SRGB:
            case GL.GL_SRGB8:
                return GL.GL_RGB;
            // RGBA pixel format.
            case GL.GL_COMPRESSED_RGBA:
            case GL.GL_RGBA:
            case GL.GL_RGBA2:
            case GL.GL_RGBA4:
            case GL.GL_RGB5_A1:
            case GL.GL_RGBA8:
            case GL.GL_RGB10_A2:
            case GL.GL_RGBA12:
            case GL.GL_RGBA16:
            case GL.GL_SRGB_ALPHA:
            case GL.GL_SRGB8_ALPHA8:
                return GL.GL_RGBA;
            default:
                return 0;
        }
    }

    /**
     * Returns an OpenGL pixel format corresponding to the specified texture internal format. This maps internal format
     * to pixel format as follows: <code> <table> <tr><th>Internal Format</th><th>Estimated Bits Per Pixel</th></tr>
     * <tr><td>GL.GL_ALPHA</td><td>8</td></tr> <tr><td>GL.GL_ALPHA4</td><td>4</td></tr>
     * <tr><td>GL.GL_ALPHA8</td><td>8</td></tr> <tr><td>GL.GL_ALPHA12</td><td>12</td></tr>
     * <tr><td>GL.GL_ALPHA16</td><td>16</td></tr> <tr><td>GL.GL_COMPRESSED_ALPHA</td><td>0</td></tr>
     * <tr><td>GL.GL_COMPRESSED_LUMINANCE</td><td>0</td></tr> <tr><td>GL.GL_COMPRESSED_LUMINANCE_ALPHA</td><td>0</td></tr>
     * <tr><td>GL.GL_COMPRESSED_INTENSITY</td><td>0</td></tr> <tr><td>GL.GL_COMPRESSED_RGB</td><td>0</td></tr>
     * <tr><td>GL.GL_COMPRESSED_RGBA</td><td>0</td></tr> <tr><td>GL.GL_DEPTH_COMPONENT</td><td>24</td></tr>
     * <tr><td>GL.GL_DEPTH_COMPONENT16</td><td>16</td></tr> <tr><td>GL.GL_DEPTH_COMPONENT24</td><td>24</td></tr>
     * <tr><td>GL.GL_DEPTH_COMPONENT32</td><td>32</td></tr> <tr><td>GL.GL_LUMINANCE</td><td>8</td></tr>
     * <tr><td>GL.GL_LUMINANCE4</td><td>4</td></tr> <tr><td>GL.GL_LUMINANCE8</td><td>8</td></tr>
     * <tr><td>GL.GL_LUMINANCE12</td><td>12</td></tr> <tr><td>GL.GL_LUMINANCE16</td><td>16</td></tr>
     * <tr><td>GL.GL_LUMINANCE_ALPHA</td><td>16</td></tr> <tr><td>GL.GL_LUMINANCE4_ALPHA4</td><td>8</td></tr>
     * <tr><td>GL.GL_LUMINANCE6_ALPHA2</td><td>8</td></tr> <tr><td>GL.GL_LUMINANCE8_ALPHA8</td><td>16</td></tr>
     * <tr><td>GL.GL_LUMINANCE12_ALPHA4</td><td>16</td></tr> <tr><td>GL.GL_LUMINANCE12_ALPHA12</td><td>24</td></tr>
     * <tr><td>GL.GL_LUMINANCE16_ALPHA16</td><td>32</td></tr> <tr><td>GL.GL_INTENSITY</td><td>8</td></tr>
     * <tr><td>GL.GL_INTENSITY4</td><td>4</td></tr> <tr><td>GL.GL_INTENSITY8</td><td>8</td></tr>
     * <tr><td>GL.GL_INTENSITY12</td><td>12</td></tr> <tr><td>GL.GL_INTENSITY16</td><td>16</td></tr>
     * <tr><td>GL.GL_R3_G3_B2</td><td>8</td></tr> <tr><td>GL.GL_RGB</td><td>24</td></tr>
     * <tr><td>GL.GL_RGB4</td><td>12</td></tr> <tr><td>GL.GL_RGB5</td><td>16 (assume the driver allocates 16 bits per
     * pixel)</td></tr> <tr><td>GL.GL_RGB8</td><td>24</td></tr> <tr><td>GL.GL_RGB10</td><td>32 (assume the driver
     * allocates 32 bits per pixel)</td></tr> <tr><td>GL.GL_RGB12</td><td>36</td></tr>
     * <tr><td>GL.GL_RGB16</td><td>48</td></tr> <tr><td>GL.GL_RGBA</td><td>32</td></tr>
     * <tr><td>GL.GL_RGBA2</td><td>8</td></tr> <tr><td>GL.GL_RGBA4</td><td>16</td></tr>
     * <tr><td>GL.GL_RGB5_A1</td><td>16</td></tr> <tr><td>GL.GL_RGBA8</td><td>32</td></tr>
     * <tr><td>GL.GL_RGB10_A2</td><td>32</td></tr> <tr><td>GL.GL_RGBA12</td><td>48</td></tr>
     * <tr><td>GL.GL_RGBA16</td><td>64</td></tr> <tr><td>GL.GL_SLUMINANCE</td><td>8</td></tr>
     * <tr><td>GL.GL_SLUMINANCE8</td><td>8</td></tr> <tr><td>GL.GL_SLUMINANCE_ALPHA</td><td>16</td></tr>
     * <tr><td>GL.GL_SLUMINANCE8_ALPHA8</td><td>16<td></tr> <tr><td>GL.GL_SRGB</td><td>24</td></tr>
     * <tr><td>GL.GL_SRGB8</td><td>24</td></tr> <tr><td>GL.GL_SRGB_ALPHA</td><td>32</td></tr>
     * <tr><td>GL.GL_SRGB8_ALPHA8</td><td>32</td></tr> </code>
     * <p/>
     * The returned estimate assumes that the driver provides does not convert the formats to another supported, such
     * converting as <code>GL.GL_ALPHA4</code> to <code>GL.GL_ALPHA8</code>. This returns 0 if the internal format is
     * not one of the recognized types. This does not attempt to estimate a memory size for compressed internal
     * formats.
     *
     * @param internalFormat the OpenGL texture internal format.
     * @param width          the texture width, in pixels.
     * @param height         the texture height, in pixels.
     * @param includeMipmaps true to include the texture's mip map data in the estimated size; false otherwise.
     *
     * @return a pixel format corresponding to the texture internal format, or 0 if the internal format is not
     *         recognized.
     *
     * @throws IllegalArgumentException if either the width or height is less than or equal to zero.
     */
    public static int estimateTextureMemorySize(int internalFormat, int width, int height, boolean includeMipmaps)
    {
        if (width < 0)
        {
            String message = Logging.getMessage("Geom.WidthInvalid", width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (height < 0)
        {
            String message = Logging.getMessage("Geom.HeightInvalid", height);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int numPixels = width * height;

        // Add the number of pixels from each level in the mipmap chain to the total number of pixels.
        if (includeMipmaps)
        {
            int maxLevel = Math.max((int) WWMath.logBase2(width), (int) WWMath.logBase2(height));
            for (int level = 1; level <= maxLevel; level++)
            {
                int w = Math.max(width >> level, 1);
                int h = Math.max(height >> level, 1);
                numPixels += w * h;
            }
        }

        switch (internalFormat)
        {
            // 4 bits per pixel.
            case GL.GL_ALPHA4:
            case GL.GL_LUMINANCE4:
            case GL.GL_INTENSITY4:
                return numPixels / 2;
            // 8 bits per pixel.
            case GL.GL_ALPHA:
            case GL.GL_ALPHA8:
            case GL.GL_LUMINANCE:
            case GL.GL_LUMINANCE8:
            case GL.GL_LUMINANCE4_ALPHA4:
            case GL.GL_LUMINANCE6_ALPHA2:
            case GL.GL_INTENSITY:
            case GL.GL_INTENSITY8:
            case GL.GL_R3_G3_B2:
            case GL.GL_RGBA2:
            case GL.GL_SLUMINANCE:
            case GL.GL_SLUMINANCE8:
                return numPixels;
            // 12 bits per pixel.
            case GL.GL_ALPHA12:
            case GL.GL_LUMINANCE12:
            case GL.GL_INTENSITY12:
            case GL.GL_RGB4:
                return 12 * numPixels / 8;
            // 16 bits per pixel.
            case GL.GL_ALPHA16:
            case GL.GL_DEPTH_COMPONENT16:
            case GL.GL_LUMINANCE16:
            case GL.GL_LUMINANCE_ALPHA:
            case GL.GL_LUMINANCE8_ALPHA8:
            case GL.GL_LUMINANCE12_ALPHA4:
            case GL.GL_INTENSITY16:
            case GL.GL_RGB5: // Assume the driver allocates 16 bits per pixel for GL_RGB5.
            case GL.GL_RGBA4:
            case GL.GL_RGB5_A1:
            case GL.GL_SLUMINANCE_ALPHA:
            case GL.GL_SLUMINANCE8_ALPHA8:
                return 2 * numPixels;
            // 24 bits per pixel.
            case GL.GL_DEPTH_COMPONENT:
            case GL.GL_DEPTH_COMPONENT24:
            case GL.GL_LUMINANCE12_ALPHA12:
            case GL.GL_RGB:
            case GL.GL_RGB8:
            case GL.GL_SRGB:
            case GL.GL_SRGB8:
                return 3 * numPixels;
            // 32 bits per pixel.
            case GL.GL_DEPTH_COMPONENT32:
            case GL.GL_LUMINANCE16_ALPHA16:
            case GL.GL_RGB10: // Assume the driver allocates 32 bits per pixel for GL_RGB10.
            case GL.GL_RGBA:
            case GL.GL_RGBA8:
            case GL.GL_RGB10_A2:
            case GL.GL_SRGB_ALPHA:
            case GL.GL_SRGB8_ALPHA8:
                return 4 * numPixels;
            // 36 bits per pixel.
            case GL.GL_RGB12:
                return 36 * numPixels / 8;
            // 48 bits per pixel.
            case GL.GL_RGB16:
            case GL.GL_RGBA12:
                return 6 * numPixels;
            // 64 bits per pixel.
            case GL.GL_RGBA16:
                return 8 * numPixels;
            // Compressed internal formats. Don't try to estimate a size for compressed formats.
            case GL.GL_COMPRESSED_ALPHA:
            case GL.GL_COMPRESSED_LUMINANCE:
            case GL.GL_COMPRESSED_LUMINANCE_ALPHA:
            case GL.GL_COMPRESSED_INTENSITY:
            case GL.GL_COMPRESSED_RGB:
            case GL.GL_COMPRESSED_RGBA:
                return 0;
            default:
                return 0;
        }
    }
}
