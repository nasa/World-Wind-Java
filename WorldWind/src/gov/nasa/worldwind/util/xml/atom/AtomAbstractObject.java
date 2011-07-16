/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.xml.atom;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * @author tag
 * @version $Id$
 */
public class AtomAbstractObject extends AbstractXMLEventParser
{
    public AtomAbstractObject(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getBase()
    {
        return (String) this.getField("base");
    }

    public String getLang()
    {
        return (String) this.getField("lang");
    }
}
