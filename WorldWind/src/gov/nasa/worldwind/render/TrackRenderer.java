/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.tracks.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.Disposable;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLU;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class TrackRenderer implements Disposable
{
    protected int lowerLimit = 0;
    protected int upperLimit = Integer.MAX_VALUE;
    protected final Shape SPHERE = new Sphere();
    protected final Shape CONE = new Cone();
    protected final Shape CYLINDER = new Cylinder();
    protected PickSupport pickSupport = new PickSupport();

    private double elevation = 10d;
    private boolean overrideMarkerElevation = false;
    private Object client;
    private double markerPixels = 8d; // TODO: these should all be configurable
    private double minMarkerSize = 3d;
    private Material material = Material.WHITE;
    private Shape shape = SPHERE;
    private boolean keepSeparated = true;

    public TrackRenderer()
    {
    }

    public void dispose()
    {
        this.CONE.dispose();
        this.CYLINDER.dispose();
        this.SPHERE.dispose();
    }

    public double getMarkerPixels()
    {
        return markerPixels;
    }

    public void setMarkerPixels(double markerPixels)
    {
        this.markerPixels = markerPixels;
    }

    public double getMinMarkerSize()
    {
        return minMarkerSize;
    }

    public void setMinMarkerSize(double minMarkerSize)
    {
        this.minMarkerSize = minMarkerSize;
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial(Material material)
    {
        if (material == null)
        {
            String msg = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // don't validate material's colors - material does that.

        this.material = material;
    }

    public void setShapeType(String shapeName)
    {
        if (shapeName.equalsIgnoreCase("Cone"))
            this.shape = CONE;
        else if (shapeName.equalsIgnoreCase("Cylinder"))
            this.shape = CYLINDER;
        else
            this.shape = SPHERE;
    }

    public boolean isKeepSeparated()
    {
        return keepSeparated;
    }

    public void setKeepSeparated(boolean keepSeparated)
    {
        this.keepSeparated = keepSeparated;
    }

    protected Vec4 draw(DrawContext dc, Iterator<TrackPoint> trackPositions)
    {
        if (dc.getVisibleSector() == null)
            return null;

        SectorGeometryList geos = dc.getSurfaceGeometry();
        if (geos == null)
            return null;

        if (!this.shape.isInitialized)
            this.shape.initialize(dc);

        Vec4 lastPointDrawn = null;

        this.begin(dc);
        {
            if (!dc.isPickingMode())
                this.material.apply(dc.getGL(), GL.GL_FRONT);

            Vec4 previousDrawnPoint = null;
            double radius;
            for (int index = 0; trackPositions.hasNext(); index++)
            {
                TrackPoint tp = trackPositions.next();

                if (index < this.lowerLimit)
                    continue;

                if (index > this.upperLimit)
                    break;

                Vec4 point = this.computeSurfacePoint(dc, tp);
                if (point == null)
                    continue;

                if (dc.isPickingMode())
                {
                    java.awt.Color color = dc.getUniquePickColor();
                    int colorCode = color.getRGB();
                    PickedObject po = new PickedObject(colorCode,
                        this.getClient() != null ? this.getClient() : tp.getPosition(), tp.getPosition(), false);
                    po.setValue(AVKey.PICKED_OBJECT_ID, index);
                    this.pickSupport.addPickableObject(po);
                    dc.getGL().glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                }

                radius = this.computeMarkerRadius(dc, point);

                if (previousDrawnPoint == null)
                {
                    // It's the first point drawn
                    previousDrawnPoint = point;
                    this.shape.render(dc, point, radius);
                    lastPointDrawn = point;
                    continue;
                }

                if (!this.keepSeparated)
                {
                    previousDrawnPoint = point;
                    this.shape.render(dc, point, radius);
                    lastPointDrawn = point;
                    continue;
                }

                double separation = point.distanceTo3(previousDrawnPoint);
                double minSeparation = 4d * radius;
                if (separation > minSeparation)
                {
                    previousDrawnPoint = point;
                    this.shape.render(dc, point, radius);
                    lastPointDrawn = point;
                }
            }
        }
        this.end(dc);

        return lastPointDrawn;
    }

    private double computeMarkerRadius(DrawContext dc, Vec4 point)
    {
        double d = point.distanceTo3(dc.getView().getEyePoint());
        double radius = this.markerPixels * dc.getView().computePixelSizeAtDistance(d);
        if (radius < this.minMarkerSize)
            radius = this.minMarkerSize;

        return radius;
    }

    public int getLowerLimit()
    {
        return this.lowerLimit;
    }

    public void setLowerLimit(int lowerLimit)
    {
        this.lowerLimit = lowerLimit;
    }

    public int getUpperLimit()
    {
        return this.upperLimit;
    }

    public void setUpperLimit(int upperLimit)
    {
        this.upperLimit = upperLimit;
    }

    public double getElevation()
    {
        return elevation;
    }

    public void setElevation(double elevation)
    {
        this.elevation = elevation;
    }

    public boolean isOverrideElevation()
    {
        return overrideMarkerElevation;
    }

    public Object getClient()
    {
        return client;
    }

    public void setClient(Object client)
    {
        this.client = client;
    }

    public void setOverrideElevation(boolean overrideMarkerElevation)
    {
        this.overrideMarkerElevation = overrideMarkerElevation;
    }

    protected Vec4 computeSurfacePoint(DrawContext dc, TrackPoint tp)
    {
        Position pos = tp.getPosition();

        if (!this.overrideMarkerElevation)
            return dc.getGlobe().computePointFromPosition(pos);

        // Compute points that are at the track-specified elevation
        Vec4 point = dc.getSurfaceGeometry().getSurfacePoint(pos.getLatitude(), pos.getLongitude(), this.elevation);
        if (point != null)
            return point;

        // Point is outside the current sector geometry, so compute it from the globe.
        return dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(), this.elevation);
    }

    protected void begin(DrawContext dc)
    {
        GL gl = dc.getGL();
        Vec4 cameraPosition = dc.getView().getEyePoint();

        if (dc.isPickingMode())
        {
            this.pickSupport.beginPicking(dc);

            gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_TRANSFORM_BIT);
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glDisable(GL.GL_COLOR_MATERIAL);
        }
        else
        {
            gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_LIGHTING_BIT | GL.GL_TRANSFORM_BIT);
            gl.glDisable(GL.GL_TEXTURE_2D);

            float[] lightPosition =
                {(float) (cameraPosition.x * 2), (float) (cameraPosition.y / 2), (float) (cameraPosition.z), 0.0f};
            float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
            float[] lightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
            float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};

            gl.glDisable(GL.GL_COLOR_MATERIAL);

            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightSpecular, 0);

            gl.glDisable(GL.GL_LIGHT0);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_NORMALIZE);
        }

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
    }

    protected void end(DrawContext dc)
    {
        GL gl = dc.getGL();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();

        if (dc.isPickingMode())
        {
            this.pickSupport.endPicking(dc);
        }
        else
        {
            gl.glDisable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glDisable(GL.GL_LIGHTING);
            gl.glDisable(GL.GL_NORMALIZE);
        }

        gl.glPopAttrib();
    }

    public Vec4 pick(DrawContext dc, Iterator<TrackPoint> trackPositions, java.awt.Point pickPoint, Layer layer)
    {
        this.pickSupport.clearPickList();
        Vec4 lastPointDrawn = this.draw(dc, trackPositions);
        this.pickSupport.resolvePick(dc, pickPoint, layer);
        this.pickSupport.clearPickList(); // to ensure entries can be garbage collected

        return lastPointDrawn;
    }

    public Vec4 render(DrawContext dc, Iterator<TrackPoint> trackPositions)
    {
        return this.draw(dc, trackPositions);
    }

    protected static abstract class Shape
    {
        protected String name;
        protected int glListId;
        protected GLUquadric quadric;
        protected boolean isInitialized = false;

        abstract protected void doRender(DrawContext dc, Vec4 point, double radius);

        protected void initialize(DrawContext dc)
        {
            this.glListId = dc.getGL().glGenLists(1);
            this.quadric = dc.getGLU().gluNewQuadric();
            dc.getGLU().gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            dc.getGLU().gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
            dc.getGLU().gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
            dc.getGLU().gluQuadricTexture(quadric, false);
        }

        private void dispose()
        {
            if (this.isInitialized)
            {
                GLU glu = new GLU();
                glu.gluDeleteQuadric(this.quadric);
                this.isInitialized = false;

                GLContext glc = GLContext.getCurrent();
                if (glc == null)
                    return;

                glc.getGL().glDeleteLists(this.glListId, 1);

                this.glListId = -1;
            }
        }

        protected void render(DrawContext dc, Vec4 point, double radius)
        {
            dc.getView().pushReferenceCenter(dc, point);
            this.doRender(dc, point, radius);
            dc.getView().popReferenceCenter(dc);
        }
    }

    private static class Sphere extends Shape
    {
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Sphere";
            double radius = 1;
            int slices = 36;
            int stacks = 18;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluSphere(this.quadric, radius, slices, stacks);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Vec4 point, double radius)
        {
            dc.getGL().glScaled(radius, radius, radius);
            dc.getGL().glCallList(this.glListId);
        }
    }

    private static class Cone extends Shape
    {
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Cone";
            int slices = 30;
            int stacks = 30;
            int loops = 2;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
            dc.getGLU().gluCylinder(quadric, 1d, 0d, 2d, slices, (int) (2 * (Math.sqrt(stacks)) + 1));
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Vec4 point, double size)
        {
            PolarPoint p = PolarPoint.fromCartesian(point);

            dc.getGL().glScaled(size, size, size);
            dc.getGL().glRotated(p.getLongitude().getDegrees(), 0, 1, 0);
            dc.getGL().glRotated(Math.abs(p.getLatitude().getDegrees()), Math.signum(p.getLatitude().getDegrees()) * -1,
                0, 0);
            dc.getGL().glCallList(this.glListId);
        }
    }

    protected static class Cylinder extends Shape
    {
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Cylinder";
            int slices = 30;
            int stacks = 1;
            int loops = 1;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluCylinder(quadric, 1d, 1d, 2d, slices, (int) (2 * (Math.sqrt(stacks)) + 1));
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glTranslated(0, 0, 2);
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glTranslated(0, 0, -2);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Vec4 point, double size)
        {
            PolarPoint p = PolarPoint.fromCartesian(point);

            dc.getGL().glScaled(size, size, size);
            dc.getGL().glRotated(p.getLongitude().getDegrees(), 0, 1, 0);
            dc.getGL().glRotated(Math.abs(p.getLatitude().getDegrees()), Math.signum(p.getLatitude().getDegrees()) * -1,
                0, 0);
            dc.getGL().glCallList(this.glListId);
        }
    }
}
