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
import gov.nasa.worldwind.symbology.milstd2525.*;

import java.awt.image.*;
import java.util.ArrayList;

/**
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

            TacticalGraphicFactory factory = new MilStd2525GraphicFactory();

            // Create a Friendly Phase Line
            ArrayList<Position> positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8221, -117.8043, 0));
            positions.add(Position.fromDegrees(34.8235, -117.7196, 0));
            TacticalGraphic graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: friendly");
            graphic.setValue(AVKey.TEXT, "A");
            layer.addRenderable(graphic);

            // Create a Hostile Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7327, -117.8347, 0));
            positions.add(Position.fromDegrees(34.7328, -117.7305, 0));
            graphic = factory.createGraphic("GHGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile");
            graphic.setValue(AVKey.TEXT, "B");
            layer.addRenderable(graphic);

            // Create a Hostile, Anticipated, Phase Line
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8052, -117.8769, 0));
            positions.add(Position.fromDegrees(34.7445, -117.9252, 0));
            graphic = factory.createGraphic("GHGAGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile (anticipated)");
            graphic.setValue(AVKey.TEXT, "C");
            layer.addRenderable(graphic);

            // Create a General Area
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.8193, -117.6454, 0));
            positions.add(Position.fromDegrees(34.8043, -117.5682, 0));
            positions.add(Position.fromDegrees(34.7800, -117.6040, 0));
            positions.add(Position.fromDegrees(34.7819, -117.6687, 0));
            graphic = factory.createGraphic("GHGPGAG----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "General Area");
            graphic.setValue(AVKey.TEXT, "Area");
            layer.addRenderable(graphic);

            // Create an Assembly Area
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.7152, -117.6526, 0));
            positions.add(Position.fromDegrees(34.7219, -117.6089, 0));
            positions.add(Position.fromDegrees(34.6918, -117.5904, 0));
            positions.add(Position.fromDegrees(34.6818, -117.6665, 0));
            graphic = factory.createGraphic("GFGPGAA----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Assembly Area");
            graphic.setValue(AVKey.TEXT, "Area");
            layer.addRenderable(graphic);

            // Create a Deception graphic
            positions = new ArrayList<Position>();
            positions.add(Position.fromDegrees(34.6665, -117.9306, 0));
            positions.add(Position.fromDegrees(34.6476, -117.9601, 0));
            positions.add(Position.fromDegrees(34.6426, -117.9020, 0));
            graphic = factory.createGraphic("GHGPPD-----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Dummy (Deception/Decoy)");
            layer.addRenderable(graphic);

            // Create tactical icon
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever();
            AVListImpl params = new AVListImpl();
            String url = "wwdev.tomgaskins.net";
            params.setValue(SymbolCode.SOURCE_TYPE, "url");
            params.setValue(SymbolCode.SOURCE_SERVER, url);
            params.setValue(SymbolCode.SOURCE_PATH, "/milstd2525/");
            BufferedImage img = symGen.createIcon("SPAPC----------", params);
            Sector s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.8),
                Angle.fromDegrees(-117.9), Angle.fromDegrees(-117.8));
            SurfaceImage symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            this.getWwd().getModel().getLayers().add(layer);
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
