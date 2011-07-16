/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.data;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.*;

import javax.xml.xpath.*;
import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

/**
 * BasicRasterServer maintains a list of data sources and their properties in the BasicRasterServerCache
 * and is used to compose (mosaic) a data raster of the given region of interest from data sources.
 *
*/
public class BasicRasterServer extends WWObjectImpl implements RasterServer
{
    protected final String XPATH_RASTER_SERVER = "/RasterServer";
    protected final String XPATH_RASTER_SERVER_PROPERTY = XPATH_RASTER_SERVER + "/Property";
    protected final String XPATH_RASTER_SERVER_SOURCE = XPATH_RASTER_SERVER + "/Sources/Source";
    protected final String XPATH_RASTER_SERVER_SOURCE_SECTOR = XPATH_RASTER_SERVER_SOURCE + "/Sector";

    private java.util.List<DataRaster> dataRasterList = new java.util.ArrayList<DataRaster>();

    protected DataRasterReaderFactory readerFactory;

    protected static final MemoryCache cache = new BasicRasterServerCache();

    /**
     * BasicRasterServer constructor reads a list of data raster sources from *.RasterServer.xml (the file that accompanies layer
     * description XML file), reads sector of each source and maintains a list of data sources, their properties,
     *
     * @param o  the RasterServer.xml source to read.  May by a {@link java.io.File}, a file path,
     * a URL or an {@link org.w3c.dom.Element}
     *
     * @param params  optional metadata associated with the data source that might be useful to the BasicRasterServer.
     *
     */
    public BasicRasterServer(Object o, AVList params)
    {
        super();

        if (null != params)
        {
            this.setValues(params);
        }

        try
        {
            this.readerFactory = (DataRasterReaderFactory) WorldWind.createConfigurationComponent(
                AVKey.DATA_RASTER_READER_FACTORY_CLASS_NAME);
        }
        catch (Exception e)
        {
            this.readerFactory = new BasicDataRasterReaderFactory();
        }

        this.init(o);
    }

    /**
     * Returns an instance of the MemoryCache that contains DataRasters and their properties
     */
    public MemoryCache getCache()
    {
        return cache;
    }

    /**
     * Returns TRUE, if the DataRaster list is not empty
     *
     * @return true, if the DataRaster list is not empty
     */
    public boolean hasDataRasters()
    {
        return (this.dataRasterList.size() > 0);
    }

