/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Demonstrates how to create and render MIL-STD-2525 tactical symbols. See the <a title="Symbology Usage Guide"
 * href="http://goworldwind.org/developers-guide/symbology/" target="_blank">Usage Guide</a> for more information on
 * symbology support in World Wind.
 *
 * @author dcollins
 * @version $Id$
 */
public class TacticalSymbols extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected RenderableLayer symbolLayer;
        protected TacticalSymbolAttributes sharedAttrs;
        protected TacticalSymbolAttributes sharedHighlightAttrs;

        public AppFrame()
        {
            // Create a renderable layer to display the tactical symbol. This example adds only two symbols, but many
            // symbols can be added to a single layer. Note that tactical symbols and tactical graphics can be combined
            // in a single layer.
            this.symbolLayer = new RenderableLayer();
            this.symbolLayer.setName("Tactical Symbols");

            // Create attribute bundles that are shared by all tactical symbols. Changes to these attribute bundles are
            // reflected in all symbols.
            this.sharedAttrs = new BasicTacticalSymbolAttributes();
            this.sharedHighlightAttrs = new BasicTacticalSymbolAttributes();

            // Create an air tactical symbol for the MIL-STD-2525 symbology set. This symbol identifier specifies a
            // MIL-STD-2525 friendly Special Operations Forces Drone Aircraft. MilStd2525TacticalSymbol automatically
            // sets the altitude mode to WorldWind.ABSOLUTE.
            TacticalSymbol airSymbol = new MilStd2525TacticalSymbol("SFAPMFQM-------",
                Position.fromDegrees(32.4520, 63.44553, 3000));
            airSymbol.setAttributes(this.sharedAttrs);
            airSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
            airSymbol.setModifier(SymbologyConstants.ECHELON, SymbologyConstants.ECHELON_DIVISION);
            airSymbol.setModifier(SymbologyConstants.TASK_FORCE, Boolean.TRUE);
            airSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(235));
            this.symbolLayer.addRenderable(airSymbol);

            // Create a ground tactical symbol for the MIL-STD-2525 symbology set. This symbol identifier specifies
            // multiple hostile Self-Propelled Rocket Launchers. MilStd2525TacticalSymbol automatically sets the
            // altitude mode to WorldWind.CLAMP_TO_GROUND.
            TacticalSymbol groundSymbol = new MilStd2525TacticalSymbol("SHGXUCFRMS-----",
                Position.fromDegrees(32.4014, 63.3894, 0));
            groundSymbol.setAttributes(this.sharedAttrs);
            groundSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
            groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(90));
            groundSymbol.setModifier(SymbologyConstants.SPEED_LEADER_SCALE, 0.5);
            this.symbolLayer.addRenderable(groundSymbol);

            // Create a heavy US machine gun tactical symbol with a friendly frame. This symbol is taken from the
            // MIL-STD-2525C specification section 5.9.3 (page 49).
            TacticalSymbol machineGunSymbol = new MilStd2525TacticalSymbol("SFGPEWRH--MTUSG",
                Position.fromDegrees(32.3902, 63.4161, 0));
            machineGunSymbol.setAttributes(this.sharedAttrs);
            machineGunSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
            machineGunSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(300));
            machineGunSymbol.setModifier(SymbologyConstants.SPEED_LEADER_SCALE, 0.5);
            machineGunSymbol.setModifier(SymbologyConstants.QUANTITY, 200);
            machineGunSymbol.setModifier(SymbologyConstants.STAFF_COMMENTS, "FOR REINFORCEMENTS");
            machineGunSymbol.setModifier(SymbologyConstants.ADDITIONAL_INFORMATION, "ADDED SUPPORT FOR JJ");
            machineGunSymbol.setModifier(SymbologyConstants.TYPE, "MACHINE GUN");
            machineGunSymbol.setModifier(SymbologyConstants.DATE_TIME_GROUP, "30140000ZSEP97");
            machineGunSymbol.setModifier(SymbologyConstants.LOCATION, "32.39020N063.41610E");
            this.symbolLayer.addRenderable(machineGunSymbol);

            // Add the symbol layer to the World Wind model.
            this.getWwd().getModel().getLayers().add(symbolLayer);

            // Update the layer panel to display the symbol layer.
            this.getLayerPanel().update(this.getWwd());

            // Create a Swing control panel that provides user control over the symbol's appearance.
            this.addSymbolControls();
        }

        protected void addSymbolControls()
        {
            Box box = Box.createVerticalBox();
            box.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel label = new JLabel("Scale");
            JSlider slider = new JSlider(0, 100, 100);
            slider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent changeEvent)
                {
                    JSlider slider = (JSlider) changeEvent.getSource();
                    double scale = (double) slider.getValue() / 100d;
                    sharedAttrs.setScale(scale);
                    sharedHighlightAttrs.setScale(scale);
                    getWwd().redraw();
                }
            });
            label.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            slider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            box.add(label);
            box.add(slider);

            label = new JLabel("Opacity");
            slider = new JSlider(0, 100, 100);
            slider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent changeEvent)
                {
                    JSlider slider = (JSlider) changeEvent.getSource();
                    double opacity = (double) slider.getValue() / 100d;
                    sharedAttrs.setOpacity(opacity);
                    getWwd().redraw();
                }
            });
            box.add(Box.createVerticalStrut(10));
            label.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            slider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            box.add(label);
            box.add(slider);

            JCheckBox cb = new JCheckBox("Graphic Modifiers", true);
            cb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    boolean tf = ((JCheckBox) actionEvent.getSource()).isSelected();

                    for (Renderable r : symbolLayer.getRenderables())
                    {
                        if (r instanceof TacticalSymbol)
                            ((TacticalSymbol) r).setShowGraphicModifiers(tf);
                        getWwd().redraw();
                    }
                }
            });
            cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            box.add(Box.createVerticalStrut(10));
            box.add(cb);

            cb = new JCheckBox("Text Modifiers", true);
            cb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    boolean tf = ((JCheckBox) actionEvent.getSource()).isSelected();

                    for (Renderable r : symbolLayer.getRenderables())
                    {
                        if (r instanceof TacticalSymbol)
                            ((TacticalSymbol) r).setShowTextModifiers(tf);
                        getWwd().redraw();
                    }
                }
            });
            cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            box.add(Box.createVerticalStrut(10));
            box.add(cb);

            this.getLayerPanel().add(box, BorderLayout.SOUTH);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 32.49);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 63.455);
        Configuration.setValue(AVKey.INITIAL_HEADING, 24);
        Configuration.setValue(AVKey.INITIAL_PITCH, 80);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 18000);

        start("World Wind Tactical Symbols", AppFrame.class);
    }
}
