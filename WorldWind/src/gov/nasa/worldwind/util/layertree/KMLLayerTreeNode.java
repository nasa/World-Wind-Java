/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.util.layertree;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.tree.*;

import javax.swing.*;
import java.beans.*;

/**
 * A <code>LayerTreeNode</code> that represents a KML feature hierarchy defined by a <code>{@link
 * gov.nasa.worldwind.ogc.kml.KMLRoot}</code>.
 *
 * @author dcollins
 * @version $Id$
 * @see KMLFeatureTreeNode
 */
public class KMLLayerTreeNode extends LayerTreeNode
{
    /** Indicates the KML feature hierarchy this node represents. Initialized during construction. */
    protected KMLRoot kmlRoot;

    /**
     * Creates a new <code>KMLLayerTreeNode</code> from the specified <code>layer</code> and <code>kmlRoot</code>. The
     * node's name is set to the layer's name, and the node's hierarchy is populated from the feature hierarchy of the
     * <code>KMLRoot</code>.
     *
     * @param layer   the <code>Layer</code> the <code>kmlRoot</code> corresponds to.
     * @param kmlRoot the KML feature hierarchy this node represents.
     *
     * @throws IllegalArgumentException if the <code>layer</code> is <code>null</code>, or if <code>kmlRoot</code> is
     *                                  <code>null</code>.
     */
    public KMLLayerTreeNode(Layer layer, KMLRoot kmlRoot)
    {
        super(layer);

        if (kmlRoot == null)
        {
            String message = Logging.getMessage("nullValue.KMLRootIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.kmlRoot = kmlRoot;
        this.addChildFeatures();

        // Add a listener that will refresh the tree model when the KML document is updated.
        this.kmlRoot.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event != null && AVKey.UPDATED.equals(event.getPropertyName()))
                {
                    // Ensure that the node list is manipulated on the EDT
                    if (SwingUtilities.isEventDispatchThread())
                    {
                        KMLLayerTreeNode.this.refresh();
                    }
                    else
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                KMLLayerTreeNode.this.refresh();
                            }
                        });
                    }
                }
            }
        });

        // Set the context of the KML document node to the root feature in the document.
        this.setValue(AVKey.CONTEXT, kmlRoot.getFeature());
    }

    /**
     * Specifies whether this node's layer is enabled for rendering. If the KMLRoot's feature is a container (Document
     * or Folder), this method sets the visibility of that container as well as the node that represents the layer.
     *
     * @param selected <code>true</code> to enable the layer, otherwise <code>false</code>.
     */
    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);

        KMLAbstractFeature feature = this.kmlRoot.getFeature();
        if (feature instanceof KMLAbstractContainer)
        {
            feature.setVisibility(selected);
        }
    }

    /**
     * Adds a new <code>KMLFeatureTreeNode</code> to this node for each KML feature in the <code>KMLRoot</code>.
     * <p/>
     * If the <code>KMLRoot</code>'s top level feature is a <code>Document</code> or <code>Folder</code>, this method
     * ignores this container and adds its children directly to this node. Creating a node for the container adds an
     * extra level to the tree node that doesn't provide any meaningful grouping.
     * <p/>
     * This does nothing if the <code>KMLRoot</code>'s top level feature is <code>null</code>.
     */
    protected void addChildFeatures()
    {
        KMLAbstractFeature rootFeature = this.kmlRoot.getFeature();
        if (rootFeature == null)
            return;

        // A KML document has only one top-level feature. Except for very simple files, this top level is typically a
        // Document or Folder. In this case we skip the top level container, and attach tree nodes for the features
        // beneath the container. Attaching the container as a tree node would add an extra level to the tree that
        // doesn't provide any meaningful grouping.

        if (rootFeature instanceof KMLDocument || rootFeature instanceof KMLFolder)
        {
            KMLAbstractContainer container = (KMLAbstractContainer) rootFeature;
            for (KMLAbstractFeature child : container.getFeatures())
            {
                if (child != null)
                    this.addFeatureNode(child);
            }

            // Create a KMLFeatureTreeNode only to construct the description string for the container and set it on this
            // node. We do not add the container node to the tree.
            KMLFeatureTreeNode containerNode = KMLFeatureTreeNode.fromKMLFeature(container);
            this.setDescription(containerNode.getDescription());

            // Initialize the selected state of this node to match the visibility of the root container.
            Boolean visibility = container.getVisibility();
            this.setSelected(visibility == null || visibility);
        }
        else
        {
            this.addFeatureNode(rootFeature);
        }
    }

    /**
     * Adds the a new <code>KMLFeatureTreeNode</code> created with the specified <code>feature</code> to this node.
     *
     * @param feature the KML feature to add.
     */
    protected void addFeatureNode(KMLAbstractFeature feature)
    {
        TreeNode featureNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        if (featureNode != null)
            this.addChild(featureNode);
    }

    /**
     * Expands paths in the specified <code>tree</code> corresponding to open KML container elements. This assumes that
     * the <code>tree</code>'s model contains this node.
     * <p/>
     * This node's path is expanded if it's top level KML feature is an open KML container, an open KML network link, or
     * is any other kind of KML feature.
     * <p/>
     * This calls <code>expandOpenContainers</code> on all children which are instances of
     * <code>KMLFeatureTreeNode</code>.
     *
     * @param tree the <code>Tree</code> who's paths should be expanded.
     *
     * @throws IllegalArgumentException if the <code>tree</code> is null.
     */
    public void expandOpenContainers(Tree tree)
    {
        if (tree == null)
        {
            String message = Logging.getMessage("nullValue.TreeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.mustExpandNode())
            tree.expandPath(this.getPath());

        for (TreeNode child : this.getChildren())
        {
            if (child instanceof KMLFeatureTreeNode)
                ((KMLFeatureTreeNode) child).expandOpenContainers(tree);
        }
    }

    /**
     * Indicates whether the tree path for this node must expanded. If the <code>KMLRoot</code>'s feature is a KML
     * container or a KML network link, this returns whether that KML element's <code>open</code> property is
     * <code>true</code>. Otherwise this returns <code>true</code>
     *
     * @return <code>true</code> if the tree path for this node must be expanded, otherwise <code>false</code>.
     */
    protected boolean mustExpandNode()
    {
        if (this.kmlRoot.getFeature() instanceof KMLAbstractContainer
            || this.kmlRoot.getFeature() instanceof KMLNetworkLink)
        {
            return Boolean.TRUE.equals(this.kmlRoot.getFeature().getOpen());
        }

        return this.kmlRoot.getFeature() != null;
    }

    /** Refresh the tree model to match the contents of the KML document. */
    protected void refresh()
    {
        this.removeAllChildren();
        this.addChildFeatures();
    }
}
