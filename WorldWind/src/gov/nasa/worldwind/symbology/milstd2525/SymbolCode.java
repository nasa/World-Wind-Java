/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Logging;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class SymbolCode extends AVListImpl
{
    // Constants for the fields of a symbol code. These constants match the property
    // names used by tactical graphic implementation classes, so factories can use
    // reflection to configure graphic objects.
    public static final String SCHEME = "Scheme";
    public static final String STANDARD_IDENTITY = "StandardIdentity";
    public static final String CATEGORY = "Category";
    public static final String BATTLE_DIMENSION = "BattleDimension";
    public static final String FUNCTION_ID = "FunctionId";
    public static final String ECHELON = "Echelon";
    public static final String SYMBOL_MODIFIER = "SymbolModifier";
    public static final String STATUS = "Status";
    public static final String COUNTRY_CODE = "CountryCode";
    public static final String ORDER_OF_BATTLE = "OrderOfBattle";

    public static final String SCHEME_WARFIGHTING = "S";
    public static final String SCHEME_TACTICAL_GRAPHICS = "G";

    public static final String IDENTITY_PENDING = "P";
    public static final String IDENTITY_UNKNOWN = "U";
    public static final String IDENTITY_ASSUMED_FRIEND = "A";
    public static final String IDENTITY_FRIEND = "F";
    public static final String IDENTITY_NEUTRAL = "N";
    public static final String IDENTITY_SUSPECT = "S";
    public static final String IDENTITY_HOSTILE = "H";
    public static final String IDENTITY_EXERCISE_PENDING = "G";
    public static final String IDENTITY_EXERCISE_UNKNOWN = "W";
    public static final String IDENTITY_EXERCISE_ASSUMED_FRIEND = "M";
    public static final String IDENTITY_EXERCISE_FRIEND = "D";
    public static final String IDENTITY_EXERCISE_NEUTRAL = "L";
    public static final String IDENTITY_JOKER = "J";
    public static final String IDENTITY_FAKER = "K";

    public static final String BATTLE_DIMENSION_UNKNOWN = "Z";
    public static final String BATTLE_DIMENSION_SPACE = "P";
    public static final String BATTLE_DIMENSION_AIR = "A";
    public static final String BATTLE_DIMENSION_GROUND = "G";
    public static final String BATTLE_DIMENSION_SEA_SURFACE = "S";
    public static final String BATTLE_DIMENSION_SUBSURFACE = "U";
    public static final String BATTLE_DIMENSION_SOF = "F";

    public static final String CATEGORY_TASKS = "T";
    public static final String CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER = "G";
    public static final String CATEGORY_MOBILITY_SURVIVAL = "M";
    public static final String CATEGORY_FIRE_SUPPORT_COMBAT_SERVICE_SUPPORT = "F";
    public static final String CATEGORY_OTHER = "S";

    public static final String STATUS_ANTICIPATED = "A";
    public static final String STATUS_SUSPECTED = "S";
    public static final String STATUS_PRESENT = "P";
    public static final String STATUS_KNOWN = "K";

    // fill colors
    public static final int COLOR_UNKNOWN = 60;      // yellow
    public static final int COLOR_FRIEND = 195;      // blue
    public static final int COLOR_NEUTRAL = 120;     // green  TODO: not quite the correct green, Sat = 33, not 50
    public static final int COLOR_HOSTILE = 0;      // red
    public static final int COLOR_CIVILIAN = 300;     // purple   TODO:  Sat = 37, not 50

    public SymbolCode()
    {
        // Intentionally blank
    }

    public SymbolCode(String symCode)
    {
        if (symCode == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (symCode.length() != 15)
        {
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        String scheme = symCode.substring(0, 1);
        if (SCHEME_TACTICAL_GRAPHICS.equals(scheme))
        {
            this.parseTacticalGraphic(symCode);
        }
        else if (SCHEME_WARFIGHTING.equals(scheme))
        {
            this.parseTacticalSymbol(symCode);
        }
        else
        {
            // Scheme code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    protected void parseTacticalGraphic(String symCode)
    {
        char c = symCode.charAt(1);
        this.setValue(SymbolCode.STANDARD_IDENTITY, Character.toString(c));

        c = symCode.charAt(2);
        this.setValue(SymbolCode.CATEGORY, Character.toString(c));

        c = symCode.charAt(3);
        this.setValue(SymbolCode.STATUS, Character.toString(c));

        String s = symCode.substring(4, 10);
        this.setValue(SymbolCode.FUNCTION_ID, s);

        s = symCode.substring(10, 12);
        this.setValue(SymbolCode.ECHELON, s);

        s = symCode.substring(12, 14);
        this.setValue(SymbolCode.COUNTRY_CODE, s);

        s = symCode.substring(14, 15);
        this.setValue(SymbolCode.ORDER_OF_BATTLE, s);
    }

    protected void parseTacticalSymbol(String symCode)
    {
        char c = symCode.charAt(0);
        this.setValue(SymbolCode.SCHEME, Character.toString(c));

        c = symCode.charAt(1);
        if ("PUAFNSHGWMDLJKpuafnshgwmdljk".indexOf(c) == -1)
        {
            // Standard Identity code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setValue(SymbolCode.STANDARD_IDENTITY, Character.toString(c));

        c = symCode.charAt(2);
        if ("PAGSUFXZpagsufxz".indexOf(c) == -1)
        {
            // Battle Dimension code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setValue(SymbolCode.BATTLE_DIMENSION, Character.toString(c));

        c = symCode.charAt(3);
        if ("APCDXFapcdxf".indexOf(c) == -1)
        {
            // Status/Operational Condition code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setValue(SymbolCode.STATUS, Character.toString(c));

        String s = symCode.substring(4, 10);
        this.setValue(SymbolCode.FUNCTION_ID, s);

        s = symCode.substring(10, 12);
        this.setValue(SymbolCode.SYMBOL_MODIFIER, s);

        s = symCode.substring(12, 14);
        this.setValue(SymbolCode.COUNTRY_CODE, s);

        c = symCode.charAt(14);
        if ("-AECGNSaecgns".indexOf(c) == -1)
        {
            // Order of Battle code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setValue(SymbolCode.ORDER_OF_BATTLE, Character.toString(c));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        String scheme = this.getStringValue(SCHEME);
        if (SCHEME_TACTICAL_GRAPHICS.equals(scheme))
        {
            sb.append(scheme);
            sb.append(this.getStringValue(STANDARD_IDENTITY));
            sb.append(this.getStringValue(CATEGORY));
            sb.append(this.getStringValue(STATUS));
            sb.append(this.getStringValue(FUNCTION_ID));
            sb.append(this.getStringValue(ECHELON));
            sb.append(this.getStringValue(COUNTRY_CODE));
            sb.append(this.getStringValue(ORDER_OF_BATTLE));
        }
        else if (SCHEME_WARFIGHTING.equals(scheme))
        {
            sb.append(scheme);
            sb.append(this.getStringValue(STANDARD_IDENTITY));
            sb.append(this.getStringValue(BATTLE_DIMENSION));
            sb.append(this.getStringValue(STATUS));
            sb.append(this.getStringValue(FUNCTION_ID));
            sb.append(this.getStringValue(SYMBOL_MODIFIER));
            sb.append(this.getStringValue(COUNTRY_CODE));
            sb.append(this.getStringValue(ORDER_OF_BATTLE));
        }

        return sb.toString();
    }
}
