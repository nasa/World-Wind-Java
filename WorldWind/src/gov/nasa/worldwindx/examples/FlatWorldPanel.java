/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.view.orbit.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel to control a flat or round world projection. The panel includes a radio button to switch between flat and round
 * globes, and a list box of map projections for the flat globe. The panel is attached to a WorldWindow, and changes
 * the WorldWindow to match the users globe selection.
 *
 * @author Patrick Murris
 * @version $Id$
 */

public class FlatWorldPanel extends JPanel
{
    private WorldWindow wwd;
    private Globe roundGlobe;
    private FlatGlobe flatGlobe;
    private JComboBox projectionCombo;

    public FlatWorldPanel(WorldWindow wwd)
    {
        super(new GridLayout(0, 1, 0, 0));
        this.wwd = wwd;
        if (isFlatGlobe())
        {
            this.flatGlobe = (FlatGlobe)wwd.getModel().getGlobe();
            this.roundGlobe = new Earth();
        }
        else
        {
            this.flatGlobe = new EarthFlat();
            this.roundGlobe = wwd.getModel().getGlobe();
        }
        this.makePanel();
    }

    private JPanel makePanel()
    {
        JPanel controlPanel = this;
        controlPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("World")));
        controlPanel.setToolTipText("Set the current projection");

        // Flat vs round buttons
        JPanel radioButtonPanel = new JPanel(new GridLayout(0, 2, 0, 0));
        radioButtonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JRadioButton roundRadioButton = new JRadioButton("Round");
        roundRadioButton.setSelected(!isFlatGlobe());
        roundRadioButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                projectionCombo.setEnabled(false);
                enableFlatGlobe(false);
            }
        });
        radioButtonPanel.add(roundRadioButton);
        JRadioButton flatRadioButton = new JRadioButton("Flat");
        flatRadioButton.setSelected(isFlatGlobe());
        flatRadioButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                projectionCombo.setEnabled(true);
                enableFlatGlobe(true);
            }
        });
        radioButtonPanel.add(flatRadioButton);
        ButtonGroup group = new ButtonGroup();
        group.add(roundRadioButton);
        group.add(flatRadioButton);

        // Projection combo
        JPanel comboPanel = new JPanel(new GridLayout(0, 2, 0, 0));
        comboPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        comboPanel.add(new JLabel("Projection:"));
        this.projectionCombo = new JComboBox(new String[] {"Mercator", "Lat-Lon", "Modified Sin.", "Sinusoidal"});
        this.projectionCombo.setEnabled(isFlatGlobe());
        this.projectionCombo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                updateProjection();
            }
        });
        comboPanel.add(this.projectionCombo);

        controlPanel.add(radioButtonPanel);
        controlPanel.add(comboPanel);
        return controlPanel;
    }

    // Update flat globe projection
    private void updateProjection()
    {
        if (!isFlatGlobe())
                return;

        // Update flat globe projection
        this.flatGlobe.setProjection(this.getProjection());
        this.wwd.redraw();
    }

    private String getProjection()
    {
        String item = (String) projectionCombo.getSelectedItem();
        if(item.equals("Mercator"))
            return FlatGlobe.PROJECTION_MERCATOR;
        else if(item.equals("Sinusoidal"))
            return FlatGlobe.PROJECTION_SINUSOIDAL;
        else if(item.equals("Modified Sin."))
            return FlatGlobe.PROJECTION_MODIFIED_SINUSOIDAL;
        // Default to lat-lon
        return FlatGlobe.PROJECTION_LAT_LON;
    }

    public boolean isFlatGlobe()
    {
        return wwd.getModel().getGlobe() instanceof FlatGlobe;
    }

    public void enableFlatGlobe(boolean flat)
    {
        if(isFlatGlobe() == flat)
            return;

        if(!flat)
        {
            // Switch to round globe
            wwd.getModel().setGlobe(roundGlobe) ;
            // Switch to orbit view and update with current position
            FlatOrbitView flatOrbitView = (FlatOrbitView)wwd.getView();
            BasicOrbitView orbitView = new BasicOrbitView();
            orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
            orbitView.setZoom(flatOrbitView.getZoom( ));
            orbitView.setHeading(flatOrbitView.getHeading());
            orbitView.setPitch(flatOrbitView.getPitch());
            wwd.setView(orbitView);
            // Change sky layer
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyColorLayer)
                    layers.set(i, new SkyGradientLayer());
            }
        }
        else
        {
            // Switch to flat globe
            wwd.getModel().setGlobe(flatGlobe);
            flatGlobe.setProjection(this.getProjection());
            // Switch to flat view and update with current position
            BasicOrbitView orbitView = (BasicOrbitView)wwd.getView();
            FlatOrbitView flatOrbitView = new FlatOrbitView();
            flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
            flatOrbitView.setZoom(orbitView.getZoom( ));
            flatOrbitView.setHeading(orbitView.getHeading());
            flatOrbitView.setPitch(orbitView.getPitch());
            wwd.setView(flatOrbitView);
            // Change sky layer
            LayerList layers = wwd.getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof SkyGradientLayer)
                    layers.set(i, new SkyColorLayer());
            }
        }
        
        wwd.redraw();
    }

}
