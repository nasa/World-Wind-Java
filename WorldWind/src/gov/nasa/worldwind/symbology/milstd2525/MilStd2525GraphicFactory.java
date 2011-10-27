/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception.Dummy;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines.PhaseLine;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.areas.PenetrationBox;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.areas.axis.ground.SupportingAttack;
import gov.nasa.worldwind.util.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

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
        this.classMap.put(PhaseLine.FUNCTION_ID, PhaseLine.class);
        this.classMap.put(GeneralArea.FUNCTION_ID, GeneralArea.class);
        this.classMap.put(AssemblyArea.FUNCTION_ID, AssemblyArea.class);
        this.classMap.put(EngagementArea.FUNCTION_ID, EngagementArea.class);
        this.classMap.put(AirfieldZone.FUNCTION_ID, AirfieldZone.class);
        this.classMap.put(Dummy.FUNCTION_ID, Dummy.class);
        this.classMap.put(PenetrationBox.FUNCTION_ID, PenetrationBox.class);
        this.classMap.put(SupportingAttack.FUNCTION_ID, SupportingAttack.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param sidc MIL-STD-2525 symbol identification code (SIDC).
     */
    public TacticalGraphic createGraphic(String sidc, Position position, AVList params)
    {
        return this.createGraphic(sidc, Arrays.asList(position), params);
    }

    /**
     * {@inheritDoc}
     *
     * @param sidc MIL-STD-2525 symbol identification code (SIDC).
     */
    public TacticalGraphic createGraphic(String sidc, Iterable<Position> positions, AVList params)
    {
        SymbolCode symbolCode = new SymbolCode(sidc);

        Class clazz = this.getClassForCode(symbolCode);
        if (clazz == null)
        {
            return null;
        }

        if (!TacticalGraphic.class.isAssignableFrom(clazz))
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        TacticalGraphic graphic;
        try
        {
            graphic = (TacticalGraphic) clazz.newInstance();
            graphic.setPositions(positions);
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("Symbology.ExceptionCreatingGraphic", e.getMessage());
            Logging.logger().severe(msg);
            throw new WWRuntimeException(e);
        }

        this.setProperties(graphic, symbolCode);

        if (params != null)
        {
            this.setProperties(graphic, params);
        }

        return graphic;
    }

    protected void setProperties(Object o, AVList props)
    {
        for (Map.Entry<String, Object> entry : props.getEntries())
        {
            try
            {
                WWUtil.invokePropertyMethod(o, entry.getKey(), (String) entry.getValue()); // TODO: support non-strings
            }
            catch (NoSuchMethodException e)
            {
                // Property not supported, ignore
            }
            catch (IllegalAccessException e)
            {
                // Property not supported, ignore
            }
            catch (InvocationTargetException e)
            {
                String msg = Logging.getMessage("generic.ExceptionInvokingPropertyMethod", entry.getKey());
                Logging.logger().severe(msg);
                throw new WWRuntimeException(e);
            }
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
        String key = symbolCode.getStringValue(SymbolCode.FUNCTION_ID);
        return this.classMap.get(key);
    }
}
