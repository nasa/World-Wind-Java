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
import gov.nasa.worldwind.symbology.*;
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
            TacticalGraphicFactory factory = new MilStd2525GraphicFactory();

            // Create a Friendly Phase Line
            ArrayList<Position> positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8221, -117.8043, 0));
            positions.add(Position.fromDegrees(34.8235, -117.7196, 0));
            TacticalGraphic graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: friendly");
            graphic.setModifier(AVKey.TEXT, "A");
            layer.addRenderable(graphic);

            // Create a Hostile Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7327, -117.8347, 0));
            positions.add(Position.fromDegrees(34.7328, -117.7305, 0));
            graphic = factory.createGraphic("GHGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile");
            graphic.setModifier(AVKey.TEXT, "B");
            layer.addRenderable(graphic);

            // Create a Hostile, Anticipated, Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8052, -117.8769, 0));
            positions.add(Position.fromDegrees(34.7445, -117.9252, 0));
            graphic = factory.createGraphic("GHGAGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile (anticipated)");
            graphic.setModifier(AVKey.TEXT, "C");
            layer.addRenderable(graphic);

            // Create a General Area
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8193, -117.6454, 0));
            positions.add(Position.fromDegrees(34.8043, -117.5682, 0));
            positions.add(Position.fromDegrees(34.7800, -117.6040, 0));
            positions.add(Position.fromDegrees(34.7819, -117.6687, 0));
            graphic = factory.createGraphic("GHGPGAG----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "General Area");
            graphic.setModifier(AVKey.TEXT, "Area");
            layer.addRenderable(graphic);

            // Create an Airfield Zone
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7152, -117.6526, 0));
            positions.add(Position.fromDegrees(34.7219, -117.6089, 0));
            positions.add(Position.fromDegrees(34.6918, -117.5904, 0));
            positions.add(Position.fromDegrees(34.6818, -117.6665, 0));
            graphic = factory.createGraphic("GPGPGAZ----AUSX", positions, null);
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

            // Create a Supporting Attack graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.5610, -117.4541, 0)); // Pt. 1: Tip of the arrow
            positions.add(Position.fromDegrees(34.5614, -117.5852, 0)); // Pt. 2: First path control point
            positions.add(Position.fromDegrees(34.5287, -117.6363, 0));
            positions.add(Position.fromDegrees(34.4726, -117.6363, 0)); // Pt. N - 1: Last path control point
            positions.add(Position.fromDegrees(34.5820, -117.4700, 0)); // Pt. N: Width of the arrow head
            graphic = factory.createGraphic("GFGPOLAGS-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Supporting Attack");
            layer.addRenderable(graphic);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 34.73);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -117.77);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 50000);

        ApplicationTemplate.start("World Wind Symbology", AppFrame.class);
    }
}
