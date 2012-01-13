/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * @author dcollins
 * @version $Id$
 */
public class MilStd2525Util
{
    protected static final Offset CLOVER_OFFSET = Offset.fromFraction(0.0625, 0.0625);
    protected static final Size CLOVER_SIZE = Size.fromFraction(0.890625, 0.890625);
    protected static final Offset CLOVER_C2_HQ_OFFSET = Offset.fromFraction(0.0, -0.0546875);

    protected static final Offset CLOVER_UP_OFFSET = Offset.fromFraction(0.03125, 0.1875);
    protected static final Size CLOVER_UP_SIZE = Size.fromFraction(0.9375, 0.8046875);

    protected static final Offset CLOVER_DOWN_OFFSET = Offset.fromFraction(0.03125, 0.0078125);
    protected static final Size CLOVER_DOWN_SIZE = Size.fromFraction(0.9375, 0.8046875);

    protected static final Offset ARCH_UP_OFFSET = Offset.fromFraction(0.15625, 0.1953125);
    protected static final Size ARCH_UP_SIZE = Size.fromFraction(0.6875, 0.734375);

    protected static final Offset ARCH_DOWN_OFFSET = Offset.fromFraction(0.15625, 0.0703125);
    protected static final Size ARCH_DOWN_SIZE = Size.fromFraction(0.6875, 0.734375);

    protected static final Offset CIRCLE_OFFSET = Offset.fromFraction(0.125, 0.125);
    protected static final Size CIRCLE_SIZE = Size.fromFraction(0.75, 0.75);

    protected static final Offset RECTANGLE_OFFSET = Offset.fromFraction(0.0390625, 0.1875);
    protected static final Size RECTANGLE_SIZE = Size.fromFraction(0.921875, 0.625);
    protected static final Offset RECTANGLE_C2_HQ_OFFSET = Offset.fromFraction(0.0, -0.3);

    protected static final Offset HAT_UP_OFFSET = Offset.fromFraction(0.15625, 0.1953125);
    protected static final Size HAT_UP_SIZE = Size.fromFraction(0.6875, 0.734375);

    protected static final Offset HAT_DOWN_OFFSET = Offset.fromFraction(0.15625, 0.0703125);
    protected static final Size HAT_DOWN_SIZE = Size.fromFraction(0.6875, 0.734375);

    protected static final Offset SQUARE_OFFSET = Offset.fromFraction(0.15625, 0.15625);
    protected static final Size SQUARE_SIZE = Size.fromFraction(0.6875, 0.6875);
    protected static final Offset SQUARE_C2_HQ_OFFSET = Offset.fromFraction(0.0, -0.22728);

    protected static final Offset TENT_UP_OFFSET = Offset.fromFraction(0.15625, 0.1875);
    protected static final Size TENT_UP_SIZE = Size.fromFraction(0.6875, 0.8046875);

    protected static final Offset TENT_DOWN_OFFSET = Offset.fromFraction(0.15625, 0.0);
    protected static final Size TENT_DOWN_SIZE = Size.fromFraction(0.6875, 0.8046875);

    protected static final Offset DIAMOND_OFFSET = Offset.fromFraction(0.046875, 0.046875);
    protected static final Size DIAMOND_SIZE = Size.fromFraction(0.90625, 0.90625);
    protected static final Offset DIAMOND_C2_HQ_OFFSET = Offset.fromFraction(0.0, -0.05172);

    protected static final double TEXT_MODIFIER_FONT_SIZE_FACTOR = 0.15;

    public static class SymbolInfo
    {
        public Offset iconOffset;
        public Size iconSize;
        public Offset offset;
        public boolean isGroundSymbol;
    }

