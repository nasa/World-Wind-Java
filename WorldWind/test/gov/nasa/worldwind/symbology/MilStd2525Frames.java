/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
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

            // Create tactical icons
            String URL = "http://worldwindserver.net/milstd2525/";
            MilStd2525IconRetriever symGen = new MilStd2525IconRetriever(URL);
            float deltaLat = 0.0f;
            float deltaLon = 0.0f;
            float deltaDeltaLon = 0.18f;

            //********************************
            // Standard Identity - Pending (P):
/*
            // Battle Dimension: Unknown (Z)
            String symCode = "SPZP-----------";
            BufferedImage img = symGen.createIcon(symCode);
            Sector s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            //SurfaceImage symbol = new SurfaceImage(img, s);
            Position textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            SurfaceText labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            SurfaceText labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")", textPos);
            SurfaceText labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/
            // Battle Dimension: Space (P)
            String symCode = "SPPP-----------";
            BufferedImage img = symGen.createIcon(symCode);
            Sector s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            SurfaceImage symbol = new SurfaceImage(img, s);
            Position textPos = new Position(Angle.fromDegrees(47.73 + deltaLat),
                Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            SurfaceText labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            SurfaceText labelSI = new SurfaceText("Standard Identity: Pending (" + symCode.charAt(1) + ")",
                textPos.subtract(Position.fromDegrees(0.01, 0)));
            SurfaceText labelBD = new SurfaceText("Battle Dimension: Space (" + symCode.charAt(2) + ")",
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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SUZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Unknown (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SFZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Friend (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SNZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Neutral (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SHZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Hostile (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SAZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Assumed Friend (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SSZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Suspect (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SGZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Pending (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            /*
            // Battle Dimension: Unknown (Z)
            symCode = "SWZP-----------";
            img = symGen.createIcon(symCode);
            s = new Sector(Angle.fromDegrees(47.6 + deltaLat), Angle.fromDegrees(47.7 + deltaLat),
                Angle.fromDegrees(-122.33 + deltaLon), Angle.fromDegrees(-122.17 + deltaLon));
            symbol = new SurfaceImage(img, s);
            textPos = new Position(Angle.fromDegrees(47.73 + deltaLat), Angle.fromDegreesLongitude(-122.25 + deltaLon), 0);
            labelSymCode = new SurfaceText("Symbol Code: " + symCode, textPos);
            labelSI = new SurfaceText("Standard Identity: Exercise Unknown (" + symCode.charAt(1) + ")", textPos.subtract(Position.fromDegrees(0.01, 0)));
            labelBD = new SurfaceText("Battle Dimension: Unknown (" + symCode.charAt(2) + ")", textPos.subtract(Position.fromDegrees(0.02, 0)));
            layer.addRenderable(symbol);
            layer.addRenderable(labelSymCode);
            layer.addRenderable(labelSI);
            layer.addRenderable(labelBD);

            deltaLon += deltaDeltaLon;
*/

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

            deltaLon += deltaDeltaLon;
*/

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

            deltaLon += deltaDeltaLon;
*/

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

            deltaLon += deltaDeltaLon;
*/

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

            deltaLon += deltaDeltaLon;
*/

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

            deltaLon += deltaDeltaLon;
*/

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
            symCode = "SKFP-----------";
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

            this.getWwd().getModel().getLayers().add(layer);
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
