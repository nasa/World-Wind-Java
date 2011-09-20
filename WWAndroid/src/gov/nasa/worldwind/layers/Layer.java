/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers;

import android.graphics.Point;
import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.render.DrawContext;

/**
 * @author dcollins
 * @version $Id$
 */
public interface Layer extends WWObject
{
    /**
     * Returns the layer's name, as specified in the most recent call to {@link #setName(String)}.
     *
     * @return the layer's name.
     */
    String getName();

    /**
     * Set the layer's name. The name is a convenience attribute typically used to identify the layer in user
     * interfaces. By default, a layer has no name.
     *
     * @param name the name to assign to the layer.
     */
    void setName(String name);

    /**
     * Indicates whether the layer is enabled for rendering and selection.
     *
     * @return true if the layer is enabled, else false.
     */
    boolean isEnabled();

    /**
     * Controls whether the layer is enabled for rendering and selection.
     *
     * @param enabled <code>true</code> if the layer is enabled, else <code>false</code>.
     */
    void setEnabled(boolean enabled);

    /**
     * Indicates whether the layer performs selection during picking.
     * <p/>
     * Most layers enable picking by default. However, this becomes inconvenient for @{link SurfaceImage} when the image
     * covers a large area because the view input handlers detect the surface image rather than the terrain as the top
     * picked object, and will not respond to the user's attempts at navigation. The solution is to disable picking for
     * the layer.
     *
     * @return <code>true</code> if picking is enabled, else <code>false</code>.
     */
    boolean isPickEnabled();

    /**
     * Controls whether the layer should perform picking.
     *
     * @param enabled <code>true</code> if the layer should perform picking, else <code>false</code>.
     */
    void setPickEnabled(boolean enabled);

    /**
     * Indicates whether the layer is allowed to retrieve data from the network. Many layers have no need to retrieve
     * data from the network. This state is meaningless for such layers.
     *
     * @return <code>true</code> if the layer is enabled to retrieve network data, else <code>false</code>.
     */
    boolean isNetworkRetrievalEnabled();

    /**
     * Controls whether the layer is allowed to retrieve data from the network. Many layers have no need for data from
     * the network. This state may be set but is meaningless for such layers.
     *
     * @param enabled <code>true</code> if network retrieval is allowed, else <code>false</code>.
     */
    void setNetworkRetrievalEnabled(boolean enabled);

    /**
     * Returns the minimum altitude at which the layer is displayed.
     *
     * @return the minimum altitude at which the layer is displayed.
     */
    double getMinActiveAltitude();

    /**
     * Specifies the minimum altitude at which to display the layer.
     *
     * @param altitude the minimum altitude at which to display the layer.
     */
    void setMinActiveAltitude(double altitude);

    /**
     * Returns the maximum altitude at which to display the layer.
     *
     * @return the maximum altitude at which to display the layer.
     */
    double getMaxActiveAltitude();

    /**
     * Specifies the maximum altitude at which to display the layer.
     *
     * @param altitude the maximum altitude at which to display the layer.
     */
    void setMaxActiveAltitude(double altitude);

    /**
     * Indicates whether the layer is active based on arbitrary criteria. The method implemented here is a default
     * indicating the layer is active if the current altitude is within the layer's min and max active altitudes.
     * Subclasses able to consider more criteria should override this implementation.
     *
     * @param dc the current draw context
     *
     * @return <code>true</code> if the layer is active, <code>false</code> otherwise.
     */
    boolean isLayerActive(DrawContext dc);

    /**
     * Indicates whether the layer is in the view. The method implemented here is a default indicating the layer is in
     * view. Subclasses able to determine their presence in the view should override this implementation.
     *
     * @param dc the current draw context
     *
     * @return <code>true</code> if the layer is in the view, <code>false</code> otherwise.
     */
    boolean isLayerInView(DrawContext dc);

    /**
     * Cause the layer to draw its representation.
     *
     * @param dc the current draw context for rendering.
     */
    void render(DrawContext dc);

    /**
     * Cause the layer to perform picking, which determines whether the object or its components intersect a given point
     * on the screen. Objects that intersect that point are added to the draw context's pick list and are conveyed to
     * the application via selection events.
     *
     * @param dc        the current draw context for rendering.
     * @param pickPoint the screen coordinate point.
     */
    void pick(DrawContext dc, Point pickPoint);
}
