/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import com.sun.opengl.util.BufferUtil;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil;
import gov.nasa.worldwind.terrain.Terrain;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import javax.xml.stream.*;
import java.io.IOException;
import java.nio.*;
import java.util.*;

import static gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil.kmlBoolean;

// TODO: Measurement (getLength), Texture, lighting

/**
 * Displays a line or curve between positions. The path is drawn between input positions to achieve a specified path
 * type, e.g., {@link AVKey#GREAT_CIRCLE}. It can also conform to the underlying terrain. A curtain may be formed by
 * extruding the path to the ground.
 * <p/>
 * Altitudes within the path's positions are interpreted according to the path's altitude mode. If the altitude mode is
 * {@link WorldWind#ABSOLUTE}, the altitudes are considered as height above the ellipsoid. If the altitude mode is
 * {@link WorldWind#RELATIVE_TO_GROUND}, the altitudes are added to the elevation of the terrain at the position. If the
 * altitude mode is {@link WorldWind#CLAMP_TO_GROUND} the altitudes are ignored.
 * <p/>
 * Between the specified positions the path is drawn along a curve specified by the path's path type, either {@link
 * AVKey#GREAT_CIRCLE}, {@link AVKey#RHUMB_LINE} or {@link AVKey#LINEAR}. (See {@link #setPathType(String)}.)
 * <p/>
 * Paths have separate attributes for normal display and highlighted display. If no attributes are specified, default
 * attributes are used. See {@link #DEFAULT_INTERIOR_MATERIAL}, {@link #DEFAULT_OUTLINE_MATERIAL}, and {@link
 * #DEFAULT_HIGHLIGHT_MATERIAL}.
 * <p/>
 * When the path type is <code>LINEAR</code> the path conforms to terrain only if the follow-terrain property is true.
 * Otherwise the path control points will be connected by straight line segments.
 * <p/>
 * The terrain conformance of <code>GREAT_CIRCLE</code> or <code>RHUMB_LINE</code> paths is determined by the path's
 * follow-terrain and terrain-conformance properties. When the follow-terrain property is true, terrain conformance
 * adapts as the view moves relative to the path; the terrain-conformance property governs the precision of conformance,
 * and the number of intermediate positions computed varies. See {@link #setFollowTerrain(boolean)} and {@link
 * #setTerrainConformance(double)}. If the follow-terrain property is false, the view position is not considered and the
 * number of intermediate positions between specified positions is the constant value specified by the num-subsegments
 * property (see {@link #setNumSubsegments(int)}). The latter case may produce higher performance than the former.
 * <p/>
 *
 * @author tag
 * @version $Id$
 */
public class Path extends AbstractShape
{
    /** The default interior color. */
    protected static final Material DEFAULT_INTERIOR_MATERIAL = Material.PINK;
    /** The default outline color. */
    protected static final Material DEFAULT_OUTLINE_MATERIAL = Material.RED;
    /** The default path type. */
    protected static final String DEFAULT_PATH_TYPE = AVKey.LINEAR;
    /**
     * The offset applied to a terrain following Path's depth values to to ensure it shows over the terrain: 0.99.
     * Values less than 1.0 pull the path in front of the terrain, values greater than 1.0 push the path behind the
     * terrain.
     */
    protected static final double SURFACE_PATH_DEPTH_OFFSET = 0.99;

    /**
     * Overrides the default materials specified in the base class.
     */
    static
    {
        defaultAttributes.setInteriorMaterial(DEFAULT_INTERIOR_MATERIAL);
        defaultAttributes.setOutlineMaterial(DEFAULT_OUTLINE_MATERIAL);
    }

    /**
     * Maintains globe-dependent computed data such as Cartesian vertices and extents. One entry exists for each
     * distinct globe that this shape encounters in calls to {@link AbstractShape#render(DrawContext)}. See {@link
     * AbstractShape}.
     */
    protected static class PathData extends AbstractShapeData
    {
        /** The positions formed from applying path type and terrain conformance. */
        protected List<Position> tessellatedPositions;
        /**
         * The model coordinate vertices to render, all relative to this shape data's reference center. If the path is
         * extruded, the base vertices are interleaved: Vcap, Vbase, Vcap, Vbase, ...
         */
        protected FloatBuffer renderedPath;
        /**
         * Indices in the <code>renderedPath</code> identifying the vertices of the originally specified boundary
         * positions. This is used to draw vertical lines at those positions when the path is extruded.
         */
        protected IntBuffer polePositions; // identifies original positions in rendered path
        /** Indicates whether the rendered path has extrusion points in addition to path points. */
        protected boolean hasExtrusionPoints; // true when the rendered path contains extrusion points

        public PathData(DrawContext dc, Path shape)
        {
            super(dc, shape.minExpiryTime, shape.maxExpiryTime);
        }

        public List<Position> getTessellatedPositions()
        {
            return tessellatedPositions;
        }

        public void setTessellatedPositions(List<Position> tessellatedPositions)
        {
            this.tessellatedPositions = tessellatedPositions;
        }

        public FloatBuffer getRenderedPath()
        {
            return renderedPath;
        }

        public void setRenderedPath(FloatBuffer renderedPath)
        {
            this.renderedPath = renderedPath;
        }

