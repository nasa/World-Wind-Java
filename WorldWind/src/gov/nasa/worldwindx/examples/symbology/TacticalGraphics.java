/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples.symbology;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525GraphicFactory;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Demonstrates how to create and display World Wind tactical graphics. See the <a title="Symbology Usage Guide"
 * href="http://goworldwind.org/developers-guide/symbology/" target="_blank">Symbology Usage Guide</a> for more
 * information on symbology support in World Wind.
 * <p/>
 * See the {@link TacticalSymbols} for a detailed example of using World Wind tactical symbols in an application.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class TacticalGraphics extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            RenderableLayer pointLayer = new RenderableLayer();
            pointLayer.setName("Tactical Graphics (Points)");

            RenderableLayer lineLayer = new RenderableLayer();
            lineLayer.setName("Tactical Graphics (Lines)");

            RenderableLayer areaLayer = new RenderableLayer();
            areaLayer.setName("Tactical Graphics (Areas)");

            // Create tactical symbols and graphics and add them to the layer
            this.createPointGraphics(pointLayer);
            this.createLineGraphics(lineLayer);
            this.createAreaGraphics(areaLayer);

            insertBeforePlacenames(getWwd(), pointLayer);
            insertBeforePlacenames(getWwd(), lineLayer);
            insertBeforePlacenames(getWwd(), areaLayer);

            this.getLayerPanel().update(this.getWwd());

            // Add a BasicDragger so that graphics can be moved by clicking and dragging.
            this.getWwd().addSelectListener(new BasicDragger(this.getWwd()));

            // Size the World Window to provide enough screen space for the graphics, and center the World Window
            // on the screen.
            Dimension size = new Dimension(1800, 1000);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }

        protected void createPointGraphics(RenderableLayer layer)
        {
            TacticalGraphicFactory factory = new MilStd2525GraphicFactory();
            TacticalGraphic graphic;

            /////////////////////////////////////////////
            // Underwater Datum (2.X.2.1.1.1.1.1)
            /////////////////////////////////////////////

            graphic = factory.createPoint("GFGPGPUUD-----X", Position.fromDegrees(35.2144, -117.8824, 0), null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Underwater Datum (2.X.2.1.1.1.1.1)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Search Center (2.X.2.1.1.1.3.3)
            /////////////////////////////////////////////

            graphic = factory.createPoint("GFGPGPUSC-----X", Position.fromDegrees(35.2144, -117.9824, 0), null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Search Center (2.X.2.1.1.1.3.3)");
            layer.addRenderable(graphic);
        }

        protected void createLineGraphics(RenderableLayer layer)
        {
            TacticalGraphicFactory factory = new MilStd2525GraphicFactory();
            TacticalGraphic graphic;

            /////////////////////////////////////////////
            // Phase line (2.X.2.1.2.4)
            /////////////////////////////////////////////

            // Create a Friendly Phase Line
            List<Position> positions = Arrays.asList(
                Position.fromDegrees(34.9349, -117.6303, 0),
                Position.fromDegrees(34.9843, -117.5885, 0),
                Position.fromDegrees(34.9961, -117.4891, 0));
            graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: friendly (2.X.2.2.2.2)");
            graphic.setText("A");
            layer.addRenderable(graphic);

            // Create a Hostile Phase Line
            positions = Arrays.asList(
                Position.fromDegrees(34.9190, -117.5919, 0),
                Position.fromDegrees(34.9589, -117.5618, 0),
                Position.fromDegrees(34.9701, -117.4798, 0));
            graphic = factory.createGraphic("GHGPGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile (2.X.2.2.2.2)");
            graphic.setText("B");
            layer.addRenderable(graphic);

            // Create a Hostile, Anticipated, Phase Line
            positions = Arrays.asList(
                Position.fromDegrees(34.8910, -117.5726, 0),
                Position.fromDegrees(34.9367, -117.5354, 0),
                Position.fromDegrees(34.9480, -117.4741, 0));
            graphic = factory.createGraphic("GHGAGLP----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: hostile, anticipated (2.X.2.2.2.2)");
            graphic.setText("C");
            layer.addRenderable(graphic);

            // Create a line with a custom color and font
            positions = Arrays.asList(
                Position.fromDegrees(34.8703, -117.5525, 0),
                Position.fromDegrees(34.9158, -117.5153, 0),
                Position.fromDegrees(34.9238, -117.4600, 0));
            graphic = factory.createGraphic("GFGPGLP----AUSX", positions, null);
            graphic.setText("D");
            graphic.setValue(AVKey.DISPLAY_NAME, "Phase line: custom color and font (2.X.2.2.2.2)");

            // Create a custom attributes bundle. Any fields set in this bundle will override the default attributes.
            TacticalGraphicAttributes attrs = new BasicTacticalGraphicAttributes();
            attrs.setOutlineMaterial(Material.GRAY);
            attrs.setTextModifierFont(Font.decode("Arial-12"));

            // Apply the attributes to the graphic
            graphic.setAttributes(attrs);
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Main Attack (2.X.2.5.2.1.4.1)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.6643, -117.7323, 0), // Pt. 1: Tip of the arrow
                Position.fromDegrees(34.6962, -117.7808, 0), // Pt. 2: First path control point
                Position.fromDegrees(34.6934, -117.8444, 0),
                Position.fromDegrees(34.6602, -117.8570, 0), // Pt. N - 1: Last path control point
                Position.fromDegrees(34.6844, -117.7303, 0)); // Pt. N: Width of the arrow head
            graphic = factory.createGraphic("GFGPOLAGM-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Main Attack (2.X.2.5.2.1.4.1)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Attack, rotary wing (2.X.2.5.2.1.3)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.7610, -117.4541, 0),
                Position.fromDegrees(34.7614, -117.5852, 0),
                Position.fromDegrees(34.7287, -117.6363, 0),
                Position.fromDegrees(34.6726, -117.6363, 0),
                Position.fromDegrees(34.7820, -117.4700, 0));
            graphic = factory.createGraphic("GFGPOLAR------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Attack, Rotary Wing (2.X.2.5.2.1.3)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Aviation axis of advance (2.X.2.5.2.1.1)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.7437, -117.8007, 0),
                Position.fromDegrees(34.7535, -117.9256, 0),
                Position.fromDegrees(34.8051, -117.9707, 0),
                Position.fromDegrees(34.7643, -117.8219, 0));
            graphic = factory.createGraphic("GFGPOLAV------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Aviation (2.X.2.5.2.1.1)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Supporting attack (2.X.2.5.2.1.4.2)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.6980, -117.5541, 0),
                Position.fromDegrees(34.6951, -117.4667, 0),
                Position.fromDegrees(34.6733, -117.4303, 0),
                Position.fromDegrees(34.6217, -117.4056, 0),
                Position.fromDegrees(34.6780, -117.53, 0));
            graphic = factory.createGraphic("GFGPOLAGS-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Supporting Attack (2.X.2.5.2.1.4.2)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Minimum risk route (2.X.2.2.2.2)
            /////////////////////////////////////////////

            // Create a Minimum Risk Route graphic
            // First create some Air Control Points to place along the route
            List<TacticalPoint> controlPoints = new ArrayList<TacticalPoint>();

            // Create an Air Control Point
            TacticalPoint point = factory.createPoint("GFFPAPP-------X", Position.fromDegrees(34.8802, -117.9773, 0),
                null);
            point.setText("1");
            controlPoints.add(point);

            // And another Air Control Point
            point = factory.createPoint("GFFPAPP-------X", Position.fromDegrees(35.0947, -117.9578, 0), null);
            point.setText("2");
            controlPoints.add(point);

            // And a Communication Checkpoint
            point = factory.createPoint("GFFPAPC-------X", Position.fromDegrees(35.1739, -117.6957, 0), null);
            point.setText("3");
            controlPoints.add(point);

            // Now create the route from the control points
            TacticalRoute route = factory.createRoute("GFGPALM-------X", controlPoints, null);
            route.setValue(AVKey.DISPLAY_NAME, "Minimum Risk Route (2.X.2.2.2.2)");
            route.setText("KNIGHT");
            route.setModifier(SymbologyConstants.DISTANCE, 2000.0);
            route.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("270600Z", "271845Z"));
            route.setModifier(SymbologyConstants.ALTITUDE_DEPTH, Arrays.asList("50 FT AGL", "200 FT AGL"));

            // And add the route to the layer. Note that we do not need to add the individual control points
            // to the layer because the route will take care of drawing them.
            layer.addRenderable(route);

            ///////////////////////////////////////////////////
            // Direction of Main Attack (2.X.2.5.2.2.2.1)
            ///////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.0459, -117.5633, 0),
                Position.fromDegrees(35.0459, -117.3795, 0));
            graphic = factory.createGraphic("GFGPOLKGM-----X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Direction of Main Attack (2.X.2.5.2.2.2.1)");
            layer.addRenderable(graphic);

            ///////////////////////////////////////////////////
            // Final Protective Fire (2.X.4.2.1.2)
            ///////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.8917, -117.8344, 0),
                Position.fromDegrees(34.8283, -117.6794, 0));
            graphic = factory.createGraphic("GFGPLTF-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Final Protective Fire (2.X.4.2.1.2)");
            graphic.setModifier(SymbologyConstants.UNIQUE_DESIGNATION, Arrays.asList("AG1201", "2-22 INF MORT"));
            layer.addRenderable(graphic);

            ///////////////////////////////////////////////////
            // Ambush (2.X.2.6.1.1)
            ///////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.5556, -117.4123, 0),
                Position.fromDegrees(34.5210, -117.4786, 0),
                Position.fromDegrees(34.5942, -117.4766, 0));
            graphic = factory.createGraphic("GFGPSLA-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Ambush (2.X.2.6.1.1)");
            layer.addRenderable(graphic);
        }

        protected void createAreaGraphics(RenderableLayer layer)
        {
            TacticalGraphicFactory factory = new MilStd2525GraphicFactory();
            TacticalGraphic graphic;

            /////////////////////////////////////////////
            // Assembly area (2.X.2.1.3.2)
            /////////////////////////////////////////////

            List<Position> positions = Arrays.asList(
                Position.fromDegrees(34.9130, -117.1897, 0),
                Position.fromDegrees(34.9789, -117.1368, 0),
                Position.fromDegrees(34.9706, -116.9900, 0),
                Position.fromDegrees(34.9188, -116.9906, 0));
            graphic = factory.createGraphic("GHGPGAA----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Assembly Area (2.X.2.1.3.2)");
            graphic.setText("Atlanta");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Fortified area (2.X.2.1.3.4)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.7985, -117.1938, 0),
                Position.fromDegrees(34.8358, -117.1282, 0),
                Position.fromDegrees(34.8456, -117.0773, 0),
                Position.fromDegrees(34.8159, -116.9723, 0),
                Position.fromDegrees(34.7836, -117.1010, 0),
                Position.fromDegrees(34.7985, -117.1938, 0));
            graphic = factory.createGraphic("GHGPGAF----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Fortified Area (2.X.2.1.3.4)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Airfield zone (2.X.2.1.3.11)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.9001, -117.4044, 0),
                Position.fromDegrees(34.9910, -117.3297, 0),
                Position.fromDegrees(34.9851, -117.2224, 0),
                Position.fromDegrees(34.9134, -117.2670, 0));
            graphic = factory.createGraphic("GFGPGAZ----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Airfield Zone (2.X.2.1.3.11)");
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Restricted Operation Zone (2.X.2.2.3.1)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.2331, -117.6217, 0),
                Position.fromDegrees(35.2331, -117.3552, 0),
                Position.fromDegrees(35.1998, -117.2560, 0),
                Position.fromDegrees(35.0851, -117.3604, 0),
                Position.fromDegrees(35.0857, -117.6261, 0));
            graphic = factory.createGraphic("GFGPAAR----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Restricted Operations Zone (2.X.2.2.3.1)");
            graphic.setText("(Unit ID)");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("180500Z", "180615Z"));
            graphic.setModifier(SymbologyConstants.ALTITUDE_DEPTH, Arrays.asList("2000 FT AGL", "3000 FT AGL"));
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Weapons Free Zone (2.X.2.2.3.5)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.7849, -117.3661, 0),
                Position.fromDegrees(34.6715, -117.3738, 0),
                Position.fromDegrees(34.6374, -117.3208, 0),
                Position.fromDegrees(34.6549, -117.1448, 0),
                Position.fromDegrees(34.7506, -117.1436, 0));
            graphic = factory.createGraphic("GFGPAAW----AUSX", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Weapons Free Zone (2.X.2.2.3.5)");
            graphic.setText("(Unit ID)");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("1000Z", "1300Z"));
            layer.addRenderable(graphic);

            /////////////////////////////////////////////
            // Circular target (2.X.4.3.1.2)
            /////////////////////////////////////////////

            Position position = (Position.fromDegrees(35.1108, -117.0870, 0));
            TacticalCircle circle = factory.createCircle("GHFPATC-------X", position, 5000.0, null);
            circle.setValue(AVKey.DISPLAY_NAME, "Circular Target (2.X.4.3.1.2)");
            circle.setText("AG9999");
            layer.addRenderable(circle);

            /////////////////////////////////////////////
            // Rectangular target (2.X.4.3.1.1)
            /////////////////////////////////////////////

            position = (Position.fromDegrees(35.0295, -116.9290, 0));
            TacticalQuad quad = factory.createQuad("GHFPATR-------X", Arrays.asList(position), null);
            quad.setLength(8000.0);
            quad.setWidth(4000.0);
            quad.setText("AB0176");
            quad.setValue(AVKey.DISPLAY_NAME, "Rectangular Target (2.X.4.3.1.1)");
            layer.addRenderable(quad);

            /////////////////////////////////////////////
            // Position Area for Artillery, Circular target (2.X.4.3.2.6.2)
            /////////////////////////////////////////////

            position = Position.fromDegrees(35.2106, -117.0092, 0);
            circle = factory.createCircle("GFFPACPC------X", position, 5000.0, null);
            circle.setValue(AVKey.DISPLAY_NAME, "Position Area for Artillery (2.X.4.3.2.6.2)");
            layer.addRenderable(circle);

            /////////////////////////////////////////////
            // Position Area for Artillery, Rectangular target (2.X.4.3.2.6.1)
            /////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.2520, -116.8968, 0),
                Position.fromDegrees(35.2364, -116.8063, 0));
            quad = factory.createQuad("GFFPACPR------X", positions, null);
            quad.setWidth(5000.0);
            quad.setValue(AVKey.DISPLAY_NAME, "Position Area for Artillery (2.X.4.3.2.6.1)");
            layer.addRenderable(quad);

            //////////////////////////////////////////////////
            // Circular Weapon/Sensor Range Fan (2.X.4.3.4.1)
            //////////////////////////////////////////////////

            position = (Position.fromDegrees(34.6813, -116.9724, 0));
            graphic = factory.createGraphic("GFFPAXC-------X", Arrays.asList(position), null);
            graphic.setModifier(SymbologyConstants.DISTANCE, Arrays.asList(1000.0, 6000.0, 11000.0));
            graphic.setModifier(SymbologyConstants.ALTITUDE_DEPTH, Arrays.asList("GL", "GL", "GL"));
            graphic.setValue(AVKey.DISPLAY_NAME, "Weapon/Sensor Range Fan (2.X.4.3.4.1)");
            layer.addRenderable(graphic);

            //////////////////////////////////////////////////
            // Sector Weapon/Sensor Range Fan (2.X.4.3.4.2)
            //////////////////////////////////////////////////

            List<Angle> azimuths = Arrays.asList(
                Angle.fromDegrees(290), Angle.fromDegrees(30), // Range 1, left azimuth, right azimuth
                Angle.fromDegrees(290), Angle.fromDegrees(30), // Range 2, left azimuth, right azimuth
                Angle.fromDegrees(315), Angle.fromDegrees(30), // Range 3, left azimuth, right azimuth
                Angle.fromDegrees(315), Angle.fromDegrees(35));

            position = (Position.fromDegrees(34.5798, -116.6591, 0));
            graphic = factory.createGraphic("GFFPAXS-------X", Arrays.asList(position), null);
            graphic.setModifier(SymbologyConstants.DISTANCE, Arrays.asList(4000.0, 6000.0, 11000.0, 15000.0));
            graphic.setModifier(SymbologyConstants.AZIMUTH, azimuths);
            graphic.setModifier(SymbologyConstants.ALTITUDE_DEPTH, Arrays.asList("GL", "GL", "GL", "GL"));
            graphic.setValue(AVKey.DISPLAY_NAME, "Weapon/Sensor Range Fan (2.X.4.3.4.2)");
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Restrictive Fire Area (RFA), Rectangular (2.X.4.3.2.5.2)
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.9672, -116.8974, 0),
                Position.fromDegrees(34.9188, -116.8205, 0));
            graphic = factory.createGraphic("GFFPACRR------X", positions, null);
            graphic.setModifier(SymbologyConstants.DISTANCE, 5000.0);
            graphic.setValue(AVKey.DISPLAY_NAME, "Restrictive Fire Area (2.X.4.3.2.5.2)");
            graphic.setText("X CORPS");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("051030", "051600Z"));
            layer.addRenderable(graphic);

            /////////////////////////////////////////////////////
            // Fire Support Area (FSA), Circular (2.X.4.3.2.1.3)
            /////////////////////////////////////////////////////

            position = Position.fromDegrees(34.8208, -116.8284, 0);
            graphic = factory.createCircle("GFFPACSC------X", position, 5000.0, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Fire Support Area (2.X.4.3.2.1.3)");
            graphic.setText("GREEN");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("051030", "051600Z"));
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Sensor Zone, Rectangular
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.0592, -117.2903, 0),
                Position.fromDegrees(35.0620, -117.1606, 0));
            graphic = factory.createGraphic("GFFPACER------X", positions, null);
            graphic.setModifier(SymbologyConstants.DISTANCE, 5000.0);
            graphic.setValue(AVKey.DISPLAY_NAME, "Sensor Zone");
            graphic.setText("Q37");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("051030", "051600Z"));
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Call for Fire Zone, Irregular (2.X.4.3.3.2.1)
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.1386, -116.9302, 0),
                Position.fromDegrees(35.1030, -116.9302, 0),
                Position.fromDegrees(35.0731, -116.9561, 0),
                Position.fromDegrees(35.0620, -116.8345, 0),
                Position.fromDegrees(35.1422, -116.8193, 0));
            graphic = factory.createGraphic("GFFPAZXI------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Call for Fire Zone (2.X.4.3.3.2.1)");
            graphic.setText("3BDE 4ID");
            graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList("051030", "051600Z"));
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Series or Group of Targets (2.X.4.3.1.3)
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.0160, -116.6625, 0),
                Position.fromDegrees(35.0601, -116.6773, 0),
                Position.fromDegrees(35.0542, -116.7611, 0),
                Position.fromDegrees(35.0092, -116.7790, 0));
            graphic = factory.createGraphic("GFFPATG-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Series or Group of Targets (2.X.4.3.1.3)");
            graphic.setText("JEFF");
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Airhead (2.X.2.6.2.2)
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(35.1260, -116.6625, 0),
                Position.fromDegrees(35.1701, -116.6773, 0),
                Position.fromDegrees(35.1642, -116.7611, 0),
                Position.fromDegrees(35.1192, -116.7790, 0));
            graphic = factory.createGraphic("GFFPSAA-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Airhead (2.X.2.6.2.2)");
            graphic.setText("DELTA");
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Minimum Safe Distance Zones (2.X.3.4.1)
            ////////////////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.9073, -116.7041, 0), // Center point of graphic
                Position.fromDegrees(34.9230, -116.6842, 0), // Radius of ring 1
                Position.fromDegrees(34.8723, -116.6841, 0), // Radius of ring 2
                Position.fromDegrees(34.8870, -116.7783, 0)); // Radius of ring 3
            graphic = factory.createGraphic("GFFPNM--------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Minimum Safe Distance Zones (2.X.3.4.1)");
            layer.addRenderable(graphic);

            ////////////////////////////////////////////////////////////
            // Pull-Up Point (2.X.3.4.1)
            ////////////////////////////////////////////////////////////

            position = Position.fromDegrees(35.2132, -117.8042, 0);
            circle = factory.createCircle("GFFPAPU-------X", position, 2000.0, null);
            circle.setValue(AVKey.DISPLAY_NAME, "Pull-Up Point (2.X.2.2.1.3)");
            layer.addRenderable(circle);

            ///////////////////////////////////////////////////
            // Attack by Fire Position (2.X.2.5.3.3)
            ///////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.6297, -117.6718, 0),
                Position.fromDegrees(34.5807, -117.5902, 0),
                Position.fromDegrees(34.5344, -117.6686, 0));
            graphic = factory.createGraphic("GFGPOAF-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Attack by Fire Position (2.X.2.5.3.3)");
            layer.addRenderable(graphic);

            ///////////////////////////////////////////////////
            // Support by Fire Position (2.X.2.5.3.4)
            ///////////////////////////////////////////////////

            positions = Arrays.asList(
                Position.fromDegrees(34.6159, -117.8571, 0),
                Position.fromDegrees(34.5775, -117.7887, 0),
                Position.fromDegrees(34.6308, -117.8477, 0),
                Position.fromDegrees(34.5837, -117.7725, 0));
            graphic = factory.createGraphic("GFGPOAS-------X", positions, null);
            graphic.setValue(AVKey.DISPLAY_NAME, "Support by Fire Position (2.X.2.5.3.4)");
            layer.addRenderable(graphic);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 34.90);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -117.44);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 155000);

        ApplicationTemplate.start("World Wind Tactical Graphics", AppFrame.class);
    }
}
