/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525IconRetriever;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.image.*;

/**
 * @author ccrick
 * @version $Id: Symbology.java 132 2011-10-25 18:47:52Z ccrick $
 */
public class MilStd2525Frames extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            RenderableLayer layer = new RenderableLayer();

            //displayTacticalIconFrames(layer);
            displayUnframedTacticalIcons(layer);

            this.getWwd().getModel().getLayers().add(layer);
        }

        protected void displayTacticalIconFrames(RenderableLayer layer)
        {
            // Create tactical icons
            String URL = "http://worldwindserver.net/milstd2525/";
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            float deltaLat = 0.0f;
            float deltaLon = 0.0f;
            float deltaDeltaLon = 0.18f;

            //********************************
            // Standard Identity - Pending (P):

            // Battle Dimension: Unknown (Z)
            String symCode = "SPZP-----------";
            BufferedImage img = symGen.createIcon(symCode);
            Sector s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            SurfaceImage symbol = new SurfaceImage(img, s);
            Position textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            SurfaceText labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            SurfaceText labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")", textPos);
            SurfaceText labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SPPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SPAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SPGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SPGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SPGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SPSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SPUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SPFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Unknown (U):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SUZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SUPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SUAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SUGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SUGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SUGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SUSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SUUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SUFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Friend (F):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SFZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SFPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SFAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SFGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SFGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SFGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SFSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SFUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SFFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Neutral (N):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SNZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SNPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SNAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SNGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SNGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SNGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SNSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SNUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SNFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Hostile (H):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SHZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SHPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SHAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SHGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SHGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SHGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SHSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SHUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SHFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Assumed Friend (A):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SAZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SAPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SAAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SAGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SAGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SAGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SASP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SAUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SAFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Suspect (S):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SSZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SSPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SSAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SSGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SSGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SSGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SSSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SSUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SSFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //-------------

            //********************************
            // Standard Identity - Exercise Pending (G):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SGZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SGPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SGAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SGGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SGGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SGGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SGSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SGUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SGFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Exercise Unknown (W):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SWZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SWPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SWAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SWGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SWGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SWGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SWSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SWUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SWFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Exercise Friend (D):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SDZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
*/
            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SDPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SDAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SDGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SDGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SDGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SDSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SDUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SDFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Exercise Neutral (L):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SLZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
*/
            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SLPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SLAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SLGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SLGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SLGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SLSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SLUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SLFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Exercise Assumed Friend (M):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SMZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
*/
            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SMPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SMAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SMGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SMGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SMGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SMSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SMUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SMFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Joker (J):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SJZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
*/
            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SJPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SJAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SJGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SJGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SJGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SJSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SJUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SJFP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Faker (K):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SKZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
*/
            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SKPP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SKAP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SKGPU----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SKGPE----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SKGPI-----H----";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SKSP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SKUP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SKFP---------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Faker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
        }

        protected void displayUnframedTacticalIcons(RenderableLayer layer)
        {
            // Create tactical icons
            String URL = "http://worldwindserver.net/milstd2525/";
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            AVListImpl params = new AVListImpl();
            params.setValue(SymbologyConstants.SHOW_FRAME, false);

            float deltaLat = 0.0f;
            float deltaLon = 0.0f;
            float deltaDeltaLon = 0.18f;

            //********************************
            // Standard Identity - Pending (P):

            // Battle Dimension: Unknown (Z)
            String symCode = "SPZP-----------";
            BufferedImage img = symGen.createIcon(symCode, params);
            Sector s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            SurfaceImage symbol = new SurfaceImage(img, s);
            Position textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            SurfaceText labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            SurfaceText labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            SurfaceText labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SPPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SPAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SPGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SPGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SPGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SPSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SPSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SPSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SPUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SPUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SPUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SPUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SPUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SPFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Unknown (U):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SUZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SUPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SUAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SUGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SUGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SUGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SUSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SUSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SUSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SUUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SUUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SUUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SUUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SUUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SUFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Friend (F):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SFZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SFPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SFAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SFGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SFGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SFGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SFSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SFSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SFSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SFUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SFUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SFUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SFUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SFUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SFFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Neutral (N):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SNZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SNPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SNAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SNGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SNGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SNGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SNSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SNSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SNSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SNUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SNUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SNUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SNUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SNUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SNFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Hostile (H):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SHZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SHPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SHAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SHGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SHGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SHGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SHSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SHSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SHSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SHUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SHUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SHUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SHUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SHUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SHFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Assumed Friend (A):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SAZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SAPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SAAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SAGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SAGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SAGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SASPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SASPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SASPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SAUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SAUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SAUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SAUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SAUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SAFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Suspect (S):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SSZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SSPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SSAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity:Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SSGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SSGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SSGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SSSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SSSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SSSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SSUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SSUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SSUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SSUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SSUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SSFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            //********************************
            // Standard Identity - Joker (J):

            deltaLat -= 0.2f;
            deltaLon = 0.0f;

            // Battle Dimension: Unknown (Z)
            symCode = "SJZP-----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Space (P)
            symCode = "SJPPV----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Air (A)
            symCode = "SJAPWMAP-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Air (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Units
            symCode = "SJGPUCDSV------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Units (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Equipment
            symCode = "SJGPEWMALE-----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Equip (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Ground (G), Installations
            symCode = "SJGPIMFP--H----";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText(
                "Battle Dimension: Ground (" + symCode.charAt(2) + "), Instal (" + symCode.charAt(4) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S)
            symCode = "SJSPGG---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Non-military
            symCode = "SJSPXFDR-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Sea Surface (S), Own Track
            symCode = "SJSPO----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Sea Surface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U)
            symCode = "SJUPSCG--------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Sea Mine
            symCode = "SJUPWMGD-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Underwater Decoy, Sea Mine
            symCode = "SJUPWDMM-------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Unexploded Ordinance Area
            symCode = "SJUPX----------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon),
                0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: Subsurface (U), Non-Submarine, Diver
            symCode = "SJUPND---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Subsurface (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;

            // Battle Dimension: SOF (F)
            symCode = "SJFPNB---------";
            img = symGen.createIcon(symCode, params);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Joker (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: SOF (" + symCode.charAt(2) + ")",
                textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 47.63);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -122.07);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 50000);

        ApplicationTemplate.start("World Wind MIL-STD2525 Standard Identities", AppFrame.class);
    }
}
