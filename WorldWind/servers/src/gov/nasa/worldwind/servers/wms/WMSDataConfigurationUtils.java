/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import org.w3c.dom.Element;

/**
 * @author dcollins
 * @version $Id$
 */
public class WMSDataConfigurationUtils
{
    public static boolean isWorldWindLayerConfig(Element configElement, AVList params)
    {
        if (configElement == null)
        {
            String message = Logging.getMessage("nullValue.ElementIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (params == null)
        {
            String message = Logging.getMessage("nullValue.ParametersIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String type = DataConfigurationUtils.getDataConfigType(configElement);
        return type != null && type.equalsIgnoreCase("Layer");
    }

    public static boolean isWorldWindElevationModelConfig(Element configElement, AVList params)
    {
        if (configElement == null)
        {
            String message = Logging.getMessage("nullValue.ElementIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (params == null)
        {
            String message = Logging.getMessage("nullValue.ParametersIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String type = DataConfigurationUtils.getDataConfigType(configElement);
        return type != null && (type.equalsIgnoreCase("Layer") || type.equalsIgnoreCase("ElevationModel"));
    }

    public static boolean isWorldWindTiledConfig(Element configElement, AVList params)
    {
        if (configElement == null)
        {
            String message = Logging.getMessage("nullValue.ElementIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (params == null)
        {
            String message = Logging.getMessage("nullValue.ParametersIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return isWorldWindLayerConfig(configElement, params) || isWorldWindElevationModelConfig(configElement, params);
    }
}
