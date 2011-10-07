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
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines.PhaseLine;
import gov.nasa.worldwind.util.*;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525GraphicFactory implements TacticalGraphicFactory
{
    /**
     * Each type of graphic in 2525C has a unique function code. This map associates each
     * function code with the class that implements that type of graphic.
     */
    protected Map<String, Class> classMap = new ConcurrentHashMap<String, Class>();

    public MilStd2525GraphicFactory()
    {
        this.classMap.put(PhaseLine.FUNCTION_ID, PhaseLine.class);
    }

    public TacticalGraphic createGraphic(String symbolIdentifier, Iterable<Position> positions, AVList params)
    {
        SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

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

            if (graphic instanceof TacticalShape)
            {
                ((TacticalShape) graphic).setPositions(positions);
            }
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

    protected Class getClassForCode(AVList symbolCode)
    {
        String key = symbolCode.getStringValue(SymbolCode.FUNCTION_ID);
        return this.classMap.get(key);
    }
}
