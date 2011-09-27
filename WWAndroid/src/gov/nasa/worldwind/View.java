/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import android.graphics.Rect;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

/**
 * @author dcollins
 * @version $Id$
 */
public interface View extends WWObject
{
    Matrix getModelviewMatrix();

    /**
     * Gets the projection matrix. The projection matrix transforms eye coordinates to screen coordinates. This matrix
     * is constructed using the projection parameters specific to each implementation of <code>View</code>. The method
     * {@link #getFrustum} returns the geometry corresponding to this matrix. This value is computed in the most recent
     * call to <code>apply</code>.
     *
     * @return the current projection matrix.
     */
    Matrix getProjectionMatrix();

    Matrix getModelviewProjectionMatrix();

    /**
     * Returns the bounds (left, bottom, right, top) of the viewport. The implementation will configure itself to render
     * in this viewport. This value is computed in the most recent call to <code>apply</code>.
     *
     * @return the Rect of the viewport.
     */
    Rect getViewport();

    /**
     * Returns the near clipping plane distance, in eye coordinates.  Implementations of the <code>View</code> interface
     * are not required to have a method for setting the near and far distance.
     *
     * @return near clipping plane distance, in eye coordinates.
     */
    double getNearClipDistance();

    /**
     * Returns the far clipping plane distance, in eye coordinates. Implementations of the <code>View</code> interface
     * are not required to have a method for setting the near and far distance.
     *
     * @return far clipping plane distance, in eye coordinates.
     */
    double getFarClipDistance();

    /**
     * Returns the viewing <code>Frustum</code> in eye coordinates. The <code>Frustum</code> is the portion of viewable
     * space defined by three sets of parallel 'clipping' planes. This value is computed in the most recent call to
     * <code>apply</code>.
     *
     * @return viewing Frustum in eye coordinates.
     */
    Frustum getFrustum();

    /**
     * Returns the viewing <code>Frustum</code> in model coordinates. Model coordinate frustums are useful for
     * performing visibility tests against world geometry. This frustum has the same shape as the frustum returned in
     * <code>getFrustum</code>, but it has been transformed into model space. This value is computed in the most recent
     * call to <code>apply</code>.
     *
     * @return viewing Frustum in model coordinates.
     */
    Frustum getFrustumInModelCoordinates();

    /**
     * Returns the horizontal field-of-view angle (the angle of visibility), or null if the implementation does not
     * support a field-of-view.
     *
     * @return Angle of the horizontal field-of-view, or null if none exists.
     */
    Angle getFieldOfView();

    /**
     * Sets the horizontal field-of-view angle (the angle of visibility) to the specified <code>fieldOfView</code>. This
     * may be ignored if the implementation that do not support a field-of-view.
     *
     * @param fieldOfView the horizontal field-of-view angle.
     *
     * @throws IllegalArgumentException If the implementation supports field-of-view, and <code>fieldOfView</code> is
     *                                  null.
     */
    void setFieldOfView(Angle fieldOfView);

    /**
     * Gets the current eye point in world coordinates.
     *
     * @return the current eye point
     */
    Vec4 getEyePoint();

    /**
     * Returns the current geographic coordinates of this view's eye position, corresponding to this view's most recent
     * state.
     *
     * @return the position of the eye corresponding to the most recent state of this view
     */
    Position getEyePosition(Globe globe);

    void apply(DrawContext dc);
}
