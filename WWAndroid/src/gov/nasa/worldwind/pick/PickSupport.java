/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.pick;

import android.graphics.Point;
import android.opengl.GLES20;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class PickSupport
{
    protected Map<Integer, PickedObject> pickableObjects = new HashMap<Integer, PickedObject>();

    public void clearPickList()
    {
        this.getPickableObjects().clear();
    }

    public void addPickableObject(int colorCode, Object o, Position position, boolean isTerrain)
    {
        this.getPickableObjects().put(colorCode, new PickedObject(colorCode, o, position, isTerrain));
    }

    public void addPickableObject(int colorCode, Object o, Position position)
    {
        this.getPickableObjects().put(colorCode, new PickedObject(colorCode, o, position, false));
    }

    public void addPickableObject(int colorCode, Object o)
    {
        this.getPickableObjects().put(colorCode, new PickedObject(colorCode, o));
    }

    public void addPickableObject(PickedObject po)
    {
        this.getPickableObjects().put(po.getColorCode(), po);
    }

    public PickedObject getTopObject(DrawContext dc, Point pickPoint)
    {
        if (this.getPickableObjects().isEmpty())
            return null;

        int colorCode = this.getTopColor(dc, pickPoint);
        if (colorCode == dc.getClearColor().getRGB())
            return null;

        PickedObject pickedObject = getPickableObjects().get(colorCode);
        if (pickedObject == null)
            return null;

        return pickedObject;
    }

    public PickedObject resolvePick(DrawContext dc, Point pickPoint, Layer layer)
    {
        PickedObject pickedObject = this.getTopObject(dc, pickPoint);
        if (pickedObject != null)
        {
            if (layer != null)
                pickedObject.setParentLayer(layer);

            dc.addPickedObject(pickedObject);
        }

        this.clearPickList();

        return pickedObject;
    }

    public int getTopColor(DrawContext dc, Point pickPoint)
    {
        if (pickPoint == null)
            return 0;

        java.nio.ByteBuffer pixel = ByteBuffer.allocate(3);
        int yInGLCoords = dc.getView().getViewport().top - pickPoint.y - 1;
        GLES20.glReadPixels(pickPoint.x, yInGLCoords, 1, 1,
            GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, pixel);

        Color topColor = null;
        try
        {
            topColor = new Color(pixel.get(0) & 0xff, pixel.get(1) & 0xff, pixel.get(2) & 0xff, 0);
        }
        catch (Exception e)
        {
            Logging.error("layers.InvalidPickColorRead");
        }

        return topColor != null ? topColor.getRGB() : 0;
    }

    public void beginPicking(DrawContext dc)
    {
        // TODO: is this necessary?
        //gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT);

        GLES20.glDisable(GLES20.GL_DITHER);
        //GLES20.glDisable(GL.GL_LIGHTING);
        //GLES20.glDisable(GL.GL_FOG);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);

        if (dc.isDeepPickingEnabled())
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void endPicking(DrawContext dc)
    {
        // TODO: GLES20 equivalent?
        //dc.getGL().glPopAttrib();
    }

    protected Map<Integer, PickedObject> getPickableObjects()
    {
        return this.pickableObjects;
    }

    /**
     * Indicates whether two picked objects refer to the same user object.
     *
     * @param a the first picked object.
     * @param b the second picked object.
     *
     * @return true if both objects are not null and they refer to the same user object, otherwise false.
     */
    public static boolean areSelectionsTheSame(PickedObject a, PickedObject b)
    {
        return a != null && b != null && a.getObject() == b.getObject();
    }
}

