/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception.Dummy;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines.PhaseLine;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.OffenseArea;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.axis.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.axis.ground.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.AreaTarget;
import gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.command.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target.*;
import gov.nasa.worldwind.util.Logging;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Graphic factory to create tactical graphics for the MIL-STD-2525 symbol set.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525GraphicFactory implements TacticalGraphicFactory
{
    /** Map to associate MIL-STD-2525C function codes with implementation classes. */
    protected Map<String, Class> classMap = new ConcurrentHashMap<String, Class>();

    /** Create a new factory. */
    public MilStd2525GraphicFactory()
    {
        this.populateClassMap();
    }

    /** Populate the map that maps function IDs to implementation classes. */
    protected void populateClassMap()
    {
        this.mapClass(PhaseLine.class, PhaseLine.FUNCTION_ID);

        this.mapClass(BasicArea.class,
            BasicArea.FUNCTION_ID_GENERAL,
            BasicArea.FUNCTION_ID_ASSEMBLY,
            BasicArea.FUNCTION_ID_ENGAGEMENT,
            BasicArea.FUNCTION_ID_DROP,
            BasicArea.FUNCTION_ID_EXTRACTION,
            BasicArea.FUNCTION_ID_LANDING,
            BasicArea.FUNCTION_ID_PICKUP);

        this.mapClass(AirfieldZone.class, AirfieldZone.FUNCTION_ID);

        this.mapClass(AviationZone.class,
            AviationZone.FUNCTION_ID_RESTRICTED_OPERATIONS_ZONE,
            AviationZone.FUNCTION_ID_SHORT_RANGE_AIR_DEFENSE,
            AviationZone.FUNCTION_ID_HI_DENSITY_AIRSPACE,
            AviationZone.FUNCTION_ID_MISSILE_ZONE,
            AviationZone.FUNCTION_ID_LO_ALT_MISSILE_ZONE,
            AviationZone.FUNCTION_ID_HI_ALT_MISSILE_ZONE);

        this.mapClass(RoutePoint.class,
            RoutePoint.FUNCTION_ID_AIR_CONTROL,
            RoutePoint.FUNCTION_ID_COMMUNICATIONS_CHECKPOINT);

        this.mapClass(Route.class,
            Route.FUNCTION_ID_AIR_CORRIDOR,
            Route.FUNCTION_ID_MINIMUM_RISK,
            Route.FUNCTION_ID_LOW_LEVEL_TRANSIT,
            Route.FUNCTION_ID_STANDARD_FLIGHT,
            Route.FUNCTION_ID_UNMANNED_AIRCRAFT);

        this.mapClass(OffenseArea.class,
            OffenseArea.FUNCTION_ID_ASSAULT_POSITION,
            OffenseArea.FUNCTION_ID_ATTACK_POSITION,
            OffenseArea.FUNCTION_ID_OBJECTIVE,
            OffenseArea.FUNCTION_ID_PENETRATION_BOX);

        this.mapClass(Dummy.class, Dummy.FUNCTION_ID);
        this.mapClass(SupportingAttack.class, SupportingAttack.FUNCTION_ID);
        this.mapClass(Aviation.class, Aviation.FUNCTION_ID);
        this.mapClass(MainAttack.class, MainAttack.FUNCTION_ID);
        this.mapClass(AttackRotaryWing.class, AttackRotaryWing.FUNCTION_ID);
        this.mapClass(AreaTarget.class, AreaTarget.FUNCTION_ID);
        this.mapClass(CircularTarget.class, CircularTarget.FUNCTION_ID);
        this.mapClass(RectangularTarget.class, RectangularTarget.FUNCTION_ID);
        this.mapClass(Bomb.class, Bomb.FUNCTION_ID);
        this.mapClass(Smoke.class, Smoke.FUNCTION_ID);
        this.mapClass(TerminallyGuidedMunitionFootprint.class, TerminallyGuidedMunitionFootprint.FUNCTION_ID);
        this.mapClass(IrregularAirspaceCoordinationArea.class, IrregularAirspaceCoordinationArea.FUNCTION_ID);
    }

    /**
     * Associate an implementation class with one or more function IDs.
     *
     * @param clazz       Class that implements one or more tactical graphics.
     * @param functionIds Function IDs of the graphics implemented by {@code clazz}.
     */
    protected void mapClass(Class clazz, String... functionIds)
    {
        for (String functionId : functionIds)
        {
            this.classMap.put(functionId, clazz);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param sidc MIL-STD-2525 symbol identification code (SIDC).
     */
    public MilStd2525TacticalGraphic createGraphic(String sidc, Position position, AVList params)
    {
        return this.createGraphic(sidc, Arrays.asList(position), params);
    }

    /**
     * {@inheritDoc}
     *
     * @param sidc MIL-STD-2525 symbol identification code (SIDC).
     */
    public MilStd2525TacticalGraphic createGraphic(String sidc, Iterable<Position> positions, AVList modifiers)
    {
        SymbolCode symbolCode = new SymbolCode(sidc);

        Class clazz = this.getClassForCode(symbolCode);
        if (clazz == null)
        {
            return null;
        }

        if (!MilStd2525TacticalGraphic.class.isAssignableFrom(clazz))
        {
            String msg = Logging.getMessage("Symbology.CannotCast", clazz,
                MilStd2525TacticalGraphic.class);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        MilStd2525TacticalGraphic graphic;
        try
        {
            graphic = (MilStd2525TacticalGraphic) clazz.newInstance();
            graphic.setPositions(positions);
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("Symbology.ExceptionCreatingGraphic", e.getMessage());
            Logging.logger().severe(msg);
            throw new WWRuntimeException(e);
        }

        this.setModifiers(graphic, symbolCode);

        if (modifiers != null)
        {
            this.setModifiers(graphic, modifiers);
        }

        return graphic;
    }

    protected void setModifiers(TacticalGraphic graphic, AVList props)
    {
        for (Map.Entry<String, Object> entry : props.getEntries())
        {
            graphic.setModifier(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Get the implementation class that implements a particular graphic.
     *
     * @param symbolCode Parsed SIDC that identifies the graphic.
     *
     * @return The implementation class for the specified SIDC, or {@code null} if no implementation class is found.
     */
    protected Class getClassForCode(SymbolCode symbolCode)
    {
        String key = symbolCode.getFunctionId();
        return key != null ? this.classMap.get(key) : null;
    }
}
