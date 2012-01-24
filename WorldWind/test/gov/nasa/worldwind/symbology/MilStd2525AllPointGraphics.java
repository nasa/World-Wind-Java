/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525PointGraphic;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.util.List;

/**
 * Display all MIL-STD-2525 point graphics in a grid so that a human operator can confirm that they are rendered
 * correctly.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525AllPointGraphics extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            RenderableLayer layer = new RenderableLayer();

            this.displayPoints(layer);

            this.getWwd().getModel().getLayers().add(layer);

            // Size the World Window to provide enough screen space for the graphics, and center the World Window
            // on the screen.
            Dimension size = new Dimension(1800, 1000);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }

        protected void displayPoints(RenderableLayer layer)
        {
            List<String> allGraphics = MilStd2525PointGraphic.getSupportedGraphics();

            int numGraphics = allGraphics.size();
            int cols = (int) Math.sqrt(numGraphics);

            double startLon = -118.5439;

            double latitude = 43.3464;
            double longitude = startLon;
            double delta = 0.02;

            for (int i = 0; i < numGraphics; i++)
            {
                Position pos = Position.fromDegrees(latitude, longitude, 0);

                StringBuffer sidc = new StringBuffer(allGraphics.get(i));

                sidc.setCharAt(1, 'H'); // Standard identify: Friend
                sidc.setCharAt(3, 'P'); // Status: Present

                TacticalPoint graphic = new MilStd2525PointGraphic(sidc.toString());
                graphic.setPosition(pos);

                graphic.setText("T");
                graphic.setModifier(SymbologyConstants.ADDITIONAL_INFORMATION, "H");
                graphic.setModifier(SymbologyConstants.ALTITUDE_DEPTH, "X");
                graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, "W");//Arrays.asList("W", "W1"));
                graphic.setModifier(SymbologyConstants.QUANTITY, "C");
                graphic.setModifier(SymbologyConstants.TYPE, "V");

                layer.addRenderable(graphic);

                if ((i + 1) % cols == 0)
                {
                    latitude -= delta;
                    longitude = startLon;
                }
                else
                {
                    longitude += delta;
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 43.3046);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -118.4961);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20000);

        ApplicationTemplate.start("World Wind MIL-STD2525 Tactical Point Graphics", AppFrame.class);
    }
}
