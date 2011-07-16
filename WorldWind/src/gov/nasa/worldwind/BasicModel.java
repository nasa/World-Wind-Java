/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.Message;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.util.Logging;
import org.w3c.dom.Element;

import java.util.logging.Level;

/**
 * This class aggregates the objects making up a model: the globe and layers. Through the globe it also indirectly
 * includes the elevation model and the surface geometry tessellator. A default model is defined in
 * <code>worldwind.xml</code> or its application-specified alternate.
 *
 * @author Tom Gaskins
 * @version $Id$
 */
public class BasicModel extends WWObjectImpl implements Model
{
    private Globe globe;
    private LayerList layers;
    private boolean showWireframeInterior = false;
    private boolean showWireframeExterior = false;
    private boolean showTessellationBoundingVolumes = false;

    public BasicModel()
    {
        String globeName = Configuration.getStringValue(AVKey.GLOBE_CLASS_NAME);
        if (globeName == null)
            return;

        this.setGlobe((Globe) WorldWind.createComponent(globeName));

        // Look for the old-style, property-based layer configuration first. If not found then use the new-style
        // configuration.
        LayerList layers = null;
        String layerNames = Configuration.getStringValue(AVKey.LAYERS_CLASS_NAMES);
        if (layerNames != null)
        {
            // Usage of this deprecated method is intentional. It provides backwards compatibility for deprecated
            // functionality.
            //noinspection deprecation
            layers = this.createLayersFromProperties(layerNames);
        }
        else
        {
            Element el = Configuration.getElement("./LayerList");
            if (el != null)
                layers = this.createLayersFromElement(el);
        }

        this.setLayers(layers != null ? layers : new LayerList(/*empty list*/)); // an empty list is ok
    }

    public BasicModel(Globe globe, LayerList layers)
    {
        this.setGlobe(globe);
        this.setLayers(layers != null ? layers : new LayerList(/*empty list*/)); // an empty list is ok
    }

    /**
     * Create the layer list from an XML configuration element.
     *
     * @param element the configuration description.
     *
     * @return a new layer list matching the specified description.
     */
    protected LayerList createLayersFromElement(Element element)
    {
        Object o = BasicFactory.create(AVKey.LAYER_FACTORY, element);

        if (o instanceof LayerList)
            return (LayerList) o;

        if (o instanceof Layer)
            return new LayerList(new Layer[] {(Layer) o});

        if (o instanceof LayerList[])
        {
            LayerList[] lists = (LayerList[]) o;
            if (lists.length > 0)
                return LayerList.collapseLists((LayerList[]) o);
        }

        return null;
    }

    /**
     * Create the layer list from the old-style properties list of layer class names.
     *
     * @param layerNames a comma separated list of layer class names.
     *
     * @return a new layer list containing the specified layers.
     *
     * @deprecated Use {@link #createLayersFromElement(org.w3c.dom.Element)} instead.
     */
    protected LayerList createLayersFromProperties(String layerNames)
    {
        LayerList layers = new LayerList();
        if (layerNames == null)
            return null;

        String[] names = layerNames.split(",");
        for (String name : names)
        {
            try
            {
                if (name.length() > 0)
                {
                    Layer l = (Layer) WorldWind.createComponent(name);
                    layers.add(l);
                }
            }
            catch (Exception e)
            {
                Logging.logger().log(Level.WARNING, Logging.getMessage("BasicModel.LayerNotFound", name), e);
            }
        }

        return layers;
    }

    /**
     * Specifies the model's globe.
     *
     * @param globe the model's new globe. May be null, in which case the current globe will be detached from the
     *              model.
     */
    public void setGlobe(Globe globe)
    {
        // don't raise an exception if globe == null. In that case, we are disassociating the model from any globe

        //remove property change listener "this" from the current globe.
        if (this.globe != null)
            this.globe.removePropertyChangeListener(this);

        // if the new globe is not null, add "this" as a property change listener.
        if (globe != null)
            globe.addPropertyChangeListener(this);

        Globe old = this.globe;
        this.globe = globe;
        this.firePropertyChange(AVKey.GLOBE, old, this.globe);
    }

