/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.gx.GXConstants;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import javax.xml.stream.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Renders a single image contained in a local file, a remote file, or a <code>BufferedImage</code>.
 * <p/>
 * Note: The view input handlers detect the surface image rather than the terrain as the top picked object in {@link
 * gov.nasa.worldwind.event.SelectEvent}s and will not respond to the user's attempts at navigation when the cursor is
 * over the image. If this is not the desired behavior, disable picking for the layer containing the surface image.
 *
 * @version $Id$
 */
public class SurfaceImage implements SurfaceTile, Renderable, PreRenderable, Movable, Disposable, Exportable
{
    // TODO: Handle date-line spanning sectors

    private Sector sector;
    private Position referencePosition;
    private double opacity = 1.0;
    private boolean pickEnabled = true;

    protected Object imageSource;
    protected WWTexture sourceTexture;
    protected WWTexture generatedTexture;
    protected List<LatLon> corners;
    protected WWTexture previousSourceTexture;
    protected WWTexture previousGeneratedTexture;

    /**
     * A list that contains only a reference to this instance. Used as an argument to the surface tile renderer to
     * prevent its having to create a list every time this surface image is rendered.
     */
    protected List<SurfaceImage> thisList = Arrays.asList(this);

    /** Create a new surface image with no image source. The image will not be rendered until an image source is set. */
    public SurfaceImage()
    {
    }

