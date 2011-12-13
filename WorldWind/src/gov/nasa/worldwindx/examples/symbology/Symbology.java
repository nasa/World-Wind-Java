/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples.symbology;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.milstd1477.MilStd1477IconRetriever;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

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

            RenderableLayer symbolLayer = new RenderableLayer();
            symbolLayer.setName("Tactical Symbols");

            RenderableLayer lineLayer = new RenderableLayer();
            lineLayer.setName("Tactical Graphics");

            RenderableLayer controlPointLayer = new RenderableLayer();
            controlPointLayer.setName("Tactical Graphics Control Points");

            // Create tactical symbols and graphics and add them to the layer
            this.createSymbols(symbolLayer);
            this.createLineGraphics(lineLayer, controlPointLayer);

            insertBeforePlacenames(getWwd(), symbolLayer);
            insertBeforePlacenames(getWwd(), lineLayer);
            insertBeforePlacenames(getWwd(), controlPointLayer);

            this.getLayerPanel().update(this.getWwd());

            // Size the World Window to provide enough screen space for the graphics, and center the World Window
            // on the screen.
            Dimension size = new Dimension(1200, 800);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }

        protected void createSymbols(RenderableLayer layer)
        {
            // Display a MIL-STD2525 tactical icon
            //      Warfighting
            String URL = "http://worldwindserver.net/milstd2525/";
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVListImpl params = new AVListImpl();
            BufferedImage img = symGen.createIcon("SUGPIXH---H----", params);
            //BufferedImage img = symGen.createIcon("SKGPUSTST------", params);
            Sector s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.8),
                Angle.fromDegrees(-117.7), Angle.fromDegrees(-117.57));
            SurfaceImage symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            //      Signals Intelligence
            img = symGen.createIcon("IGAPSCO--------");
            s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.6),
                Angle.fromDegrees(-117.7), Angle.fromDegrees(-117.57));
            symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            //      Stability Operations
            img = symGen.createIcon("OHOPYT---------");
            s = new Sector(Angle.fromDegrees(34.8), Angle.fromDegrees(34.7),
                Angle.fromDegrees(-117.55), Angle.fromDegrees(-117.42));
            symbol = new SurfaceImage(img, s);
            layer.addRenderable(symbol);

            //      Emergency Management
            img = symGen.createIcon("ESFPBB----H----");
            s = new Sector(Angle.fromDegrees(34.7), Angle.fromDegrees(34.6),
                Angle.fromDegrees(-117.9), Angle.fromDegrees(-117.77));
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
        }

        protected void createLineGraphics(RenderableLayer layer, RenderableLayer controlPointLayer)
        {
            MilStd2525GraphicFactory factory = new MilStd2525GraphicFactory();
            MilStd2525TacticalGraphic graphic;

            /////////////////////////////////////////////
            // Supporting attack (2.X.2.5.2.1.4.2)
            /////////////////////////////////////////////

            List<Position> positions = Arrays.asList(
                Position.fromDegrees(34.4980, -117.5541, 0),
                Position.fromDegrees(34.4951, -117.4667, 0),
                Position.fromDegrees(34.4733, -117.4303, 0),
                Position.fromDegrees(34.4217, -117.4056, 0),
                Position.fromDegrees(34.4780, -117.53, 0));
            graphic = factory.createGraphic("GFGPOLAGS-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Supporting Attack (2.X.2.5.2.1.4.2)");
            layer.addRenderable(graphic);

            this.addControlPoints(controlPointLayer, positions);

            /////////////////////////////////////////////
            // Phase line (2.X.2.1.2.4)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.5643, -117.4918, 0),
                Position.fromDegrees(34.5297, -117.3825, 0),
                Position.fromDegrees(34.4487, -117.3381, 0));
            graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line (2.X.2.2.2.2)");
            graphic.setText("A");
            layer.addRenderable(graphic);
        }

        /**
         * Add placemarks to a layer to mark the position of tactical graphic control points.
         *
         * @param layer Layer to receive control point placemarks.
         * @param positions Position of control points.
         */
        protected void addControlPoints(RenderableLayer layer, List<Position> positions)
        {
            PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
            attrs.setUsePointAsDefaultImage(true);

            int i = 1;
            for (Position p : positions)
            {
                PointPlacemark placemark = new PointPlacemark(p);
                placemark.setValue(AVKey.DISPLAY_NAME, "Point " + i);
                placemark.setAttributes(attrs);
                placemark.setHighlightAttributes(attrs);
                layer.addRenderable(placemark);
                i += 1;
            }
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 34.59);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -117.59);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 100000);

        ApplicationTemplate.start("World Wind Symbology", AppFrame.class);
    }
}