    /**
     * Specifies the model's layers.
     *
     * @param layers the model's new layers. May be null, in which case the current layers will be detached from the
     *               model.
     */
    public void setLayers(LayerList layers)
    {
        // don't raise an exception if layers == null. In that case, we are disassociating the model from any layer set

        if (this.layers != null)
            this.layers.removePropertyChangeListener(this);
        if (layers != null)
            layers.addPropertyChangeListener(this);

        LayerList old = this.layers;
        this.layers = layers;
        this.firePropertyChange(AVKey.LAYERS, old, this.layers);
    }

    public Globe getGlobe()
    {
        return this.globe;
    }

    public LayerList getLayers()
    {
        return this.layers;
    }

    /**
     * Specifies whether to display as wireframe the interior geometry of the tessellated globe surface.
     *
     * @param show true causes the geometry to be shown, false, the default, does not.
     */
    public void setShowWireframeInterior(boolean show)
    {
        this.showWireframeInterior = show;
    }

    /**
     * Specifies whether to display as wireframe the exterior geometry of the tessellated globe surface.
     *
     * @param show true causes the geometry to be shown, false, the default, does not.
     */
    public void setShowWireframeExterior(boolean show)
    {
        this.showWireframeExterior = show;
    }

    /**
     * Indicates whether the globe surface's interior geometry is to be drawn.
     *
     * @return true if it is to be drawn, otherwise false.
     */
    public boolean isShowWireframeInterior()
    {
        return this.showWireframeInterior;
    }

    /**
     * Indicates whether the globe surface's exterior geometry is to be drawn.
     *
     * @return true if it is to be drawn, otherwise false.
     */
    public boolean isShowWireframeExterior()
    {
        return this.showWireframeExterior;
    }

    /**
     * Indicates whether the bounding volumes of the tessellated globe's surface geometry should be displayed.
     *
     * @return true if the bounding volumes are to be drawn, otherwise false.
     */
    public boolean isShowTessellationBoundingVolumes()
    {
        return showTessellationBoundingVolumes;
    }

    /**
     * Specifies whether the bounding volumes of the globes tessellated surface geometry is to be drawn.
     *
     * @param showTessellationBoundingVolumes
     *         true if the bounding volumes should be drawn, false, the default, if not.
     */
    public void setShowTessellationBoundingVolumes(boolean showTessellationBoundingVolumes)
    {
        this.showTessellationBoundingVolumes = showTessellationBoundingVolumes;
    }

    /**
     * Returns the bounding sphere in Cartesian world coordinates of the model.
     *
     * @return the model's bounding sphere in Cartesian coordinates, or null if the extent cannot be determined.
     */
    public Extent getExtent()
    {
        // See if the layers have it.
        LayerList layers = BasicModel.this.getLayers();
        if (layers != null)
        {
            for (Object layer1 : layers)
            {
                Layer layer = (Layer) layer1;
                Extent e = (Extent) layer.getValue(AVKey.EXTENT);
                if (e != null)
                    return e;
            }
        }

        // See if the Globe has it.
        Globe globe = this.getGlobe();
        if (globe != null)
        {
            Extent e = globe.getExtent();
            if (e != null)
                return e;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation forwards the message each layer in the model.
     *
     * @param msg The message that was received.
     */
    @Override
    public void onMessage(Message msg)
    {
        if (this.getLayers() != null)
        {
            for (Layer layer : this.getLayers())
            {
                try
                {
                    if (layer != null)
                    {
                        layer.onMessage(msg);
                    }
                }
                catch (Exception e)
                {
                    String message = Logging.getMessage("generic.ExceptionInvokingMessageListener");
                    Logging.logger().log(Level.SEVERE, message, e);
                    // Don't abort; continue on to the next layer.
                }
            }
        }
    }
}