        public IntBuffer getPolePositions()
        {
            return polePositions;
        }

        public void setPolePositions(IntBuffer polePositions)
        {
            this.polePositions = polePositions;
        }

        public boolean isHasExtrusionPoints()
        {
            return hasExtrusionPoints;
        }

        public void setHasExtrusionPoints(boolean hasExtrusionPoints)
        {
            this.hasExtrusionPoints = hasExtrusionPoints;
        }
    }

    @Override
    protected AbstractShapeData createCacheEntry(DrawContext dc)
    {
        return new PathData(dc, this);
    }

    protected PathData getCurrentPathData()
    {
        return (PathData) this.getCurrentData();
    }

    protected Iterable<? extends Position> positions; // the positions as provided by the application
    protected int numPositions; // the number of positions in the positions field.

    protected String pathType = DEFAULT_PATH_TYPE;
    protected boolean followTerrain; // true if altitude mode indicates terrain following
    protected boolean extrude;
    protected double terrainConformance = 10;
    protected int numSubsegments = 10;
    protected boolean drawVerticals = true;

    /** Creates a path with no positions. */
    public Path()
    {
    }

    /**
     * Creates a path with specified positions.
     * <p/>
     * Note: If fewer than two positions is specified, no path is drawn.
     *
     * @param positions the path positions. This reference is retained by this shape; the positions are not copied. If
     *                  any positions in the set change, {@link #setPositions(Iterable)} must be called to inform this
     *                  shape of the change.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    public Path(Iterable<? extends Position> positions)
    {
        this.setPositions(positions);
    }

    /**
     * Creates a path with positions specified via a generic list.
     * <p/>
     * Note: If fewer than two positions is specified, the path is not drawn.
     *
     * @param positions the path positions. This reference is retained by this shape; the positions are not copied. If
     *                  any positions in the set change, {@link #setPositions(Iterable)} must be called to inform this
     *                  shape of the change.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    public Path(Position.PositionList positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setPositions(positions.list);
    }

    /**
     * Creates a path between two positions.
     *
     * @param posA the first position.
     * @param posB the second position.
     *
     * @throws IllegalArgumentException if either position is null.
     */
    public Path(Position posA, Position posB)
    {
        if (posA == null || posB == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        List<Position> endPoints = new ArrayList<Position>(2);
        endPoints.add(posA);
        endPoints.add(posB);
        this.setPositions(endPoints);
    }

    @Override
    protected void initialize()
    {
        // Nothing to initialize in this class.
    }

    @Override
    protected void reset()
    {
        for (ShapeDataCache.ShapeDataCacheEntry entry : this.shapeDataCache)
        {
            ((PathData) entry).tessellatedPositions = null;
        }

        super.reset();
    }

    /**
     * Returns this path's positions.
     *
     * @return this path's positions. Will be null if no positions have been specified.
     */
    public Iterable<? extends Position> getPositions()
    {
        return this.positions;
    }

    /**
     * Specifies this path's positions, which replace this path's current positions, if any.
     * <p/>
     * Note: If fewer than two positions is specified, this path is not drawn.
     *
     * @param positions this path's positions.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.positions = positions;
        this.computePositionCount();
        this.reset();
    }

    /**
     * Indicates whether to extrude this path. Extruding the path extends a filled interior from the path to the
     * terrain.
     *
     * @return true to extrude this path, otherwise false.
     *
     * @see #setExtrude(boolean)
     */
    public boolean isExtrude()
    {
        return extrude;
    }

    /**
     * Specifies whether to extrude this path. Extruding the path extends a filled interior from the path to the
     * terrain.
     *
     * @param extrude true to extrude this path, otherwise false. The default value is false.
     */
    public void setExtrude(boolean extrude)
    {
        this.extrude = extrude;
        this.reset();
    }

    /**
     * Indicates whether this path is terrain following.
     *
     * @return true if terrain following, otherwise false.
     *
     * @see #setFollowTerrain(boolean)
     */
    public boolean isFollowTerrain()
    {
        return this.followTerrain;
    }

    /**
     * Specifies whether this path is terrain following.
     *
     * @param followTerrain true if terrain following, otherwise false. The default value is false.
     */
    public void setFollowTerrain(boolean followTerrain)
    {
        if (this.followTerrain == followTerrain)
            return;

        this.followTerrain = followTerrain;
        this.reset();
    }

    /**
     * Indicates the number of segments used between specified positions to achieve this path's path type. Higher values
     * cause the path to conform more closely to the path type but decrease performance.
     * <p/>
     * Note: The sub-segments number is ignored when the path follows terrain or when the path type is {@link
     * AVKey#LINEAR}.
     *
     * @return the number of sub-segments.
     *
     * @see #setNumSubsegments(int)
     */
    public int getNumSubsegments()
    {
        return numSubsegments;
    }

    /**
     * Specifies the number of segments used between specified positions to achieve this path's path type. Higher values
     * cause the path to conform more closely to the path type but decrease performance.
     * <p/>
     * Note: The sub-segments number is ignored when the path follows terrain or when the path type is {@link
     * AVKey#LINEAR}.
     *
     * @param numSubsegments the number of sub-segments. The default is 10.
     */
    public void setNumSubsegments(int numSubsegments)
    {
        this.numSubsegments = numSubsegments;
        this.reset();
    }

    /**
     * Indicates the terrain conformance target when this path follows the terrain. The value indicates the maximum
     * number of pixels between which intermediate positions of a path segment -- the path portion between two specified
     * positions -- are computed.
     *
     * @return the terrain conformance, in pixels.
     *
     * @see #setTerrainConformance(double)
     */
    public double getTerrainConformance()
    {
        return terrainConformance;
    }

    /**
     * Specifies how accurately this path must adhere to the terrain when the path is terrain following. The value
     * specifies the maximum number of pixels between tessellation points. Lower values increase accuracy but decrease
     * performance.
     *
     * @param terrainConformance the number of pixels between tessellation points.
     */
    public void setTerrainConformance(double terrainConformance)
    {
        this.terrainConformance = terrainConformance;
        this.reset();
    }

    /**
     * Indicates this paths path type.
     *
     * @return the path type.
     *
     * @see #setPathType(String)
     */
    public String getPathType()
    {
        return pathType;
    }

    /**
     * Specifies this path's path type. Recognized values are {@link AVKey#GREAT_CIRCLE}, {@link AVKey#RHUMB_LINE} and
     * {@link AVKey#LINEAR}.
     *
     * @param pathType the current path type. The default value is {@link AVKey#LINEAR}.
     */
    public void setPathType(String pathType)
    {
        this.pathType = pathType;
        this.reset();
    }

    /**
     * Indicates whether to draw at each specified path position when this path is extruded.
     *
     * @return true to draw the lines, otherwise false.
     *
     * @see #setDrawVerticals(boolean)
     */
    public boolean isDrawVerticals()
    {
        return drawVerticals;
    }

    /**
     * Specifies whether to draw vertical lines at each specified path position when this path is extruded.
     *
     * @param drawVerticals true to draw the lines, otherwise false. The default value is true.
     */
    public void setDrawVerticals(boolean drawVerticals)
    {
        this.drawVerticals = drawVerticals;
        this.reset();
    }

    public Sector getSector()
    {
        if (this.sector == null && this.positions != null)
            this.sector = Sector.boundingSector(this.positions);

        return this.sector;
    }

    @Override
    protected boolean mustDrawInterior()
    {
        return super.mustDrawInterior() && this.getCurrentPathData().hasExtrusionPoints;
    }

    @Override
    protected boolean mustApplyLighting(DrawContext dc)
    {
        return false; // TODO: Lighting; need to compute normals
    }

    @Override
    protected boolean mustApplyTexture(DrawContext dc)
    {
        return false;
    }

    protected boolean mustRegenerateGeometry(DrawContext dc)
    {
        if (this.getCurrentPathData() == null || this.getCurrentPathData().renderedPath == null)
            return true;

        if (this.getCurrentPathData().tessellatedPositions == null)
            return true;

        if (dc.getVerticalExaggeration() != this.getCurrentPathData().getVerticalExaggeration())
            return true;

        //noinspection SimplifiableIfStatement
        if (this.getAltitudeMode() == WorldWind.ABSOLUTE
            && this.getCurrentPathData().getGlobeStateKey() != null
            && this.getCurrentPathData().getGlobeStateKey().equals(dc.getGlobe().getGlobeStateKey(dc)))
            return false;

        return super.mustRegenerateGeometry(dc);
    }

    protected boolean shouldUseVBOs(DrawContext dc)
    {
        return this.getCurrentPathData().tessellatedPositions.size() > VBO_THRESHOLD && super.shouldUseVBOs(dc);
    }

    /**
     * Indicates whether this Path's defining positions and the positions in between are located on the underlying
     * terrain. This returns <code>true</code> if this Path's altitude mode is <code>WorldWind.CLAMP_TO_GROUND</code>
     * and the follow-terrain property is <code>true</code>. Otherwise this returns <code>false</code>.
     *
     * @return <code>true</code> if this Path's positions and the positions in between are located on the underlying
     *         terrain, and <code>false</code> otherwise.
     */
    protected boolean isSurfacePath()
    {
        return this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND && this.isFollowTerrain();
    }

    @Override
    protected void determineActiveAttributes()
    {
        // When the interior is drawn the vertex buffer has a different layout, so it may need to be rebuilt.
        boolean isDrawInterior = this.activeAttributes.isDrawInterior();

        super.determineActiveAttributes();

        if (this.activeAttributes != null && this.activeAttributes.isDrawInterior() != isDrawInterior)
            this.getCurrentData().setExpired(true);
    }

    /** Counts the number of positions in this path's specified positions. */
    protected void computePositionCount()
    {
        this.numPositions = 0;

        if (this.positions != null)
        {
            //noinspection UnusedDeclaration
            for (Position pos : this.positions)
            {
                ++this.numPositions;
            }
        }
    }

    @Override
    protected boolean doMakeOrderedRenderable(DrawContext dc)
    {
        // currentData must be set prior to calling this method
        PathData pathData = this.getCurrentPathData();

        pathData.setReferencePoint(this.computeReferenceCenter(dc));
        if (pathData.getReferencePoint() == null)
            return false;

        // Recompute tessellated positions because the geometry or view may have changed.
        this.makeTessellatedPositions(dc, pathData);
        if (pathData.tessellatedPositions == null || pathData.tessellatedPositions.size() < 2)
            return false;

        // Create the rendered Cartesian points.
        int previousSize = pathData.renderedPath != null ? pathData.renderedPath.limit() : 0;
        this.computePath(dc, pathData.tessellatedPositions, pathData);
        if (pathData.renderedPath == null || pathData.renderedPath.limit() < 6)
            return false;

        if (pathData.renderedPath.limit() > previousSize && this.shouldUseVBOs(dc))
            this.clearCachedVbos(dc);

        pathData.setExtent(this.computeExtent(pathData));

        // If the shape is less that a pixel in size, don't render it.
        if (this.getExtent() == null || dc.isSmall(this.getExtent(), 1))
            return false;

        if (!this.intersectsFrustum(dc))
            return false;

        pathData.setEyeDistance(this.computeEyeDistance(dc, pathData));
        pathData.setGlobeStateKey(dc.getGlobe().getGlobeStateKey(dc));
        pathData.setVerticalExaggeration(dc.getVerticalExaggeration());

        return true;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to place this Path behind other ordered renderables when this Path is entirely located on the
     * underlying terrain. In this case this Path must be drawn first to ensure that other ordered renderables are
     * correctly drawn on top of it and are not affected by this Path's depth offset. If two paths are both located on
     * the terrain, they are drawn with respect to their layer ordering.
     */
    @Override
    protected void addOrderedRenderable(DrawContext dc)
    {
        if (this.isSurfacePath())
        {
            dc.addOrderedRenderable(this, true); // Specify that this Path is behind before other renderables.
        }
        else
        {
            super.addOrderedRenderable(dc);
        }
    }

    @Override
    protected boolean isOrderedRenderableValid(DrawContext dc)
    {
        return this.getCurrentPathData().renderedPath != null && this.getCurrentPathData().renderedPath.limit() >= 6;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * If this Path is entirely located on the terrain, this applies an offset to the Path's depth values to to ensure
     * it shows over the terrain. This does not apply a depth offset in any other case to avoid incorrectly drawing the
     * path over objects it should be behind, including the terrain. In addition to applying a depth offset, this
     * disables writing to the depth buffer to avoid causing subsequently drawn ordered renderables to incorrectly fail
     * the depth test. Since this Path is located on the terrain, the terrain already provides the necessary depth
     * values and we can be certain that other ordered renderables should appear on top of it.
     */
    @Override
    protected void doDrawOutline(DrawContext dc)
    {
        boolean projectionOffsetPushed = false; // keep track for error recovery

        try
        {
            if (this.isSurfacePath())
            {
                // Pull the line forward just a bit to ensure it shows over the terrain.
                dc.pushProjectionOffest(SURFACE_PATH_DEPTH_OFFSET);
                dc.getGL().glDepthMask(false);
                projectionOffsetPushed = true;
            }

            if (this.shouldUseVBOs(dc))
            {
                int[] vboIds = this.getVboIds(dc);
                if (vboIds != null)
                    this.doDrawOutlineVBO(dc, vboIds, this.getCurrentPathData());
                else
                    this.doDrawOutlineVA(dc, this.getCurrentPathData());
            }
            else
            {
                this.doDrawOutlineVA(dc, this.getCurrentPathData());
            }
        }
        finally
        {
            if (projectionOffsetPushed)
            {
                dc.popProjectionOffest();
                dc.getGL().glDepthMask(true);
            }
        }
    }

    protected void doDrawOutlineVBO(DrawContext dc, int[] vboIds, PathData pathData)
    {
        GL gl = dc.getGL();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[0]);
        gl.glVertexPointer(3, GL.GL_FLOAT, pathData.hasExtrusionPoints ? 24 : 0, 0);
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, pathData.renderedPath.limit() / (pathData.hasExtrusionPoints ? 6 : 3));

        if (pathData.hasExtrusionPoints && this.isDrawVerticals())
            this.drawVerticalOutlineVBO(dc, vboIds, pathData);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    }

    protected void doDrawOutlineVA(DrawContext dc, PathData pathData)
    {
        GL gl = dc.getGL();

        gl.glVertexPointer(3, GL.GL_FLOAT, pathData.hasExtrusionPoints ? 24 : 0, pathData.renderedPath.rewind());
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, pathData.renderedPath.limit() / (pathData.hasExtrusionPoints ? 6 : 3));

        if (pathData.hasExtrusionPoints && this.isDrawVerticals())
            this.drawVerticalOutlineVA(dc, pathData);
    }

