/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;
import gov.nasa.worldwindx.examples.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Demonstrates how to use the {@link gov.nasa.worldwindx.examples.util.ScreenSelector} utility.
 *
 * @author dcollins
 * @version $Id$
 */
public class ScreenSelection extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected ScreenSelector selector;

        public AppFrame()
        {
            // Create a screen selector to display a screen selection rectangle and specify the scene controllers
            // current pick rectangle.
            this.selector = new ScreenSelector(this.getWwd());

            // Create a button to enable and disable screen selection.
            JButton btn = new JButton(new EnableSelectorAction());
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.add(btn, BorderLayout.CENTER);
            this.getLayerPanel().add(panel, BorderLayout.SOUTH);

            // Create layer of highlightable shapes to select.
            this.addShapes();
        }

        @Override
        protected ApplicationTemplate.AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            return new AppPanel(canvasSize, includeStatusBar);
        }

        protected void addShapes()
        {
            RenderableLayer layer = new RenderableLayer();

            ShapeAttributes highlightAttrs = new BasicShapeAttributes();
            highlightAttrs.setInteriorMaterial(Material.RED);
            highlightAttrs.setOutlineMaterial(Material.WHITE);

            for (int lon = -180; lon < 180; lon += 10)
            {
                for (int lat = -90; lat < 90; lat += 10)
                {
                    ExtrudedPolygon poly = new ExtrudedPolygon(Arrays.asList(
                        LatLon.fromDegrees(lat - 1, lon - 1),
                        LatLon.fromDegrees(lat - 1, lon + 1),
                        LatLon.fromDegrees(lat + 1, lon + 1),
                        LatLon.fromDegrees(lat + 1, lon - 1)),
                        100000d);
                    poly.setHighlightAttributes(highlightAttrs);
                    poly.setSideHighlightAttributes(highlightAttrs);
                    layer.addRenderable(poly);
                }
            }

            this.getWwd().getModel().getLayers().add(layer);
        }

        protected class EnableSelectorAction extends AbstractAction
        {
            public EnableSelectorAction()
            {
                super("Start");
            }

            public void actionPerformed(ActionEvent actionEvent)
            {
                ((JButton) actionEvent.getSource()).setAction(new DisableSelectorAction());
                selector.enable();
            }
        }

        protected class DisableSelectorAction extends AbstractAction
        {
            public DisableSelectorAction()
            {
                super("Stop");
            }

            public void actionPerformed(ActionEvent actionEvent)
            {
                ((JButton) actionEvent.getSource()).setAction(new EnableSelectorAction());
                selector.disable();
            }
        }
    }

    public static class AppPanel extends ApplicationTemplate.AppPanel
    {
        public AppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            super(canvasSize, includeStatusBar);

            // Set up a custom highlight controller that highlights objects under the cursor and inside the selection
            // box.
            this.highlightController.dispose();
            this.highlightController = new BoxHighlightController(this.getWwd());
        }
    }

    protected static class BoxHighlightController extends HighlightController
    {
        protected List<Highlightable> lastBoxHighlightObjects = new ArrayList<Highlightable>();

        public BoxHighlightController(WorldWindow wwd)
        {
            super(wwd, SelectEvent.ROLLOVER);
        }

        public void selected(SelectEvent event)
        {
            super.selected(event);

            try
            {
                if (event.getEventAction().equals(SelectEvent.BOX_ROLLOVER))
                    this.highlightObjectsInBox(event.getAllTopObjects());
            }
            catch (Exception e)
            {
                // Wrap the handler in a try/catch to keep exceptions from bubbling up
                Util.getLogger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
            }
        }

        protected void highlight(Object o)
        {
            // Determine if the highlighted object under the cursor has changed, but should remain highlighted because
            // its in the selection box. In this case we assign the highlighted object under the cursor to null and
            // return, and thereby avoid changing the highlight state of objects still highlighted by the selection box.
            if (this.lastHighlightObject != o && this.lastBoxHighlightObjects.contains(this.lastHighlightObject))
            {
                this.lastHighlightObject = null;
                return;
            }

            super.highlight(o);
        }

        protected void highlightObjectsInBox(java.util.List<?> list)
        {
            if (this.lastBoxHighlightObjects.equals(list))
                return; // same thing selected

            // Turn off highlight for the last set of selected objects, if any. Since one of these objects may still be
            // highlighted due to a cursor rollover, we detect that object and avoid changing its highlight state.
            for (Highlightable h : this.lastBoxHighlightObjects)
            {
                if (h != this.lastHighlightObject)
                    h.setHighlighted(false);
            }
            this.lastBoxHighlightObjects.clear();

            if (list != null)
            {
                // Turn on highlight if object selected.
                for (Object o : list)
                {
                    if (o instanceof Highlightable)
                    {
                        ((Highlightable) o).setHighlighted(true);
                        this.lastBoxHighlightObjects.add((Highlightable) o);
                    }
                }
            }

            // We've potentially changed the highlight state of one or more objects. Request that the world window
            // redraw itself in order to refresh these object's display. This is necessary because changes in the
            // objects in the pick rectangle do not necessarily correspond to mouse movements. For example, the pick
            // rectangle may be cleared when the user releases the mouse button at the end of a drag. In this case,
            // there's no mouse movement to cause an automatic redraw.
            this.wwd.redraw();
        }
    }

    public static void main(String[] args)
    {
        start("World Wind Screen Selection", AppFrame.class);
    }
}
