/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.ogc.kml.gx.GXLatLongQuad;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import java.awt.Color;
import java.io.IOException;
import java.util.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class KMLSurfaceImageImpl extends SurfaceImage implements KMLRenderable
{
    protected KMLGroundOverlay parent;

    protected boolean attributesResolved;

    /**
     * Flag to indicate the rotation must be applied to the SurfaceImage. Rotation is applied the first time that the
     * image is rendered.
     */
    protected boolean mustApplyRotation;

    /**
     * Create an screen image.
     *
     * @param tc      the current {@link KMLTraversalContext}.
     * @param overlay the <i>Overlay</i> element containing.
     *
     * @throws NullPointerException     if the traversal context is null.
     * @throws IllegalArgumentException if the parent overlay or the traversal context is null.
     */
    public KMLSurfaceImageImpl(KMLTraversalContext tc, KMLGroundOverlay overlay)
    {
        this.parent = overlay;

        if (tc == null)
        {
            String msg = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (overlay == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // Positions are specified either as a kml:LatLonBox or a gx:LatLonQuad
        KMLLatLonBox box = overlay.getLatLonBox();
        if (box != null)
        {
            Sector sector = KMLUtil.createSectorFromLatLonBox(box);
            this.initializeGeometry(sector);

            // Check to see if a rotation is provided. The rotation will be applied when the image is rendered, because
            // how the rotation is performed depends on the globe.
            Double rotation = box.getRotation();
            if (rotation != null)
            {
                this.mustApplyRotation = true;
            }
        }
        else
        {
            GXLatLongQuad latLonQuad = overlay.getLatLonQuad();
            if (latLonQuad != null && latLonQuad.getCoordinates() != null)
            {
                this.initializeGeometry(latLonQuad.getCoordinates().list);
            }
        }

        // Apply opacity to the surface image
        String colorStr = overlay.getColor();
        if (!WWUtil.isEmpty(colorStr))
        {
            Color color = WWUtil.decodeColorABGR(colorStr);
            int alpha = color.getAlpha();

            this.setOpacity((double) alpha / 255);
        }

        this.setPickEnabled(false);
    }

    public void preRender(KMLTraversalContext tc, DrawContext dc)
    {
        if (this.mustResolveHref()) // resolve the href to either a local file or a remote URL
        {
            // The icon reference may be to a support file within a KMZ file, so check for that. If it's not, then just
            // let the normal SurfaceImage code resolve the reference.
            String href = this.parent.getIcon().getHref();
            String localAddress = null;
            try
            {
                localAddress = this.parent.getRoot().getSupportFilePath(href);
            }
            catch (IOException ignored)
            {
            }

            this.setImageSource((localAddress != null ? localAddress : href), this.getCorners());
        }

        // Apply rotation the first time the overlay is rendered
        if (this.mustApplyRotation)
        {
            this.applyRotation(dc);
            this.mustApplyRotation = false;
        }

        super.preRender(dc);
    }

    protected boolean mustResolveHref()
    {
        return this.getImageSource() == null
            && this.parent.getIcon() != null
            && this.parent.getIcon().getHref() != null;
    }

    /** {@inheritDoc} */
    public void render(KMLTraversalContext tc, DrawContext dc)
    {
        // We've already resolved the SurfaceImage's attributes during the preRender pass. During the render pass we
        // simply draw the SurfaceImage.
        super.render(dc);
    }

    /**
     * Apply a rotation to the corner points of the overlay.
     *
     * @param dc Current draw context.
     */
    protected void applyRotation(DrawContext dc)
    {
        KMLLatLonBox box = this.parent.getLatLonBox();
        if (box != null)
        {
            Double rotation = box.getRotation();
            if (rotation != null)
            {
                List<LatLon> corners = this.computeRotatedCorners(dc, this.getSector(), Angle.fromDegrees(rotation));
                this.setCorners(corners);
            }
        }
    }

    /**
     * Rotate the corners of the overlay region. Rotation is performed around a surface normal through the center of the
     * overlay sector.
     *
     * @param dc       Current draw context.
     * @param sector   Sector that defines the overlay region.
     * @param rotation Rotation angle. Positive angles produce counterclockwise rotation.
     *
     * @return Rotated corners.
     */
    protected java.util.List<LatLon> computeRotatedCorners(DrawContext dc, Sector sector, Angle rotation)
    {
        LatLon[] corners = sector.getCorners();
        List<LatLon> transformedCorners = new ArrayList<LatLon>(corners.length);

        Globe globe = dc.getGlobe();

        // Using the four corners of the sector to compute the rotation axis avoids any problems with dateline
        // spanning polygons.
        Vec4[] verts = sector.computeCornerPoints(globe, 1);
        Vec4 normalVec = verts[2].subtract3(verts[0]).cross3(verts[3].subtract3(verts[1])).normalize3();
        Matrix rotationMatrix = Matrix.fromAxisAngle(rotation, normalVec);

        Vec4 centerPoint = sector.computeCenterPoint(globe, 1);

        // Rotate each point around the surface normal, and convert back to geographic
        for (Vec4 point : verts)
        {
            point = point.subtract3(centerPoint).transformBy3(rotationMatrix).add3(centerPoint);
            LatLon ll = globe.computePositionFromPoint(point);

            transformedCorners.add(ll);
        }

        return transformedCorners;
    }
}
