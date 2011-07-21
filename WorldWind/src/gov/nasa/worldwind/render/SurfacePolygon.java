/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.Exportable;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil;
import gov.nasa.worldwind.util.*;

import javax.xml.stream.*;
import java.io.*;
import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class SurfacePolygon extends AbstractSurfaceShape implements Exportable
{
    protected List<Iterable<? extends LatLon>> boundaries = new ArrayList<Iterable<? extends LatLon>>();

    /** Constructs a new surface polygon with the default attributes and no locations. */
    public SurfacePolygon()
    {
    }

    /**
     * Constructs a new surface polygon with the specified normal (as opposed to highlight) attributes and no locations.
     * Modifying the attribute reference after calling this constructor causes this shape's appearance to change
     * accordingly.
     *
     * @param normalAttrs the normal attributes. May be null, in which case default attributes are used.
     */
    public SurfacePolygon(ShapeAttributes normalAttrs)
    {
        super(normalAttrs);
    }

    /**
     * Constructs a new surface polygon with the default attributes and the specified iterable of locations.
     * <p/>
     * Note: If fewer than three locations is specified, no polygon is drawn.
     *
     * @param iterable the polygon locations.
     *
     * @throws IllegalArgumentException if the locations iterable is null.
     */
    public SurfacePolygon(Iterable<? extends LatLon> iterable)
    {
        if (iterable == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setOuterBoundary(iterable);
    }

    /**
     * Constructs a new surface polygon with the specified normal (as opposed to highlight) attributes and the specified
     * iterable of locations. Modifying the attribute reference after calling this constructor causes this shape's
     * appearance to change accordingly.
     * <p/>
     * Note: If fewer than three locations is specified, no polygon is drawn.
     *
     * @param normalAttrs the normal attributes. May be null, in which case default attributes are used.
     * @param iterable    the polygon locations.
     *
     * @throws IllegalArgumentException if the locations iterable is null.
     */
    public SurfacePolygon(ShapeAttributes normalAttrs, Iterable<? extends LatLon> iterable)
    {
        super(normalAttrs);

        if (iterable == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setOuterBoundary(iterable);
    }

    public Iterable<? extends LatLon> getLocations(Globe globe)
    {
        return this.getOuterBoundary();
    }

    public Iterable<? extends LatLon> getLocations()
    {
        return this.getOuterBoundary();
    }

    public void setLocations(Iterable<? extends LatLon> iterable)
    {
        if (iterable == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setOuterBoundary(iterable);
    }

    public Iterable<? extends LatLon> getOuterBoundary()
    {
        return this.boundaries.size() > 0 ? this.boundaries.get(0) : null;
    }

    public void setOuterBoundary(Iterable<? extends LatLon> iterable)
    {
        if (iterable == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.boundaries.size() > 0)
            this.boundaries.set(0, iterable);
        else
            this.boundaries.add(iterable);

        this.onShapeChanged();
    }

    public void addInnerBoundary(Iterable<? extends LatLon> iterable)
    {
        if (iterable == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.boundaries.add(iterable);
        this.onShapeChanged();
    }

    public Position getReferencePosition()
    {
        if (this.getOuterBoundary() == null)
            return null;

        Iterator<? extends LatLon> iterator = this.getOuterBoundary().iterator();
        if (!iterator.hasNext())
            return null;

        return new Position(iterator.next(), 0);
    }

    protected List<List<LatLon>> createGeometry(Globe globe, SurfaceTileDrawContext sdc)
    {
        if (this.boundaries.isEmpty())
            return null;

        ArrayList<List<LatLon>> geom = new ArrayList<List<LatLon>>();
        double edgeIntervalsPerDegree = this.computeEdgeIntervalsPerDegree(sdc);

        for (Iterable<? extends LatLon> boundary : this.boundaries)
        {
            ArrayList<LatLon> drawLocations = new ArrayList<LatLon>();

            this.generateIntermediateLocations(boundary, edgeIntervalsPerDegree, true, drawLocations);

            // Ensure all contours have counter-clockwise winding order. The GLU tessellator we'll use to tessellate
            // these contours is configured to recognize interior holes when all contours have counter clockwise winding
            // order.
            //noinspection StringEquality
            if (WWMath.computeWindingOrderOfLocations(drawLocations) != AVKey.COUNTER_CLOCKWISE)
                Collections.reverse(drawLocations);

            geom.add(drawLocations);
        }

        if (geom.isEmpty() || geom.get(0).size() < 3)
            return null;

        return geom;
    }

    protected void doMoveTo(Position oldReferencePosition, Position newReferencePosition)
    {
        if (this.boundaries.isEmpty())
            return;

        for (int i = 0; i < this.boundaries.size(); i++)
        {
            ArrayList<LatLon> newLocations = new ArrayList<LatLon>();

            for (LatLon ll : this.boundaries.get(i))
            {
                Angle heading = LatLon.greatCircleAzimuth(oldReferencePosition, ll);
                Angle pathLength = LatLon.greatCircleDistance(oldReferencePosition, ll);
                newLocations.add(LatLon.greatCircleEndPosition(newReferencePosition, heading, pathLength));
            }

            this.boundaries.set(i, newLocations);
        }

        // We've changed the polygon's list of boundaries; flag the shape as changed.
        this.onShapeChanged();
    }

    //**************************************************************//
    //********************  Interior Tessellation  *****************//
    //**************************************************************//

    /**
     * Overridden to clear the polygon's locations iterable upon an unsuccessful tessellation attempt. This ensures the
     * polygon won't attempt to re-tessellate itself each frame.
     *
     * @param dc the current DrawContext.
     */
    @Override
    protected void handleUnsuccessfulInteriorTessellation(DrawContext dc)
    {
        super.handleUnsuccessfulInteriorTessellation(dc);

        // If tessellating the polygon's interior was unsuccessful, we modify the polygon's to avoid any additional
        // tessellation attempts, and free any resources that the polygon won't use. This is accomplished by clearing
        // the polygon's boundary list.
        this.boundaries.clear();
        this.onShapeChanged();
    }

    //**************************************************************//
    //******************** Restorable State  ***********************//
    //**************************************************************//

    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doGetRestorableState(rs, context);

        if (!this.boundaries.isEmpty())
        {
            RestorableSupport.StateObject so = rs.addStateObject(context, "boundaries");
            for (Iterable<? extends LatLon> boundary : this.boundaries)
            {
                rs.addStateValueAsLatLonList(so, "boundary", boundary);
            }
        }
    }

    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doRestoreState(rs, context);

        RestorableSupport.StateObject so = rs.getStateObject(context, "boundaries");
        if (so != null)
        {
            this.boundaries.clear();

            RestorableSupport.StateObject[] sos = rs.getAllStateObjects(so, "boundary");
            if (sos != null)
            {
                for (RestorableSupport.StateObject boundary : sos)
                {
                    if (boundary == null)
                        continue;

                    Iterable<LatLon> locations = rs.getStateObjectAsLatLonList(boundary);
                    if (locations != null)
                        this.boundaries.add(locations);
                }
            }

            // We've changed the polygon's list of boundaries; flag the shape as changed.
            this.onShapeChanged();
        }
    }

    protected void legacyRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.legacyRestoreState(rs, context);

        Iterable<LatLon> locations = rs.getStateValueAsLatLonList(context, "locationList");

        if (locations == null)
            locations = rs.getStateValueAsLatLonList(context, "locations");

        if (locations != null)
            this.setOuterBoundary(locations);
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
     * Export the Polygon. The {@code output} object will receive the exported data. The type of this object depends on
     * the export format. The formats and object types supported by this class are:
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
     * Export the polygon to KML as a {@code <Placemark>} element. The {@code output} object will receive the data. This
     * object must be one of: java.io.Writer java.io.OutputStream javax.xml.stream.XMLStreamWriter
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

        xmlWriter.writeStartElement("Placemark");

        String property = (String) getValue(AVKey.DISPLAY_NAME);
        if (property != null)
        {
            xmlWriter.writeStartElement("name");
            xmlWriter.writeCharacters(property);
            xmlWriter.writeEndElement();
        }

        xmlWriter.writeStartElement("visibility");
        xmlWriter.writeCharacters(KMLExportUtil.kmlBoolean(this.isVisible()));
        xmlWriter.writeEndElement();

        String shortDescription = (String) getValue(AVKey.SHORT_DESCRIPTION);
        if (shortDescription != null)
        {
            xmlWriter.writeStartElement("Snippet");
            xmlWriter.writeCharacters(shortDescription);
            xmlWriter.writeEndElement();
        }

        String description = (String) getValue(AVKey.BALLOON_TEXT);
        if (description != null)
        {
            xmlWriter.writeStartElement("description");
            xmlWriter.writeCharacters(description);
            xmlWriter.writeEndElement();
        }

        // KML does not allow separate attributes for cap and side, so just use the side attributes.
        final ShapeAttributes normalAttributes = getAttributes();
        final ShapeAttributes highlightAttributes = getHighlightAttributes();

        // Write style map
        if (normalAttributes != null || highlightAttributes != null)
        {
            xmlWriter.writeStartElement("StyleMap");
            KMLExportUtil.exportAttributesAsKML(xmlWriter, KMLConstants.NORMAL, normalAttributes);
            KMLExportUtil.exportAttributesAsKML(xmlWriter, KMLConstants.HIGHLIGHT, highlightAttributes);
            xmlWriter.writeEndElement(); // StyleMap
        }

        // Write geometry
        xmlWriter.writeStartElement("Polygon");

        xmlWriter.writeStartElement("extrude");
        xmlWriter.writeCharacters("0");
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters("clampToGround");
        xmlWriter.writeEndElement();

        // Outer boundary
        Iterable<? extends LatLon> outerBoundary = this.getOuterBoundary();
        if (outerBoundary != null)
        {
            xmlWriter.writeStartElement("outerBoundaryIs");
            KMLExportUtil.exportBoundaryAsLinearRing(xmlWriter, outerBoundary, null);
            xmlWriter.writeEndElement(); // outerBoundaryIs
        }

        // Inner boundaries
        Iterator<Iterable<? extends LatLon>> boundaryIterator = boundaries.iterator();
        if (boundaryIterator.hasNext())
            boundaryIterator.next(); // Skip outer boundary, we already dealt with it above

        while (boundaryIterator.hasNext())
        {
            Iterable<? extends LatLon> boundary = boundaryIterator.next();

            xmlWriter.writeStartElement("innerBoundaryIs");
            KMLExportUtil.exportBoundaryAsLinearRing(xmlWriter, boundary, null);
            xmlWriter.writeEndElement(); // innerBoundaryIs
        }

        xmlWriter.writeEndElement(); // Polygon
        xmlWriter.writeEndElement(); // Placemark

        xmlWriter.flush();
        if (closeWriterWhenFinished)
            xmlWriter.close();
    }
}
