/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWUnrecognizedException;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.util.Logging;

/**
 * Class to parse and manipulate MIL-STD-2525 symbol identification codes (SIDC). Different sections of the 2525
 * specification use different types of symbol codes, so not all fields will be defined in every code.
 * <p/>
 * Fields in a parsed symbol code can be accessed using explicit get and set methods, or using String keys. The possible
 * keys are: <ul><li>SymbologyConstants.SCHEME</li> <li>SymbologyConstants.STANDARD_IDENTITY</li>
 * <li>SymbologyConstants.CATEGORY</li> <li>SymbologyConstants.BATTLE_DIMENSION</li>
 * <li>SymbologyConstants.FUNCTION_ID</li> <li>SymbologyConstants.ECHELON</li> <li>SymbologyConstants.SYMBOL_MODIFIER</li>
 * <li>SymbologyConstants.STATUS</li> <li>SymbologyConstants.COUNTRY_CODE</li> <li>SymbologyConstants.ORDER_OF_BATTLE</li>
 * </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class SymbolCode extends AVListImpl
{
    // fill colors (hue, out of 360)
    public static final int COLOR_UNKNOWN = 60;       // yellow
    public static final int COLOR_FRIEND = 195;       // blue
    public static final int COLOR_NEUTRAL = 120;      // green  TODO: not quite the correct green, Sat = 33, not 50
    public static final int COLOR_HOSTILE = 0;        // red
    public static final int COLOR_CIVILIAN = 300;     // purple   TODO:  Sat = 37, not 50

    /** Indicates the character for an unused position in a MIL-STD-2525 symbol identification code */
    protected static final String UNUSED_POSITION_CODE = "-";

    /** Create a new symbol code. All fields will be null. */
    public SymbolCode()
    {
        // Intentionally blank
    }

    /**
     * Creates a new SymCode by parsing the fields of the specified MIL-STD-2525 15-character alphanumeric symbol
     * identification code (SIDC).
     *
     * @param symCode the symbol identification code to parse.
     *
     * @throws IllegalArgumentException if the symCode is null or has length other than 15.
     * @throws WWUnrecognizedException  if any field in the symCode is invalid or cannot be recognized.
     */
    public SymbolCode(String symCode)
    {
        if (symCode == null)
        {
            String msg = Logging.getMessage("nullValue.SymbolCodeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (symCode.length() != 15)
        {
            String msg = Logging.getMessage("Symbology.SymbolCodeLengthInvalid", symCode);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        String s = this.parseSymCode(symCode);
        if (s != null)
        {
            Logging.logger().severe(s);
            throw new WWUnrecognizedException(s);
        }
    }

    /**
     * Retrieves the Coding Scheme field of the symbol code.
     *
     * @return The value of the coding scheme field. May be null.
     */
    public String getScheme()
    {
        return this.getStringValue(SymbologyConstants.SCHEME);
    }

    /**
     * Sets the value of the Coding Scheme field.
     *
     * @param scheme New value for the coding scheme field. May be null.
     */
    public void setScheme(String scheme)
    {
        this.setValue(SymbologyConstants.SCHEME, scheme);
    }

    /**
     * Retrieves the StandardIdentity field of the symbol code.
     *
     * @return The value of the standard identity field. May be null.
     */
    public String getStandardIdentity()
    {
        return this.getStringValue(SymbologyConstants.STANDARD_IDENTITY);
    }

    /**
     * Sets the value of the Standard Identity field.
     *
     * @param value New value for the coding scheme field. May be null.
     */
    public void setStandardIdentity(String value)
    {
        this.setValue(SymbologyConstants.STANDARD_IDENTITY, value);
    }

    /**
     * Retrieves the Battle Dimension field of the symbol code.
     *
     * @return The value of the battle dimension field. May be null.
     */
    public String getBattleDimension()
    {
        return this.getStringValue(SymbologyConstants.BATTLE_DIMENSION);
    }

    /**
     * Sets the value of the Battle Dimension field.
     *
     * @param value New value for the Battle Dimension field. May be null.
     */
    public void setBattleDimension(String value)
    {
        this.setValue(SymbologyConstants.BATTLE_DIMENSION, value);
    }

    /**
     * Retrieves the Category field of the symbol code.
     *
     * @return The value of the category field. May be null.
     */
    public String getCategory()
    {
        return this.getStringValue(SymbologyConstants.CATEGORY);
    }

    /**
     * Sets the value of the Category field.
     *
     * @param value New value for the coding scheme field. May be null.
     */
    public void setCategory(String value)
    {
        this.setValue(SymbologyConstants.CATEGORY, value);
    }

    /**
     * Retrieves the Status/Operation Condition field of the symbol code.
     *
     * @return The value of the Status field. May be null.
     */
    public String getStatus()
    {
        return this.getStringValue(SymbologyConstants.STATUS);
    }

    /**
     * Sets the value of the Status field.
     *
     * @param value New value for the Status field. May be null.
     */
    public void setStatus(String value)
    {
        this.setValue(SymbologyConstants.STATUS, value);
    }

    /**
     * Retrieves the Function ID field of the symbol code.
     *
     * @return The value of the function ID field. May be null.
     */
    public String getFunctionId()
    {
        return this.getStringValue(SymbologyConstants.FUNCTION_ID);
    }

    /**
     * Sets the value of the Function ID field.
     *
     * @param value New value for the Function ID field. May be null.
     */
    public void setFunctionId(String value)
    {
        this.setValue(SymbologyConstants.FUNCTION_ID, value);
    }

    /**
     * Retrieves the Echelon field of the symbol code.
     *
     * @return The value of the echelon field. May be null.
     */
    public String getEchelon()
    {
        return this.getStringValue(SymbologyConstants.ECHELON);
    }

    /**
     * Sets the value of the Echelon field.
     *
     * @param value New value for the Echelon field. May be null.
     */
    public void setEchelon(String value)
    {
        this.setValue(SymbologyConstants.ECHELON, value);
    }

    /**
     * Retrieves the Symbol Modifier field of the symbol code.
     *
     * @return The value of the Symbol Modifier field. May be null.
     */
    public String getSymbolModifier()
    {
        return this.getStringValue(SymbologyConstants.SYMBOL_MODIFIER);
    }

    /**
     * Sets the value of the Symbol Modifier field.
     *
     * @param value New value for the Symbol Modifier field. May be null.
     */
    public void setSymbolModifier(String value)
    {
        this.setValue(SymbologyConstants.SYMBOL_MODIFIER, value);
    }

    public AVList getSymbolModifierParams(AVList params)
    {
        String code = this.getSymbolModifier();

        if (code == null || code.length() != 2 || code.equals("--"))
            return params;

        if (params == null)
            params = new AVListImpl();

        String firstChar = code.substring(0, 1);
        String secondChar = code.substring(1, 2);
        String uppercaseCode = code.toUpperCase();
        String uppercaseFirstChar = firstChar.toUpperCase();
        String uppercaseSecondChar = secondChar.toUpperCase();

        if (UNUSED_POSITION_CODE.equals(uppercaseFirstChar)
            || SymbologyConstants.UNIT_EQUIPMENT_ALL.contains(uppercaseFirstChar))
        {
            if (SymbologyConstants.ECHELON_ALL.contains(uppercaseSecondChar))
                params.setValue(SymbologyConstants.ECHELON, secondChar);

            if (SymbologyConstants.UNIT_EQUIPMENT_ALL_HEADQUARTERS.contains(uppercaseFirstChar))
                params.setValue(SymbologyConstants.HEADQUARTERS, Boolean.TRUE);

            if (SymbologyConstants.UNIT_EQUIPMENT_ALL_TASK_FORCE.contains(uppercaseFirstChar))
                params.setValue(SymbologyConstants.TASK_FORCE, Boolean.TRUE);

            if (SymbologyConstants.UNIT_EQUIPMENT_ALL_FEINT_DUMMY.contains(uppercaseFirstChar))
                params.setValue(SymbologyConstants.FEINT_DUMMY, Boolean.TRUE);
        }
        else if (SymbologyConstants.INSTALLATION_ALL.contains(uppercaseCode))
        {
            params.setValue(SymbologyConstants.INSTALLATION, code);

            if (SymbologyConstants.INSTALLATION_FEINT_DUMMY.equalsIgnoreCase(code))
                params.setValue(SymbologyConstants.FEINT_DUMMY, Boolean.TRUE);
        }
        else if (SymbologyConstants.MOBILITY_ALL.contains(uppercaseCode))
        {
            params.setValue(SymbologyConstants.MOBILITY, code);
        }
        else if (SymbologyConstants.AUXILIARY_EQUIPMENT_ALL.contains(uppercaseCode))
        {
            params.setValue(SymbologyConstants.AUXILIARY_EQUIPMENT, code);
        }

        return params;
    }

    /**
     * Retrieves the Country Code field of the symbol code.
     *
     * @return The value of the Country Code field. May be null.
     */
    public String getCountryCode()
    {
        return this.getStringValue(SymbologyConstants.COUNTRY_CODE);
    }

    /**
     * Sets the value of the Country Code field.
     *
     * @param value New value for the Country Code field. May be null.
     */
    public void setCountryCode(String value)
    {
        this.setValue(SymbologyConstants.COUNTRY_CODE, value);
    }

    /**
     * Retrieves the Order of Battle field of the symbol code.
     *
     * @return The value of the Order of Battle field. May be null.
     */
    public String getOrderOfBattle()
    {
        return this.getStringValue(SymbologyConstants.ORDER_OF_BATTLE);
    }

    /**
     * Sets the value of the Order of Battle field.
     *
     * @param value New value for the Order of Battle field. May be null.
     */
    public void setOrderOfBattle(String value)
    {
        this.setValue(SymbologyConstants.ORDER_OF_BATTLE, value);
    }

    public String toString()
    {
        return this.composeSymCode();
    }

    protected String parseSymCode(String symCode)
    {
        // Coding Scheme (position 1).
        String scheme = symCode.substring(0, 1);

        if (SymbologyConstants.SCHEME_WARFIGHTING.equalsIgnoreCase(scheme))
        {
            return this.parseWarfightingSymCode(symCode);
        }
        else if (SymbologyConstants.SCHEME_TACTICAL_GRAPHICS.equalsIgnoreCase(scheme))
        {
            return this.parseTacticalGraphicsSymCode(symCode);
        }
        else if (SymbologyConstants.SCHEME_METOC.equalsIgnoreCase(scheme))
        {
            return this.parseMetocSymCode(symCode);
        }
        else if (SymbologyConstants.SCHEME_INTELLIGENCE.equalsIgnoreCase(scheme))
        {
            return this.parseIntelligenceSymCode(symCode);
        }
        else if (SymbologyConstants.SCHEME_STABILITY_OPERATIONS.equalsIgnoreCase(scheme))
        {
            return this.parseStabilityOperationsSymCode(symCode);
        }
        else if (SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equalsIgnoreCase(scheme))
        {
            return this.parseEmergencyManagementSymCode(symCode);
        }
        else
        {
            return this.parseUnrecognizedSymCode(symCode);
        }
    }

    protected String parseUnrecognizedSymCode(String symCode)
    {
        // The scheme code is not recognized. Throw an exception indicating that the symCode is invalid. Note that
        // MIL-STD-2525C, Appendix F does not specify a symbology scheme. Instead, it provides guidelines for
        // displaying symbology in 3D displays.
        String scheme = symCode.substring(0, 1);
        return Logging.getMessage("Symbology.SymbolCodeSchemeUnrecognized", scheme, symCode);
    }

    /**
     * Parses symbol codes encoded for the Warfighting coding scheme. The Warfighting coding scheme is defined in
     * MIL-STD-2525C, Appendix A, table A-I (p. 51).
     *
     * @param symCode the symbol code to parse. Must be non-null and have length of 15 or greater. Characters beyond the
     *                15th are ignored.
     *
     * @return <code>null</code> if the symbol code is recognized, otherwise a non-null string listing the unrecognized
     *         symbol elements.
     */
    protected String parseWarfightingSymCode(String symCode)
    {
        StringBuilder sb = new StringBuilder();

        // Coding Scheme (position 1).
        String s = symCode.substring(0, 1);
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.SCHEME_WARFIGHTING))
            this.setScheme(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.scheme"));

        // Standard Identity/Exercise Amplifying Descriptor (position 2).
        s = symCode.substring(1, 2);
        if (SymbologyConstants.STANDARD_IDENTITY_ALL.contains(s.toUpperCase()))
            this.setStandardIdentity(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.standardIdentity"));

        // Battle Dimension (position 3).
        s = symCode.substring(2, 3);
        if (SymbologyConstants.BATTLE_DIMENSION_ALL.contains(s.toUpperCase()))
            this.setBattleDimension(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.battleDimension"));

        // Status/Operational Condition (position 4).
        s = symCode.substring(3, 4);
        if (SymbologyConstants.STATUS_ALL_UEI_SIGINT_SO.contains(s.toUpperCase()))
            this.setStatus(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.status"));

        // Function ID (positions 5-10).
        s = symCode.substring(4, 10);
        if (!"-----".equals(s)) // Just validate that the function id is not null.
            this.setFunctionId(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.functionID"));

        // Symbol Modifier (positions 11-12).
        s = symCode.substring(10, 12);
        if (this.isUnitSymbolModifier(s)
            || SymbologyConstants.INSTALLATION_ALL.contains(s.toUpperCase())
            || SymbologyConstants.MOBILITY_ALL.contains(s.toUpperCase())
            || SymbologyConstants.AUXILIARY_EQUIPMENT_ALL.contains(s.toUpperCase()))
        {
            this.setSymbolModifier(s);
        }
        else if (!"--".equals(s)) // "--" is accepted and indicates a null symbol modifier.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.symbolModifier"));

        // Country Code (positions 13-14).
        s = symCode.substring(12, 14);
        if (!"--".equals(s)) // "--" is accepted and indicates a null country code.
            this.setCountryCode(s);

        // Order Of Battle (position 15).
        s = symCode.substring(14, 15);
        if (SymbologyConstants.ORDER_OF_BATTLE_ALL_UEI_SIGINT_SO_EM.contains(s.toUpperCase()))
            this.setOrderOfBattle(s);
        else if (!"-".equals(s)) // "-" is accepted and indicates a null order of battle.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.orderOfBattle"));

        return sb.length() > 0
            ? Logging.getMessage("Symbology.SymbolCodeElementsUnrecognized", sb.toString(), symCode) : null;
    }

    /**
     * Parses symbol codes encoded for the Tactical Graphics coding scheme. The Tactical Graphics coding scheme is
     * defined in MIL-STD-2525C, Appendix B, table B-I (p. 305).
     *
     * @param symCode the symbol code to parse. Must be non-null and have length of 15 or greater. Characters beyond the
     *                15th are ignored.
     *
     * @return <code>null</code> if the symbol code is recognized, otherwise a non-null string listing the unrecognized
     *         symbol elements.
     */
    protected String parseTacticalGraphicsSymCode(String symCode)
    {
        StringBuilder sb = new StringBuilder();

        // Coding Scheme (position 1).
        String s = symCode.substring(0, 1);
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.SCHEME_TACTICAL_GRAPHICS))
            this.setScheme(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.scheme"));

        // Standard Identity/Exercise Amplifying Descriptor (position 2).
        s = symCode.substring(1, 2);
        if (SymbologyConstants.STANDARD_IDENTITY_ALL.contains(s.toUpperCase()))
            this.setStandardIdentity(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.standardIdentity"));

        // Category (position 3).
        s = symCode.substring(2, 3);
        if (SymbologyConstants.CATEGORY_ALL_TACTICAL_GRAPHICS.contains(s.toUpperCase()))
            this.setCategory(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.category"));

        // Status/Operational Condition (position 4).
        s = symCode.substring(3, 4);
        if (SymbologyConstants.STATUS_ALL_TACTICAL_GRAPHICS_METOC.contains(s.toUpperCase()))
            this.setStatus(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.status"));

        // Function ID (positions 5-10).
        s = symCode.substring(4, 10);
        if (!"-----".equals(s)) // Just validate that the function id is not null.
            this.setFunctionId(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.functionID"));

        // Echelon (positions 11-12, position 11 is unused in MIL-STD-2525C).
        s = symCode.substring(11, 12);
        if (SymbologyConstants.ECHELON_ALL.contains(s.toUpperCase()))
            this.setEchelon(s);
        else if (!UNUSED_POSITION_CODE.equals(s)) // "-" is accepted and indicates a null echelon.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.echelon"));

        // Country Code (positions 13-14).
        s = symCode.substring(12, 14);
        if (!"--".equals(s)) // "--" is accepted and indicates a null country code. We don't validate country codes.
            this.setCountryCode(s);

        // Order Of Battle (position 15).
        s = symCode.substring(14, 15);
        if (SymbologyConstants.ORDER_OF_BATTLE_ALL_TACTICAL_GRAPHICS.contains(s.toUpperCase()))
            this.setOrderOfBattle(s);
        else if (!"-".equals(s)) // "-" is accepted and indicates a null order of battle.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.orderOfBattle"));

        return sb.length() > 0
            ? Logging.getMessage("Symbology.SymbolCodeElementsUnrecognized", sb.toString(), symCode) : null;
    }

    protected String parseMetocSymCode(String symCode)
    {
        // TODO: See MIL-STD-2525C, Appendix C
        // Causes constructor to throw a WWUnsupportedException.
        String scheme = symCode.substring(0, 1);
        return Logging.getMessage("Symbology.SymbolCodeSchemeUnrecognized", scheme, symCode);
    }

    protected String parseIntelligenceSymCode(String symCode)
    {
        StringBuilder sb = new StringBuilder();

        // Coding Scheme (position 1).
        String s = symCode.substring(0, 1);
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.SCHEME_INTELLIGENCE))
            this.setScheme(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.scheme"));

        // Standard Identity/Exercise Amplifying Descriptor (position 2).
        s = symCode.substring(1, 2);
        if (SymbologyConstants.STANDARD_IDENTITY_ALL.contains(s.toUpperCase()))
            this.setStandardIdentity(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.standardIdentity"));

        // Battle Dimension (position 3).
        s = symCode.substring(2, 3);
        if (SymbologyConstants.BATTLE_DIMENSION_ALL_INTELLIGENCE.contains(s.toUpperCase()))
            this.setBattleDimension(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.battleDimension"));

        // Status/Operational Condition (position 4)
        s = symCode.substring(3, 4);
        if (SymbologyConstants.STATUS_ALL_UEI_SIGINT_SO.contains(s.toUpperCase()))
            this.setStatus(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.status"));

        // Function ID (positions 5-10)
        s = symCode.substring(4, 10);
        if (!"-----".equals(s)) // Just validate that the function id is not null.
            this.setFunctionId(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.functionID"));

        // Not Used (positions 11-12).
        s = symCode.substring(10, 12);
        if (!"--".equals(s)) // "--" is the only accepted string in positions 11-12.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.symbolModifier"));

        // Country Code (positions 13-14).
        s = symCode.substring(12, 14);
        if (!"--".equals(s)) // "--" is accepted and indicates a null country code.
            this.setCountryCode(s);

        // Order of Battle (position 15).
        s = symCode.substring(14, 15);
        if (SymbologyConstants.ORDER_OF_BATTLE_ALL_UEI_SIGINT_SO_EM.contains(s.toUpperCase()))
            this.setOrderOfBattle(s);
        else if (!"-".equals(s)) // "-" is accepted and indicates a null order of battle.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.orderOfBattle"));

        return sb.length() > 0
            ? Logging.getMessage("Symbology.SymbolCodeElementsUnrecognized", sb.toString(), symCode) : null;
    }

    protected String parseStabilityOperationsSymCode(String symCode)
    {
        StringBuilder sb = new StringBuilder();

        // Coding Scheme (position 1).
        String s = symCode.substring(0, 1);
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.SCHEME_STABILITY_OPERATIONS))
            this.setScheme(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.scheme"));

        // Standard Identity/Exercise Amplifying Descriptor (position 2).
        s = symCode.substring(1, 2);
        if (SymbologyConstants.STANDARD_IDENTITY_ALL.contains(s.toUpperCase()))
            this.setStandardIdentity(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.standardIdentity"));

        // Category (position 3).
        s = symCode.substring(2, 3);
        if (SymbologyConstants.CATEGORY_ALL_STABILITY_OPERATIONS.contains(s.toUpperCase()))
            this.setCategory(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.battleDimension"));

        // Status/Operational Condition (position 4).
        s = symCode.substring(3, 4);
        if (SymbologyConstants.STATUS_ALL_UEI_SIGINT_SO.contains(s.toUpperCase()))
            this.setStatus(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.status"));

        // Function ID (positions 5-10).
        s = symCode.substring(4, 10);
        if (!"-----".equals(s)) // Just validate that the function id is not null.
            this.setFunctionId(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.functionID"));

        // Symbol Modifier (positions 11-12).
        s = symCode.substring(10, 12);
        if (this.isUnitSymbolModifier(s) || SymbologyConstants.INSTALLATION_ALL.contains(s.toUpperCase()))
            this.setSymbolModifier(s);
        else if (!"--".equals(s)) // "--" is accepted and indicates a null symbol modifier.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.symbolModifier"));

        // Country Code (positions 13-14).
        s = symCode.substring(12, 14);
        if (!"--".equals(s)) // "--" is accepted and indicates a null country code.
            this.setCountryCode(s);

        // Order Of Battle (position 15).
        s = symCode.substring(14, 15);
        if (SymbologyConstants.ORDER_OF_BATTLE_ALL_UEI_SIGINT_SO_EM.contains(s.toUpperCase()))
            this.setOrderOfBattle(s);
        else if (!"-".equals(s)) // "-" is accepted and indicates a null order of battle.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.orderOfBattle"));

        return sb.length() > 0
            ? Logging.getMessage("Symbology.SymbolCodeElementsUnrecognized", sb.toString(), symCode) : null;
    }

    protected String parseEmergencyManagementSymCode(String symCode)
    {
        StringBuilder sb = new StringBuilder();

        // Coding Scheme (position 1).
        String s = symCode.substring(0, 1);
        if (s != null && s.equalsIgnoreCase(SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT))
            this.setScheme(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.scheme"));

        // Standard Identity/Exercise Amplifying Descriptor (position 2).
        s = symCode.substring(1, 2);
        if (SymbologyConstants.STANDARD_IDENTITY_ALL.contains(s.toUpperCase()))
            this.setStandardIdentity(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.standardIdentity"));

        // Category (position 3).
        s = symCode.substring(2, 3);
        if (SymbologyConstants.CATEGORY_ALL_EMERGENCY_MANAGEMENT.contains(s.toUpperCase()))
            this.setCategory(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.battleDimension"));

        // Status/Operational Condition (position 4).
        s = symCode.substring(3, 4);
        if (SymbologyConstants.STATUS_ALL_EMERGENCY_MANAGEMENT.contains(s.toUpperCase()))
            this.setStatus(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.status"));

        // Function ID (positions 5-10).
        s = symCode.substring(4, 10);
        if (!"-----".equals(s)) // Just validate that the function id is not null.
            this.setFunctionId(s);
        else
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.functionID"));

        // Symbol Modifier (positions 11-12).
        s = symCode.substring(10, 12);
        if (SymbologyConstants.INSTALLATION_ALL.contains(s.toUpperCase())
            || SymbologyConstants.MOBILITY_ALL.contains(s.toUpperCase()))
        {
            this.setSymbolModifier(s);
        }
        else if (!"--".equals(s)) // "--" is accepted and indicates a null symbol modifier.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.symbolModifier"));

        // Country Code (positions 13-14).
        s = symCode.substring(12, 14);
        if (!"--".equals(s)) // "--" is accepted and indicates a null country code.
            this.setCountryCode(s);

        // Order Of Battle (position 15).
        s = symCode.substring(14, 15);
        if (SymbologyConstants.ORDER_OF_BATTLE_ALL_UEI_SIGINT_SO_EM.contains(s.toUpperCase()))
            this.setOrderOfBattle(s);
        else if (!"-".equals(s)) // "-" is accepted and indicates a null order of battle.
            sb.append(sb.length() > 0 ? ", " : "").append(Logging.getMessage("term.orderOfBattle"));

        return sb.length() > 0
            ? Logging.getMessage("Symbology.SymbolCodeElementsUnrecognized", sb.toString(), symCode) : null;
    }

    protected boolean isUnitSymbolModifier(String value)
    {
        String firstChar = value.substring(0, 1).toUpperCase();
        String secondChar = value.substring(1, 2).toUpperCase();

        return (UNUSED_POSITION_CODE.equals(firstChar) || SymbologyConstants.UNIT_EQUIPMENT_ALL.contains(firstChar))
            && SymbologyConstants.ECHELON_ALL.contains(secondChar.toUpperCase());
    }

    protected String composeSymCode()
    {
        String scheme = this.getScheme();

        if (SymbologyConstants.SCHEME_WARFIGHTING.equalsIgnoreCase(scheme))
        {
            return this.composeWarfightingSymCode();
        }
        else if (SymbologyConstants.SCHEME_TACTICAL_GRAPHICS.equalsIgnoreCase(scheme))
        {
            return this.composeTacticalGraphicsSymCode();
        }
        else if (SymbologyConstants.SCHEME_METOC.equalsIgnoreCase(scheme))
        {
            return this.composeMetocSymCode();
        }
        else if (SymbologyConstants.SCHEME_INTELLIGENCE.equalsIgnoreCase(scheme))
        {
            return this.composeIntelligenceSymCode();
        }
        else if (SymbologyConstants.SCHEME_STABILITY_OPERATIONS.equalsIgnoreCase(scheme))
        {
            return this.composeStabilityOperationsSymCode();
        }
        else if (SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equalsIgnoreCase(scheme))
        {
            return this.composeEmergencyManagementSymCode();
        }
        else
        {
            return this.composeUnrecognizedSymCode();
        }
    }

    protected String composeUnrecognizedSymCode()
    {
        return null;
    }

    protected String composeWarfightingSymCode()
    {
        StringBuilder sb = new StringBuilder();

        this.appendCode(sb, this.getScheme(), 1); // Position 1.
        this.appendCode(sb, this.getStandardIdentity(), 1); // Position 2.
        this.appendCode(sb, this.getBattleDimension(), 1); // Position 3.
        this.appendCode(sb, this.getStatus(), 1); // Position 4.
        this.appendCode(sb, this.getFunctionId(), 5); // Positions 5-10.
        this.appendCode(sb, this.getSymbolModifier(), 2); // Positions 11-12.
        this.appendCode(sb, this.getCountryCode(), 2);  // Positions 13-14.
        this.appendCode(sb, this.getOrderOfBattle(), 1);// Position 15.

        return sb.toString();
    }

    protected String composeTacticalGraphicsSymCode()
    {
        StringBuilder sb = new StringBuilder();

        this.appendCode(sb, this.getScheme(), 1); // Position 1.
        this.appendCode(sb, this.getStandardIdentity(), 1); // Position 2.
        this.appendCode(sb, this.getCategory(), 1); // Position 3.
        this.appendCode(sb, this.getStatus(), 1); // Position 4.
        this.appendCode(sb, this.getFunctionId(), 5); // Positions 5-10.
        sb.append(UNUSED_POSITION_CODE); // Position 11. Unused because the echelon code uses only position 12.
        this.appendCode(sb, this.getEchelon(), 1); // Position 12.
        this.appendCode(sb, this.getCountryCode(), 2);  // Positions 13-14.
        this.appendCode(sb, this.getOrderOfBattle(), 1);// Position 15.

        return sb.toString();
    }

    protected String composeMetocSymCode()
    {
        // TODO: See MIL-STD-2525C, Appendix C
        return null;
    }

    protected String composeIntelligenceSymCode()
    {
        StringBuilder sb = new StringBuilder();

        this.appendCode(sb, this.getScheme(), 1); // Position 1.
        this.appendCode(sb, this.getStandardIdentity(), 1); // Position 2.
        this.appendCode(sb, this.getBattleDimension(), 1); // Position 3.
        this.appendCode(sb, this.getStatus(), 1); // Position 4.
        this.appendCode(sb, this.getFunctionId(), 5); // Positions 5-10.
        sb.append(UNUSED_POSITION_CODE).append(UNUSED_POSITION_CODE); // Positions 11-12 are not used.
        this.appendCode(sb, this.getCountryCode(), 2);  // Positions 13-14.
        this.appendCode(sb, this.getOrderOfBattle(), 1);// Position 15.

        return sb.toString();
    }

    protected String composeStabilityOperationsSymCode()
    {
        StringBuilder sb = new StringBuilder();

        this.appendCode(sb, this.getScheme(), 1); // Position 1.
        this.appendCode(sb, this.getStandardIdentity(), 1); // Position 2.
        this.appendCode(sb, this.getCategory(), 1); // Position 3.
        this.appendCode(sb, this.getStatus(), 1); // Position 4.
        this.appendCode(sb, this.getFunctionId(), 5); // Positions 5-10.
        this.appendCode(sb, this.getSymbolModifier(), 2); // Positions 11-12.
        this.appendCode(sb, this.getCountryCode(), 2);  // Positions 13-14.
        this.appendCode(sb, this.getOrderOfBattle(), 1);// Position 15.

        return sb.toString();
    }

    protected String composeEmergencyManagementSymCode()
    {
        StringBuilder sb = new StringBuilder();

        this.appendCode(sb, this.getScheme(), 1); // Position 1.
        this.appendCode(sb, this.getStandardIdentity(), 1); // Position 2.
        this.appendCode(sb, this.getCategory(), 1); // Position 3.
        this.appendCode(sb, this.getStatus(), 1); // Position 4.
        this.appendCode(sb, this.getFunctionId(), 5); // Positions 5-10.
        this.appendCode(sb, this.getSymbolModifier(), 2); // Positions 11-12.
        this.appendCode(sb, this.getCountryCode(), 2);  // Positions 13-14.
        this.appendCode(sb, this.getOrderOfBattle(), 1);// Position 15.

        return sb.toString();
    }

    protected void appendCode(StringBuilder sb, String code, int length)
    {
        // Append the code's characters, starting at character 0 and stopping after the number of character positions
        // assigned to the code have been reached or the code's characters are exhausted, whichever comes first. This
        // does nothing if the code is null or empty. If the code contains fewer characters then its assigned length,
        // then only those characters are appended.
        if (code != null && code.length() > 0)
            sb.append(code, 0, code.length() < length ? code.length() : length);

        // Append the "unused" character for each unused character position assigned to the code. We encounter unused
        // positions when the code is null or its length is less than the number of assigned character positions.
        for (int i = (code != null ? code.length() : 0); i < length; i++)
        {
            sb.append(UNUSED_POSITION_CODE);
        }
    }
}
