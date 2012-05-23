/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.xml.atom;

/**
 * Parses the Atom Person element and provides access to it's contents.
 *
 * @author tag
 * @version $Id$
 */
public class AtomPerson extends AtomAbstractObject
{
    public AtomPerson(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getName()
    {
        return (String) this.getField("name");
    }

    public String getUri()
    {
        return (String) this.getField("uri");
    }

    public String getEmail()
    {
        return (String) this.getField("email");
    }
}
