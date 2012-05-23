/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.examples.multiwindow;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.Earth.*;
import gov.nasa.worldwind.util.StatusBar;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows how to create two World Windows, each in its own JFrame. The World Windows share a globe and some
 * layers.
 * <p/>
 * Applications using multiple World Wind windows simultaneously should instruct World Wind to share OpenGL and other
 * resources among those windows. Most World Wind classes are designed to be shared across {@link WorldWindow} objects
 * and are shared automatically. But OpenGL resources are not automatically shared. To share them, a reference to a
 * previously created WorldWindow must be specified as a constructor argument for subsequently created WorldWindows.
 * <p/>
 * Most World Wind {@link gov.nasa.worldwind.globes.Globe} and {@link gov.nasa.worldwind.layers.Layer} objects can be
 * shared among World Windows. Those that cannot be shared have an operational dependency on the World Window they're
 * associated with. An example is the {@link gov.nasa.worldwind.layers.ViewControlsLayer} layer for on-screen
 * navigation. Because this layer responds to input events within a specific World Window, it is not sharable. Refer to
 * the World Wind Overview page for a list of layers that cannot be shared.
 * // TODO: include the reference to overview.html.
 *
 * @author tag
 * @version $Id$
 */
public class MultiFrame
{
    // A panel to hold a World Window and status bar.
    private static class WWPanel extends JPanel
    {
        private WorldWindowGLCanvas wwd;

        public WWPanel(WorldWindowGLCanvas shareWith, int width, int height, Model model)
        {
            // To share resources among World Windows, pass the first World Window to the constructor of the other
            // World Windows.
            this.wwd = shareWith != null ? new WorldWindowGLCanvas(shareWith) : new WorldWindowGLCanvas();
            this.wwd.setSize(new java.awt.Dimension(width, height));
            this.wwd.setModel(model);

            this.setLayout(new BorderLayout(5, 5));
            this.add(this.wwd, BorderLayout.CENTER);

            StatusBar statusBar = new StatusBar();
            statusBar.setEventSource(wwd);
            this.add(statusBar, BorderLayout.SOUTH);
        }
    }

    // A JFrame to hold one World Window panel. Multiple of these are created in main below.
    private static class CanvasFrame extends javax.swing.JFrame
    {
        private WWPanel wwp;

        public CanvasFrame(WorldWindow shareWith, Model model, String side)
        {
            this.getContentPane().setLayout(new BorderLayout(5, 5));

            this.wwp = new WWPanel((WorldWindowGLCanvas) shareWith, 500, 500, model);
            this.getContentPane().add(wwp, BorderLayout.CENTER);

            this.pack();

            java.awt.Dimension wwSize = this.getPreferredSize();
            wwSize.setSize(wwSize.getWidth(), 1.1 * wwSize.getHeight());
            this.setSize(wwSize);

            // Position the windows side-by-side.
            java.awt.Dimension parentSize;
            java.awt.Point parentLocation = new java.awt.Point(0, 0);
            parentSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            int x = parentLocation.x + (parentSize.width / 2 + (side.equals("left") ? -wwSize.width : 20));
            int y = parentLocation.y + (parentSize.height - wwSize.height) / 2;
            this.setLocation(x, y);
            this.setResizable(true);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            // Create a Model for each window, starting with the Globe they share.
            Globe earth = new Earth();

            // Create layers that both World Windows can share.
            Layer[] layers = new Layer[]
                {
                    new StarsLayer(),
                    new CompassLayer(),
                    new BMNGWMSLayer(),
                    new LandsatI3WMSLayer(),
                };

            // Create two models and pass them the shared layers.
            Model modelForWindowA = new BasicModel();
            modelForWindowA.setGlobe(earth);
            modelForWindowA.setLayers(new LayerList(layers));

            Model modelForWindowB = new BasicModel();
            modelForWindowB.setGlobe(earth);
            modelForWindowB.setLayers(new LayerList(layers));

            // Create two frames and give each their own model.
            CanvasFrame frameA = new CanvasFrame(null, modelForWindowA, "left");
            frameA.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameA.setTitle("Frame A");
            frameA.wwp.wwd.setModel(modelForWindowA);
            frameA.setVisible(true);

            // When creating the second frame, specify resource sharing with the first one.
            CanvasFrame frameB = new CanvasFrame(frameA.wwp.wwd, modelForWindowB, "right");
            frameB.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameB.setTitle("Frame B");
            frameB.wwp.wwd.setModel(modelForWindowB);
            frameB.setVisible(true);

            // Add view control layers, which the World Windows cannnot share.
            ViewControlsLayer viewControlsA = new ViewControlsLayer();
            frameA.wwp.wwd.getModel().getLayers().add(viewControlsA);
            frameA.wwp.wwd.addSelectListener(new ViewControlsSelectListener(frameA.wwp.wwd, viewControlsA));

            ViewControlsLayer viewControlsB = new ViewControlsLayer();
            frameB.wwp.wwd.getModel().getLayers().add(viewControlsB);
            frameB.wwp.wwd.addSelectListener(new ViewControlsSelectListener(frameB.wwp.wwd, viewControlsB));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
