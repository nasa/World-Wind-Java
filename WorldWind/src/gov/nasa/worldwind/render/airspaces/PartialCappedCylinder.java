/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.util.*;

/**
 * A cylinder defined by a geographic position, a radius in meters, and minimum and maximum altitudes.
 *
 * @author tag
 * @version $Id$
 */
public class PartialCappedCylinder extends CappedCylinder
{
    private Angle leftAzimuth = Angle.ZERO;
    private Angle rightAzimuth = Angle.POS360;

    public PartialCappedCylinder(LatLon location, double radius, Angle leftAzimuth, Angle rightAzimuth)
    {
        super(location, radius);

        if (leftAzimuth == null)
        {
            String message = "nullValue.LeftAzimuthIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (rightAzimuth == null)
        {
            String message = "nullValue.RightAzimuthIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.leftAzimuth = leftAzimuth;
        this.rightAzimuth = rightAzimuth;
    }

    public PartialCappedCylinder(LatLon location, double radius)
    {
        super(location, radius);
    }

    public PartialCappedCylinder(AirspaceAttributes attributes)
    {
        super(attributes);
    }

    public PartialCappedCylinder()
    {
    }

    public Angle[] getAzimuths()
    {
        Angle[] array = new Angle[2];
        array[0] = this.leftAzimuth;
        array[1] = this.rightAzimuth;
        return array;
    }

    public void setAzimuths(Angle leftAzimuth, Angle rightAzimuth)
    {
        if (leftAzimuth == null)
        {
            String message = "nullValue.LeftAzimuthIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (rightAzimuth == null)
        {
            String message = "nullValue.RightAzimuthIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.leftAzimuth = leftAzimuth;
        this.rightAzimuth = rightAzimuth;
        this.setExtentOutOfDate();
    }

    protected Box computeExtent(Globe globe, double verticalExaggeration)
    {
        List<Vec4> points = this.computeMinimalGeometry(globe, verticalExaggeration);
        if (points == null || points.isEmpty())
            return null;

        // A bounding box typically provides a better fit for a partial capped cylinder than a bounding cylinder.
        return Box.computeBoundingBox(points);
    }

    @Override
    protected List<Vec4> computeMinimalGeometry(Globe globe, double verticalExaggeration)
    {
        double[] angles = this.computeAngles();
        // Angles are equal, fall back to building a closed cylinder.
        if (angles == null)
            return super.computeMinimalGeometry(globe, verticalExaggeration);

        double[] radii = this.getRadii();
        Matrix transform = this.computeTransform(globe, verticalExaggeration);

        GeometryBuilder gb = this.getGeometryBuilder();
        int count = gb.getPartialDiskVertexCount(MINIMAL_GEOMETRY_SLICES, MINIMAL_GEOMETRY_LOOPS);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        gb.makePartialDiskVertices(
            (float) radii[0], (float) radii[1], // Inner radius, outer radius.
            MINIMAL_GEOMETRY_SLICES, MINIMAL_GEOMETRY_LOOPS, // Slices, loops,
            (float) angles[0], (float) angles[2], // Start angle, sweep angle.
            verts);

        List<LatLon> locations = new ArrayList<LatLon>();
        for (int i = 0; i < numCoords; i += 3)
        {
            Vec4 v = new Vec4(verts[i], verts[i + 1], verts[i + 2]);
            v = v.transformBy4(transform);
            locations.add(globe.computePositionFromPoint(v));
        }

        ArrayList<Vec4> points = new ArrayList<Vec4>();
        this.makeExtremePoints(globe, verticalExaggeration, locations, points);

        return points;
    }

    //**************************************************************//
    //********************  Geometry Rendering  ********************//
    //**************************************************************//

    protected double[] computeAngles()
    {
        // Compute the start and sweep angles such that the partial cylinder shape tranverses a clockwise path from
        // the start angle to the stop angle.
        Angle startAngle, stopAngle, sweepAngle;
        startAngle = normalizedAzimuth(this.leftAzimuth);
        stopAngle = normalizedAzimuth(this.rightAzimuth);

        int i = startAngle.compareTo(stopAngle);
        // Angles are equal, fallback to building a closed cylinder.
        if (i == 0)
            return null;

        if (i < 0)
            sweepAngle = stopAngle.subtract(startAngle);
        else // (i > 0)
            sweepAngle = Angle.POS360.subtract(startAngle).add(stopAngle);

        double[] array = new double[3];
        array[0] = startAngle.radians;
        array[1] = stopAngle.radians;
        array[2] = sweepAngle.radians;
        return array;
    }

    protected Angle normalizedAzimuth(Angle azimuth)
    {
        if (azimuth == null)
        {
            String message = "nullValue.AzimuthIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double degrees = azimuth.degrees;
        double normalizedDegrees = degrees < 0.0 ? degrees + 360.0 : (degrees >= 360.0 ? degrees - 360.0 : degrees);
        return Angle.fromDegrees(normalizedDegrees);
    }

    protected void doRenderGeometry(DrawContext dc, String drawStyle)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double[] angles = this.computeAngles();
        // Angles are equal, fallback to drawing a closed cylinder.
        if (angles == null)
        {
            super.doRenderGeometry(dc, drawStyle);
            return;
        }

        double[] altitudes = this.getAltitudes(dc.getVerticalExaggeration());
        boolean[] terrainConformant = this.isTerrainConforming();
        double[] radii = this.getRadii();
        int slices = this.getSlices();
        int stacks = this.getStacks();
        int loops = this.getLoops();

        if (this.isEnableLevelOfDetail())
        {
            DetailLevel level = this.computeDetailLevel(dc);

            Object o = level.getValue(SLICES);
            if (o != null && o instanceof Integer)
                slices = (Integer) o;

            o = level.getValue(STACKS);
            if (o != null && o instanceof Integer)
                stacks = (Integer) o;

            o = level.getValue(LOOPS);
            if (o != null && o instanceof Integer)
                loops = (Integer) o;

            o = level.getValue(DISABLE_TERRAIN_CONFORMANCE);
            if (o != null && o instanceof Boolean && ((Boolean) o))
                terrainConformant[0] = terrainConformant[1] = false;
        }

        Vec4 referenceCenter = this.computeReferenceCenter(dc);
        this.setExpiryTime(this.nextExpiryTime(dc, terrainConformant));
        this.clearElevationMap();

        GL gl = dc.getGL();
        OGLStackHandler ogsh = new OGLStackHandler();
        try
        {
            dc.getView().pushReferenceCenter(dc, referenceCenter);

            if (Airspace.DRAW_STYLE_OUTLINE.equals(drawStyle))
            {
                this.drawRadialWallOutline(dc, radii, angles[0], altitudes, terrainConformant,
                    loops, stacks, GeometryBuilder.INSIDE, referenceCenter);
                this.drawRadialWallOutline(dc, radii, angles[1], altitudes, terrainConformant,
                    loops, stacks, GeometryBuilder.OUTSIDE, referenceCenter);

                // Outer cylinder isn't rendered if outer radius is zero.
                if (radii[1] != 0.0)
                {
                    this.drawPartialCylinderOutline(dc, radii[1], altitudes, terrainConformant,
                        slices, stacks, GeometryBuilder.OUTSIDE, angles[0], angles[2], referenceCenter);
                }
                // Inner cylinder isn't rendered if inner radius is zero.
                if (radii[0] != 0.0)
                {
                    this.drawPartialCylinderOutline(dc, radii[0], altitudes, terrainConformant,
                        slices, stacks, GeometryBuilder.INSIDE, angles[0], angles[2], referenceCenter);
                }
            }
            else if (Airspace.DRAW_STYLE_FILL.equals(drawStyle))
            {
                if (this.isEnableCaps())
                {
                    ogsh.pushAttrib(gl, GL.GL_POLYGON_BIT);
                    gl.glEnable(GL.GL_CULL_FACE);
                    gl.glFrontFace(GL.GL_CCW);
                }

                if (this.isEnableCaps())
                {
                    // Caps aren't rendered if radii are equal.
                    if (radii[0] != radii[1])
                    {
                        this.drawPartialDisk(dc, radii, altitudes[1], terrainConformant[1],
                            slices, loops, GeometryBuilder.OUTSIDE, angles[0], angles[2], referenceCenter);
                        // Bottom cap isn't rendered if airspace is collapsed.
                        if (!this.isAirspaceCollapsed())
                        {
                            this.drawPartialDisk(dc, radii, altitudes[0], terrainConformant[0],
                                slices, loops, GeometryBuilder.INSIDE, angles[0], angles[2], referenceCenter);
                        }
                    }
                }

                // Cylinders aren't rendered if airspace is collapsed.
                if (!this.isAirspaceCollapsed())
                {
                    this.drawRadialWall(dc, radii, angles[0], altitudes, terrainConformant,
                        loops, stacks, GeometryBuilder.INSIDE, referenceCenter);
                    this.drawRadialWall(dc, radii, angles[1], altitudes, terrainConformant,
                        loops, stacks, GeometryBuilder.OUTSIDE, referenceCenter);

                    // Outer cylinder isn't rendered if outer radius is zero.
                    if (radii[1] != 0.0)
                    {
                        this.drawPartialCylinder(dc, radii[1], altitudes, terrainConformant,
                            slices, stacks, GeometryBuilder.OUTSIDE, angles[0], angles[2], referenceCenter);
                    }
                    // Inner cylinder isn't rendered if inner radius is zero.
                    if (radii[0] != 0.0)
                    {
                        this.drawPartialCylinder(dc, radii[0], altitudes, terrainConformant,
                            slices, stacks, GeometryBuilder.INSIDE, angles[0], angles[2], referenceCenter);
                    }
                }
            }
        }
        finally
        {
            dc.getView().popReferenceCenter(dc);
            ogsh.pop(gl);
        }
    }

    //**************************************************************//
    //********************  Partial Cylinder    ********************//
    //**************************************************************//

    private void drawPartialCylinder(DrawContext dc, double radius, double[] altitudes, boolean[] terrainConformant,
        int slices, int stacks, int orientation,
        double start, double sweep,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createPartialCylinderVertexGeometry(dc, radius, altitudes, terrainConformant,
            slices, stacks, orientation, start, sweep, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "PartialCylinder.Indices",
            slices, stacks, orientation);
        Geometry indexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (indexGeom == null)
        {
            indexGeom = new Geometry();
            this.makePartialCylinderIndices(slices, stacks, orientation, indexGeom);
            this.getGeometryCache().add(cacheKey, indexGeom);
        }

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void drawPartialCylinderOutline(DrawContext dc, double radius, double[] altitudes,
        boolean[] terrainConformant,
        int slices, int stacks, int orientation,
        double start, double sweep,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createPartialCylinderVertexGeometry(dc, radius, altitudes, terrainConformant,
            slices, stacks, orientation, start, sweep, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "PartialCylinder.OutlineIndices",
            slices, stacks, orientation);
        Geometry outlineIndexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (outlineIndexGeom == null)
        {
            outlineIndexGeom = new Geometry();
            this.makePartialCylinderOutlineIndices(slices, stacks, orientation, outlineIndexGeom);
            this.getGeometryCache().add(cacheKey, outlineIndexGeom);
        }

        this.getRenderer().drawGeometry(dc, outlineIndexGeom, vertexGeom);
    }

    private Geometry createPartialCylinderVertexGeometry(DrawContext dc, double radius, double[] altitudes,
        boolean[] terrainConformant, int slices, int stacks, int orientation,
        double start, double sweep,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "PartialCylinder.Vertices",
            radius, altitudes[0], altitudes[1], terrainConformant[0], terrainConformant[1],
            slices, stacks, orientation, start, sweep, referenceCenter);
        Geometry vertexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (vertexGeom == null || this.isExpired(dc, vertexGeom))
        {
            if (vertexGeom == null)
                vertexGeom = new Geometry();
            this.makePartialCylinder(dc, radius, altitudes, terrainConformant,
                slices, stacks, orientation, start, sweep, referenceCenter, vertexGeom);
            this.updateExpiryCriteria(dc, vertexGeom);
            this.getGeometryCache().add(cacheKey, vertexGeom);
        }

        return vertexGeom;
    }

    private void makePartialCylinder(DrawContext dc, double radius, double[] altitudes, boolean[] terrainConformant,
        int slices, int stacks, int orientation,
        double start, double sweep,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);
        float height = (float) (altitudes[1] - altitudes[0]);

        int count = gb.getPartialCylinderVertexCount(slices, stacks);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        float[] norms = new float[numCoords];
        gb.makePartialCylinderVertices((float) radius, height, slices, stacks, (float) start, (float) sweep,
            verts);
        gb.makePartialCylinderNormals((float) radius, height, slices, stacks, (float) start, (float) sweep,
            norms);
        this.makePartialCylinderTerrainConformant(dc, slices, stacks, verts,
            altitudes, terrainConformant, referenceCenter);

        dest.setVertexData(count, verts);
        dest.setNormalData(count, norms);
    }

    private void makePartialCylinderTerrainConformant(DrawContext dc, int slices, int stacks, float[] verts,
        double[] altitudes, boolean[] terrainConformant,
        Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        Matrix transform = this.computeTransform(dc.getGlobe(), dc.getVerticalExaggeration());

        for (int i = 0; i <= slices; i++)
        {
            int index = i * (stacks + 1);
            index = 3 * index;
            Vec4 vec = new Vec4(verts[index], verts[index + 1], verts[index + 2]);
            vec = vec.transformBy4(transform);
            Position p = globe.computePositionFromPoint(vec);

            for (int j = 0; j <= stacks; j++)
            {
                double elevation = altitudes[j];
                if (terrainConformant[j])
                    elevation += this.computeElevationAt(dc, p.getLatitude(), p.getLongitude());
                vec = globe.computePointFromPosition(p.getLatitude(), p.getLongitude(), elevation);

                index = j + i * (stacks + 1);
                index = 3 * index;
                verts[index] = (float) (vec.x - referenceCenter.x);
                verts[index + 1] = (float) (vec.y - referenceCenter.y);
                verts[index + 2] = (float) (vec.z - referenceCenter.z);
            }
        }
    }

    private void makePartialCylinderIndices(int slices, int stacks, int orientation, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getPartialCylinderDrawMode();
        int count = gb.getPartialCylinderIndexCount(slices, stacks);
        int[] indices = new int[count];
        gb.makePartialCylinderIndices(slices, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    private void makePartialCylinderOutlineIndices(int slices, int stacks, int orientation, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getPartialCylinderOutlineDrawMode();
        int count = gb.getPartialCylinderOutlineIndexCount(slices, stacks);
        int[] indices = new int[count];
        gb.makePartialCylinderOutlineIndices(slices, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    //**************************************************************//
    //********************  Partial Disk        ********************//
    //**************************************************************//

    private void drawPartialDisk(DrawContext dc, double[] radii, double altitude, boolean terrainConformant,
        int slices, int loops, int orientation,
        double start, double sweep,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "PartialDisk.Vertices",
            radii[0], radii[1], altitude, terrainConformant,
            slices, loops, orientation, start, sweep, referenceCenter);
        Geometry vertexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (vertexGeom == null || this.isExpired(dc, vertexGeom))
        {
            if (vertexGeom == null)
                vertexGeom = new Geometry();
            this.makePartialDisk(dc, radii, altitude, terrainConformant,
                slices, loops, orientation, start, sweep, referenceCenter, vertexGeom);
            this.updateExpiryCriteria(dc, vertexGeom);
            this.getGeometryCache().add(cacheKey, vertexGeom);
        }

        cacheKey = new Geometry.CacheKey(this.getClass(), "PartialDisk.Indices",
            slices, loops, orientation);
        Geometry indexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (indexGeom == null)
        {
            indexGeom = new Geometry();
            this.makePartialDiskIndices(slices, loops, orientation, indexGeom);
            this.getGeometryCache().add(cacheKey, indexGeom);
        }

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void makePartialDisk(DrawContext dc, double[] radii, double altitude, boolean terrainConformant,
        int slices, int loops, int orientation,
        double start, double sweep,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int count = gb.getPartialDiskIndexCount(slices, loops);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        float[] norms = new float[numCoords];
        gb.makePartialDiskVertices((float) radii[0], (float) radii[1], slices, loops,
            (float) start, (float) sweep, verts);
        this.makePartialDiskTerrainConformant(dc, numCoords, verts, altitude, terrainConformant, referenceCenter);
        gb.makePartialDiskVertexNormals((float) radii[0], (float) radii[1], slices, loops,
            (float) start, (float) sweep, verts, norms);

        dest.setVertexData(count, verts);
        dest.setNormalData(count, norms);
    }

    private void makePartialDiskTerrainConformant(DrawContext dc, int numCoords, float[] verts,
        double altitude, boolean terrainConformant,
        Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        Matrix transform = this.computeTransform(dc.getGlobe(), dc.getVerticalExaggeration());

        for (int i = 0; i < numCoords; i += 3)
        {
            Vec4 vec = new Vec4(verts[i], verts[i + 1], verts[i + 2]);
            vec = vec.transformBy4(transform);
            Position p = globe.computePositionFromPoint(vec);

            double elevation = altitude;
            if (terrainConformant)
                elevation += this.computeElevationAt(dc, p.getLatitude(), p.getLongitude());

            vec = globe.computePointFromPosition(p.getLatitude(), p.getLongitude(), elevation);
            verts[i] = (float) (vec.x - referenceCenter.x);
            verts[i + 1] = (float) (vec.y - referenceCenter.y);
            verts[i + 2] = (float) (vec.z - referenceCenter.z);
        }
    }

    private void makePartialDiskIndices(int slices, int loops, int orientation, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getPartialDiskDrawMode();
        int count = gb.getPartialDiskIndexCount(slices, loops);
        int[] indices = new int[count];
        gb.makePartialDiskIndices(slices, loops, indices);

        dest.setElementData(mode, count, indices);
    }

    //**************************************************************//
    //********************  Radial Wall         ********************//
    //**************************************************************//

    private void drawRadialWall(DrawContext dc, double[] radii, double angle,
        double[] altitudes, boolean[] terrainConformant,
        int pillars, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createRadialWallVertexGeometry(dc, radii, angle, altitudes, terrainConformant,
            pillars, stacks, orientation, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "RadialWall.Indices",
            pillars, stacks, orientation);
        Geometry indexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (indexGeom == null)
        {
            indexGeom = new Geometry();
            this.makeRadialWallIndices(pillars, stacks, orientation, indexGeom);
            this.getGeometryCache().add(cacheKey, indexGeom);
        }

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void drawRadialWallOutline(DrawContext dc, double[] radii, double angle,
        double[] altitudes, boolean[] terrainConformant,
        int pillars, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createRadialWallVertexGeometry(dc, radii, angle, altitudes, terrainConformant,
            pillars, stacks, orientation, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "RadialWall.OutlineIndices",
            pillars, stacks, orientation);
        Geometry outlineIndexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (outlineIndexGeom == null)
        {
            outlineIndexGeom = new Geometry();
            this.makeRadialWallOutlineIndices(pillars, stacks, orientation, outlineIndexGeom);
            this.getGeometryCache().add(cacheKey, outlineIndexGeom);
        }

        this.getRenderer().drawGeometry(dc, outlineIndexGeom, vertexGeom);
    }

    private Geometry createRadialWallVertexGeometry(DrawContext dc, double[] radii, double angle,
        double[] altitudes, boolean[] terrainConformant,
        int pillars, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "RadialWall.Vertices",
            radii[0], radii[1], angle, altitudes[0], altitudes[1], terrainConformant[0], terrainConformant[1],
            pillars, stacks, orientation, referenceCenter);
        Geometry vertexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (vertexGeom == null || this.isExpired(dc, vertexGeom))
        {
            if (vertexGeom == null)
                vertexGeom = new Geometry();
            this.makeRadialWall(dc, radii, angle, altitudes, terrainConformant,
                pillars, stacks, orientation, referenceCenter, vertexGeom);
            this.updateExpiryCriteria(dc, vertexGeom);
            this.getGeometryCache().add(cacheKey, vertexGeom);
        }

        return vertexGeom;
    }

    private void makeRadialWall(DrawContext dc, double[] radii, double angle,
        double[] altitudes, boolean[] terrainConformant,
        int pillars, int stacks, int orientation,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);
        float height = (float) (altitudes[1] - altitudes[0]);

        int count = gb.getRadialWallVertexCount(pillars, stacks);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        float[] norms = new float[numCoords];
        gb.makeRadialWallVertices((float) radii[0], (float) radii[1], height, (float) angle,
            pillars, stacks, verts);
        this.makeRadialWallTerrainConformant(dc, pillars, stacks, verts, altitudes, terrainConformant, referenceCenter);
        gb.makeRadialWallNormals((float) radii[0], (float) radii[1], height, (float) angle,
            pillars, stacks, norms);

        dest.setVertexData(count, verts);
        dest.setNormalData(count, norms);
    }

    private void makeRadialWallTerrainConformant(DrawContext dc, int pillars, int stacks, float[] verts,
        double[] altitudes, boolean[] terrainConformant,
        Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        Matrix transform = this.computeTransform(dc.getGlobe(), dc.getVerticalExaggeration());

        for (int p = 0; p <= pillars; p++)
        {
            int index = p;
            index = 3 * index;
            Vec4 vec = new Vec4(verts[index], verts[index + 1], verts[index + 2]);
            vec = vec.transformBy4(transform);
            Position pos = globe.computePositionFromPoint(vec);

            for (int s = 0; s <= stacks; s++)
            {
                double elevation = altitudes[s];
                if (terrainConformant[s])
                    elevation += this.computeElevationAt(dc, pos.getLatitude(), pos.getLongitude());
                vec = globe.computePointFromPosition(pos.getLatitude(), pos.getLongitude(), elevation);

                index = p + s * (pillars + 1);
                index = 3 * index;
                verts[index] = (float) (vec.x - referenceCenter.x);
                verts[index + 1] = (float) (vec.y - referenceCenter.y);
                verts[index + 2] = (float) (vec.z - referenceCenter.z);
            }
        }
    }

    private void makeRadialWallIndices(int pillars, int stacks, int orientation, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getRadialWallDrawMode();
        int count = gb.getRadialWallIndexCount(pillars, stacks);
        int[] indices = new int[count];
        gb.makeRadialWallIndices(pillars, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    private void makeRadialWallOutlineIndices(int pillars, int stacks, int orientation, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getRadialWallOutlineDrawMode();
        int count = gb.getRadialWallOutlineIndexCount(pillars, stacks);
        int[] indices = new int[count];
        gb.makeRadialWallOutlineIndices(pillars, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    //**************************************************************//
    //********************  END Geometry Rendering  ****************//
    //**************************************************************//

    @Override
    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doGetRestorableState(rs, context);

        rs.addStateValueAsDouble(context, "leftAzimuthDegrees", this.leftAzimuth.degrees);
        rs.addStateValueAsDouble(context, "rightAzimuthDegrees", this.rightAzimuth.degrees);
    }

    @Override
    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doRestoreState(rs, context);

        Double la = rs.getStateValueAsDouble(context, "leftAzimuthDegrees");
        if (la == null)
            la = this.leftAzimuth.degrees;

        Double ra = rs.getStateValueAsDouble(context, "rightAzimuthDegrees");
        if (ra == null)
            ra = this.rightAzimuth.degrees;

        this.setAzimuths(Angle.fromDegrees(la), Angle.fromDegrees(ra));
    }
}
