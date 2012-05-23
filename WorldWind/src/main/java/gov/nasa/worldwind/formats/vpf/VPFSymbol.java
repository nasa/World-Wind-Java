/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.vpf;

/**
 * @author dcollins
 * @version $Id$
 */
public class VPFSymbol
{
    protected VPFFeature feature;
    protected VPFSymbolAttributes attributes;
    protected Object mapObject;

    public VPFSymbol(VPFFeature feature, VPFSymbolAttributes attributes, Object mapObject)
    {
        this.feature = feature;
        this.attributes = attributes;
        this.mapObject = mapObject;
    }

    public VPFFeature getFeature()
    {
        return this.feature;
    }

    public VPFSymbolAttributes getAttributes()
    {
        return this.attributes;
    }

    public Object getMapObject()
    {
        return this.mapObject;
    }
}