    protected void drawVerticalOutlineVBO(DrawContext dc, int[] vboIds, PathData pathData)
    {
        IntBuffer polePositions = pathData.polePositions;
        if (polePositions == null || polePositions.limit() < 1)
            return;

        GL gl = dc.getGL();

        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
        gl.glDrawElements(GL.GL_LINES, polePositions.limit(), GL.GL_UNSIGNED_INT, 0);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Draws vertical lines at this path's specified positions.
     *
     * @param dc       the current draw context.
     * @param pathData the current globe-specific path data.
     */
    protected void drawVerticalOutlineVA(DrawContext dc, PathData pathData)
    {
        IntBuffer polePositions = pathData.polePositions;
        if (polePositions == null || polePositions.limit() < 1)
            return;

        dc.getGL().glVertexPointer(3, GL.GL_FLOAT, 0, pathData.renderedPath.rewind());
        dc.getGL().glDrawElements(GL.GL_LINES, polePositions.limit(), GL.GL_UNSIGNED_INT, polePositions.rewind());
    }

    /**
     * Draws this path's interior when the path is extruded.
     *
     * @param dc the current draw context.
     */
    protected void doDrawInterior(DrawContext dc)
    {
        if (this.shouldUseVBOs(dc))
        {
            int[] vboIds = this.getVboIds(dc);
            if (vboIds != null)
                this.doDrawInteriorVBO(dc, vboIds, this.getCurrentPathData());
            else
                this.doDrawInteriorVA(dc, this.getCurrentPathData());
        }
        else
        {
            this.doDrawInteriorVA(dc, this.getCurrentPathData());
        }
    }

    protected void doDrawInteriorVBO(DrawContext dc, int[] vboIds, PathData pathData)
    {
        GL gl = dc.getGL();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[0]);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, pathData.renderedPath.limit() / 3);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    }

    protected void doDrawInteriorVA(DrawContext dc, PathData pathData)
    {
        dc.getGL().glVertexPointer(3, GL.GL_FLOAT, 0, pathData.renderedPath.rewind());
        dc.getGL().glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, pathData.renderedPath.limit() / 3);
    }

    /**
     * Computes the shape's model-coordinate path from a list of positions. Applies the path's terrain-conformance
     * settings. Adds extrusion points -- those on the ground -- when the path is extruded.
     *
     * @param dc        the current draw context.
     * @param positions the positions to create a path for.
     * @param pathData  the current globe-specific path data.
     */
    protected void computePath(DrawContext dc, List<Position> positions, PathData pathData)
    {
        pathData.hasExtrusionPoints = false;

        FloatBuffer path = pathData.renderedPath;

        if (this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND)
            path = this.computePointsRelativeToTerrain(dc, positions, 0d, path, pathData);
        else if (this.getAltitudeMode() == WorldWind.RELATIVE_TO_GROUND)
            path = this.computePointsRelativeToTerrain(dc, positions, null, path, pathData);
        else
            path = this.computeAbsolutePoints(dc, positions, path, pathData);

        path.flip(); // since the path is reused the limit might not be the same as the previous usage

        pathData.renderedPath = path;
    }

    /**
     * Computes a terrain-conforming, model-coordinate path from a list of positions, using either a specified altitude
     * or the altitudes in the specified positions. Adds extrusion points -- those on the ground -- when the path is
     * extruded and the specified single altitude is not 0.
     *
     * @param dc        the current draw context.
     * @param positions the positions to create a path for.
     * @param altitude  if non-null, the height above the terrain to use for all positions. If null, each position's
     *                  altitude is used as the height above the terrain.
     * @param path      a buffer in which to store the computed points. May be null. The buffer is not used if it is
     *                  null or tool small for the required number of points. A new buffer is created in that case and
     *                  returned by this method. This method modifies the buffer,s position and limit fields.
     * @param pathData  the current globe-specific path data.
     *
     * @return the buffer in which to place the computed points.
     */
    protected FloatBuffer computePointsRelativeToTerrain(DrawContext dc, List<Position> positions,
        Double altitude, FloatBuffer path, PathData pathData)
    {
        boolean extrudeIt = this.isExtrude() && !(altitude != null && altitude == 0);
        int numPoints = extrudeIt ? 2 * positions.size() : positions.size();

        if (path == null || path.capacity() < numPoints * 3)
            path = BufferUtil.newFloatBuffer(3 * numPoints);

        path.clear();

        for (Position pos : positions)
        {
            double height = altitude != null ? altitude : pos.getAltitude();
            Vec4 referencePoint = pathData.getReferencePoint();
            Vec4 pt = dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), height);
            path.put((float) (pt.x - referencePoint.x));
            path.put((float) (pt.y - referencePoint.y));
            path.put((float) (pt.z - referencePoint.z));

            if (extrudeIt)
                this.appendTerrainPoint(dc, pos, path, pathData);
        }

        return path;
    }

    /**
     * Computes a model-coordinate path from a list of positions, using the altitudes in the specified positions. Adds
     * extrusion points -- those on the ground -- when the path is extruded and the specified single altitude is not 0.
     *
     * @param dc        the current draw context.
     * @param positions the positions to create a path for.
     * @param path      a buffer in which to store the computed points. May be null. The buffer is not used if it is
     *                  null or tool small for the required number of points. A new buffer is created in that case and
     *                  returned by this method. This method modifies the buffer,s position and limit fields.
     * @param pathData  the current globe-specific path data.
     *
     * @return the buffer in which to place the computed points.
     */
    protected FloatBuffer computeAbsolutePoints(DrawContext dc, List<Position> positions, FloatBuffer path,
        PathData pathData)
    {
        int numPoints = this.isExtrude() ? 2 * positions.size() : positions.size();

        if (path == null || path.capacity() < numPoints * 3)
            path = BufferUtil.newFloatBuffer(3 * numPoints);

        path.clear();

        Globe globe = dc.getGlobe();
        Vec4 referencePoint = pathData.getReferencePoint();

        if (dc.getVerticalExaggeration() != 1)
        {
            double ve = dc.getVerticalExaggeration();
            for (Position pos : positions)
            {
                Vec4 pt = globe.computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
                    ve * (pos.getAltitude()));
                path.put((float) (pt.x - referencePoint.x));
                path.put((float) (pt.y - referencePoint.y));
                path.put((float) (pt.z - referencePoint.z));

                if (this.isExtrude())
                    this.appendTerrainPoint(dc, pos, path, pathData);
            }
        }
        else
        {
            for (Position pos : positions)
            {
                Vec4 pt = globe.computePointFromPosition(pos);
                path.put((float) (pt.x - referencePoint.x));
                path.put((float) (pt.y - referencePoint.y));
                path.put((float) (pt.z - referencePoint.z));

                if (this.isExtrude())
                    this.appendTerrainPoint(dc, pos, path, pathData);
            }
        }

        return path;
    }

    /**
     * Computes a point on a path and adds it to the renderable geometry. Used to generate extrusion vertices.
     *
     * @param dc       the current draw context.
     * @param position the path position.
     * @param path     the path to append to. Assumes that the path has adequate capacity.
     * @param pathData the current globe-specific path data.
     */
    protected void appendTerrainPoint(DrawContext dc, Position position, FloatBuffer path, PathData pathData)
    {
        Vec4 referencePoint = pathData.getReferencePoint();
        Vec4 pt = dc.computeTerrainPoint(position.getLatitude(), position.getLongitude(), 0d);
        path.put((float) (pt.x - referencePoint.x));
        path.put((float) (pt.y - referencePoint.y));
        path.put((float) (pt.z - referencePoint.z));

        pathData.hasExtrusionPoints = true;
    }

    /**
     * Generates positions defining this path with path type and terrain-conforming properties applied. Builds the
     * path's <code>tessellatedPositions</code> and <code>polePositions</code> fields.
     *
     * @param pathData the current globe-specific path data.
     * @param dc       the current draw context.
     */
    protected void makeTessellatedPositions(DrawContext dc, PathData pathData)
    {
        if (this.numPositions < 2)
            return;

        if (pathData.tessellatedPositions == null)
        {
            int size = (this.numSubsegments * (this.numPositions - 1) + 1) * (this.isExtrude() ? 2 : 1);
            pathData.tessellatedPositions = new ArrayList<Position>(size);
        }
        else
        {
            pathData.tessellatedPositions.clear();
        }

        if (pathData.polePositions == null || pathData.polePositions.capacity() < this.numPositions * 2)
            pathData.polePositions = BufferUtil.newIntBuffer(this.numPositions * 2);
        else
            pathData.polePositions.clear();

        Iterator<? extends Position> iter = this.positions.iterator();
        Position posA = iter.next();
        this.addTessellatedPosition(posA, true, pathData); // add the first position of the path

        // Tessellate each segment of the path.
        Vec4 ptA = this.computePoint(dc.getTerrain(), posA);

        for (int i = 1; i <= this.numPositions; i++)
        {
            Position posB;
            if (i < this.numPositions)
                posB = iter.next();
            else
                break;

            Vec4 ptB = this.computePoint(dc.getTerrain(), posB);

            // If the segment is very small or not visible, don't tessellate it, just add the segment's end position.
            if (this.isSmall(dc, ptA, ptB, 8) || !this.isSegmentVisible(dc, posA, posB, ptA, ptB))
                this.addTessellatedPosition(posB, true, pathData);
            else
                this.makeSegment(dc, posA, posB, ptA, ptB, pathData);

            posA = posB;
            ptA = ptB;
        }
    }

    /**
     * Adds a position to this path's <code>tessellated</code> list and optionally its <code>polePositions</code> list.
     *
     * @param pos          the position to add.
     * @param polePosition if true, add the positions index to the <code>polePositions</code>.
     * @param pathData     the current globe-specific path data.
     */
    protected void addTessellatedPosition(Position pos, boolean polePosition, PathData pathData)
    {
        if (polePosition)
        {
            int index = pathData.tessellatedPositions.size() * 2;
            pathData.polePositions.put(index).put(index + 1);
        }

        pathData.tessellatedPositions.add(pos); // be sure to do the add after the pole position is set
    }

    /**
     * Determines whether the segment between two path positions is visible.
     *
     * @param dc   the current draw context.
     * @param posA the segment's first position.
     * @param posB the segment's second position.
     * @param ptA  the model-coordinate point corresponding to the segment's first position.
     * @param ptB  the model-coordinate point corresponding to the segment's second position.
     *
     * @return true if the segment is visible relative to the current view frustum, otherwise false.
     */
    protected boolean isSegmentVisible(DrawContext dc, Position posA, Position posB, Vec4 ptA, Vec4 ptB)
    {
        Frustum f = dc.getView().getFrustumInModelCoordinates();

        if (f.contains(ptA))
            return true;

        if (f.contains(ptB))
            return true;

        if (ptA.equals(ptB))
            return false;

        Position posC = Position.interpolateRhumb(0.5, posA, posB);
        Vec4 ptC = this.computePoint(dc.getTerrain(), posC);
        if (f.contains(ptC))
            return true;

        double r = Line.distanceToSegment(ptA, ptB, ptC);
        Cylinder cyl = new Cylinder(ptA, ptB, r == 0 ? 1 : r);
        return cyl.intersects(dc.getView().getFrustumInModelCoordinates());
    }

    /**
     * Creates the interior segment positions to adhere to the current path type and terrain-following settings.
     *
     * @param dc       the current draw context.
     * @param posA     the segment's first position.
     * @param posB     the segment's second position.
     * @param ptA      the model-coordinate point corresponding to the segment's first position.
     * @param ptB      the model-coordinate point corresponding to the segment's second position.
     * @param pathData the current globe-specific path data.
     */
    @SuppressWarnings( {"StringEquality"})
    protected void makeSegment(DrawContext dc, Position posA, Position posB, Vec4 ptA, Vec4 ptB, PathData pathData)
    {
        // This method does not add the first position of the segment to the position list. It adds only the
        // subsequent positions, including the segment's last position.

        double arcLength =
            this.getPathType() == AVKey.LINEAR ? ptA.distanceTo3(ptB) : this.computeSegmentLength(dc, posA, posB);
        if (arcLength <= 0 || (this.getPathType() == AVKey.LINEAR && !this.isFollowTerrain()))
        {
            if (!ptA.equals(ptB))
                this.addTessellatedPosition(posB, true, pathData);
            return;
        }

        // Variables for great circle and rhumb computation.
        Angle segmentAzimuth = null;
        Angle segmentDistance = null;

        for (double s = 0, p = 0; s < 1;)
        {
            if (this.isFollowTerrain())
                p += this.terrainConformance * dc.getView().computePixelSizeAtDistance(
                    ptA.distanceTo3(dc.getView().getEyePoint()));
            else
                p += arcLength / this.numSubsegments;

            Position pos;

            s = p / arcLength;
            if (s >= 1)
            {
                pos = posB;
            }
            else if (this.pathType == AVKey.RHUMB_LINE || this.pathType == AVKey.LINEAR) // or LOXODROME
            {
                if (segmentAzimuth == null)
                {
                    segmentAzimuth = LatLon.rhumbAzimuth(posA, posB);
                    segmentDistance = LatLon.rhumbDistance(posA, posB);
                }
                Angle distance = Angle.fromRadians(s * segmentDistance.radians);
                LatLon latLon = LatLon.rhumbEndPosition(posA, segmentAzimuth, distance);
                pos = new Position(latLon, (1 - s) * posA.getElevation() + s * posB.getElevation());
            }
            else // GREAT_CIRCLE
            {
                if (segmentAzimuth == null)
                {
                    segmentAzimuth = LatLon.greatCircleAzimuth(posA, posB);
                    segmentDistance = LatLon.greatCircleDistance(posA, posB);
                }
                Angle distance = Angle.fromRadians(s * segmentDistance.radians);
                LatLon latLon = LatLon.greatCircleEndPosition(posA, segmentAzimuth, distance);
                pos = new Position(latLon, (1 - s) * posA.getElevation() + s * posB.getElevation());
            }

            this.addTessellatedPosition(pos, s >= 1, pathData);

            ptA = ptB;
        }
    }

    /**
     * Computes the approximate model-coordinate, great-circle length between two positions.
     *
     * @param dc   the current draw context.
     * @param posA the first position.
     * @param posB the second position.
     *
     * @return the distance between the positions.
     */
    protected double computeSegmentLength(DrawContext dc, Position posA, Position posB)
    {
        LatLon llA = new LatLon(posA.getLatitude(), posA.getLongitude());
        LatLon llB = new LatLon(posB.getLatitude(), posB.getLongitude());

        Angle ang = LatLon.greatCircleDistance(llA, llB);

        if (this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND)
            return ang.radians * (dc.getGlobe().getRadius());

        double height = 0.5 * (posA.getElevation() + posB.getElevation());
        return ang.radians * (dc.getGlobe().getRadius() + height * dc.getVerticalExaggeration());
    }

    /**
     * Computes this path's reference center.
     *
     * @param dc the current draw context.
     *
     * @return the computed reference center, or null if it cannot be computed.
     */
    protected Vec4 computeReferenceCenter(DrawContext dc)
    {
        if (this.positions == null)
            return null;

        Position pos = this.getReferencePosition();
        if (pos == null)
            return null;

        return dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
            dc.getVerticalExaggeration() * pos.getAltitude());
    }

    /**
     * Computes the minimum distance between this Path and the eye point.
     * <p/>
     * A {@link gov.nasa.worldwind.render.AbstractShape.AbstractShapeData} must be current when this method is called.
     *
     * @param dc       the draw context.
     * @param pathData the current shape data for this shape.
     *
     * @return the minimum distance from the shape to the eye point.
     */
    protected double computeEyeDistance(DrawContext dc, PathData pathData)
    {
        double minDistanceSquared = Double.MAX_VALUE;
        Vec4 eyePoint = dc.getView().getEyePoint();
        Vec4 refPt = pathData.getReferencePoint();

        pathData.renderedPath.rewind();
        while (pathData.renderedPath.hasRemaining())
        {
            double x = eyePoint.x - (pathData.renderedPath.get() + refPt.x);
            double y = eyePoint.y - (pathData.renderedPath.get() + refPt.y);
            double z = eyePoint.z - (pathData.renderedPath.get() + refPt.z);

            double d = x * x + y * y + z * z;
            if (d < minDistanceSquared)
                minDistanceSquared = d;
        }

        return Math.sqrt(minDistanceSquared);
    }

    /**
     * Computes the path's bounding box from the current rendering path. Assumes the rendering path is up-to-date.
     *
     * @param current the current data for this shape.
     *
     * @return the computed extent.
     */
    protected Extent computeExtent(PathData current)
    {
        if (current.renderedPath == null)
            return null;

        current.renderedPath.rewind();
        Box box = Box.computeBoundingBox(new BufferWrapper.FloatBufferWrapper(current.renderedPath));

        // The path points are relative to the reference center, so translate the extent to the reference center.
        box = box.translate(current.getReferencePoint()); // TODO

        return box;
    }

    public Extent getExtent(Globe globe, double verticalExaggeration)
    {
        // See if we've cached an extent associated with the globe.
        Extent extent = super.getExtent(globe, verticalExaggeration);
        if (extent != null)
            return extent;

        PathData current = (PathData) this.shapeDataCache.getEntry(globe);
        if (current == null)
            return null;

        // Use the tessellated positions if they exist because they best represent the actual shape.
        Iterable<? extends Position> posits = current.tessellatedPositions != null
            ? current.tessellatedPositions : this.getPositions();
        if (posits == null)
            return null;

        return super.computeExtentFromPositions(globe, verticalExaggeration, posits);
    }

    /**
     * Computes the path's reference position. The position returned is the center-most ordinal position in the path's
     * specified positions.
     *
     * @return the computed reference position.
     */
    public Position getReferencePosition()
    {
        return this.numPositions < 1 ? null : this.positions.iterator().next(); // use the first position
    }

    protected void fillVBO(DrawContext dc)
    {
        PathData pathData = this.getCurrentPathData();
        int numIds = pathData.hasExtrusionPoints && this.isDrawVerticals() ? 2 : 1;

        int[] vboIds = (int[]) dc.getGpuResourceCache().get(pathData.getVboCacheKey());
        if (vboIds != null && vboIds.length != numIds)
        {
            this.clearCachedVbos(dc);
            vboIds = null;
        }

        GL gl = dc.getGL();

        int vSize = pathData.renderedPath.limit() * 4;
        int iSize = pathData.hasExtrusionPoints && this.isDrawVerticals() ? this.numPositions * 2 * 4 : 0;

        if (vboIds == null)
        {
            vboIds = new int[numIds];
            gl.glGenBuffers(vboIds.length, vboIds, 0);
            dc.getGpuResourceCache().put(pathData.getVboCacheKey(), vboIds, GpuResourceCache.VBO_BUFFERS,
                vSize + iSize);
        }

        try
        {
            FloatBuffer vb = pathData.renderedPath;
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboIds[0]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, vb.limit() * 4, vb.rewind(), GL.GL_STATIC_DRAW);

            if (pathData.hasExtrusionPoints && this.isDrawVerticals())
            {
                IntBuffer ib = pathData.polePositions;
                gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
                gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ib.limit() * 4, ib.rewind(), GL.GL_STATIC_DRAW);
            }
        }
        finally
        {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    @Override
    public List<Intersection> intersect(Line line, Terrain terrain) throws InterruptedException // TODO
    {
        return null;
    }

    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this Path has no positions. In this case moving the Path by a
        // relative delta is meaningless because the Path has no geographic location. Therefore we fail softly by
        // exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.numPositions == 0)
            return;

        Position oldPosition = this.getReferencePosition();

        // The reference position is null if this Path has no positions. In this case moving the Path to a new
        // reference position is meaningless because the Path has no geographic location. Therefore we fail softly
        // by exiting and doing nothing.
        if (oldPosition == null)
            return;

        List<Position> newPositions = Position.computeShiftedPositions(oldPosition, position, this.positions);

        if (newPositions != null)
            this.setPositions(newPositions);
    }

    protected boolean isSmall(DrawContext dc, Vec4 ptA, Vec4 ptB, int numPixels)
    {
        return ptA.distanceTo3(ptB) <= numPixels * dc.getView().computePixelSizeAtDistance(
            dc.getView().getEyePoint().distanceTo3(ptA));
    }

    /** {@inheritDoc} */
    protected void doExportAsKML(XMLStreamWriter xmlWriter) throws IOException, XMLStreamException
    {
        // Write geometry
        xmlWriter.writeStartElement("LineString");

        xmlWriter.writeStartElement("extrude");
        xmlWriter.writeCharacters(kmlBoolean(isExtrude()));
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("tessellate");
        xmlWriter.writeCharacters(kmlBoolean(isFollowTerrain()));
        xmlWriter.writeEndElement();

        final String altitudeMode = KMLExportUtil.kmlAltitudeMode(getAltitudeMode());
        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters(altitudeMode);
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("coordinates");
        for (Position position : this.positions)
        {
            xmlWriter.writeCharacters(String.format("%f,%f,%f ",
                position.getLongitude().getDegrees(),
                position.getLatitude().getDegrees(),
                position.getElevation()));
        }
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement(); // LineString
        xmlWriter.writeEndElement(); // Placemark
    }
}
