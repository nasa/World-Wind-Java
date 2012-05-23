/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package performance;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;

import java.util.ArrayList;

/**
 * @author tag
 * @version $Id$
 */
public class PolygonsEverywhere extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            makeMany();
        }

        protected void makeMany()
        {
            int altitudeMode = WorldWind.ABSOLUTE;

            double minLat = -50, maxLat = 50, minLon = -140, maxLon = -10;
            double delta = 1.5;
            double intervals = 5;
            double dLat = 1 / intervals;
            double dLon = 1 / intervals;

            ArrayList<Position> positions = new ArrayList<Position>();

            RenderableLayer layer = new RenderableLayer();
            layer.setPickEnabled(false);

            int count = 0;
            for (double lat = minLat; lat <= maxLat; lat += delta)
            {
                for (double lon = minLon; lon <= maxLon; lon += delta)
                {
                    positions.clear();
                    double innerLat = lat;
                    double innerLon = lon;

                    for (int i = 0; i <= intervals; i++)
                    {
                        innerLon += dLon;
                        positions.add(Position.fromDegrees(innerLat, innerLon, 5e4));
                    }

                    for (int i = 0; i <= intervals; i++)
                    {
                        innerLat += dLat;
                        positions.add(Position.fromDegrees(innerLat, innerLon, 5e4));
                    }

                    for (int i = 0; i <= intervals; i++)
                    {
                        innerLon -= dLon;
                        positions.add(Position.fromDegrees(innerLat, innerLon, 5e4));
                    }

                    for (int i = 0; i <= intervals; i++)
                    {
                        innerLat -= dLat;
                        positions.add(Position.fromDegrees(innerLat, innerLon, 5e4));
                    }

                    Polygon pgon = new Polygon(positions);
                    pgon.setAltitudeMode(altitudeMode);
                    ShapeAttributes attrs = new BasicShapeAttributes();
                    attrs.setDrawOutline(false);
                    attrs.setInteriorMaterial(Material.RED);
                    attrs.setEnableLighting(true);
                    pgon.setAttributes(attrs);
                    layer.addRenderable(pgon);
                    ++count;
                }
            }
            System.out.printf("%d Polygons, %d positions each, Altitude mode = %s\n", count, positions.size(),
                altitudeMode == WorldWind.RELATIVE_TO_GROUND ? "RELATIVE_TO_GROUND" : "ABSOLUTE");

            insertBeforeCompass(getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Very Many Polygons", AppFrame.class);
    }
}
