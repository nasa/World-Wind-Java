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
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.points.AirControlPoint;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception.Dummy;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines.PhaseLine;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.areas.PenetrationBox;
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
        this.classMap.put(PhaseLine.FUNCTION_ID, PhaseLine.class);
        this.classMap.put(GeneralArea.FUNCTION_ID, GeneralArea.class);
        this.classMap.put(AssemblyArea.FUNCTION_ID, AssemblyArea.class);
        this.classMap.put(EngagementArea.FUNCTION_ID, EngagementArea.class);
        this.classMap.put(DropZone.FUNCTION_ID, DropZone.class);
        this.classMap.put(ExtractionZone.FUNCTION_ID, ExtractionZone.class);
        this.classMap.put(LandingZone.FUNCTION_ID, LandingZone.class);
        this.classMap.put(PickupZone.FUNCTION_ID, PickupZone.class);
        this.classMap.put(AirfieldZone.FUNCTION_ID, AirfieldZone.class);
        this.classMap.put(RestrictedOperationsZone.FUNCTION_ID, RestrictedOperationsZone.class);
        this.classMap.put(ShortRangeAirDefenseEngagementZone.FUNCTION_ID, ShortRangeAirDefenseEngagementZone.class);
        this.classMap.put(HighDensityAirspaceControlZone.FUNCTION_ID, HighDensityAirspaceControlZone.class);
        this.classMap.put(MissileEngagementZone.FUNCTION_ID, MissileEngagementZone.class);
        this.classMap.put(LowAltitudeMissileEngagementZone.FUNCTION_ID, LowAltitudeMissileEngagementZone.class);
        this.classMap.put(HighAltitudeMissileEngagementZone.FUNCTION_ID, HighAltitudeMissileEngagementZone.class);
        this.classMap.put(AirControlPoint.FUNCTION_ID, AirControlPoint.class);
        this.classMap.put(Dummy.FUNCTION_ID, Dummy.class);
        this.classMap.put(PenetrationBox.FUNCTION_ID, PenetrationBox.class);
        this.classMap.put(SupportingAttack.FUNCTION_ID, SupportingAttack.class);
        this.classMap.put(Aviation.FUNCTION_ID, Aviation.class);
        this.classMap.put(MainAttack.FUNCTION_ID, MainAttack.class);
        this.classMap.put(AttackRotaryWing.FUNCTION_ID, AttackRotaryWing.class);
        this.classMap.put(AreaTarget.FUNCTION_ID, AreaTarget.class);
        this.classMap.put(CircularTarget.FUNCTION_ID, CircularTarget.class);
        this.classMap.put(RectangularTarget.FUNCTION_ID, RectangularTarget.class);
        this.classMap.put(Bomb.FUNCTION_ID, Bomb.class);
        this.classMap.put(Smoke.FUNCTION_ID, Smoke.class);
        this.classMap.put(TerminallyGuidedMunitionFootprint.FUNCTION_ID, TerminallyGuidedMunitionFootprint.class);
        this.classMap.put(IrregularAirspaceCoordinationArea.FUNCTION_ID, IrregularAirspaceCoordinationArea.class);
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
            String msg = Logging.getMessage("Symbology.StringIsNull");
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
