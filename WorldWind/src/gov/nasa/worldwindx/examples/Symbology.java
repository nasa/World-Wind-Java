/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.symbology.milstd1477.MilStd1477IconRetriever;
import gov.nasa.worldwind.symbology.milstd2525.*;

import java.awt.image.*;
import java.util.ArrayList;

/**
 * Demonstrates how to create and render symbols from the MIL-STD-2525 symbol set. See the <a title="Symbology Usage
 * Guide" href="http://goworldwind.org/developers-guide/symbology/" target="_blank">Usage Guide</a> for more information
 * on symbology support in World Wind.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class Symbology extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            RenderableLayer layer = new RenderableLayer();

            // Create tactical graphics and add them to the layer
            this.createGraphics(layer);

            // Display a MIL-STD2525 tactical icon
            String URL = "http://worldwindserver.net/milstd2525/";
            //String URL = "file:///C:/WorldWind/release/trunk/WorldWind/src/gov/nasa/worldwind/symbology/milstd2525/icons";
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVListImpl params = new AVListImpl();
            BufferedImage img = symGen.createIcon("SWGPIR----H----", params);
            //BufferedImage img = symGen.createIcon("SKGPUSTST------", params);
            Sector s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.8),
                Angle.fromDegrees(-117.7), Angle.fromDegrees(-117.57));
            SurfaceImage symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            img = symGen.createIcon("SUGPIRM---H----");
            s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.6),
                Angle.fromDegrees(-117.7), Angle.fromDegrees(-117.57));
            symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            // Display a MIL-STD1477 icon
            URL = "http://worldwindserver.net/milstd1477/";
            MilStd1477IconRetriever symGen1477 = new MilStd1477IconRetriever(URL);
            params = new AVListImpl();
            // use temporary test values: Storage_Location, Tree, Building, Church, Tower, Mountain, Bridge
            img = symGen1477.createIcon("Storage_Location", params);
            s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.8),
                Angle.fromDegrees(-117.9), Angle.fromDegrees(-117.77));
            symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            this.getWwd().getModel().getLayers().add(layer);
        }

        protected void createGraphics(RenderableLayer layer)
        {
            MilStd2525GraphicFactory factory = new MilStd2525GraphicFactory();
            MilStd2525TacticalGraphic graphic;

            // Create a Friendly Phase Line
            ArrayList<Position> positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8221, -117.8043, 0));
            positions.add(Position.fromDegrees(34.8235, -117.7196, 0));
            graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: friendly");
            graphic.setText("A");
            layer.addRenderable(graphic);

            // Create a Hostile Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7327, -117.8347, 0));
            positions.add(Position.fromDegrees(34.7328, -117.7305, 0));
            graphic = factory.createGraphic("GHGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile");
            graphic.setText("B");
            layer.addRenderable(graphic);

            // Create a Hostile, Anticipated, Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8052, -117.8769, 0));
            positions.add(Position.fromDegrees(34.7445, -117.9252, 0));
            graphic = factory.createGraphic("GHGAGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile (anticipated)");
            graphic.setText("C");
            layer.addRenderable(graphic);

            // Create a General Area
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8193, -117.6454, 0));
            positions.add(Position.fromDegrees(34.8043, -117.5682, 0));
            positions.add(Position.fromDegrees(34.7800, -117.6040, 0));
            positions.add(Position.fromDegrees(34.7819, -117.6687, 0));
            graphic = factory.createGraphic("GHGPGAG----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "General Area");
            graphic.setText("Area");
            layer.addRenderable(graphic);

            // Create an Airfield Zone
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7152, -117.6526, 0));
            positions.add(Position.fromDegrees(34.7219, -117.6089, 0));
            positions.add(Position.fromDegrees(34.6918, -117.5904, 0));
            positions.add(Position.fromDegrees(34.6818, -117.6665, 0));
            graphic = factory.createGraphic("GFGPGAZ----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Airfield Zone");
            layer.addRenderable(graphic);

            // Create a Deception graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.6665, -117.9306, 0));
            positions.add(Position.fromDegrees(34.6476, -117.9601, 0));
            positions.add(Position.fromDegrees(34.6426, -117.9020, 0));
            graphic = factory.createGraphic("GHGPPD-----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Dummy (Deception/Decoy)");
            layer.addRenderable(graphic);

            // Create a Main Attack graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.4643, -117.7323, 0)); // Pt. 1: Tip of the arrow
            positions.add(Position.fromDegrees(34.4962, -117.7808, 0)); // Pt. 2: First path control point
            positions.add(Position.fromDegrees(34.4934, -117.8444, 0)); // Pt. N - 1: Last path control point
            positions.add(Position.fromDegrees(34.4602, -117.8570, 0));
            positions.add(Position.fromDegrees(34.4844, -117.7303, 0)); // Pt. N: Width of the arrow head
            graphic = factory.createGraphic("GFGPOLAGM-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Main Attack");
            layer.addRenderable(graphic);

            // Create an Rotary Wing graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.5610, -117.4541, 0));
            positions.add(Position.fromDegrees(34.5614, -117.5852, 0));
            positions.add(Position.fromDegrees(34.5287, -117.6363, 0));
            positions.add(Position.fromDegrees(34.4726, -117.6363, 0));
            positions.add(Position.fromDegrees(34.5820, -117.4700, 0));
            graphic = factory.createGraphic("GFGPOLAR------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Attack, Rotary Wing");
            layer.addRenderable(graphic);

            // Create an Aviation offensive graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.5437, -117.8007, 0));
            positions.add(Position.fromDegrees(34.5535, -117.9256, 0));
            positions.add(Position.fromDegrees(34.6051, -117.9707, 0));
            positions.add(Position.fromDegrees(34.5643, -117.8219, 0));
            graphic = factory.createGraphic("GFGPOLAV------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Aviation");
            layer.addRenderable(graphic);

            // Create a Supporting Attack graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.4980, -117.5541, 0));
            positions.add(Position.fromDegrees(34.4951, -117.4667, 0));
            positions.add(Position.fromDegrees(34.4733, -117.4303, 0));
            positions.add(Position.fromDegrees(34.4217, -117.4056, 0));
            positions.add(Position.fromDegrees(34.4780, -117.53, 0));
            graphic = factory.createGraphic("GFGPOLAGS-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Supporting Attack");
            layer.addRenderable(graphic);

            // Create a Circular Target graphic
            Position position = (Position.fromDegrees(34.7202, -117.4278, 0));
            graphic = factory.createGraphic("GHFPATC-------X", position, null);
            graphic.setText("AG9999");
            graphic.setModifier(AVKey.DISTANCE, 5000.0); // Radius of the circle
            graphic.setValue(AVKey.DISPLAY_NAME, "Circular Target");
            layer.addRenderable(graphic);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 34.64);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -117.73);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 80000);

        ApplicationTemplate.start("World Wind Symbology", AppFrame.class);
    }
}
