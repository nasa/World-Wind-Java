/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;

/**
 * @author dcollins
 * @version $Id$
 */
public class Color
{
    public float r;
    public float g;
    public float b;
    public float a;

    public Color(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int rgba, boolean hasalpha)
    {
        if (hasalpha)
        {
            a = (rgba >> 24) & 0xFF;
            r = (rgba >> 16) & 0xFF;
            g = (rgba >> 8) & 0xFF;
            b = (rgba >> 0) & 0xFF;
        }
        else
        {
            a = 0xFF;
            r = (rgba >> 16) & 0xFF;
            g = (rgba >> 8) & 0xFF;
            b = (rgba >> 0) & 0xFF;
        }
    }

    public int getRGB()
    {
        int value = (((int) a & 0xFF) << 24) |
            (((int) r & 0xFF) << 16) |
            (((int) g & 0xFF) << 8) |
            (((int) b & 0xFF) << 0);

        return value;
    }

    public int getRGBA()
    {
        int value = (((int) r & 0xFF) << 24) |
            (((int) g & 0xFF) << 16) |
            (((int) b & 0xFF) << 8) |
            (((int) a & 0xFF) << 0);

        return value;
    }

    public static Color fromRGBAInt(int r, int g, int b, int a)
    {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static Color fromTransparentBlack()
    {
        return new Color(0f, 0f, 0f, 0f);
    }

    public static Color fromGray()
    {
        return fromRGBAInt(88, 88, 88, 255);
    }
}
