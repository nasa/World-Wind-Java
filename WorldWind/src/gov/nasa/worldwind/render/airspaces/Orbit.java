/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class Orbit extends AbstractAirspace
{
    public interface OrbitType
    {
        public static final String LEFT = "Left";
        public static final String CENTER = "Center";
        public static final String RIGHT = "Right";
    }

    protected static final int DEFAULT_ARC_SLICES = 16;
    protected static final int DEFAULT_LENGTH_SLICES = 32;
    protected static final int DEFAULT_STACKS = 1;
    protected static final int DEFAULT_LOOPS = 4;
    protected static final int MINIMAL_GEOMETRY_ARC_SLICES = 4;
    protected static final int MINIMAL_GEOMETRY_LENGTH_SLICES = 8;
    protected static final int MINIMAL_GEOMETRY_LOOPS = 2;

    private LatLon location1 = LatLon.ZERO;
    private LatLon location2 = LatLon.ZERO;
    private String orbitType = OrbitType.CENTER;
    private double width = 1.0;
    private boolean enableCaps = true;
    // Geometry.
    private int arcSlices = DEFAULT_ARC_SLICES;
    private int lengthSlices = DEFAULT_LENGTH_SLICES;
    private int stacks = DEFAULT_STACKS;
    private int loops = DEFAULT_LOOPS;

    public Orbit(LatLon location1, LatLon location2, String orbitType, double width)
    {
        if (location1 == null)
        {
            String message = "nullValue.Location1IsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (location2 == null)
        {
            String message = "nullValue.Location2IsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (orbitType == null)
        {
            String message = "nullValue.OrbitTypeIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width=" + width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.location1 = location1;
        this.location2 = location2;
        this.orbitType = orbitType;
        this.width = width;
        this.makeDefaultDetailLevels();
    }

    public Orbit(AirspaceAttributes attributes)
    {
        super(attributes);
        this.makeDefaultDetailLevels();
    }

    public Orbit()
    {
        this.makeDefaultDetailLevels();
    }

    private void makeDefaultDetailLevels()
    {
        List<DetailLevel> levels = new ArrayList<DetailLevel>();
        double[] ramp = ScreenSizeDetailLevel.computeDefaultScreenSizeRamp(5);

        DetailLevel level;
        level = new ScreenSizeDetailLevel(ramp[0], "Detail-Level-0");
        level.setValue(ARC_SLICES, 16);
        level.setValue(LENGTH_SLICES, 32);
        level.setValue(STACKS, 1);
        level.setValue(LOOPS, 4);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[1], "Detail-Level-1");
        level.setValue(ARC_SLICES, 13);
        level.setValue(LENGTH_SLICES, 25);
        level.setValue(STACKS, 1);
        level.setValue(LOOPS, 3);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[2], "Detail-Level-2");
        level.setValue(ARC_SLICES, 10);
        level.setValue(LENGTH_SLICES, 18);
        level.setValue(STACKS, 1);
        level.setValue(LOOPS, 2);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[3], "Detail-Level-3");
        level.setValue(ARC_SLICES, 7);
        level.setValue(LENGTH_SLICES, 11);
        level.setValue(STACKS, 1);
        level.setValue(LOOPS, 1);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[4], "Detail-Level-4");
        level.setValue(ARC_SLICES, 4);
        level.setValue(LENGTH_SLICES, 4);
        level.setValue(STACKS, 1);
        level.setValue(LOOPS, 1);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, true);
        levels.add(level);

        this.setDetailLevels(levels);
    }

    public LatLon[] getLocations()
    {
        LatLon[] array = new LatLon[2];
        array[0] = this.location1;
        array[1] = this.location2;
        return array;
    }

    public void setLocations(LatLon location1, LatLon location2)
    {
        if (location1 == null)
        {
            String message = "nullValue.Location1IsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (location2 == null)
        {
            String message = "nullValue.Location2IsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.location1 = location1;
        this.location2 = location2;
        this.setExtentOutOfDate();
    }

    public String getOrbitType()
    {
        return this.orbitType;
    }

    public void setOrbitType(String orbitType)
    {
        if (orbitType == null)
        {
            String message = "nullValue.OrbitTypeIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.orbitType = orbitType;
        this.setExtentOutOfDate();
    }

    public double getWidth()
    {
        return this.width;
    }

    public void setWidth(double width)
    {
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width=" + width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.width = width;
        this.setExtentOutOfDate();
    }

    public boolean isEnableCaps()
    {
        return this.enableCaps;
    }

    public void setEnableCaps(boolean enable)
    {
        this.enableCaps = enable;
    }

    public Position getReferencePosition()
    {
        double[] altitudes = this.getAltitudes();
        return new Position(this.location1, altitudes[0]);
    }

    protected Extent computeExtent(Globe globe, double verticalExaggeration)
    {
        List<Vec4> points = this.computeMinimalGeometry(globe, verticalExaggeration);
        if (points == null || points.isEmpty())
            return null;

        return Box.computeBoundingBox(points);
    }

    @Override
    protected List<Vec4> computeMinimalGeometry(Globe globe, double verticalExaggeration)
    {
        Matrix transform = this.computeTransform(globe, verticalExaggeration);

        Vec4 point1 = globe.computePointFromPosition(this.location1.getLatitude(), this.location1.getLongitude(), 0.0);
        Vec4 point2 = globe.computePointFromPosition(this.location2.getLatitude(), this.location2.getLongitude(), 0.0);
        double radius = this.width / 2.0;
        double length = point1.distanceTo3(point2);

        GeometryBuilder gb = this.getGeometryBuilder();
        int count = gb.getLongDiskIndexCount(MINIMAL_GEOMETRY_ARC_SLICES, MINIMAL_GEOMETRY_LENGTH_SLICES,
            MINIMAL_GEOMETRY_LOOPS);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        gb.makeLongDiskVertices(0f, (float) radius, // Inner radius, outer radius.
            (float) length, // Length
            MINIMAL_GEOMETRY_ARC_SLICES, MINIMAL_GEOMETRY_LENGTH_SLICES, MINIMAL_GEOMETRY_LOOPS,
            // Arc slices, length slices, loops.
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

    protected void doMoveTo(Position oldRef, Position newRef)
    {
        if (oldRef == null)
        {
            String message = "nullValue.OldRefIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (newRef == null)
        {
            String message = "nullValue.NewRefIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        super.doMoveTo(oldRef, newRef);

        LatLon[] locations = this.getLocations();
        int count = locations.length;
        for (int i = 0; i < count; i++)
        {
            double distance = LatLon.greatCircleDistance(oldRef, locations[i]).radians;
            double azimuth = LatLon.greatCircleAzimuth(oldRef, locations[i]).radians;
            locations[i] = LatLon.greatCircleEndPosition(newRef, azimuth, distance);
        }
        this.setLocations(locations[0], locations[1]);
    }

    protected int getArcSlices()
    {
        return this.arcSlices;
    }

    protected void setArcSlices(int arcSlices)
    {
        if (arcSlices < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "arcSlices=" + arcSlices);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.arcSlices = arcSlices;
    }

    protected int getLengthSlices()
    {
        return this.lengthSlices;
    }

    protected void setLengthSlices(int lengthSlices)
    {
        if (lengthSlices < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "lengthSlices=" + lengthSlices);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.lengthSlices = lengthSlices;
    }

    protected int getStacks()
    {
        return this.stacks;
    }

    protected int getLoops()
    {
        return this.loops;
    }

    protected void setLoops(int loops)
    {
        if (loops < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "loops=" + loops);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.loops = loops;
    }

    //**************************************************************//
    //********************  Geometry Rendering  ********************//
    //**************************************************************//

    protected Vec4 computeReferenceCenter(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGlobe() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Globe globe = dc.getGlobe();
        double[] altitudes = this.getAltitudes(dc.getVerticalExaggeration());
        Vec4 point1 = globe.computePointFromPosition(
            this.location1.getLatitude(), this.location1.getLongitude(), altitudes[0]);
        Vec4 point2 = globe.computePointFromPosition(
            this.location2.getLatitude(), this.location2.getLongitude(), altitudes[0]);
        Vec4 centerPoint = Vec4.mix3(0.5, point1, point2);
        Position centerPos = globe.computePositionFromPoint(centerPoint);
        return globe.computePointFromPosition(centerPos.getLatitude(), centerPos.getLongitude(), altitudes[0]);
    }

    protected Matrix computeTransform(Globe globe, double verticalExaggeration)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double[] altitudes = this.getAltitudes(verticalExaggeration);
        double radius = this.width / 2.0;

        Vec4 point1 = globe.computePointFromPosition(
            this.location1.getLatitude(), this.location1.getLongitude(), altitudes[0]);
        Vec4 point2 = globe.computePointFromPosition(
            this.location2.getLatitude(), this.location2.getLongitude(), altitudes[0]);
        Vec4 centerPoint = Vec4.mix3(0.5, point1, point2);
        Vec4 upVec = globe.computeSurfaceNormalAtPoint(centerPoint);
        Vec4 axis = point2.subtract3(point1);
        axis = axis.normalize3();

        Matrix transform = Matrix.fromModelLookAt(point1, point1.add3(upVec), axis);
        if (OrbitType.LEFT.equals(this.orbitType))
            transform = transform.multiply(Matrix.fromTranslation(-radius, 0.0, 0.0));
        else if (OrbitType.RIGHT.equals(this.orbitType))
            transform = transform.multiply(Matrix.fromTranslation(radius, 0.0, 0.0));

        return transform;
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
        if (dc.getGlobe() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double[] altitudes = this.getAltitudes(dc.getVerticalExaggeration());
        boolean[] terrainConformant = this.isTerrainConforming();
        double[] radii = new double[] {0.0, this.width / 2};
        String type = this.orbitType;
        int arcSlices = this.arcSlices;
        int lengthSlices = this.lengthSlices;
        int stacks = this.stacks;
        int loops = this.loops;

        Globe globe = dc.getGlobe();
        Vec4 point1 = globe.computePointFromPosition(
            this.location1.getLatitude(), this.location1.getLongitude(), altitudes[0]);
        Vec4 point2 = globe.computePointFromPosition(
            this.location2.getLatitude(), this.location2.getLongitude(), altitudes[0]);
        double length = point1.distanceTo3(point2);

        if (this.isEnableLevelOfDetail())
        {
            DetailLevel level = this.computeDetailLevel(dc);

            Object o = level.getValue(ARC_SLICES);
            if (o != null && o instanceof Integer)
                arcSlices = (Integer) o;

            o = level.getValue(LENGTH_SLICES);
            if (o != null && o instanceof Integer)
                lengthSlices = (Integer) o;

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
                this.drawLongCylinderOutline(dc, radii[1], length, altitudes, terrainConformant, type,
                    arcSlices, lengthSlices, stacks, GeometryBuilder.OUTSIDE, referenceCenter);
            }
            else if (Airspace.DRAW_STYLE_FILL.equals(drawStyle))
            {
                if (this.enableCaps)
                {
                    ogsh.pushAttrib(gl, GL.GL_POLYGON_BIT);
                    gl.glEnable(GL.GL_CULL_FACE);
                    gl.glFrontFace(GL.GL_CCW);
                }

                if (this.enableCaps)
                {
                    // Caps aren't rendered if radii are equal.
                    if (radii[0] != radii[1])
                    {
                        this.drawLongDisk(dc, radii, length, altitudes[1], terrainConformant[1], type,
                            arcSlices, lengthSlices, loops, GeometryBuilder.OUTSIDE, referenceCenter);
                        // Bottom cap isn't rendered if airspace is collapsed.
                        if (!this.isAirspaceCollapsed())
                        {
                            this.drawLongDisk(dc, radii, length, altitudes[0], terrainConformant[0], type,
                                arcSlices, lengthSlices, loops, GeometryBuilder.INSIDE, referenceCenter);
                        }
                    }
                }

                // Long cylinder isn't rendered if airspace is collapsed.
                if (!this.isAirspaceCollapsed())
                {
                    this.drawLongCylinder(dc, radii[1], length, altitudes, terrainConformant, type,
                        arcSlices, lengthSlices, stacks, GeometryBuilder.OUTSIDE, referenceCenter);
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
    //********************  Long Cylinder       ********************//
    //**************************************************************//

    private void drawLongCylinder(DrawContext dc,
        double radius, double length, double[] altitudes, boolean[] terrainConformant,
        String type,
        int arcSlices, int lengthSlices, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createLongCylinderVertexGeometry(dc, radius, length, altitudes, terrainConformant, type,
            arcSlices, lengthSlices, stacks, orientation, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "LongCylinder.Indices",
            arcSlices, lengthSlices, stacks, orientation);
        Geometry indexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (indexGeom == null)
        {
            indexGeom = new Geometry();
            this.makeLongCylinderIndices(arcSlices, lengthSlices, stacks, orientation, indexGeom);
            this.getGeometryCache().add(cacheKey, indexGeom);
        }

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void drawLongCylinderOutline(DrawContext dc,
        double radius, double length, double[] altitudes, boolean[] terrainConformant,
        String type,
        int arcSlices, int lengthSlices, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Geometry vertexGeom = createLongCylinderVertexGeometry(dc, radius, length, altitudes, terrainConformant, type,
            arcSlices, lengthSlices, stacks, orientation, referenceCenter);

        Object cacheKey = new Geometry.CacheKey(this.getClass(), "LongCylinder.OutlineIndices",
            arcSlices, lengthSlices, stacks, orientation);
        Geometry outlineIndexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (outlineIndexGeom == null)
        {
            outlineIndexGeom = new Geometry();
            this.makeLongCylinderOutlineIndices(arcSlices, lengthSlices, stacks, orientation, outlineIndexGeom);
            this.getGeometryCache().add(cacheKey, outlineIndexGeom);
        }

        this.getRenderer().drawGeometry(dc, outlineIndexGeom, vertexGeom);
    }

    private Geometry createLongCylinderVertexGeometry(DrawContext dc, double radius, double length,
        double[] altitudes, boolean[] terrainConformant, String type,
        int arcSlices, int lengthSlices, int stacks, int orientation,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "LongCylinder.Vertices",
            radius, length, altitudes[0], altitudes[1], terrainConformant[0], terrainConformant[1], type,
            arcSlices, lengthSlices, stacks, orientation, referenceCenter);
        Geometry vertexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (vertexGeom == null || this.isExpired(dc, vertexGeom))
        {
            if (vertexGeom == null)
                vertexGeom = new Geometry();
            this.makeLongCylinder(dc, radius, length, altitudes, terrainConformant,
                arcSlices, lengthSlices, stacks, orientation, referenceCenter, vertexGeom);
            this.updateExpiryCriteria(dc, vertexGeom);
            this.getGeometryCache().add(cacheKey, vertexGeom);
        }

        return vertexGeom;
    }

    private void makeLongCylinder(DrawContext dc,
        double radius, double length, double[] altitudes, boolean[] terrainConformant,
        int arcSlices, int lengthSlices, int stacks, int orientation,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);
        float height = (float) (altitudes[1] - altitudes[0]);

        int count = gb.getLongCylinderVertexCount(arcSlices, lengthSlices, stacks);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        float[] norms = new float[numCoords];
        gb.makeLongCylinderVertices((float) radius, (float) length, height,
            arcSlices, lengthSlices, stacks, verts);
        this.makeLongCylinderTerrainConformant(dc, arcSlices, lengthSlices, stacks, verts,
            altitudes, terrainConformant, referenceCenter);
        gb.makeLongCylinderNormals(arcSlices, lengthSlices, stacks, norms);

        dest.setVertexData(count, verts);
        dest.setNormalData(count, norms);
    }

    private void makeLongCylinderTerrainConformant(DrawContext dc, int arcSlices, int lengthSlices, int stacks,
        float[] verts, double[] altitudes, boolean[] terrainConformant,
        Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        Matrix transform = this.computeTransform(dc.getGlobe(), dc.getVerticalExaggeration());
        int slices = 2 * (arcSlices + 1) + 2 * (lengthSlices - 1);

        for (int i = 0; i < slices; i++)
        {
            int index = i;
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

                index = i + j * slices;
                index = 3 * index;
                verts[index] = (float) (vec.x - referenceCenter.x);
                verts[index + 1] = (float) (vec.y - referenceCenter.y);
                verts[index + 2] = (float) (vec.z - referenceCenter.z);
            }
        }
    }

    private void makeLongCylinderIndices(int arcSlices, int lengthSlices, int stacks, int orientation,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getLongCylinderDrawMode();
        int count = gb.getLongCylinderIndexCount(arcSlices, lengthSlices, stacks);
        int[] indices = new int[count];
        gb.makeLongCylinderIndices(arcSlices, lengthSlices, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    private void makeLongCylinderOutlineIndices(int arcSlices, int lengthSlices, int stacks, int orientation,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getLongCylinderOutlineDrawMode();
        int count = gb.getLongCylinderOutlineIndexCount(arcSlices, lengthSlices, stacks);
        int[] indices = new int[count];
        gb.makeLongCylinderOutlineIndices(arcSlices, lengthSlices, stacks, indices);

        dest.setElementData(mode, count, indices);
    }

    //**************************************************************//
    //********************  Long Disk           ********************//
    //**************************************************************//

    private void drawLongDisk(DrawContext dc,
        double[] radii, double length, double altitudes, boolean terrainConformant, String type,
        int arcSlices, int lengthSlices, int loops, int orientation,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "LongDisk.Vertices",
            radii[0], radii[1], length, altitudes, terrainConformant, type,
            arcSlices, lengthSlices, loops, orientation,
            referenceCenter);
        Geometry vertexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (vertexGeom == null || this.isExpired(dc, vertexGeom))
        {
            if (vertexGeom == null)
                vertexGeom = new Geometry();
            this.makeLongDisk(dc, radii, length, altitudes, terrainConformant,
                arcSlices, lengthSlices, loops, orientation, referenceCenter, vertexGeom);
            this.updateExpiryCriteria(dc, vertexGeom);
            this.getGeometryCache().add(cacheKey, vertexGeom);
        }

        cacheKey = new Geometry.CacheKey(this.getClass(), "LongDisk.Indices",
            arcSlices, lengthSlices, loops, orientation);
        Geometry indexGeom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (indexGeom == null)
        {
            indexGeom = new Geometry();
            this.makeLongDiskIndices(arcSlices, lengthSlices, loops, orientation, indexGeom);
            this.getGeometryCache().add(cacheKey, indexGeom);
        }

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void makeLongDisk(DrawContext dc,
        double[] radii, double length, double altitudes, boolean terrainConformant,
        int arcSlices, int lengthSlices, int loops, int orientation,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int count = gb.getLongDiskVertexCount(arcSlices, lengthSlices, loops);
        int numCoords = 3 * count;
        float[] verts = new float[numCoords];
        float[] norms = new float[numCoords];
        gb.makeLongDiskVertices((float) radii[0], (float) radii[1], (float) length,
            arcSlices, lengthSlices, loops, verts);
        this.makeLongDiskTerrainConformant(dc, numCoords, verts,
            altitudes, terrainConformant, referenceCenter);
        gb.makeLongDiskVertexNormals((float) radii[0], (float) radii[1], (float) length,
            arcSlices, lengthSlices, loops, verts, norms);

        dest.setVertexData(count, verts);
        dest.setNormalData(count, norms);
    }

    private void makeLongDiskTerrainConformant(DrawContext dc, int numCoords, float[] verts,
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

    private void makeLongDiskIndices(int arcSlices, int lengthSlices, int loops, int orientation,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        gb.setOrientation(orientation);

        int mode = gb.getLongDiskDrawMode();
        int count = gb.getLongDiskIndexCount(arcSlices, lengthSlices, loops);
        int[] indices = new int[count];
        gb.makeLongDiskIndices(arcSlices, lengthSlices, loops, indices);

        dest.setElementData(mode, count, indices);
    }

    //**************************************************************//
    //********************  END Geometry Rendering  ****************//
    //**************************************************************//

    @Override
    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doGetRestorableState(rs, context);

        rs.addStateValueAsLatLon(context, "location1", this.location1);
        rs.addStateValueAsLatLon(context, "location2", this.location2);
        rs.addStateValueAsString(context, "orbitType", this.orbitType);
        rs.addStateValueAsDouble(context, "width", this.width);
        rs.addStateValueAsBoolean(context, "enableCaps", this.enableCaps);
    }

    @Override
    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doRestoreState(rs, context);

        LatLon loc1 = rs.getStateValueAsLatLon(context, "location1");
        if (loc1 == null)
            loc1 = this.getLocations()[0];

        LatLon loc2 = rs.getStateValueAsLatLon(context, "location2");
        if (loc2 == null)
            loc2 = this.getLocations()[1];

        this.setLocations(loc1, loc2);

        String s = rs.getStateValueAsString(context, "orbitType");
        if (s != null)
            this.setOrbitType(s);

        Double d = rs.getStateValueAsDouble(context, "width");
        if (d != null)
            this.setWidth(d);

        Boolean booleanState = rs.getStateValueAsBoolean(context, "enableCaps");
        if (booleanState != null)
            this.setEnableCaps(booleanState);
    }
}