    public static SymbolInfo computeTacticalSymbolInfo(String symbolId)
    {
        if (symbolId == null)
        {
            String msg = Logging.getMessage("nullValue.SymbolCodeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        SymbolCode symbolCode = new SymbolCode(symbolId);
        SymbolInfo symbolInfo = new SymbolInfo();

        String scheme = symbolCode.getScheme();
        String si = symbolCode.getStandardIdentity();
        String bd = symbolCode.getBattleDimension();
        String fi = symbolCode.getFunctionId();

        // Clover, Clover Up, and Clover Down.
        if (si != null && (si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_PENDING)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_UNKNOWN)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_PENDING)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_UNKNOWN)))
        {
            // Clover icon.
            if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF)))
            {
                symbolInfo.iconOffset = CLOVER_OFFSET;
                symbolInfo.iconSize = CLOVER_SIZE;
            }
            // Clover icon for Special C2 Headquarters symbols. Must appear before Clover icon for Ground symbols.
            else if (scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_UNKNOWN)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && fi != null && fi.toUpperCase().equalsIgnoreCase("UH----"))
            {
                symbolInfo.iconOffset = CLOVER_OFFSET;
                symbolInfo.iconSize = CLOVER_SIZE;
                symbolInfo.offset = CLOVER_C2_HQ_OFFSET;
                symbolInfo.isGroundSymbol = true;
            }
            // Clover icon for Ground symbols.
            else if ((bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
                || (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS)
                || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT))))
            {
                symbolInfo.iconOffset = CLOVER_OFFSET;
                symbolInfo.iconSize = CLOVER_SIZE;
                symbolInfo.isGroundSymbol = true;
            }
            // Clover Up icon (Clover without a bottom leaf).
            else if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SPACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_AIR)))
            {
                symbolInfo.iconOffset = CLOVER_UP_OFFSET;
                symbolInfo.iconSize = CLOVER_UP_SIZE;
            }
            // Clover Down icon (Clover without a top leaf).
            else if (bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                symbolInfo.iconOffset = CLOVER_DOWN_OFFSET;
                symbolInfo.iconSize = CLOVER_DOWN_SIZE;
            }
        }
        // Arch Up, Arch Down, Circle, and Rectangle.
        else if (si != null && (si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_FRIEND)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_ASSUMED_FRIEND)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_FRIEND)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_ASSUMED_FRIEND)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_JOKER)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_FAKER)))
        {
            // Arch Up icon.
            if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SPACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_AIR)))
            {
                symbolInfo.iconOffset = ARCH_UP_OFFSET;
                symbolInfo.iconSize = ARCH_UP_SIZE;
            }
            // Arch Down icon.
            else if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE)))
            {
                symbolInfo.iconOffset = ARCH_DOWN_OFFSET;
                symbolInfo.iconSize = ARCH_DOWN_SIZE;
            }
            // Circle icon.
            else if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE)))
            {
                symbolInfo.iconOffset = CIRCLE_OFFSET;
                symbolInfo.iconSize = CIRCLE_SIZE;
            }
            // Circle icon for Ground Symbols.
            else if ((scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && fi != null && fi.matches("E....."))
                || (scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_INTELLIGENCE)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)))
            {
                symbolInfo.iconOffset = CIRCLE_OFFSET;
                symbolInfo.iconSize = CIRCLE_SIZE;
                symbolInfo.isGroundSymbol = true;
            }
            // Rectangle icon.
            else if (bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF))
            {
                symbolInfo.iconOffset = RECTANGLE_OFFSET;
                symbolInfo.iconSize = RECTANGLE_SIZE;
            }
            // Rectangle icon for Special C2 Headquarters symbols. Must appear before Rectangle icon for Ground symbols.
            else if (scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_FRIEND)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && fi != null && fi.equalsIgnoreCase("UH----"))
            {
                symbolInfo.iconOffset = RECTANGLE_OFFSET;
                symbolInfo.iconSize = RECTANGLE_SIZE;
                symbolInfo.offset = RECTANGLE_C2_HQ_OFFSET;
                symbolInfo.isGroundSymbol = true;
            }
            // Rectangle icon for Ground symbols.
            else if ((scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && (fi == null || (fi.equalsIgnoreCase("-----") || fi.toUpperCase().matches("U.....")
                || fi.toUpperCase().matches("I....."))))
                || (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS)
                || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT))))
            {
                symbolInfo.iconOffset = RECTANGLE_OFFSET;
                symbolInfo.iconSize = RECTANGLE_SIZE;
                symbolInfo.isGroundSymbol = true;
            }
        }
        // Hat Up, Hat Down, and Square.
        else if (si != null && (si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_NEUTRAL)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_EXERCISE_NEUTRAL)))
        {
            // Hat Up icon (tall rectangle without a bottom edge).
            if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SPACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_AIR)))
            {
                symbolInfo.iconOffset = HAT_UP_OFFSET;
                symbolInfo.iconSize = HAT_UP_SIZE;
            }
            // Hat Down icon (tall rectangle without a top edge).
            else if (bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                symbolInfo.iconOffset = HAT_DOWN_OFFSET;
                symbolInfo.iconSize = HAT_DOWN_SIZE;
            }
            // Square icon.
            else if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF)))
            {
                symbolInfo.iconOffset = SQUARE_OFFSET;
                symbolInfo.iconSize = SQUARE_SIZE;
            }
            // Square icon for Special C2 Headquarters symbols. Must appear before Square icon for Ground symbols.
            else if (scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && si != null && si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_NEUTRAL)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && fi != null && fi.equalsIgnoreCase("UH----"))
            {
                symbolInfo.iconOffset = SQUARE_OFFSET;
                symbolInfo.iconSize = SQUARE_SIZE;
                symbolInfo.iconOffset = SQUARE_C2_HQ_OFFSET;
                symbolInfo.isGroundSymbol = true;
            }
            // Square icon for Ground symbols.
            else if ((bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
                || (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS)
                || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT))))
            {
                symbolInfo.iconOffset = SQUARE_OFFSET;
                symbolInfo.iconSize = SQUARE_SIZE;
                symbolInfo.isGroundSymbol = true;
            }
        }
        // Tent Up, Tent Down, Diamond.
        else if (si != null && (si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_HOSTILE)
            || si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_SUSPECT)))
        {
            // Tent Up icon.
            if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SPACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_AIR)))
            {
                symbolInfo.iconOffset = TENT_UP_OFFSET;
                symbolInfo.iconSize = TENT_UP_SIZE;
            }
            // Tent Down icon.
            else if (bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SUBSURFACE))
            {
                symbolInfo.iconOffset = TENT_DOWN_OFFSET;
                symbolInfo.iconSize = TENT_DOWN_SIZE;
            }
            // Diamond icon.
            else if (bd != null && (bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_UNKNOWN)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SEA_SURFACE)
                || bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_SOF)))
            {
                symbolInfo.iconOffset = DIAMOND_OFFSET;
                symbolInfo.iconSize = DIAMOND_SIZE;
            }
            // Diamond icon for Special C2 Headquarters symbols. Must appear before Diamond icon for Ground symbols.
            else if (scheme != null && scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING)
                && si != null && si.equalsIgnoreCase(SymbologyConstants.STANDARD_IDENTITY_HOSTILE)
                && bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND)
                && fi != null && fi.equalsIgnoreCase("UH----"))
            {
                symbolInfo.iconOffset = DIAMOND_OFFSET;
                symbolInfo.iconSize = DIAMOND_SIZE;
                symbolInfo.offset = DIAMOND_C2_HQ_OFFSET;
                symbolInfo.isGroundSymbol = true;
            }
            // Diamond icon for Ground symbols.
            else if ((bd != null && bd.equalsIgnoreCase(SymbologyConstants.BATTLE_DIMENSION_GROUND))
                || (scheme != null && (scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS)
                || scheme.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT))))
            {
                symbolInfo.iconOffset = DIAMOND_OFFSET;
                symbolInfo.iconSize = DIAMOND_SIZE;
                symbolInfo.isGroundSymbol = true;
            }
        }

        return symbolInfo;
    }

    public static List<? extends Point2D> computeCenterHeadingIndicatorPoints(DrawContext dc, Angle heading,
        double length)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (heading == null)
        {
            String msg = Logging.getMessage("nullValue.HeadingIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Angle angle = dc.getView().getHeading().add(Angle.POS90).subtract(heading);
        double dx = length * angle.cos();
        double dy = length * angle.sin();

        return Arrays.asList(
            new Point2D.Double(0, 0),
            new Point2D.Double(dx, dy));
    }

    public static List<? extends Point2D> computeGroundHeadingIndicatorPoints(DrawContext dc, Angle heading,
        double length, double frameHeight)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (heading == null)
        {
            String msg = Logging.getMessage("nullValue.HeadingIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Angle angle = dc.getView().getHeading().add(Angle.POS90).subtract(heading);
        double dx = length * angle.cos();
        double dy = length * angle.sin();

        return Arrays.asList(
            new Point2D.Double(0, 0),
            new Point2D.Double(0, -frameHeight / 2d),
            new Point2D.Double(dx, -frameHeight / 2d + dy));
    }

    public static Font computeTextModifierFont(double frameHeight)
    {
        Font defaultFont = BasicTacticalSymbolAttributes.DEFAULT_TEXT_MODIFIER_FONT;
        double textHeight = TEXT_MODIFIER_FONT_SIZE_FACTOR * frameHeight;

        Integer scaledSize = WWUtil.convertPixelsToFontSize((int) textHeight);
        return scaledSize != null ? new Font(defaultFont.getName(), defaultFont.getStyle(), scaledSize) : null;
    }
}
