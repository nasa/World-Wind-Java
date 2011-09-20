/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import android.graphics.Point;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.cache.GpuResourceCache;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.util.*;

import java.util.Collection;

/**
 * @author dcollins
 * @version $Id$
 */
public class DrawContextImpl extends WWObjectImpl implements DrawContext
{
    protected static final double DEFAULT_VERTICAL_EXAGGERATION = 1;

    protected int viewportWidth;
    protected int viewportHeight;
    protected Color clearColor = new Color(0, 0, 0, 0);
    protected Model model;
    protected View view;
    protected double verticalExaggeration = DEFAULT_VERTICAL_EXAGGERATION;
    protected Sector visibleSector;
    protected GpuResourceCache gpuResourceCache;
    protected SectorGeometryList surfaceGeometry;
    protected SurfaceTileRenderer surfaceTileRenderer = new SurfaceTileRenderer();
    protected Layer currentLayer;
    protected GpuProgram currentProgram;
    protected long frameTimestamp;
    protected boolean pickingMode;
    protected boolean deepPickingMode;
    protected int uniquePickNumber;
    protected Point pickPoint;
    protected PickedObjectList pickedObjects = new PickedObjectList();
    protected Collection<PerformanceStatistic> perFrameStatistics;

    public DrawContextImpl()
    {
    }

    /** {@inheritDoc} */
    public void initialize(int viewportWidth, int viewportHeight)
    {
        if (viewportWidth < 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", viewportWidth);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportHeight < 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", viewportHeight);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.model = null;
        this.view = null;
        this.verticalExaggeration = DEFAULT_VERTICAL_EXAGGERATION;
        this.visibleSector = null;
        this.gpuResourceCache = null;
        this.surfaceGeometry = null;
        this.currentLayer = null;
        this.currentProgram = null;
        this.frameTimestamp = 0;
        this.perFrameStatistics = null;
        this.pickingMode = false;
        this.deepPickingMode = false;
        this.pickPoint = null;
        this.pickedObjects.clear();
    }

    /** {@inheritDoc} */
    public int getViewportWidth()
    {
        return this.viewportWidth;
    }

    /** {@inheritDoc} */
    public int getViewportHeight()
    {
        return this.viewportHeight;
    }

    /** {@inheritDoc} */
    public Color getClearColor()
    {
        return this.clearColor;
    }

    /** {@inheritDoc} */
    public Model getModel()
    {
        return this.model;
    }

    /** {@inheritDoc} */
    public void setModel(Model model)
    {
        this.model = model;
    }

    /** {@inheritDoc} */
    public View getView()
    {
        return this.view;
    }

    /** {@inheritDoc} */
    public void setView(View view)
    {
        this.view = view;
    }

    /** {@inheritDoc} */
    public Globe getGlobe()
    {
        return this.model != null ? this.model.getGlobe() : null;
    }

    /** {@inheritDoc} */
    public LayerList getLayers()
    {
        return this.model != null ? this.model.getLayers() : null;
    }

    /** {@inheritDoc} */
    public double getVerticalExaggeration()
    {
        return this.verticalExaggeration;
    }

    /** {@inheritDoc} */
    public void setVerticalExaggeration(double verticalExaggeration)
    {
        this.verticalExaggeration = verticalExaggeration;
    }

    /** {@inheritDoc} */
    public Sector getVisibleSector()
    {
        return this.visibleSector;
    }

    /** {@inheritDoc} */
    public void setVisibleSector(Sector sector)
    {
        this.visibleSector = sector;
    }

    /** {@inheritDoc} */
    public GpuResourceCache getGpuResourceCache()
    {
        return this.gpuResourceCache;
    }

    /** {@inheritDoc} */
    public void setGpuResourceCache(GpuResourceCache cache)
    {
        if (cache == null)
        {
            String msg = Logging.getMessage("nullValue.CacheIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.gpuResourceCache = cache;
    }

    /** {@inheritDoc} */
    public SectorGeometryList getSurfaceGeometry()
    {
        return this.surfaceGeometry;
    }

    /** {@inheritDoc} */
    public void setSurfaceGeometry(SectorGeometryList surfaceGeometry)
    {
        this.surfaceGeometry = surfaceGeometry;
    }

    /** {@inheritDoc} */
    public SurfaceTileRenderer getSurfaceTileRenderer()
    {
        return this.surfaceTileRenderer;
    }

    /** {@inheritDoc} */
    public Layer getCurrentLayer()
    {
        return this.currentLayer;
    }

    /** {@inheritDoc} */
    public void setCurrentLayer(Layer layer)
    {
        this.currentLayer = layer;
    }

    /** {@inheritDoc} */
    public GpuProgram getCurrentProgram()
    {
        return this.currentProgram;
    }

    /** {@inheritDoc} */
    public void setCurrentProgram(GpuProgram program)
    {
        this.currentProgram = program;
    }

    /** {@inheritDoc} */
    public long getFrameTimeStamp()
    {
        return this.frameTimestamp;
    }

    /** {@inheritDoc} */
    public void setFrameTimeStamp(long timeStamp)
    {
        this.frameTimestamp = timeStamp;
    }

    /** {@inheritDoc} */
    public boolean isPickingMode()
    {
        return this.pickingMode;
    }

    /** {@inheritDoc} */
    public void setPickingMode(boolean tf)
    {
        this.pickingMode = tf;
    }

    /** {@inheritDoc} */
    public boolean isDeepPickingEnabled()
    {
        return this.deepPickingMode;
    }

    /** {@inheritDoc} */
    public void setDeepPickingEnabled(boolean tf)
    {
        this.deepPickingMode = tf;
    }

    /** {@inheritDoc} */
    public Color getUniquePickColor()
    {
        this.uniquePickNumber++;

        int clearColorCode = this.getClearColor().getRGB();
        if (clearColorCode == this.uniquePickNumber)
            this.uniquePickNumber++;

        if (this.uniquePickNumber >= 0x00FFFFFF)
        {
            this.uniquePickNumber = 1;  // no black, no white
            if (clearColorCode == this.uniquePickNumber)
                this.uniquePickNumber++;
        }

        return new Color(this.uniquePickNumber, true); // has alpha
    }

    /** {@inheritDoc} */
    public Point getPickPoint()
    {
        return pickPoint;
    }

    /** {@inheritDoc} */
    public void setPickPoint(Point point)
    {
        this.pickPoint = point;
    }

    /** {@inheritDoc} */
    public PickedObjectList getPickedObjects()
    {
        return this.pickedObjects;
    }

    /** {@inheritDoc} */
    public void addPickedObject(PickedObject pickedObject)
    {
        if (pickedObject == null)
        {
            String msg = Logging.getMessage("nullValue.PickedObject");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.pickedObjects.add(pickedObject);
    }

    /** {@inheritDoc} */
    public Collection<PerformanceStatistic> getPerFrameStatistics()
    {
        return this.perFrameStatistics;
    }

    /** {@inheritDoc} */
    public void setPerFrameStatistics(Collection<PerformanceStatistic> perFrameStatistics)
    {
        this.perFrameStatistics = perFrameStatistics;
    }

    /** {@inheritDoc} */
    public void addPerFrameStatistic(String key, String displayName, Object value)
    {
        if (this.perFrameStatistics == null)
            return;

        if (WWUtil.isEmpty(key))
        {
            String msg = Logging.getMessage("nullValue.KeyIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (WWUtil.isEmpty(displayName))
        {
            String msg = Logging.getMessage("nullValue.NameIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.perFrameStatistics.add(new PerformanceStatistic(key, displayName, value));
    }
}
