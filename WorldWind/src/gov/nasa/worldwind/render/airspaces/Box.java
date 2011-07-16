/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.util.*;

/**
 * @author lado
 * @version $Id$
 */
public class Box extends AbstractAirspace
{
    public static final int FACE_TOP = 0;
    public static final int FACE_BOTTOM = 1;
    public static final int FACE_LEFT = 2;
    public static final int FACE_RIGHT = 3;
    public static final int FACE_FRONT = 4;
    public static final int FACE_BACK = 5;

    protected static final int A_LOW_LEFT = 0;
    protected static final int A_LOW_RIGHT = 1;
    protected static final int A_UPR_LEFT = 2;
    protected static final int A_UPR_RIGHT = 3;
    protected static final int B_LOW_LEFT = 4;
    protected static final int B_LOW_RIGHT = 5;
    protected static final int B_UPR_LEFT = 6;
    protected static final int B_UPR_RIGHT = 7;

    protected static final int LOW_FACE = 0;
    protected static final int UPR_FACE = 1;
    protected static final int SIDE_FACE = 2;

    protected static final int DEFAULT_PILLARS = 8;
    protected static final int DEFAULT_STACKS = 4;
    protected static final int DEFAULT_HEIGHT_STACKS = 1;
    protected static final int MINIMAL_GEOMETRY_PILLARS = 8;
    protected static final int MINIMAL_GEOMETRY_STACKS = 4;

    private LatLon location1 = LatLon.ZERO;
    private LatLon location2 = LatLon.ZERO;
    private double leftWidth = 1.0;
    private double rightWidth = 1.0;
    private boolean enableStartCap = true;
    private boolean enableEndCap = true;
    // Geometry.
    private Vec4[] vertices;
    private boolean forceCullFace = false;
    private int pillars = DEFAULT_PILLARS;
    private int stacks = DEFAULT_STACKS;
    private int heightStacks = DEFAULT_HEIGHT_STACKS;

