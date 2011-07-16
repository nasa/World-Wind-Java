/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVList;
import org.w3c.dom.Element;

import java.util.EventObject;

/**
 * @author garakl
 * @version $Id$
 */

public class WMSLayerEvent extends EventObject
{
    private Object configSource;
    private Element configElement;
    private AVList params;

    public WMSLayerEvent(Object source, Object configSource, Element configElement, AVList params)
    {
        super(source);
        this.configSource = configSource;
        this.configElement = configElement;
        this.params = params;
    }

    public Object getConfigurationSource()
    {
        return this.configSource;
    }

    public Element getConfigurationDocument()
    {
        return this.configElement;
    }

    public AVList getParameters()
    {
        return this.params;
    }
}
