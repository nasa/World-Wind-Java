/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel to display a list of layers. A layer can be turned on or off by clicking a check box next to the layer name.
 *
 * @version $Id$
 *
 * @see LayerTreeUsage
 * @see OnScreenLayerManager
 */
public class LayerPanel extends JPanel
{
    protected JPanel layersPanel;
    protected JPanel westPanel;
    protected JScrollPane scrollPane;
    protected Font defaultFont;

    /**
     * Create a panel with the default size.
     *
     * @param wwd WorldWindow to supply the layer list.
     */
    public LayerPanel(WorldWindow wwd)
    {
        // Make a panel at a default size.
        super(new BorderLayout());
        this.makePanel(wwd, new Dimension(200, 400));
    }

    /**
     * Create a panel with a size.
     *
     * @param wwd  WorldWindow to supply the layer list.
     * @param size Size of the panel.
     */
    public LayerPanel(WorldWindow wwd, Dimension size)
    {
        // Make a panel at a specified size.
        super(new BorderLayout());
        this.makePanel(wwd, size);
    }

    protected void makePanel(WorldWindow wwd, Dimension size)
    {
        // Make and fill the panel holding the layer titles.
        this.layersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.layersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(wwd);

        // Must put the layer grid in a container to prevent scroll panel from stretching their vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.layersPanel, BorderLayout.NORTH);

        // Put the name panel in a scroll bar.
        this.scrollPane = new JScrollPane(dummyPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        if (size != null)
            this.scrollPane.setPreferredSize(size);

        // Add the scroll bar and name panel to a titled panel that will resize with the main window.
        westPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        westPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Layers")));
        westPanel.setToolTipText("Layers to Show");
        westPanel.add(scrollPane);
        this.add(westPanel, BorderLayout.CENTER);
    }

    protected void fill(WorldWindow wwd)
    {
        // Fill the layers panel with the titles of all layers in the world window's current model.
        for (Layer layer : wwd.getModel().getLayers())
        {
            LayerAction action = new LayerAction(layer, wwd, layer.isEnabled());
            JCheckBox jcb = new JCheckBox(action);
            jcb.setSelected(action.selected);
            this.layersPanel.add(jcb);

            if (defaultFont == null)
            {
                this.defaultFont = jcb.getFont();
            }
        }
    }

    /**
     * Update the panel to match the layer list active in a WorldWindow.
     *
     * @param wwd WorldWindow that will supply the new layer list.
     */
    public void update(WorldWindow wwd)
    {
        // Replace all the layer names in the layers panel with the names of the current layers.
        this.layersPanel.removeAll();
        this.fill(wwd);
        this.westPanel.revalidate();
        this.westPanel.repaint();
    }

    @Override
    public void setToolTipText(String string)
    {
        this.scrollPane.setToolTipText(string);
    }

    protected static class LayerAction extends AbstractAction
    {
        WorldWindow wwd;
        private Layer layer;
        private boolean selected;

        public LayerAction(Layer layer, WorldWindow wwd, boolean selected)
        {
            super(layer.getName());
            this.wwd = wwd;
            this.layer = layer;
            this.selected = selected;
            this.layer.setEnabled(this.selected);
        }

        public void actionPerformed(ActionEvent actionEvent)
        {
            // Simply enable or disable the layer based on its toggle button.
            if (((JCheckBox) actionEvent.getSource()).isSelected())
                this.layer.setEnabled(true);
            else
                this.layer.setEnabled(false);

            wwd.redraw();
        }
    }
}
