/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import java.util.Iterator;

/**
 * Utility methods for working with tactical graphics.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class TacticalGraphicUtil
{
    /**
     * Get the date range from a graphic's modifiers. This method looks at the value of the
     * <code>AVKey.DATE_TIME</code> modifier, and returns the results as a two element array. If the value of the
     * modifier is an <code>Iterable</code>, then this method returns the first two values of the iteration. If the
     * value of the modifier is a single object, this method returns an array containing that object and
     * <code>null</code>.
     *
     * @param graphic Graphic from which to retrieve dates.
     *
     * @return A two element array containing the altitude modifiers. One or both elements may be null.
     */
    public static Object[] getDateRange(TacticalGraphic graphic)
    {
        Object date1 = null;
        Object date2 = null;

        Object o = graphic.getModifier(SymbologyConstants.DATE_TIME_GROUP);
        if (o instanceof Iterable)
        {
            Iterator iterator = ((Iterable) o).iterator();
            if (iterator.hasNext())
            {
                date1 = iterator.next();
            }

            if (iterator.hasNext())
            {
                date2 = iterator.next();
            }
        }
        else
        {
            date1 = o;
        }

        return new Object[] {date1, date2};
    }

    /**
     * Get the altitude range from the graphic's modifiers. This method looks at the value of the
     * <code>AVKey.ALTITUDE</code> modifier, and returns the results as a two element array. If the value of the
     * modifier is an <code>Iterable</code>, then this method returns the first two values of the iteration. If the
     * value of the modifier is a single object, this method returns an array containing that object and
     * <code>null</code>.
     *
     * @param graphic Graphic from which to retrieve dates.
     *
     * @return A two element array containing the altitude modifiers. One or both elements may be null.
     */
    public static Object[] getAltitudeRange(TacticalGraphic graphic)
    {
        Object alt1 = null;
        Object alt2 = null;

        Object o = graphic.getModifier(SymbologyConstants.ALTITUDE_DEPTH);
        if (o instanceof Iterable)
        {
            Iterator iterator = ((Iterable) o).iterator();
            if (iterator.hasNext())
            {
                alt1 = iterator.next();
            }

            if (iterator.hasNext())
            {
                alt2 = iterator.next();
            }
        }
        else
        {
            alt1 = o;
        }

        return new Object[] {alt1, alt2};
    }
}
