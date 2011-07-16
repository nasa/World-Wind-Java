/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util.webview;

import java.net.URL;

/**
 * @author pabercrombie
 * @version $Id$
 */
public interface WebResourceResolver
{
    URL resolve(String address);
}
