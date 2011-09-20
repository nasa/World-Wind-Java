/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class AbstractElevationModel extends WWObjectImpl implements ElevationModel
{
    protected double[] minAndMaxElevations = new double[2];

    public AbstractElevationModel()
    {
    }

    protected AbstractElevationModel(AVList params)
    {
        if (params == null)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        this.validateParams(params, sb);
        if (sb.length() > 0)
        {
            String msg = Logging.getMessage("generic.ParamsAreInvalid", sb.toString());
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithParams(params);
    }

    protected AbstractElevationModel(Element element)
    {
        if (element == null)
        {
            String msg = Logging.getMessage("nullValue.ElementIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        this.validateConfigDoc(element, sb);
        if (sb.length() > 0)
        {
            String msg = Logging.getMessage("generic.ConfigDocIsInvalid", sb.toString());
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithConfigDoc(element);
    }

    protected void initWithParams(AVList params)
    {
        Object o = params.getValue(AVKey.DISPLAY_NAME);
        if (o != null && o instanceof String && !WWUtil.isEmpty(o))
            this.setName((String) params.getValue(AVKey.DISPLAY_NAME));

        o = params.getValue(AVKey.ELEVATION_MIN);
        if (o != null && o instanceof Number)
            this.minAndMaxElevations[0] = ((Number) o).doubleValue();

        o = params.getValue(AVKey.ELEVATION_MAX);
        if (o != null && o instanceof Number)
            this.minAndMaxElevations[1] = ((Number) o).doubleValue();

        this.init();
    }

    protected void initWithConfigDoc(Element element)
    {
        XPath xpath = WWXML.makeXPath();

        String s = WWXML.getText(element, "DisplayName", xpath);
        if (s != null && !WWUtil.isEmpty(s))
            this.setName(s);

        Double d = WWXML.getDouble(element, "ExtremeElevations/@min", xpath);
        if (d != null)
            this.minAndMaxElevations[0] = d;

        d = WWXML.getDouble(element, "ExtremeElevations/@max", xpath);
        if (d != null)
            this.minAndMaxElevations[1] = d;

        this.init();
    }

    protected void init()
    {
    }

    /**
     * Determines whether the constructor arguments are valid.
     *
     * @param params the list of parameters to validate.
     * @param sb     the StringBuilder to append a description of why it's invalid.
     */
    protected void validateParams(AVList params, StringBuilder sb)
    {
    }

    /**
     * Determines whether the constructor configuration document is valid.
     *
     * @param element the configuration document element.
     * @param sb      the StringBuilder to append a description of why it's invalid.
     */
    protected void validateConfigDoc(Element element, StringBuilder sb)
    {
    }

    /** {@inheritDoc} */
    public String getName()
    {
        Object n = this.getValue(AVKey.DISPLAY_NAME);
        return n != null ? n.toString() : null;
    }

    /** {@inheritDoc} */
    public void setName(String name)
    {
        this.setValue(AVKey.DISPLAY_NAME, name);
    }

    /** {@inheritDoc} */
    public double getMinElevation()
    {
        return this.minAndMaxElevations[0];
    }

    /** {@inheritDoc} */
    public double getMaxElevation()
    {
        return this.minAndMaxElevations[1];
    }

    /** {@inheritDoc} */
    public double[] getMinAndMaxElevations(Angle latitude, Angle longitude)
    {
        return this.minAndMaxElevations;
    }

    /** {@inheritDoc} */
    public double[] getMinAndMaxElevations(Sector sector)
    {
        return this.minAndMaxElevations;
    }

    @Override
    public String toString()
    {
        String n = this.getName();
        return n != null ? n : super.toString();
    }
}
