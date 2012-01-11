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
import gov.nasa.worldwind.symbology.milstd2525.graphics.BasicArea;
import gov.nasa.worldwind.symbology.milstd2525.graphics.combatsupport.CombatSupportArea;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception.Dummy;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines.PhaseLine;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.OffenseArea;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.special.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.lines.LinearTarget;
import gov.nasa.worldwind.symbology.milstd2525.graphics.mobility.MinimumSafeDistanceZones;
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
        this.mapClass(PhaseLine.class,
            PhaseLine.FUNCTION_ID_PHASE,
            PhaseLine.FUNCTION_ID_LIGHT,
            PhaseLine.FUNCTION_ID_FINAL,
            PhaseLine.FUNCTION_ID_ADVANCE,
            PhaseLine.FUNCTION_ID_DEPARTURE,
            PhaseLine.FUNCTION_ID_DEPARTURE_CONTACT,
            PhaseLine.FUNCTION_ID_DEPLOYMENT,
            PhaseLine.FUNCTION_ID_RELEASE,
            PhaseLine.FUNCTION_ID_NO_FIRE);

        this.mapClass(BasicArea.class,
            BasicArea.FUNCTION_ID_GENERAL,
            BasicArea.FUNCTION_ID_ASSEMBLY,
            BasicArea.FUNCTION_ID_ENGAGEMENT,
            BasicArea.FUNCTION_ID_DROP,
            BasicArea.FUNCTION_ID_EXTRACTION,
            BasicArea.FUNCTION_ID_LANDING,
            BasicArea.FUNCTION_ID_PICKUP);

        this.mapClass(AirfieldZone.class, AirfieldZone.FUNCTION_ID);
        this.mapClass(FortifiedArea.class, FortifiedArea.FUNCTION_ID);
        this.mapClass(WeaponsFreeZone.class, WeaponsFreeZone.FUNCTION_ID);

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

        this.mapClass(PullUpPoint.class, PullUpPoint.FUNCTION_ID);

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

        this.mapClass(CombatSupportArea.class,
            CombatSupportArea.FUNCTION_ID_DETAINEE,
            CombatSupportArea.FUNCTION_ID_EPW,
            CombatSupportArea.FUNCTION_ID_FARP,
            CombatSupportArea.FUNCTION_ID_REFUGEE,
            CombatSupportArea.FUNCTION_ID_SUPPORT_BRIGADE,
            CombatSupportArea.FUNCTION_ID_SUPPORT_DIVISION,
            CombatSupportArea.FUNCTION_ID_SUPPORT_REGIMENTAL);

        this.mapClass(SpecialInterestArea.class,
            SpecialInterestArea.FUNCTION_ID_OPERATIONS,
            SpecialInterestArea.FUNCTION_ID_NAMED,
            SpecialInterestArea.FUNCTION_ID_TARGETED);

        this.mapClass(Airhead.class, Airhead.FUNCTION_ID);

        this.mapClass(DirectionOfAttack.class,
            DirectionOfAttack.FUNCTION_ID_MAIN,
            DirectionOfAttack.FUNCTION_ID_SUPPORTING);

        this.mapClass(RectangularFireSupportArea.class,
            RectangularFireSupportArea.FUNCTION_ID_FSA,
            RectangularFireSupportArea.FUNCTION_ID_FFA,
            RectangularFireSupportArea.FUNCTION_ID_RFA,
            RectangularFireSupportArea.FUNCTION_ID_SENSOR_ZONE,
            RectangularFireSupportArea.FUNCTION_ID_DEAD_SPACE_AREA,
            RectangularFireSupportArea.FUNCTION_ID_ZONE_OF_RESPONSIBILITY,
            RectangularFireSupportArea.FUNCTION_ID_TARGET_BUILDUP,
            RectangularFireSupportArea.FUNCTION_ID_TARGET_VALUE,
            RectangularFireSupportArea.FUNCTION_ID_ATI,
            RectangularFireSupportArea.FUNCTION_ID_CFF,
            RectangularFireSupportArea.FUNCTION_ID_CENSOR_ZONE,
            RectangularFireSupportArea.FUNCTION_ID_CF);

        this.mapClass(CircularFireSupportArea.class,
            CircularFireSupportArea.FUNCTION_ID_TARGET,
            CircularFireSupportArea.FUNCTION_ID_FFA,
            CircularFireSupportArea.FUNCTION_ID_RFA,
            CircularFireSupportArea.FUNCTION_ID_FSA,
            CircularFireSupportArea.FUNCTION_ID_ACA,
            CircularFireSupportArea.FUNCTION_ID_SENSOR_ZONE,
            CircularFireSupportArea.FUNCTION_ID_DEAD_SPACE_AREA,
            CircularFireSupportArea.FUNCTION_ID_ZONE_OF_RESPONSIBILITY,
            CircularFireSupportArea.FUNCTION_ID_TARGET_BUILDUP,
            CircularFireSupportArea.FUNCTION_ID_TARGET_VALUE);

        this.mapClass(IrregularFireSupportArea.class,
            IrregularFireSupportArea.FUNCTION_ID_FSA,
            IrregularFireSupportArea.FUNCTION_ID_TARGET,
            IrregularFireSupportArea.FUNCTION_ID_FFA,
            IrregularFireSupportArea.FUNCTION_ID_RFA,
            IrregularFireSupportArea.FUNCTION_ID_ACA,
            IrregularFireSupportArea.FUNCTION_ID_BOMB,
            IrregularFireSupportArea.FUNCTION_ID_TERMINALLY_GUIDED_MUNITIONS_FOOTPRINT,
            IrregularFireSupportArea.FUNCTION_ID_SENSOR_ZONE,
            IrregularFireSupportArea.FUNCTION_ID_DEAD_SPACE_AREA,
            IrregularFireSupportArea.FUNCTION_ID_ZONE_OF_RESPONSIBILITY,
            IrregularFireSupportArea.FUNCTION_ID_TARGET_BUILDUP,
            IrregularFireSupportArea.FUNCTION_ID_TARGET_VALUE,
            IrregularFireSupportArea.FUNCTION_ID_ATI,
            IrregularFireSupportArea.FUNCTION_ID_CFF,
            IrregularFireSupportArea.FUNCTION_ID_CENSOR_ZONE,
            IrregularFireSupportArea.FUNCTION_ID_CF);

        this.mapClass(LinearTarget.class,
            LinearTarget.FUNCTION_ID_MAIN,
            LinearTarget.FUNCTION_ID_SMOKE,
            LinearTarget.FUNCTION_ID_FPF);

        this.mapClass(Dummy.class, Dummy.FUNCTION_ID);
        this.mapClass(SupportingAttack.class, SupportingAttack.FUNCTION_ID);
        this.mapClass(Aviation.class, Aviation.FUNCTION_ID);
        this.mapClass(MainAttack.class, MainAttack.FUNCTION_ID);
        this.mapClass(AttackRotaryWing.class, AttackRotaryWing.FUNCTION_ID);
        this.mapClass(RectangularTarget.class, RectangularTarget.FUNCTION_ID);
        this.mapClass(Smoke.class, Smoke.FUNCTION_ID);
        this.mapClass(CircularRangeFan.class, CircularRangeFan.FUNCTION_ID);
        this.mapClass(SectorRangeFan.class, SectorRangeFan.FUNCTION_ID);
        this.mapClass(CircularPositionArea.class, CircularPositionArea.FUNCTION_ID);
        this.mapClass(RectangularPositionArea.class, RectangularPositionArea.FUNCTION_ID);
        this.mapClass(GroupOfTargets.class, GroupOfTargets.FUNCTION_ID);
        this.mapClass(MinimumSafeDistanceZones.class, MinimumSafeDistanceZones.FUNCTION_ID);
        this.mapClass(AttackByFirePosition.class, AttackByFirePosition.FUNCTION_ID);
        this.mapClass(SupportByFirePosition.class, SupportByFirePosition.FUNCTION_ID);
        this.mapClass(Ambush.class, Ambush.FUNCTION_ID);
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
    public MilStd2525TacticalGraphic createGraphic(String sidc, Iterable<? extends Position> positions,
        AVList modifiers)
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
            if (positions != null)
            {
                graphic.setPositions(positions);
            }
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("Symbology.ExceptionCreatingGraphic", e.getMessage());
            Logging.logger().severe(msg);
            throw new WWRuntimeException(e);
        }

        graphic.setModifier(SymbologyConstants.STANDARD_IDENTITY, symbolCode.getStandardIdentity());
        graphic.setModifier(SymbologyConstants.STATUS, symbolCode.getStatus());
        graphic.setModifier(SymbologyConstants.FUNCTION_ID, symbolCode.getFunctionId());
        graphic.setModifier(SymbologyConstants.COUNTRY_CODE, symbolCode.getCountryCode());
        graphic.setModifier(SymbologyConstants.ECHELON, symbolCode.getEchelon());

        if (modifiers != null)
        {
            this.setModifiers(graphic, modifiers);
        }

        return graphic;
    }

    /**
     * {@inheritDoc}
     *
     * @param sidc MIL-STD-2525 symbol identification code (SIDC).
     */
    public TacticalPoint createPoint(String sidc, Position position, AVList params)
    {
        TacticalGraphic graphic = this.createGraphic(sidc, Arrays.asList(position), params);
        if (graphic instanceof TacticalPoint)
        {
            return (TacticalPoint) graphic;
        }
        else if (graphic != null)
        {
            String className = graphic.getClass().getName();
            String msg = Logging.getMessage("Symbology.CannotCast", className, TacticalPoint.class.getName());
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return null;
    }

    /** {@inheritDoc} */
    public TacticalCircle createCircle(String sidc, Position center, double radius, AVList modifiers)
    {
        TacticalGraphic graphic = this.createPoint(sidc, center, modifiers);
        if (graphic instanceof TacticalCircle)
        {
            TacticalCircle circle = (TacticalCircle) graphic;
            circle.setRadius(radius);
            return circle;
        }
        else if (graphic != null)
        {
            String className = graphic.getClass().getName();
            String msg = Logging.getMessage("Symbology.CannotCast", className, TacticalCircle.class.getName());
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return null;
    }

    /** {@inheritDoc} */
    public TacticalQuad createQuad(String sidc, Iterable<? extends Position> positions, AVList modifiers)
    {
        TacticalGraphic graphic = this.createGraphic(sidc, positions, modifiers);
        if (graphic instanceof TacticalQuad)
        {
            return (TacticalQuad) graphic;
        }
        else if (graphic != null)
        {
            String className = graphic.getClass().getName();
            String msg = Logging.getMessage("Symbology.CannotCast", className, TacticalQuad.class.getName());
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return null;
    }

    /** {@inheritDoc} */
    public TacticalRoute createRoute(String sidc, Iterable<? extends TacticalPoint> controlPoints,
        AVList modifiers)
    {
        TacticalGraphic graphic = this.createGraphic(sidc, null, modifiers);
        if (graphic instanceof TacticalRoute)
        {
            TacticalRoute route = (TacticalRoute) graphic;
            route.setControlPoints(controlPoints);
            return route;
        }
        else if (graphic != null)
        {
            String className = graphic.getClass().getName();
            String msg = Logging.getMessage("Symbology.CannotCast", className, TacticalRoute.class.getName());
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return null;
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
