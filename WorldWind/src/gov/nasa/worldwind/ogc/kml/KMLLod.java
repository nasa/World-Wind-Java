/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Lod</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLLod extends KMLAbstractObject
{
    /**
     * Flag to indicate that the minLod has been fetched from the hashmap.
     */
    protected boolean minLodFetched = false;
    protected Double minLodPixels = null;

    /**
     * Flag to indicate that the maxLod has been fetched from the hashmap.
     */
    protected boolean maxLodFetched = false;
    protected Double maxLodPixels = null;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLLod(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Double getMinLodPixels()
    {
        if (!this.minLodFetched)
        {
            this.minLodFetched = true;
            this.minLodPixels = (Double) this.getField("minLodPixels");
        }

        return this.minLodPixels;
    }


    public Double getMaxLodPixels()
    {
        if (!this.maxLodFetched)
        {
            this.maxLodFetched = true;
            this.maxLodPixels = (Double) this.getField("maxLodPixels");
        }

        return this.maxLodPixels;
    }

    public Double getMinFadeExtent()
    {
        return (Double) this.getField("minFadeExtent");
    }

    public Double getMaxFadeExtent()
    {
        return (Double) this.getField("maxFadeExtent");
    }
}