    protected void init(Object o)
    {
        if (null == o)
        {
            String message = Logging.getMessage("nullValue.ObjectIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Element rootElement = null;

        if (o instanceof Element)
        {
            rootElement = (Element) o;
        }
        else if (o instanceof Document)
        {
            rootElement = ((Document) o).getDocumentElement();
        }
        else
        {
            Document doc = WWXML.openDocument(o);
            if (null != doc)
            {
                rootElement = doc.getDocumentElement();
            }
        }

        if (null == rootElement)
        {
            String message = Logging.getMessage("generic.UnexpectedObjectType", o.getClass().getName());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String rootElementName = rootElement.getNodeName();
        if (!"RasterServer".equals(rootElementName))
        {
            String message = Logging.getMessage("generic.InvalidDataSource", rootElementName);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        XPath xpath = WWXML.makeXPath();

        this.extractProperties(rootElement, xpath);

        this.buildRasterFilesList(rootElement, xpath);
    }

    /**
     * Extracts all <Property></Property> key and values from the given DOM element
     * @param domElement XML object as DOM
     * @param xpath XPath instance
     *
     */
    protected void extractProperties(Element domElement, XPath xpath)
    {
        Element[] props = WWXML.getElements(domElement, XPATH_RASTER_SERVER_PROPERTY, xpath);

        if (props != null && props.length > 0)
        {
            for (Element prop : props)
            {
                String key = prop.getAttribute("name");
                String value = prop.getAttribute("value");
                if (!WWUtil.isEmpty(key) && !WWUtil.isEmpty(value))
                {
                    if (!this.hasKey(key))
                    {
                        this.setValue(key, value);
                    }
                    else
                    {
                        Object oldValue = this.getValue(key);
                        if (value.equals(oldValue))
                        {
                            continue;
                        }

                        String msg = Logging.getMessage("generic.AttemptToChangeExistingProperty", key, oldValue,
                            value);
                        Logging.logger().fine(msg);
                    }
                }
            }
        }
    }

    protected void buildRasterFilesList(Element domElement, XPath xpath)
    {
        long startTime = System.currentTimeMillis();

        int numSources = 0;
        Sector extent = null;

        try
        {
            XPathExpression xpExpression = xpath.compile(XPATH_RASTER_SERVER_SOURCE);
            NodeList nodes = (NodeList) xpExpression.evaluate(domElement, XPathConstants.NODESET);

            if (nodes == null || nodes.getLength() == 0)
            {
                return;
            }

            numSources = nodes.getLength();
            for (int i = 0; i < numSources; i++)
            {
                Thread.yield();
                try
                {
                    Node node = nodes.item(i);
                    node.getParentNode().removeChild(node);
                    if (node.getNodeType() != Node.ELEMENT_NODE)
                    {
                        continue;
                    }

                    Element source = (Element) node;

                    String path = source.getAttribute("path");
                    if (WWUtil.isEmpty(path))
                    {
                        continue;
                    }

                    AVList rasterMetadata = new AVListImpl();
                    Object rasterSource = new File(path);

                    DataRasterReader rasterReader = this.findDataRasterReader(rasterSource, rasterMetadata);
                    if (null == rasterReader)
                    {
//                        String message = Logging.getMessage("nullValue.ReaderIsNull") + " : " + rasterFile.getAbsolutePath();
//                        Logging.logger().severe(message);
                        continue;
                    }

                    Sector sector = WWXML.getSector(source, "Sector", xpath);
                    if (null == sector)
                    {
                        rasterReader.readMetadata(rasterSource, rasterMetadata);

                        Object o = rasterMetadata.getValue(AVKey.SECTOR);
                        if (o != null && o instanceof Sector)
                        {
                            sector = (Sector) o;
                        }
                    }
                    else
                    {
                        rasterMetadata.setValue(AVKey.SECTOR, sector);
                    }

                    // validate that all data rasters are the same type - we do not allow to mix elevations and imagery
                    if (this.getValue(AVKey.PIXEL_FORMAT) == null)
                    {
                        if (AVKey.IMAGE.equals(rasterMetadata.getStringValue(AVKey.PIXEL_FORMAT)))
                        {
                            this.setValue(AVKey.PIXEL_FORMAT, AVKey.IMAGE);
                        }
                        else if (AVKey.ELEVATION.equals(rasterMetadata.getStringValue(AVKey.PIXEL_FORMAT)))
                        {
                            this.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);
                        }
                        else
                        {
                            String msg = Logging.getMessage("generic.UnknownFileFormat", rasterSource);
                            Logging.logger().warning(msg);
                        }
                    }
                    else
                    {
                        String rasterType = this.getStringValue(AVKey.PIXEL_FORMAT);
                        if (!rasterType.equals(rasterMetadata.getValue(AVKey.PIXEL_FORMAT)))
                        {
                            String msg = Logging.getMessage("generic.UnexpectedRasterType", rasterSource);
                            Logging.logger().warning(msg);
                        }
                    }

                    if (null != sector)
                    {
                        extent = Sector.union(extent, sector);
                        this.dataRasterList.add(
                            new CachedDataRaster(rasterSource, rasterMetadata, rasterReader, this.getCache())
                        );
                    }
                }
                catch (Throwable t)
                {
                    String message = t.getMessage();
                    message = (WWUtil.isEmpty(message)) ? t.getCause().getMessage() : message;
                    Logging.logger().log(java.util.logging.Level.WARNING, message, t);
                }
            }

            if (null != extent && extent.getDeltaLatDegrees() > 0d && extent.getDeltaLonDegrees() > 0d)
            {
                this.setValue(AVKey.SECTOR, extent);
            }
        }
        catch (Throwable t)
        {
            String message = t.getMessage();
            message = (WWUtil.isEmpty(message)) ? t.getCause().getMessage() : message;
            Logging.logger().log(java.util.logging.Level.SEVERE, message, t);
        }
        finally
        {
            Logging.logger().finest(this.getStringValue(AVKey.DISPLAY_NAME) + ": " + numSources
                + " files in " + (System.currentTimeMillis() - startTime) + " milli-seconds");
        }
    }

    protected DataRasterReader findDataRasterReader(Object source, AVList params)
    {
        if (source == null)
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        params = (null == params) ? new AVListImpl() : params;

        DataRasterReader reader = this.readerFactory.findReaderFor(source, params);
        if (reader == null)
        {
            return null;
        }

        if (!params.hasKey(AVKey.PIXEL_FORMAT))
        {
            try
            {
                reader.readMetadata(source, params);
            }
            catch (Exception e)
            {
                // Reading the input source's metadata caused an exception. This exception does not prevent us from
                // determining if the source represents elevation data, but we want to make a note of it. Therefore we
                // log the exception with level FINE.
                String message = Logging.getMessage("generic.ExceptionWhileReading", source);
                Logging.logger().finest(message);
            }
        }

        return reader;
    }

    public Sector getSector()
    {
        return (this.hasKey(AVKey.SECTOR)) ? (Sector) this.getValue(AVKey.SECTOR) : null;
    }

    /**
     * Composes a DataRaster of the given width and height for the specific geographic region of interest (ROI).
     *
     * @param reqParams This is a required parameter, must not be null or empty;
     * Must contain AVKey.WIDTH, AVKey.HEIGHT, and AVKey.SECTOR values.
     *
     * Optional keys are:
     * AVKey.PIXEL_FORMAT (AVKey.ELEVATION | AVKey.IMAGE)
     * AVKey.DATA_TYPE
     * AVKey.BYTE_ORDER (AVKey.BIG_ENDIAN | AVKey.LITTLE_ENDIAN )
     *
     *
     * @return a DataRaster for the requested ROI
     *
     * @throws gov.nasa.worldwind.exception.WWRuntimeException
     *                                  if there is no intersection of the source rasters with the requested ROI or the
     *                                  source format is unknown or not supported by currently loaded drivers
     * @throws IllegalArgumentException if any of the required parameters or values are missing
     */
    public DataRaster composeRaster(AVList reqParams) throws IllegalArgumentException, WWRuntimeException
    {
        DataRaster reqRaster;

        if (null == reqParams)
        {
            String message = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (!reqParams.hasKey(AVKey.WIDTH))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.WIDTH);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (!reqParams.hasKey(AVKey.HEIGHT))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.HEIGHT);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Object o = reqParams.getValue(AVKey.SECTOR);
        if (null == o || !(o instanceof Sector))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SECTOR);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Sector reqSector = (Sector) o;
        Sector rasterExtent = this.getSector();
        if (!reqSector.intersects(rasterExtent))
        {
            String message = Logging.getMessage("generic.SectorRequestedOutsideCoverageArea", reqSector, rasterExtent);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        try
        {
            int reqWidth = (Integer) reqParams.getValue(AVKey.WIDTH);
            int reqHeight = (Integer) reqParams.getValue(AVKey.HEIGHT);

            if (!reqParams.hasKey(AVKey.BYTE_ORDER))
            {
                reqParams.setValue(AVKey.BYTE_ORDER, AVKey.BIG_ENDIAN);
            }

            // check if this Raster Server serves elevations or imagery
            if (AVKey.ELEVATION.equals(this.getStringValue(AVKey.PIXEL_FORMAT)))
            {
                reqParams.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);

                if (!reqParams.hasKey(AVKey.DATA_TYPE))
                {
                    reqParams.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                }

                reqRaster = new ByteBufferRaster(reqWidth, reqHeight, reqSector, reqParams);
            }
            else if (AVKey.IMAGE.equals(this.getStringValue(AVKey.PIXEL_FORMAT)))
            {
                reqParams.setValue(AVKey.PIXEL_FORMAT, AVKey.IMAGE);
                reqRaster = new BufferedImageRaster(reqWidth, reqHeight, Transparency.TRANSLUCENT, reqSector);
            }
            else
            {
                String msg = Logging.getMessage("generic.UnrecognizedSourceType", this.getValue(AVKey.PIXEL_FORMAT));
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            for (DataRaster raster : this.dataRasterList)
            {
                Sector rasterSector = raster.getSector();
                Sector overlap = reqSector.intersection(rasterSector);
                // SKIP, if not intersection, or intersects only on edges
                if (null == overlap || overlap.getDeltaLatDegrees() == 0d || overlap.getDeltaLonDegrees() == 0d)
                {
                    continue;
                }

                raster.drawOnTo(reqRaster);
            }
        }
        catch (WWRuntimeException wwe)
        {
            throw wwe;
        }
        catch (Throwable t)
        {
            String message = t.getMessage();
            message = (WWUtil.isEmpty(message)) ? t.getCause().getMessage() : message;
            Logging.logger().log(java.util.logging.Level.FINE, message, t);
            throw new WWRuntimeException(message);
        }

        return reqRaster;
    }

    /**
     * Composes a DataRaster of the given width and height for the specific geographic region of interest (ROI),
     * in the requested file format (AVKey.IMAGE_FORMAT) and returns as a ByteBuffer
     *
     * @param params This is a required parameter, must not be null or empty;
     * Must contain AVKey.WIDTH, AVKey.HEIGHT, AVKey.SECTOR, and AVKey.IMAGE_FORMAT (mime type) values.
     * Supported mime types are: "image/png", "image/jpeg", "image/dds".
     *
     * Optional keys are:
     * AVKey.PIXEL_FORMAT (AVKey.ELEVATION | AVKey.IMAGE)
     * AVKey.DATA_TYPE
     * AVKey.BYTE_ORDER (AVKey.BIG_ENDIAN | AVKey.LITTLE_ENDIAN )
     *
     * @return a DataRaster for the requested ROI
     *
     * @throws gov.nasa.worldwind.exception.WWRuntimeException if there is no intersection of the source rasters
     * with the requested ROI or the source format is unknown or not supported by currently loaded drivers
     *
     * @throws IllegalArgumentException if any of the required parameters or values are missing
     *
     */
    public ByteBuffer getRasterAsByteBuffer(AVList params)
    {
        // request may contain a specific file format, different from a default file format
        String format = (null != params && params.hasKey(AVKey.IMAGE_FORMAT))
            ? params.getStringValue(AVKey.IMAGE_FORMAT) : this.getStringValue(AVKey.IMAGE_FORMAT);
        if (WWUtil.isEmpty(format))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.IMAGE_FORMAT);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        if (this.dataRasterList.isEmpty())
        {
            String message = Logging.getMessage("generic.NoImagesAvailable");
            Logging.logger().finest(message);
            throw new WWRuntimeException(message);
        }

        try
        {
            DataRaster raster = this.composeRaster(params);

            if (raster instanceof BufferedImageRaster)
            {
                if ("image/png".equalsIgnoreCase(format))
                {
                    return ImageUtil.asPNG(raster);
                }
                else if ("image/jpeg".equalsIgnoreCase(format) || "image/jpg".equalsIgnoreCase(format))
                {
                    return ImageUtil.asJPEG(raster);
                }
                if ("image/dds".equalsIgnoreCase(format))
                {
                    return DDSCompressor.compressImage(((BufferedImageRaster) raster).getBufferedImage());
                }
                else
                {
                    String msg = Logging.getMessage("generic.UnknownFileFormat", format);
                    Logging.logger().severe(msg);
                    throw new WWRuntimeException(msg);
                }
            }
            else if (raster instanceof ByteBufferRaster)
            {
                // Elevations as BIL16 or as BIL32 are stored in the simple ByteBuffer object
                return ((ByteBufferRaster) raster).getByteBuffer();
            }
            else
            {
                String msg = Logging.getMessage("generic.UnexpectedRasterType", raster.getClass().getName());
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }
        }
        catch (WWRuntimeException wwe)
        {
            Logging.logger().finest(wwe.getMessage());
        }
        catch (Throwable t)
        {
            String message = t.getMessage();
            message = (WWUtil.isEmpty(message)) ? t.getCause().getMessage() : message;
            Logging.logger().log(java.util.logging.Level.SEVERE, message, t);
        }

        return null;
    }
}

