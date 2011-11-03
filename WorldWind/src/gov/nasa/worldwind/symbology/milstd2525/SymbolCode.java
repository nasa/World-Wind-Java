/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.util.Logging;

/**
 * Class to parse and manipulate MIL-STD-2525 Symbol Identifier Codes (SIDC). Different sections of the 2525
 * specification use different types of symbol codes, so not all fields will be defined in every code.
 * <p/>
 * Fields in a parsed symbol code can be accessed using explicit get and set methods, or using String keys. The possible
 * keys are: <ul><li>AVKey.SCHEME</li> <li>AVKey.STANDARD_IDENTITY</li> <li>AVKey.CATEGORY</li>
 * <li>AVKey.BATTLE_DIMENSION</li> <li>AVKey.FUNCTION_ID</li> <li>AVKey.ECHELON</li> <li>AVKey.SYMBOL_MODIFIER</li>
 * <li>AVKey.STATUS</li> <li>AVKey.COUNTRY_CODE</li> <li>AVKey.ORDER_OF_BATTLE</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class SymbolCode extends AVListImpl
{
    public static final String SCHEME_WARFIGHTING = "S";
    public static final String SCHEME_SIGNALS_INTELLIGENCE = "I";
    public static final String SCHEME_STABILITY_OPERATIONS = "O";
    public static final String SCHEME_EMERGENCY_MANAGEMENT = "E";
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

    // fill colors (hue, out of 360)
    public static final int COLOR_UNKNOWN = 60;       // yellow
    public static final int COLOR_FRIEND = 195;       // blue
    public static final int COLOR_NEUTRAL = 120;      // green  TODO: not quite the correct green, Sat = 33, not 50
    public static final int COLOR_HOSTILE = 0;        // red
    public static final int COLOR_CIVILIAN = 300;     // purple   TODO:  Sat = 37, not 50

    /** Create a new symbol code. All fields will be null. */
    public SymbolCode()
    {
        // Intentionally blank
    }

    /**
     * Parse an SIDC code.
     *
     * @param symCode Symbol code to parse.
     */
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

        String scheme = symCode.substring(0, 1).toUpperCase();
        if (SCHEME_TACTICAL_GRAPHICS.equals(scheme))
        {
            this.parseTacticalGraphic(symCode);
        }
        else if (SCHEME_WARFIGHTING.equals(scheme) ||
            SCHEME_SIGNALS_INTELLIGENCE.equals(scheme) ||
            SCHEME_STABILITY_OPERATIONS.equals(scheme) ||
            SCHEME_EMERGENCY_MANAGEMENT.equals(scheme))
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

    /**
     * Retrieves the Coding Scheme field of the symbol code.
     *
     * @return The value of the coding scheme field. May be null.
     */
    public String getScheme()
    {
        return this.getStringValue(AVKey.SCHEME);
    }

    /**
     * Sets the value of the Coding Scheme field.
     *
     * @param scheme New value for the coding scheme field. May be null.
     */
    public void setScheme(String scheme)
    {
        this.setValue(AVKey.SCHEME, scheme.toUpperCase());
    }

    /**
     * Retrieves the StandardIdentity field of the symbol code.
     *
     * @return The value of the standard identity field. May be null.
     */
    public String getStandardIdentity()
    {
        return this.getStringValue(AVKey.STANDARD_IDENTITY);
    }

    /**
     * Sets the value of the Standard Identity field.
     *
     * @param value New value for the coding scheme field. May be null.
     */
    public void setStandardIdentity(String value)
    {
        this.setValue(AVKey.STANDARD_IDENTITY, value.toUpperCase());
    }

    /**
     * Retrieves the Category field of the symbol code.
     *
     * @return The value of the category field. May be null.
     */
    public String getCategory()
    {
        return this.getStringValue(AVKey.CATEGORY);
    }

    /**
     * Sets the value of the Category field.
     *
     * @param value New value for the coding scheme field. May be null.
     */
    public void setCategory(String value)
    {
        this.setValue(AVKey.CATEGORY, value.toUpperCase());
    }

    /**
     * Retrieves the Battle Dimension field of the symbol code.
     *
     * @return The value of the battle dimension field. May be null.
     */
    public String getBattleDimension()
    {
        return this.getStringValue(AVKey.BATTLE_DIMENSION);
    }

    /**
     * Sets the value of the Battle Dimension field.
     *
     * @param value New value for the Battle Dimension field. May be null.
     */
    public void setBattleDimension(String value)
    {
        this.setValue(AVKey.BATTLE_DIMENSION, value.toUpperCase());
    }

    /**
     * Retrieves the Function ID field of the symbol code.
     *
     * @return The value of the function ID field. May be null.
     */
    public String getFunctionId()
    {
        return this.getStringValue(AVKey.FUNCTION_ID);
    }

    /**
     * Sets the value of the Function ID field.
     *
     * @param value New value for the Function ID field. May be null.
     */
    public void setFunctionId(String value)
    {
        this.setValue(AVKey.FUNCTION_ID, value.toUpperCase());
    }

    /**
     * Retrieves the Echelon field of the symbol code.
     *
     * @return The value of the echelon field. May be null.
     */
    public String getEchelon()
    {
        return this.getStringValue(AVKey.ECHELON);
    }

    /**
     * Sets the value of the Echelon field.
     *
     * @param value New value for the Echelon field. May be null.
     */
    public void setEchelon(String value)
    {
        this.setValue(AVKey.ECHELON, value.toUpperCase());
    }

    /**
     * Retrieves the Symbol Modifier field of the symbol code.
     *
     * @return The value of the Symbol Modifier field. May be null.
     */
    public String getSymbolModifier()
    {
        return this.getStringValue(AVKey.SYMBOL_MODIFIER);
    }

    /**
     * Sets the value of the Symbol Modifier field.
     *
     * @param value New value for the Symbol Modifier field. May be null.
     */
    public void setSymbolModifier(String value)
    {
        this.setValue(AVKey.SYMBOL_MODIFIER, value.toUpperCase());
    }

    /**
     * Retrieves the Status/Operation Condition field of the symbol code.
     *
     * @return The value of the Status field. May be null.
     */
    public String getStatus()
    {
        return this.getStringValue(AVKey.STATUS);
    }

    /**
     * Sets the value of the Status field.
     *
     * @param value New value for the Status field. May be null.
     */
    public void setStatus(String value)
    {
        this.setValue(AVKey.STATUS, value.toUpperCase());
    }

    /**
     * Retrieves the Country Code field of the symbol code.
     *
     * @return The value of the Country Code field. May be null.
     */
    public String getCountryCode()
    {
        return this.getStringValue(AVKey.COUNTRY_CODE);
    }

    /**
     * Sets the value of the Country Code field.
     *
     * @param value New value for the Country Code field. May be null.
     */
    public void setCountryCode(String value)
    {
        this.setValue(AVKey.COUNTRY_CODE, value.toUpperCase());
    }

    /**
     * Retrieves the Order of Battle field of the symbol code.
     *
     * @return The value of the Order of Battle field. May be null.
     */
    public String getOrderOfBattle()
    {
        return this.getStringValue(AVKey.ORDER_OF_BATTLE);
    }

    /**
     * Sets the value of the Order of Battle field.
     *
     * @param value New value for the Order of Battle field. May be null.
     */
    public void setOrderOfBattle(String value)
    {
        this.setValue(AVKey.ORDER_OF_BATTLE, value.toUpperCase());
    }

    protected void parseTacticalGraphic(String symCode)
    {
        char c = symCode.charAt(1);
        this.setStandardIdentity(Character.toString(c));

        c = symCode.charAt(2);
        this.setCategory(Character.toString(c));

        c = symCode.charAt(3);
        this.setStatus(Character.toString(c));

        String s = symCode.substring(4, 10);
        this.setFunctionId(s);

        s = symCode.substring(10, 12);
        this.setEchelon(s);

        s = symCode.substring(12, 14);
        this.setCountryCode(s);

        s = symCode.substring(14, 15);
        this.setOrderOfBattle(s);
    }

    protected void parseTacticalSymbol(String symCode)
    {
        char c = symCode.charAt(0);
        this.setScheme(Character.toString(c));
        String scheme = this.getScheme();

        c = symCode.charAt(1);
        if ("PUAFNSHGWMDLJKpuafnshgwmdljk".indexOf(c) == -1)
        {
            // Standard Identity code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setStandardIdentity(Character.toString(c));

        c = symCode.charAt(2);
        // Warfighting and Signals Intelligence schemes use Battle Dimension
        if (SCHEME_WARFIGHTING.equals(scheme) ||
            SCHEME_SIGNALS_INTELLIGENCE.equals(scheme))
        {
            if ("PAGSUFXZpagsufxz".indexOf(c) == -1)
            {
                // Battle Dimension code not recognized
                String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.setBattleDimension(Character.toString(c));
        }
        // Stability Operations scheme uses Category
        else if (SCHEME_STABILITY_OPERATIONS.equals(scheme))
        {
            if ("VLOIPGRvloipgr".indexOf(c) == -1)
            {
                // Stability Operations Category code not recognized
                String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.setCategory(Character.toString(c));
        }
        //  Emergency Management scheme uses Category
        else if (SCHEME_EMERGENCY_MANAGEMENT.equals(scheme))
        {
            if ("INOFinof".indexOf(c) == -1)
            {
                // Emergency Management Category code not recognized
                String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            this.setCategory(Character.toString(c));
        }

        c = symCode.charAt(3);
        if ("APCDXFapcdxf".indexOf(c) == -1)
        {
            // Status/Operational Condition code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setStatus(Character.toString(c));

        String s = symCode.substring(4, 10);
        this.setFunctionId(s);

        s = symCode.substring(10, 12);
        this.setSymbolModifier(s);

        s = symCode.substring(12, 14);
        this.setCountryCode(s);

        c = symCode.charAt(14);
        if ("-AECGNSaecgns".indexOf(c) == -1)
        {
            // Order of Battle code not recognized
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.setOrderOfBattle(Character.toString(c));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        String scheme = this.getScheme();
        if (SCHEME_TACTICAL_GRAPHICS.equals(scheme))
        {
            sb.append(scheme);
            sb.append(this.getStandardIdentity());
            sb.append(this.getCategory());
            sb.append(this.getStatus());
            sb.append(this.getFunctionId());
            sb.append(this.getEchelon());
            sb.append(this.getCountryCode());
            sb.append(this.getOrderOfBattle());
        }
        else if (SCHEME_WARFIGHTING.equals(scheme) ||
            SCHEME_SIGNALS_INTELLIGENCE.equals(scheme))
        {
            sb.append(scheme);
            sb.append(this.getStandardIdentity());
            sb.append(this.getBattleDimension());
            sb.append(this.getStatus());
            sb.append(this.getFunctionId());
            sb.append(this.getSymbolModifier());
            sb.append(this.getCountryCode());
            sb.append(this.getOrderOfBattle());
        }
        else if (SCHEME_STABILITY_OPERATIONS.equals(scheme) ||
            SCHEME_EMERGENCY_MANAGEMENT.equals(scheme))
        {
            sb.append(scheme);
            sb.append(this.getStandardIdentity());
            sb.append(this.getCategory());
            sb.append(this.getStatus());
            sb.append(this.getFunctionId());
            sb.append(this.getSymbolModifier());
            sb.append(this.getCountryCode());
            sb.append(this.getOrderOfBattle());
        }

        return sb.toString();
    }
}
