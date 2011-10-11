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
import gov.nasa.worldwind.render.*;
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

            // Create an Unknown, Present, Naval Construction Engineer Combat Unit
            MilStd2525SymbolGenerator symGen = new MilStd2525SymbolGenerator();
            String path = "C:\\WorldWind\\release\\trunk\\WorldWind\\src\\gov\\nasa\\worldwind\\symbology\\milstd2525\\icons\\";
            AVListImpl params = new AVListImpl();
            params.setValue(AbstractSymbolGenerator.SOURCE_TYPE, "file");
            params.setValue(AbstractSymbolGenerator.SOURCE_PATH, path);
            BufferedImage img = symGen.createImage("SUAPC----------", params);
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