    /**
     * Renders a single image tile from a local image source.
     *
     * @param imageSource either the file path to a local image or a <code>BufferedImage</code> reference.
     * @param sector      the sector covered by the image.
     */
    public SurfaceImage(Object imageSource, Sector sector)
    {
        if (imageSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setImageSource(imageSource, sector);
    }

    public SurfaceImage(Object imageSource, Iterable<? extends LatLon> corners)
    {
        if (imageSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (corners == null)
        {
            String message = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setImageSource(imageSource, corners);
    }

    public void dispose()
    {
        this.generatedTexture = null;
    }

    public void setImageSource(Object imageSource, Sector sector)
    {
        this.setImageSource(imageSource, (Iterable<? extends LatLon>) sector);
    }

    public void setImageSource(Object imageSource, Iterable<? extends LatLon> corners)
    {
        // If the current source texture or generated texture are non-null, keep track of them and remove their textures
        // from the GPU resource cache on the next frame. This prevents SurfaceImage from leaking memory when the caller
        // continuously specifies the image source to display an animation. We ignore null textures to avoid clearing
        // previous a texture that we're already tracking. If the caller specifies a new image source more than once per
        // frame, this still keeps track of the previous texture.
        if (this.sourceTexture != null)
            this.previousSourceTexture = this.sourceTexture;
        if (this.generatedTexture != null)
            this.previousGeneratedTexture = this.generatedTexture;

        // Assign the new image source and clear the current source texture. We initialize the source texture during the
        // next frame. This enables SurfaceImage to retrieve remote images during each frame on a separate thread.
        this.imageSource = imageSource;
        this.sourceTexture = null;

        // Update the surface image's geometry. This also clears the current generated texture (if any).
        initializeGeometry(corners);
    }

    public boolean isPickEnabled()
    {
        return this.pickEnabled;
    }

    public void setPickEnabled(boolean pickEnabled)
    {
        this.pickEnabled = pickEnabled;
    }

    protected void initializeGeometry(Iterable<? extends LatLon> corners)
    {
        this.corners = new ArrayList<LatLon>(4);
        for (LatLon ll : corners)
        {
            this.corners.add(ll);
        }

        this.sector = Sector.boundingSector(this.corners);
        this.referencePosition = new Position(sector.getCentroid(), 0);
        this.generatedTexture = null;
    }

    public Object getImageSource()
    {
        return this.imageSource;
    }

    public double getOpacity()
    {
        return opacity;
    }

    public void setOpacity(double opacity)
    {
        this.opacity = opacity;
    }

    // SurfaceTile interface

    public Sector getSector()
    {
        return this.sector;
    }

    public void setCorners(Iterable<? extends LatLon> corners)
    {
        if (corners == null)
        {
            String message = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.initializeGeometry(corners);
    }

    public List<LatLon> getCorners()
    {
        return new ArrayList<LatLon>(this.corners);
    }

    public Extent getExtent(DrawContext dc)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return Sector.computeBoundingCylinder(dc.getGlobe(), dc.getVerticalExaggeration(), this.getSector());
    }

    public boolean bind(DrawContext dc)
    {
        return this.generatedTexture != null && this.generatedTexture.bind(dc);
    }

    public void applyInternalTransform(DrawContext dc, boolean textureIdentityActive)
    {
        if (this.generatedTexture != null)
            this.generatedTexture.applyInternalTransform(dc);
    }

    // Renderable interface

    public void preRender(DrawContext dc)
    {
        if (this.previousGeneratedTexture != null)
        {
            dc.getTextureCache().remove(this.previousGeneratedTexture);
            this.previousGeneratedTexture = null;
        }

        if (this.previousSourceTexture != null)
        {
            dc.getTextureCache().remove(this.previousSourceTexture.getImageSource());
            this.previousSourceTexture = null;
        }

        // Initialize the source texture if the caller specified a new image source.
        if (this.sourceTexture == null)
            this.initializeSourceTexture(dc);

        // Exit if the source texture could not be initialized.
        if (this.sourceTexture == null)
            return;

        if (this.generatedTexture == null)
            this.generatedTexture = this.initializeGeneratedTexture(dc);
    }

    public void render(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        if (dc.isPickingMode() && !this.isPickEnabled())
            return;

        if (!this.getSector().intersects(dc.getVisibleSector()))
            return;

        if (this.sourceTexture == null)
            return;

        GL gl = dc.getGL();
        try
        {
            if (!dc.isPickingMode())
            {
                double opacity = dc.getCurrentLayer() != null
                    ? this.getOpacity() * dc.getCurrentLayer().getOpacity() : this.getOpacity();

                if (opacity < 1)
                {
                    gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_POLYGON_BIT | GL.GL_CURRENT_BIT);
                    // Enable blending using white premultiplied by the current opacity.
                    gl.glColor4d(opacity, opacity, opacity, opacity);
                }
                else
                {
                    gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_POLYGON_BIT);
                }
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
            }
            else
            {
                gl.glPushAttrib(GL.GL_POLYGON_BIT);
            }

            gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
            gl.glEnable(GL.GL_CULL_FACE);
            gl.glCullFace(GL.GL_BACK);

            dc.getGeographicSurfaceTileRenderer().renderTiles(dc, this.thisList);
        }
        finally
        {
            gl.glPopAttrib();
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void initializeSourceTexture(DrawContext dc)
    {
        this.sourceTexture = new LazilyLoadedTexture(this.getImageSource(), true);

        // If this SurfaceImage's is configured with a sector there's no need to generate a texture; we can
        // use the source texture to render the SurfaceImage.
        if (Sector.isSector(this.corners) && this.sector.isSameSector(this.corners))
            this.generatedTexture = this.sourceTexture;
    }

    protected WWTexture initializeGeneratedTexture(DrawContext dc)
    {
        FramebufferTexture t = dc.getGLRuntimeCapabilities().isUseFramebufferObject() ?
            new FBOTexture(this.sourceTexture, this.sector, this.corners)
            : new FramebufferTexture(this.sourceTexture, this.sector, this.corners);

        // Bind the texture to cause it to generate its internal texture.
        t.bind(dc);

        return t;
    }

    // --- Movable interface ---

    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.moveTo(this.getReferencePosition().add(delta));
    }

    public void moveTo(Position position)
    {
        LatLon oldRef = this.getReferencePosition();
        if (oldRef == null)
            return;

        for (int i = 0; i < this.corners.size(); i++)
        {
            LatLon p = this.corners.get(i);
            double distance = LatLon.greatCircleDistance(oldRef, p).radians;
            double azimuth = LatLon.greatCircleAzimuth(oldRef, p).radians;
            LatLon pp = LatLon.greatCircleEndPosition(position, azimuth, distance);
            this.corners.set(i, pp);
        }

        this.setCorners(this.corners);
    }

    public Position getReferencePosition()
    {
        return this.referencePosition;
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected void setReferencePosition(Position referencePosition)
    {
        this.referencePosition = referencePosition;
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || this.getClass() != o.getClass())
            return false;

        SurfaceImage that = (SurfaceImage) o;

        if (this.getImageSource() == null)
            return that.imageSource == null && this.getSector().equals(that.getSector());

        return this.getImageSource().equals(that.getImageSource()) && this.getSector().equals(that.getSector());
    }

    public int hashCode()
    {
        int result;
        result = this.getImageSource() != null ? this.getImageSource().hashCode() : 0;
        result = 31 * result + this.getSector().hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public String isExportFormatSupported(String format)
    {
        if (KMLConstants.KML_MIME_TYPE.equalsIgnoreCase(format))
            return Exportable.FORMAT_SUPPORTED;
        else
            return Exportable.FORMAT_NOT_SUPPORTED;
    }

    /**
     * Export the Surface Image. The {@code output} object will receive the exported data. The type of this object
     * depends on the export format. The formats and object types supported by this class are:
     * <p/>
     * <pre>
     * Format                                         Supported output object types
     * ================================================================================
     * KML (application/vnd.google-earth.kml+xml)     java.io.Writer
     *                                                java.io.OutputStream
     *                                                javax.xml.stream.XMLStreamWriter
     * </pre>
     *
     * @param mimeType MIME type of desired export format.
     * @param output   An object that will receive the exported data. The type of this object depends on the export
     *                 format (see above).
     *
     * @throws java.io.IOException If an exception occurs writing to the output object.
     */
    public void export(String mimeType, Object output) throws IOException
    {
        if (mimeType == null)
        {
            String message = Logging.getMessage("nullValue.Format");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (output == null)
        {
            String message = Logging.getMessage("nullValue.OutputBufferIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (KMLConstants.KML_MIME_TYPE.equalsIgnoreCase(mimeType))
        {
            try
            {
                exportAsKML(output);
            }
            catch (XMLStreamException e)
            {
                Logging.logger().throwing(getClass().getName(), "export", e);
                throw new IOException(e);
            }
        }
        else
        {
            String message = Logging.getMessage("Export.UnsupportedFormat", mimeType);
            Logging.logger().warning(message);
            throw new UnsupportedOperationException(message);
        }
    }

    /**
     * Export the surface image to KML as a {@code <GroundOverlay>} element. The {@code output} object will receive the
     * data. This object must be one of: java.io.Writer java.io.OutputStream javax.xml.stream.XMLStreamWriter
     *
     * @param output Object to receive the generated KML.
     *
     * @throws XMLStreamException If an exception occurs while writing the KML
     * @throws IOException        if an exception occurs while exporting the data.
     * @see #export(String, Object)
     */
    protected void exportAsKML(Object output) throws IOException, XMLStreamException
    {
        XMLStreamWriter xmlWriter = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        boolean closeWriterWhenFinished = true;

        if (output instanceof XMLStreamWriter)
        {
            xmlWriter = (XMLStreamWriter) output;
            closeWriterWhenFinished = false;
        }
        else if (output instanceof Writer)
        {
            xmlWriter = factory.createXMLStreamWriter((Writer) output);
        }
        else if (output instanceof OutputStream)
        {
            xmlWriter = factory.createXMLStreamWriter((OutputStream) output);
        }

        if (xmlWriter == null)
        {
            String message = Logging.getMessage("Export.UnsupportedOutputObject");
            Logging.logger().warning(message);
            throw new IllegalArgumentException(message);
        }

        xmlWriter.writeStartElement("GroundOverlay");

        // Determine the type of the image source. If it's a string or a URL we can write it out. Otherwise there's
        // nothing we can do.
        String imgSourceStr = null;
        Object imgSource = this.getImageSource();
        if (imgSource instanceof String || imgSource instanceof URL)
            imgSourceStr = imgSource.toString();

        if (imgSourceStr != null)
        {
            // Write geometry
            xmlWriter.writeStartElement("Icon");
            xmlWriter.writeStartElement("href");
            xmlWriter.writeCharacters(imgSourceStr);
            xmlWriter.writeEndElement(); // href
            xmlWriter.writeEndElement();  // Icon
        }
        else
        {
            String message = Logging.getMessage("Export.UnableToExportImageSource",
                (imgSource != null ? imgSource.getClass().getName() : "null"));
            Logging.logger().info(message);
        }

        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters("clampToGround");
        xmlWriter.writeEndElement();

        // If the corners of the image are aligned to a sector, we can export the position as a KML LatLonBox. If not,
        // we'll need to use a gx:LatLonQuad.
        if (Sector.isSector(this.corners))
        {
            exportKMLLatLonBox(xmlWriter);
        }
        else
        {
            exportKMLLatLonQuad(xmlWriter);
        }

        xmlWriter.writeEndElement(); // GroundOverlay

        xmlWriter.flush();
        if (closeWriterWhenFinished)
            xmlWriter.close();
    }

    protected void exportKMLLatLonBox(XMLStreamWriter xmlWriter) throws XMLStreamException
    {
        xmlWriter.writeStartElement("LatLonBox");
        xmlWriter.writeStartElement("north");
        xmlWriter.writeCharacters(Double.toString(this.sector.getMaxLatitude().getDegrees()));
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("south");
        xmlWriter.writeCharacters(Double.toString(this.sector.getMinLatitude().getDegrees()));
        xmlWriter.writeEndElement(); // south

        xmlWriter.writeStartElement("east");
        xmlWriter.writeCharacters(Double.toString(this.sector.getMinLongitude().getDegrees()));
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("west");
        xmlWriter.writeCharacters(Double.toString(this.sector.getMaxLongitude().getDegrees()));
        xmlWriter.writeEndElement(); // west
        xmlWriter.writeEndElement(); // LatLonBox
    }

    protected void exportKMLLatLonQuad(XMLStreamWriter xmlWriter) throws XMLStreamException
    {
        xmlWriter.writeStartElement(GXConstants.GX_NAMESPACE, "LatLonQuad");
        xmlWriter.writeStartElement("coordinates");

        for (LatLon ll : this.corners)
        {
            xmlWriter.writeCharacters(Double.toString(ll.getLongitude().getDegrees()));
            xmlWriter.writeCharacters(",");
            xmlWriter.writeCharacters(Double.toString(ll.getLatitude().getDegrees()));
            xmlWriter.writeCharacters(" ");
        }

        xmlWriter.writeEndElement(); // coordinates

        xmlWriter.writeEndElement(); // gx:LatLonQuad
    }
}
