/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers;

import android.graphics.Point;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class AbstractLayer extends WWObjectImpl implements Layer
{
    protected abstract void doRender(DrawContext dc);

    protected boolean enabled = true;
    protected boolean pickable = true;
    protected boolean networkDownloadEnabled = true;
    protected double minActiveAltitude = -Double.MAX_VALUE;
    protected double maxActiveAltitude = Double.MAX_VALUE;
    protected FileStore fileStore = WorldWind.getDataFileStore();

    protected AbstractLayer()
    {
        this.init();
    }

    protected AbstractLayer(AVList params)
    {
        if (params == null)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        this.validateParams(params, sb);
        if (sb.length() > 0)
        {
            String msg = Logging.getMessage("generic.ParamsAreInvalid", sb.toString());
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithParams(params);
    }

    protected AbstractLayer(Element element)
    {
        if (element == null)
        {
            String msg = Logging.getMessage("nullValue.ElementIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        this.validateConfigDoc(element, sb);
        if (sb.length() > 0)
        {
            String msg = Logging.getMessage("generic.ConfigDocIsInvalid", sb.toString());
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithConfigDoc(element);
    }

    protected void initWithParams(AVList params)
    {
        Object o = params.getValue(AVKey.DISPLAY_NAME);
        if (o != null && o instanceof String && !WWUtil.isEmpty(o))
            this.setName((String) params.getValue(AVKey.DISPLAY_NAME));

        o = params.getValue(AVKey.MIN_ACTIVE_ALTITUDE);
        if (o != null && o instanceof Number)
            this.setMinActiveAltitude(((Number) o).doubleValue());

        o = params.getValue(AVKey.MAX_ACTIVE_ALTITUDE);
        if (o != null && o instanceof Number)
            this.setMaxActiveAltitude(((Number) o).doubleValue());

        this.init();
    }

    protected void initWithConfigDoc(Element element)
    {
        XPath xpath = WWXML.makeXPath();

        String s = WWXML.getText(element, "DisplayName", xpath);
        if (s != null && !WWUtil.isEmpty(s))
            this.setName(s);

        // TODO: set min/max active altitude from config doc

        this.init();
    }

    protected void init()
    {
    }

    /**
     * Determines whether the constructor arguments are valid.
     *
     * @param params the list of parameters to validate.
     * @param sb     the StringBuilder to append a description of why it's invalid.
     */
    protected void validateParams(AVList params, StringBuilder sb)
    {
    }

    /**
     * Determines whether the constructor configuration document is valid.
     *
     * @param element the configuration document element.
     * @param sb      the StringBuilder to append a description of why it's invalid.
     */
    protected void validateConfigDoc(Element element, StringBuilder sb)
    {
    }

    /** {@inheritDoc} */
    public String getName()
    {
        Object n = this.getValue(AVKey.DISPLAY_NAME);
        return n != null ? n.toString() : null;
    }

    /** {@inheritDoc} */
    public void setName(String name)
    {
        this.setValue(AVKey.DISPLAY_NAME, name);
    }

    /** {@inheritDoc} */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /** {@inheritDoc} */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /** {@inheritDoc} */
    public boolean isPickEnabled()
    {
        return pickable;
    }

    /** {@inheritDoc} */
    public void setPickEnabled(boolean enabled)
    {
        this.pickable = enabled;
    }

    /** {@inheritDoc} */
    public boolean isNetworkRetrievalEnabled()
    {
        return this.networkDownloadEnabled;
    }

    /** {@inheritDoc} */
    public void setNetworkRetrievalEnabled(boolean enabled)
    {
        this.networkDownloadEnabled = enabled;
    }

    /** {@inheritDoc} */
    public double getMinActiveAltitude()
    {
        return minActiveAltitude;
    }

    /** {@inheritDoc} */
    public void setMinActiveAltitude(double altitude)
    {
        this.minActiveAltitude = altitude;
    }

    /** {@inheritDoc} */
    public double getMaxActiveAltitude()
    {
        return maxActiveAltitude;
    }

    /** {@inheritDoc} */
    public void setMaxActiveAltitude(double altitude)
    {
        this.maxActiveAltitude = altitude;
    }

    public FileStore getDataFileStore()
    {
        return this.fileStore;
    }

    public void setDataFileStore(FileStore fileStore)
    {
        if (fileStore == null)
        {
            String message = Logging.getMessage("nullValue.FileStoreIsNull");
            Logging.error(message);
            throw new IllegalStateException(message);
        }

        this.fileStore = fileStore;
    }

    /** {@inheritDoc} */
    public boolean isLayerActive(DrawContext dc)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalStateException(msg);
        }

        if (dc.getView() == null)
            return true;

        Vec4 eyePoint = dc.getView().getEyePoint();
        Position eyePos = dc.getGlobe().computePositionFromPoint(eyePoint);
        if (eyePos == null)
            return true;

        double altitude = eyePos.elevation;
        return altitude >= this.minActiveAltitude && altitude <= this.maxActiveAltitude;
    }

    /** {@inheritDoc} */
    public boolean isLayerInView(DrawContext dc)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return true;
    }

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        if (!this.enabled)
            return; // Don't check for arg errors if we're disabled

        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        long beginTime = System.currentTimeMillis();

        if (!this.isLayerActive(dc))
            return;

        if (!this.isLayerInView(dc))
            return;

        this.doRender(dc);

        long endTime = System.currentTimeMillis();
        dc.addPerFrameStatistic(PerformanceStatistic.LAYER_FRAME_TIME, "Frame Time (ms): " + this.getName(),
            endTime - beginTime);
    }

    /** {@inheritDoc} */
    public void pick(DrawContext dc, Point pickPoint)
    {
        if (!this.enabled || !this.pickable)
            return; // Don't check for arg errors if we're disabled

        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        long beginTime = System.currentTimeMillis();

        if (!this.isLayerActive(dc))
            return;

        if (!this.isLayerInView(dc))
            return;

        this.doPick(dc, pickPoint);

        long endTime = System.currentTimeMillis();
        dc.addPerFrameStatistic(PerformanceStatistic.LAYER_PICK_TIME, "Pick Time (ms): " + this.getName(),
            endTime - beginTime);
    }

    protected void doPick(DrawContext dc, Point pickPoint)
    {
        // any state that could change the color needs to be disabled, such as GL_TEXTURE, GL_LIGHTING or GL_FOG.
        // re-draw with unique colors
        // store the object info in the selectable objects table
        // read the color under the cursor
        // use the color code as a key to retrieve a selected object from the selectable objects table
        // create an instance of the PickedObject and add to the dc via the dc.addPickedObject() method
    }

    @Override
    public String toString()
    {
        String n = this.getName();
        return n != null ? n : super.toString();
    }
}
