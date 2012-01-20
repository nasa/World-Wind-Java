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
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.util.*;

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

            this.addFrameTypeSymbols();

            // Size the World Window to provide enough screen space for the symbols and center the World Window on the
            // screen.
            Dimension size = new Dimension(1800, 1000);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }

        protected void addFrameTypeSymbols()
        {
            RenderableLayer layer = new RenderableLayer();
            layer.setName("Standard Frame Types");

            Iterator<String> symbolIds = this.getFrameTypeIterator().iterator();
            Iterator<Position> positions = this.getGridIterator(
                Sector.fromDegrees(39.5, 40.5, -120.5, -119.5), 14, 9, 3000).iterator();

            while (symbolIds.hasNext() && positions.hasNext())
            {
                String symbolId = symbolIds.next();
                TacticalSymbol symbol = new MilStd2525TacticalSymbol(symbolId, positions.next());
                symbol.setValue(AVKey.DISPLAY_NAME, symbolId);
                layer.addRenderable(symbol);
            }

            // Add the symbol layer to the World Wind model.
            this.getWwd().getModel().getLayers().add(layer);

            // Update the layer panel to display the symbol layer.
            this.getLayerPanel().update(this.getWwd());
        }

        protected Iterable<Position> getGridIterator(Sector sector, int numLatPoints, int numLonPoints, double altitude)
        {
            double minLat = sector.getMinLatitude().degrees;
            double maxLat = sector.getMaxLatitude().degrees;
            double minLon = sector.getMinLongitude().degrees;
            double maxLon = sector.getMaxLongitude().degrees;
            double latDelta = sector.getDeltaLatDegrees() / numLatPoints;
            double lonDelta = sector.getDeltaLonDegrees() / numLonPoints;

            ArrayList<Position> positions = new ArrayList<Position>();

            for (double lat = maxLat; lat >= minLat; lat -= latDelta)
            {
                for (double lon = minLon; lon <= maxLon; lon += lonDelta)
                {
                    positions.add(Position.fromDegrees(lat, lon, altitude));
                }
            }

            return positions;
        }

        protected Iterable<String> getFrameTypeIterator()
        {
            return Arrays.asList(
                // Standard Identity Pending
                "SPZP-----------",
                "SPPP-----------",
                "SPAP-----------",
                "SPGPU----------",
                "SPGPE----------",
                "SPGPI-----H----",
                "SPSP-----------",
                "SPUP-----------",
                "SPFP-----------",
                // Standard Identity Unknown
                "SUZP-----------",
                "SUPP-----------",
                "SUAP-----------",
                "SUGPU----------",
                "SUGPE----------",
                "SUGPI-----H----",
                "SUSP-----------",
                "SUUP-----------",
                "SUFP-----------",
                // Standard Identity Friend
                "SFZP-----------",
                "SFPP-----------",
                "SFAP-----------",
                "SFGPU----------",
                "SFGPE----------",
                "SFGPI-----H----",
                "SFSP-----------",
                "SFUP-----------",
                "SFFP-----------",
                // Standard Identity Neutral
                "SNZP-----------",
                "SNPP-----------",
                "SNAP-----------",
                "SNGPU----------",
                "SNGPE----------",
                "SNGPI-----H----",
                "SNSP-----------",
                "SNUP-----------",
                "SNFP-----------",
                // Standard Identity Hostile
                "SHZP-----------",
                "SHPP-----------",
                "SHAP-----------",
                "SHGPU----------",
                "SHGPE----------",
                "SHGPI-----H----",
                "SHSP-----------",
                "SHUP-----------",
                "SHFP-----------",
                // Standard Identity Assumed Friend
                "SAZP-----------",
                "SAPP-----------",
                "SAAP-----------",
                "SAGPU----------",
                "SAGPE----------",
                "SAGPI-----H----",
                "SASP-----------",
                "SAUP-----------",
                "SAFP-----------",
                // Standard Identity Suspect
                "SSZP-----------",
                "SSPP-----------",
                "SSAP-----------",
                "SSGPU----------",
                "SSGPE----------",
                "SSGPI-----H----",
                "SSSP-----------",
                "SSUP-----------",
                "SSFP-----------",
                // Standard Identity Exercise Pending
                "SGZP-----------",
                "SGPP-----------",
                "SGAP-----------",
                "SGGPU----------",
                "SGGPE----------",
                "SGGPI-----H----",
                "SGSP-----------",
                "SGUP-----------",
                "SGFP-----------",
                // Standard Identity Exercise Unknown
                "SWZP-----------",
                "SWPP-----------",
                "SWAP-----------",
                "SWGPU----------",
                "SWGPE----------",
                "SWGPI-----H----",
                "SWSP-----------",
                "SWUP-----------",
                "SWFP-----------",
                // Standard Identity Exercise Friend
                "SDZP-----------",
                "SDPP-----------",
                "SDAP-----------",
                "SDGPU----------",
                "SDGPE----------",
                "SDGPI-----H----",
                "SDSP-----------",
                "SDUP-----------",
                "SDFP-----------",
                // Standard Identity Exercise Neutral
                "SLZP-----------",
                "SLPP-----------",
                "SLAP-----------",
                "SLGPU----------",
                "SLGPE----------",
                "SLGPI-----H----",
                "SLSP-----------",
                "SLUP-----------",
                "SLFP-----------",
                // Standard Identity Exercise Assumed Friend
                "SMZP-----------",
                "SMPP-----------",
                "SMAP-----------",
                "SMGPU----------",
                "SMGPE----------",
                "SMGPI-----H----",
                "SMSP-----------",
                "SMUP-----------",
                "SMFP-----------",
                // Standard Identity Joker
                "SJZP-----------",
                "SJPP-----------",
                "SJAP-----------",
                "SJGPU----------",
                "SJGPE----------",
                "SJGPI-----H----",
                "SJSP-----------",
                "SJUP-----------",
                "SJFP-----------",
                // Standard Identity Faker
                "SKZP-----------",
                "SKPP-----------",
                "SKAP-----------",
                "SKGPU----------",
                "SKGPE----------",
                "SKGPI-----H----",
                "SKSP-----------",
                "SKUP-----------",
                "SKFP-----------"
            );
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 40);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -120);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 100000);

        ApplicationTemplate.start("World Wind MIL-STD-2525 Tactical Symbol Frame Types", AppFrame.class);
    }
}