    public Box(LatLon location1, LatLon location2, double leftWidth, double rightWidth)
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
        if (leftWidth < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "leftWidth=" + leftWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (rightWidth < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "rightWidth=" + rightWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.location1 = location1;
        this.location2 = location2;
        this.leftWidth = leftWidth;
        this.rightWidth = rightWidth;
        this.makeDefaultDetailLevels();
    }

    public Box(AirspaceAttributes attributes)
    {
        super(attributes);
        this.makeDefaultDetailLevels();
    }

    public Box()
    {
        this.makeDefaultDetailLevels();
    }

    private void makeDefaultDetailLevels()
    {
        List<DetailLevel> levels = new ArrayList<DetailLevel>();
        double[] ramp = ScreenSizeDetailLevel.computeDefaultScreenSizeRamp(5);

        DetailLevel level;
        level = new ScreenSizeDetailLevel(ramp[0], "Detail-Level-0");
        level.setValue(PILLARS, 8);
        level.setValue(STACKS, 4);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[1], "Detail-Level-1");
        level.setValue(PILLARS, 6);
        level.setValue(STACKS, 3);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[2], "Detail-Level-2");
        level.setValue(PILLARS, 4);
        level.setValue(STACKS, 2);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[3], "Detail-Level-3");
        level.setValue(PILLARS, 2);
        level.setValue(STACKS, 1);
        level.setValue(DISABLE_TERRAIN_CONFORMANCE, false);
        levels.add(level);

        level = new ScreenSizeDetailLevel(ramp[4], "Detail-Level-4");
        level.setValue(PILLARS, 1);
        level.setValue(STACKS, 1);
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

    /**
     * Sets the leg's locations, in geographic coordinates.
     *
     * @param location1 geographic coordinates(latitude and longitude) specifying the center of the begining edge.
     * @param location2 geographic coordinates(latitude and longitude) specifying the center of the ending edge.
     *
     * @throws IllegalArgumentException if location1 or location2 is null
     */
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

    public double[] getWidths()
    {
        double[] array = new double[2];
        array[0] = this.leftWidth;
        array[1] = this.rightWidth;
        return array;
    }

    public void setWidths(double leftWidth, double rightWidth)
    {
        if (leftWidth < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "leftWidth=" + leftWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (rightWidth < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "rightWidth=" + rightWidth);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.leftWidth = leftWidth;
        this.rightWidth = rightWidth;
        this.setExtentOutOfDate();
    }

    public boolean[] isEnableCaps()
    {
        boolean[] array = new boolean[2];
        array[0] = this.enableStartCap;
        array[1] = this.enableEndCap;
        return array;
    }

    public void setEnableCaps(boolean enableStartCap, boolean enableEndCap)
    {
        this.enableStartCap = enableStartCap;
        this.enableEndCap = enableEndCap;
    }

    public void setEnableCaps(boolean enable)
    {
        this.setEnableCaps(enable, enable);
    }

    public void setEnableStartCap(boolean enable)
    {
        this.setEnableCaps(enable, this.enableEndCap);
    }

    public void setEnableEndCap(boolean enable)
    {
        this.setEnableCaps(this.enableStartCap, enable);
    }

    public Vec4[] getVertices()
    {
        return this.vertices;
    }

    public void setVertices(Vec4[] vertices)
    {
        if (vertices == null)
        {
            this.vertices = null;
        }
        else
        {
            if (vertices.length < 8)
            {
                String message = Logging.getMessage("generic.ArrayInvalidLength", "vertices.length=" + vertices.length);
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            if (this.vertices == null)
                this.vertices = new Vec4[8];
            System.arraycopy(vertices, 0, this.vertices, 0, 8);
        }
        this.setExtentOutOfDate();
    }

    public static Vec4[] computeStandardVertices(Globe globe, double verticalExaggeration, Box box)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (box == null)
        {
            String message = Logging.getMessage("nullValue.BoxIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double[] altitudes = box.getAltitudes(verticalExaggeration);

        // Compute the Cartesian points of this Box's first and second locations at the upper and lower altitudes.
        Vec4 al = globe.computePointFromPosition(box.location1, altitudes[0]);
        Vec4 au = globe.computePointFromPosition(box.location1, altitudes[1]);
        Vec4 bl = globe.computePointFromPosition(box.location2, altitudes[0]);
        Vec4 bu = globe.computePointFromPosition(box.location2, altitudes[1]);

        // Compute vectors at the first and second locations that are perpendicular to the vector connecting the two
        // points and perpendicular to the Globe's normal at each point. These perpendicular vectors are used to
        // determine this Box's points to the left and right of its Cartesian points.
        Vec4 aNormal = globe.computeSurfaceNormalAtPoint(al);
        Vec4 bNormal = globe.computeSurfaceNormalAtPoint(bl);
        Vec4 ab = bl.subtract3(al).normalize3();
        Vec4 aPerp = ab.cross3(aNormal).normalize3();
        Vec4 bPerp = ab.cross3(bNormal).normalize3();

        Vec4[] vertices = new Vec4[8];
        vertices[Box.A_LOW_LEFT] = new Line(al, aPerp).getPointAt(-box.leftWidth);
        vertices[Box.A_LOW_RIGHT] = new Line(al, aPerp).getPointAt(box.rightWidth);
        vertices[Box.A_UPR_LEFT] = new Line(au, aPerp).getPointAt(-box.leftWidth);
        vertices[Box.A_UPR_RIGHT] = new Line(au, aPerp).getPointAt(box.rightWidth);
        vertices[Box.B_LOW_LEFT] = new Line(bl, bPerp).getPointAt(-box.leftWidth);
        vertices[Box.B_LOW_RIGHT] = new Line(bl, bPerp).getPointAt(box.rightWidth);
        vertices[Box.B_UPR_LEFT] = new Line(bu, bPerp).getPointAt(-box.leftWidth);
        vertices[Box.B_UPR_RIGHT] = new Line(bu, bPerp).getPointAt(box.rightWidth);
        return vertices;
    }

    /**
     * Returns an array of six <code>Plane</code>s that define the plane for each face of the specified <code>box</code>
     * on the specified <code>globe</code>. The each plane may be accessed using the <code>FACE</code> constants in
     * <code>Box</code> as indices into the array: {@link #FACE_TOP}, {@link #FACE_BOTTOM}, {@link #FACE_LEFT}, {@link
     * #FACE_RIGHT}, {@link #FACE_FRONT}, {@link #FACE_BACK}.
     *
     * @param globe                the <code>Globe</code> the box is related to.
     * @param verticalExaggeration the vertical exaggeration of the scene.
     * @param box                  the <code>Box</code> to compute the planes for.
     *
     * @return six <code>Plane</code>s for each face of the specified <code>box</code>, in the following order: top,
     *         bottom, left, right, front, back.
     */
    public static Plane[] computeStandardPlanes(Globe globe, double verticalExaggeration, Box box)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (box == null)
        {
            String message = Logging.getMessage("nullValue.BoxIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4[] vertices = computeStandardVertices(globe, verticalExaggeration, box);
        if (vertices == null || vertices.length != 8) // This should never happen, but we check anyway.
            return null;

        Plane[] planes = new Plane[6];
        planes[FACE_TOP] = Plane.fromPoints(vertices[A_UPR_RIGHT], vertices[B_UPR_RIGHT], vertices[A_UPR_LEFT]);
        planes[FACE_BOTTOM] = Plane.fromPoints(vertices[A_LOW_RIGHT], vertices[A_LOW_LEFT], vertices[B_LOW_RIGHT]);
        planes[FACE_LEFT] = Plane.fromPoints(vertices[A_LOW_LEFT], vertices[A_UPR_LEFT], vertices[B_LOW_LEFT]);
        planes[FACE_RIGHT] = Plane.fromPoints(vertices[A_LOW_RIGHT], vertices[B_LOW_RIGHT], vertices[A_UPR_RIGHT]);
        planes[FACE_FRONT] = Plane.fromPoints(vertices[A_LOW_RIGHT], vertices[A_UPR_RIGHT], vertices[A_LOW_LEFT]);
        planes[FACE_BACK] = Plane.fromPoints(vertices[B_LOW_LEFT], vertices[B_UPR_LEFT], vertices[B_LOW_RIGHT]);
        return planes;
    }

    public Position getReferencePosition()
    {
        double[] altitudes = this.getAltitudes();
        return new Position(this.location1, altitudes[0]);
    }

    protected gov.nasa.worldwind.geom.Box computeExtent(Globe globe, double verticalExaggeration)
    {
        List<Vec4> points = this.computeMinimalGeometry(globe, verticalExaggeration);
        if (points == null || points.isEmpty())
            return null;

        return gov.nasa.worldwind.geom.Box.computeBoundingBox(points);
    }

    @Override
    protected List<Vec4> computeMinimalGeometry(Globe globe, double verticalExaggeration)
    {
        Vec4[] verts = this.getVertices();
        if (verts == null)
            verts = computeStandardVertices(globe, verticalExaggeration, this);

        float[] controlPoints = new float[12];
        this.makeControlPoints(verts, A_UPR_RIGHT, B_UPR_RIGHT, B_UPR_LEFT, A_UPR_LEFT, Vec4.ZERO, controlPoints);

        GeometryBuilder gb = this.getGeometryBuilder();
        int count = gb.getBilinearSurfaceVertexCount(MINIMAL_GEOMETRY_PILLARS, MINIMAL_GEOMETRY_STACKS);
        int numCoords = 3 * count;
        float[] coords = new float[numCoords];
        gb.makeBilinearSurfaceVertices(controlPoints, // Surface control points.
            0, // Output starting index.
            MINIMAL_GEOMETRY_PILLARS, MINIMAL_GEOMETRY_STACKS, // uStacks, vStacks.
            coords);

        LatLon[] locations = new LatLon[count];
        for (int i = 0; i < count; i++)
        {
            Vec4 v = Vec4.fromFloatArray(coords, 3 * i, 3);
            locations[i] = globe.computePositionFromPoint(v);
        }

        ArrayList<Vec4> points = new ArrayList<Vec4>();
        this.makeExtremePoints(globe, verticalExaggeration, Arrays.asList(locations), points);

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

    protected boolean isForceCullFace()
    {
        return this.forceCullFace;
    }

    protected void setForceCullFace(boolean forceCullFace)
    {
        this.forceCullFace = forceCullFace;
    }

    protected int getPillars()
    {
        return this.pillars;
    }

    protected void setPillars(int pillars)
    {
        if (pillars < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "pillars=" + pillars);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.pillars = pillars;
    }

    protected int getStacks()
    {
        return this.stacks;
    }

    protected void setStacks(int stacks)
    {
        if (stacks < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "stacks=" + stacks);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.stacks = stacks;
    }

    protected int getHeightStacks()
    {
        return this.heightStacks;
    }

    //**************************************************************//
    //********************  Geometry Rendering  ********************//
    //**************************************************************//

    protected Vec4 computeReferenceCenter(DrawContext dc)
    {
        Extent extent = this.getExtent(dc);
        return extent != null ? extent.getCenter() : null;
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

        Vec4[] verts = this.getVertices();
        if (verts == null)
            verts = computeStandardVertices(dc.getGlobe(), dc.getVerticalExaggeration(), this);

        double[] altitudes = this.getAltitudes(dc.getVerticalExaggeration());
        boolean[] terrainConformant = this.isTerrainConforming();
        boolean[] enableCaps = this.isEnableCaps();
        int pillars = this.pillars;
        int stacks = this.stacks;
        int heightStacks = this.heightStacks;

        if (this.isEnableLevelOfDetail())
        {
            DetailLevel level = this.computeDetailLevel(dc);

            Object o = level.getValue(PILLARS);
            if (o != null && o instanceof Integer)
                pillars = (Integer) o;

            o = level.getValue(STACKS);
            if (o != null && o instanceof Integer)
                stacks = (Integer) o;

            o = level.getValue(DISABLE_TERRAIN_CONFORMANCE);
            if (o != null && o instanceof Boolean && (Boolean) o)
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

            if (this.forceCullFace || !enableCaps[0] || !enableCaps[1])
            {
                ogsh.pushAttrib(gl, GL.GL_POLYGON_BIT);
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glFrontFace(GL.GL_CCW);
            }

            if (Airspace.DRAW_STYLE_FILL.equals(drawStyle))
            {
                this.drawBoxFill(dc, verts, altitudes, terrainConformant, enableCaps, pillars, stacks, heightStacks,
                    referenceCenter);
            }
            else if (Airspace.DRAW_STYLE_OUTLINE.equals(drawStyle))
            {
                this.drawBoxOutline(dc, verts, altitudes, terrainConformant, enableCaps,
                    pillars, stacks, heightStacks, referenceCenter);
            }
        }
        finally
        {
            dc.getView().popReferenceCenter(dc);
            ogsh.pop(gl);
        }
    }

    //**************************************************************//
    //********************  Box  ***********************************//
    //**************************************************************//

    private void drawBoxFill(DrawContext dc, Vec4[] verts, double[] altitudes, boolean[] terrainConformant,
        boolean[] enableCaps,
        int pillars, int stacks, int heightStacks,
        Vec4 referenceCenter)
    {
        Geometry indexGeom = this.getBoxIndexFillGeometry(enableCaps, pillars, stacks, heightStacks);
        Geometry vertexGeom = this.getBoxVertexGeometry(dc, verts, altitudes, terrainConformant, enableCaps,
            pillars, stacks, heightStacks, referenceCenter);

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private void drawBoxOutline(DrawContext dc, Vec4[] verts, double[] altitudes, boolean[] terrainConformant,
        boolean[] enableCaps,
        int pillars, int stacks, int heightStacks,
        Vec4 referenceCenter)
    {
        Geometry indexGeom = this.getBoxIndexOutlineGeometry(enableCaps, pillars, stacks, heightStacks);
        Geometry vertexGeom = this.getBoxVertexGeometry(dc, verts, altitudes, terrainConformant, enableCaps,
            pillars, stacks, heightStacks, referenceCenter);

        this.getRenderer().drawGeometry(dc, indexGeom, vertexGeom);
    }

    private Geometry getBoxIndexFillGeometry(boolean[] enableCaps, int pillars, int stacks, int heightStacks)
    {
        Geometry.CacheKey cacheKey = new Geometry.CacheKey(this.getClass(), "Box.FillIndices",
            enableCaps[0], enableCaps[1], pillars, stacks, heightStacks);

        Geometry geom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (geom == null)
        {
            geom = new Geometry();
            this.makeBoxFillIndices(enableCaps, pillars, stacks, heightStacks, geom);
            this.getGeometryCache().add(cacheKey, geom);
        }

        return geom;
    }

    private Geometry getBoxIndexOutlineGeometry(boolean[] enableCaps,
        int pillars, int stacks, int heightStacks)
    {
        Geometry.CacheKey cacheKey = new Geometry.CacheKey(this.getClass(), "Box.OutlineIndices",
            enableCaps[0], enableCaps[1],
            pillars, stacks, heightStacks);

        Geometry geom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (geom == null)
        {
            geom = new Geometry();
            this.makeBoxOutlineIndices(enableCaps, pillars, stacks, heightStacks, geom);
            this.getGeometryCache().add(cacheKey, geom);
        }

        return geom;
    }

    private Geometry getBoxVertexGeometry(DrawContext dc, Vec4[] verts,
        double[] altitudes, boolean[] terrainConformant,
        boolean[] enableCaps,
        int pillars, int stacks, int heightStacks,
        Vec4 referenceCenter)
    {
        Object cacheKey = new Geometry.CacheKey(this.getClass(), "Box.Vertices",
            verts, altitudes[0], altitudes[1], terrainConformant[0], terrainConformant[1],
            enableCaps[0], enableCaps[1], pillars, stacks, heightStacks, referenceCenter);

        Geometry geom = (Geometry) this.getGeometryCache().getObject(cacheKey);
        if (geom == null || this.isExpired(dc, geom))
        {
            if (geom == null)
                geom = new Geometry();
            this.makeBoxVertices(dc, verts, altitudes, terrainConformant, enableCaps, pillars, stacks, heightStacks,
                referenceCenter, geom);
            this.updateExpiryCriteria(dc, geom);
            this.getGeometryCache().add(cacheKey, geom);
        }

        return geom;
    }

    private static class FaceRenderInfo
    {
        int faceType;
        int ll, lr, ul, ur;
        int uStacks, vStacks;
        int orientation;
        int firstVertex, vertexCount;
        int firstIndex, indexCount;

        private FaceRenderInfo(int faceType, int ll, int lr, int ur, int ul, int uStacks, int vStacks, int orientation)
        {
            this.faceType = faceType;
            this.ll = ll;
            this.lr = lr;
            this.ur = ur;
            this.ul = ul;
            this.uStacks = uStacks;
            this.vStacks = vStacks;
            this.orientation = orientation;
        }
    }

    private void makeFaceInfo(boolean[] enableCaps, int pillars, int stacks, int heightStacks, FaceRenderInfo[] ri)
    {
        // Top face.
        ri[FACE_TOP] = new FaceRenderInfo(UPR_FACE, A_UPR_RIGHT, B_UPR_RIGHT, B_UPR_LEFT, A_UPR_LEFT,
            pillars, stacks, GeometryBuilder.OUTSIDE);
        // Bottom face.
        ri[FACE_BOTTOM] = new FaceRenderInfo(LOW_FACE, A_LOW_LEFT, B_LOW_LEFT, B_LOW_RIGHT, A_LOW_RIGHT,
            pillars, stacks, GeometryBuilder.OUTSIDE);
        // Left side face.
        ri[FACE_LEFT] = new FaceRenderInfo(SIDE_FACE, B_LOW_LEFT, A_LOW_LEFT, A_UPR_LEFT, B_UPR_LEFT,
            pillars, heightStacks, GeometryBuilder.OUTSIDE);
        // Right side face.
        ri[FACE_RIGHT] = new FaceRenderInfo(SIDE_FACE, A_LOW_RIGHT, B_LOW_RIGHT, B_UPR_RIGHT, A_UPR_RIGHT,
            pillars, heightStacks, GeometryBuilder.OUTSIDE);
        // Front side face.
        if (enableCaps[0])
        {
            ri[FACE_FRONT] = new FaceRenderInfo(SIDE_FACE, A_LOW_LEFT, A_LOW_RIGHT, A_UPR_RIGHT, A_UPR_LEFT,
                stacks, heightStacks, GeometryBuilder.OUTSIDE);
        }
        // Back side face.
        if (enableCaps[1])
        {
            ri[FACE_BACK] = new FaceRenderInfo(SIDE_FACE, B_LOW_RIGHT, B_LOW_LEFT, B_UPR_LEFT, B_UPR_RIGHT,
                stacks, heightStacks, GeometryBuilder.OUTSIDE);
        }
    }

    private void makeBoxFillIndices(boolean[] enableCaps, int pillars, int stacks, int heightStacks, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        FaceRenderInfo[] ri = new FaceRenderInfo[6];
        this.makeFaceInfo(enableCaps, pillars, stacks, heightStacks, ri);

        int drawMode = gb.getBilinearSurfaceFillDrawMode();

        int indexCount = 0;
        int vertexCount = 0;

        FaceRenderInfo last = null;
        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                if (last != null)
                    indexCount += 2;
                ri[i].firstIndex = indexCount;
                ri[i].firstVertex = vertexCount;
                ri[i].indexCount = gb.getBilinearSurfaceFillIndexCount(ri[i].uStacks, ri[i].vStacks);
                ri[i].vertexCount = gb.getBilinearSurfaceVertexCount(ri[i].uStacks, ri[i].vStacks);
                indexCount += ri[i].indexCount;
                vertexCount += ri[i].vertexCount;
                last = ri[i];
            }
        }

        int[] indices = new int[indexCount];

        last = null;
        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                gb.makeBilinearSurfaceFillIndices(ri[i].firstVertex, ri[i].uStacks, ri[i].vStacks, ri[i].firstIndex,
                    indices);
                if (last != null)
                {
                    int joinPos = last.firstIndex + last.indexCount;
                    int lastIndex = last.firstIndex + last.indexCount - 1;
                    int firstIndex = ri[i].firstIndex;
                    indices[joinPos++] = indices[lastIndex];
                    indices[joinPos] = indices[firstIndex];
                }
                last = ri[i];
            }
        }

        dest.setElementData(drawMode, indexCount, indices);
    }

    private void makeBoxOutlineIndices(boolean[] enableCaps,
        int pillars, int stacks, int heightStacks, Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        FaceRenderInfo[] ri = new FaceRenderInfo[6];
        this.makeFaceInfo(enableCaps, pillars, stacks, heightStacks, ri);

        int drawMode = gb.getBilinearSurfaceOutlineDrawMode();

        int indexCount = 0;
        int vertexCount = 0;

        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                int mask = this.getOutlineMask(i, enableCaps);
                ri[i].firstIndex = indexCount;
                ri[i].firstVertex = vertexCount;
                ri[i].indexCount = gb.getBilinearSurfaceOutlineIndexCount(ri[i].uStacks, ri[i].vStacks, mask);
                ri[i].vertexCount = gb.getBilinearSurfaceVertexCount(ri[i].uStacks, ri[i].vStacks);
                indexCount += ri[i].indexCount;
                vertexCount += ri[i].vertexCount;
            }
        }

        int[] indices = new int[indexCount];

        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                int mask = this.getOutlineMask(i, enableCaps);
                gb.makeBilinearSurfaceOutlineIndices(ri[i].firstVertex, ri[i].uStacks, ri[i].vStacks, mask,
                    ri[i].firstIndex, indices);
            }
        }

        dest.setElementData(drawMode, indexCount, indices);
    }

    private int getOutlineMask(int face, boolean[] enableCaps)
    {
        if (face == FACE_LEFT || face == FACE_RIGHT)
        {
            return GeometryBuilder.TOP | GeometryBuilder.BOTTOM | GeometryBuilder.LEFT | GeometryBuilder.RIGHT;
        }
        else if (face == FACE_FRONT || face == FACE_BACK)
        {
            if ((face == FACE_FRONT && enableCaps[0]) || (face == FACE_BACK && enableCaps[1]))
            {
                return GeometryBuilder.TOP | GeometryBuilder.BOTTOM;
            }
        }

        return 0;
    }

    private void makeBoxVertices(DrawContext dc, Vec4[] verts, double[] altitudes, boolean[] terrainConformant,
        boolean[] enableCaps, int pillars, int stacks, int heightStacks,
        Vec4 referenceCenter,
        Geometry dest)
    {
        GeometryBuilder gb = this.getGeometryBuilder();
        FaceRenderInfo[] ri = new FaceRenderInfo[6];
        this.makeFaceInfo(enableCaps, pillars, stacks, heightStacks, ri);

        int vertexCount = 0;

        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                ri[i].firstVertex = vertexCount;
                ri[i].vertexCount = gb.getBilinearSurfaceVertexCount(ri[i].uStacks, ri[i].vStacks);
                vertexCount += ri[i].vertexCount;
            }
        }

        int numCoords = 3 * vertexCount;
        float[] vertices = new float[numCoords];
        float[] normals = new float[numCoords];

        float[] controlPoints = new float[12];
        for (int i = 0; i < 6; i++)
        {
            if (ri[i] != null)
            {
                this.makeControlPoints(verts, ri[i].ll, ri[i].lr, ri[i].ur, ri[i].ul, referenceCenter, controlPoints);
                gb.setOrientation(ri[i].orientation);

                gb.makeBilinearSurfaceVertices(controlPoints, ri[i].firstVertex, ri[i].uStacks, ri[i].vStacks,
                    vertices);
                if (ri[i].faceType == LOW_FACE || ri[i].faceType == UPR_FACE)
                {
                    this.makeTerrainConformant(dc, ri[i].faceType, altitudes, terrainConformant,
                        ri[i].firstVertex, ri[i].vertexCount, vertices, referenceCenter);
                }
                else if (ri[i].faceType == SIDE_FACE)
                {
                    this.makeSideFaceTerrainConformant(dc, altitudes, terrainConformant,
                        ri[i].firstVertex, ri[i].uStacks, ri[i].vStacks, vertices, referenceCenter);
                }

                gb.makeBilinearSurfaceVertexNormals(ri[i].firstVertex, ri[i].uStacks, ri[i].vStacks, vertices,
                    ri[i].firstVertex, normals);
            }
        }

        dest.setVertexData(vertexCount, vertices);
        dest.setNormalData(vertexCount, normals);
    }

    private void makeControlPoints(Vec4[] vertices, int ll, int lr, int ur, int ul, Vec4 referenceCenter, float[] dest)
    {
        // Lower left corner.
        dest[0] = (float) (vertices[ll].x - referenceCenter.x);
        dest[1] = (float) (vertices[ll].y - referenceCenter.y);
        dest[2] = (float) (vertices[ll].z - referenceCenter.z);
        // Lower right corner.
        dest[3] = (float) (vertices[lr].x - referenceCenter.x);
        dest[4] = (float) (vertices[lr].y - referenceCenter.y);
        dest[5] = (float) (vertices[lr].z - referenceCenter.z);
        // Upper right corner.
        dest[6] = (float) (vertices[ur].x - referenceCenter.x);
        dest[7] = (float) (vertices[ur].y - referenceCenter.y);
        dest[8] = (float) (vertices[ur].z - referenceCenter.z);
        // Upper left corner.
        dest[9] = (float) (vertices[ul].x - referenceCenter.x);
        dest[10] = (float) (vertices[ul].y - referenceCenter.y);
        dest[11] = (float) (vertices[ul].z - referenceCenter.z);
    }

    private void makeTerrainConformant(DrawContext dc, int face, double[] altitudes, boolean[] terrainConforming,
        int pos, int count, float[] verts, Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        double altitude = (face == LOW_FACE) ? altitudes[0] : altitudes[1];
        boolean isTerrainConforming =
            (face == LOW_FACE && terrainConforming[0]) || (face == UPR_FACE && terrainConforming[1]);

        for (int i = 0; i < count; i++)
        {
            int index = 3 * (pos + i);
            Vec4 vec = new Vec4(
                verts[index] + referenceCenter.x,
                verts[index + 1] + referenceCenter.y,
                verts[index + 2] + referenceCenter.z);
            Position p = globe.computePositionFromPoint(vec);

            double elevation = altitude;
            if (isTerrainConforming)
                elevation += this.computeElevationAt(dc, p.getLatitude(), p.getLongitude());

            vec = globe.computePointFromPosition(p.getLatitude(), p.getLongitude(), elevation);
            verts[index] = (float) (vec.x - referenceCenter.x);
            verts[index + 1] = (float) (vec.y - referenceCenter.y);
            verts[index + 2] = (float) (vec.z - referenceCenter.z);
        }
    }

    private void makeSideFaceTerrainConformant(DrawContext dc, double[] altitudes, boolean[] terrainConforming,
        int pos, int uStacks, int vStacks, float[] verts, Vec4 referenceCenter)
    {
        Globe globe = dc.getGlobe();
        double altitude = altitudes[0];
        double altDelta = (altitudes[1] - altitudes[0]) / vStacks;

        for (int vi = 0; vi <= vStacks; vi++, altitude += altDelta)
        {
            for (int ui = 0; ui <= uStacks; ui++)
            {
                int index = ui + vi * (uStacks + 1);
                index = 3 * (pos + index);
                Vec4 vec = new Vec4(
                    verts[index] + referenceCenter.x,
                    verts[index + 1] + referenceCenter.y,
                    verts[index + 2] + referenceCenter.z);
                Position p = globe.computePositionFromPoint(vec);

                double elev = altitude;
                if ((vi == 0 && terrainConforming[0]) || (vi == vStacks && terrainConforming[1]))
                    elev += this.computeElevationAt(dc, p.getLatitude(), p.getLongitude());

                vec = globe.computePointFromPosition(p.getLatitude(), p.getLongitude(), elev);
                verts[index] = (float) (vec.x - referenceCenter.x);
                verts[index + 1] = (float) (vec.y - referenceCenter.y);
                verts[index + 2] = (float) (vec.z - referenceCenter.z);
            }
        }
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
        rs.addStateValueAsDouble(context, "leftWidth", this.leftWidth);
        rs.addStateValueAsDouble(context, "rightWidth", this.rightWidth);
        rs.addStateValueAsBoolean(context, "enableStartCap", this.enableStartCap);
        rs.addStateValueAsBoolean(context, "enableEndCap", this.enableEndCap);
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

        Double lw = rs.getStateValueAsDouble(context, "leftWidth");
        if (lw == null)
            lw = this.getWidths()[0];

        Double rw = rs.getStateValueAsDouble(context, "rightWidth");
        if (rw == null)
            rw = this.getWidths()[1];

        this.setWidths(lw, rw);

        Boolean enableStart = rs.getStateValueAsBoolean(context, "enableStartCap");
        if (enableStart == null)
            enableStart = this.isEnableCaps()[0];

        Boolean enableEnd = rs.getStateValueAsBoolean(context, "enableEndCap");
        if (enableEnd == null)
            enableEnd = this.isEnableCaps()[1];

        this.setEnableCaps(enableStart, enableEnd);
    }
}
