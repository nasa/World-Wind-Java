/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.servers.tools.xml.XMLWriter;
import gov.nasa.worldwind.servers.wms.generators.WorldWindTiledLayer;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;
import org.w3c.dom.Element;

import java.util.logging.Level;

/**
 * @author dcollins
 * @version $Id$
 */
public class WorldWindTiledLayerMapSource extends DataConfigurationMapSource
{
    public WorldWindTiledLayerMapSource(FileStore fileStore, Element configElement, AVList params,
                                        Class<? extends WorldWindTiledLayer> serviceClass)
    {
        super(fileStore, configElement, params, serviceClass);
    }

    protected void writeLayerAttributes(XMLWriter xmlwriter) throws Exception
    {
        super.writeLayerAttributes(xmlwriter);
//        this.writeWorldWindTiledLayerAttributes(xmlwriter);
    }

    protected void calcScaleHint()
    {
        synchronized (this)
        {
            try
            {
                LatLon levelZeroTileDelta = (LatLon) this.getValue(AVKey.LEVEL_ZERO_TILE_DELTA);
//                double tileWidth = (Integer)this.getValue(AVKey.TILE_WIDTH);
                double tileHeight = (Integer) this.getValue(AVKey.TILE_HEIGHT);
                double totalLevels = (Integer) this.getValue(AVKey.NUM_LEVELS);
                double firstEmptyLevels = (Integer) this.getValue(AVKey.NUM_EMPTY_LEVELS);

                double texelSize = levelZeroTileDelta.getLatitude().degrees / tileHeight;
                this.scaleHintMin = texelSize / Math.pow(2d, firstEmptyLevels);
                this.scaleHintMax = texelSize / Math.pow(2d, totalLevels);
            }
            catch (Throwable t)
            {
                String message = WWUtil.extractExceptionReason(t);
                Logging.logger().log(Level.FINEST, message, t);
            }
        }
    }

    @Override
    public double getScaleHintMin()
    {
        if (this.scaleHintMin == 0d)
        {
            this.calcScaleHint();
        }

        return this.scaleHintMin;
    }

    @Override
    public double getScaleHintMax()
    {
        if (this.scaleHintMax == 0d)
        {
            this.calcScaleHint();
        }

        return this.scaleHintMax;
    }


    protected void writeLayerElements(XMLWriter xmlwriter) throws Exception
    {
        super.writeLayerElements(xmlwriter);
//        this.writeWorldWindTiledLayerElements(xmlwriter);
    }
//
//    protected void writeWorldWindTiledLayerAttributes(XMLWriter xmlwriter) throws Exception
//    {
//        WorldWindTiledLayer layer = (WorldWindTiledLayer) this.getMapGenerator();
//        LevelSet levelSet = layer.getLevelSet();
//
////        if (!layer.isComposable())
//        {
//            xmlwriter.addAttribute("nonComposable", "true");
//            xmlwriter.addAttribute("fixedWidth", levelSet.getFirstLevel().getTileWidth());
//            xmlwriter.addAttribute("fixedHeight", levelSet.getFirstLevel().getTileHeight());
//        }
//    }
//
//    protected void writeWorldWindTiledLayerElements(XMLWriter xmlwriter) throws Exception
//    {
//        WorldWindTiledLayer layer = (WorldWindTiledLayer) this.getMapGenerator();
//        LevelSet levelSet = layer.getLevelSet();
//
//        if (!layer.isComposable())
//        {
//            this.writeLatLon(xmlwriter, "GeographicTileOrigin", levelSet.getTileOrigin());
//
//            xmlwriter.openElement("GeographicTileDelta");
//                this.writeLatLon(xmlwriter, "MinTileDelta", levelSet.getLastLevel().getTileDelta());
//                this.writeLatLon(xmlwriter, "MaxTileDelta", levelSet.getFirstLevel().getTileDelta());
//            xmlwriter.closeElement("GeographicTileDelta");
//        }
//    }
//
//    protected void writeLatLon(XMLWriter xmlwriter, String name, LatLon ll) throws Exception
//    {
//        xmlwriter.openElement(name);
//                xmlwriter.addElement("Latitude", ll.getLatitude().getDegrees());
//                xmlwriter.addElement("Longitude", ll.getLongitude().getDegrees());
//        xmlwriter.closeElement(name);
//    }
}
