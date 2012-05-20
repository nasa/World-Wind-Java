/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.DrawContextImpl;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * @author dcollins
 * @version $Id$
 */
public class RenderableLayerTest
{
    /*************************************************************************************************************/
    /** Basic Operation Tests **/
    /** ****************************************************************************************************** */

    @Test
    public void testConstructor()
    {
        RenderableLayer layer;

        // Test the parameterless constructor.
        layer = new RenderableLayer();
        assertNotNull("", layer);
    }

    @Test
    public void testAddRenderable()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        for (Renderable item : renderables)
        {
            layer.addRenderable(item);
        }
        
        // Test that the layer contains the renderables.
        assertEquals("", renderables, layer.getRenderables());
        }

    @Test
    public void testAddRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);

        // Test that the layer contains the renderables.
        assertEquals("", renderables, layer.getRenderables());
        }

    @Test
    public void testRemoveRenderable()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        for (Renderable item : renderables)
        {
            layer.addRenderable(item);
        }
        for (Renderable item : renderables)
        {
            layer.removeRenderable(item);
        }

        // Test that the layer contains no renderables.
        assertFalse("", layer.getRenderables().iterator().hasNext());
    }

    @Test
    public void testRemoveAllRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);
        layer.removeAllRenderables();

        // Test that the layer contains no renderables.
        assertFalse("", layer.getRenderables().iterator().hasNext());
    }

    @Test
    public void testSetRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Test that the layer points to the Iterable.
        assertSame("", renderables, layer.getRenderables());
    }

    /*************************************************************************************************************/
    /** Edge Case Tests **/
    /** ****************************************************************************************************** */

    @Test
    public void testSetRenderablesClearsRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);
        layer.setRenderables(renderables);
        layer.setRenderables(null);

        // Test that the layer does not point to the Iterable.
        assertNotSame("", renderables, layer.getRenderables());
        // Test that the layer contains no renderables.
        assertFalse("", layer.getRenderables().iterator().hasNext());
    }

    @Test
    public void testSetRenderablesThenAddRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);
        layer.setRenderables(null);
        layer.addRenderables(renderables);

        // Test that the layer does not point to the Iterable.
        assertNotSame("", renderables, layer.getRenderables());
        // Test that the layer contains the renderables.
        assertEquals("", renderables, layer.getRenderables());
    }

    @Test
    public void testMaliciousGetRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);

        Iterable<Renderable> layerRenderables = layer.getRenderables();

        // Test that the returned list cannot be modified.
        try
        {
            if (layerRenderables instanceof Collection)
            {
                Collection collection = (Collection) layerRenderables;
                collection.clear();
            }
            else
            {
                Iterator<Renderable> iter = layerRenderables.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                    iter.remove();
                }
            }
        }
        catch (UnsupportedOperationException e)
        {
            e.printStackTrace();
        }

        // Test that the layer contents do not change, even if the returned list can be modified.
        assertEquals("", renderables, layer.getRenderables());
    }

    @Test
    public void testMaliciousSetRenderables()
    {
        // Create an Iterable with null elements.
        List<Renderable> list = new ArrayList<Renderable>();
        list.add(null);

        RenderableLayer layer = new RenderableLayer()
        {
            // Override to avoid View initialization issues.
            public boolean isLayerActive(DrawContext dc)
            {
                return true;
            }
        };
        layer.setRenderables(list);

        DrawContext dc = new DrawContextImpl();
        dc.setModel(new BasicModel());
        dc.setView(new BasicOrbitView());

        try
        {
            // Test that the layer does not fail when the Iterable is used.
            layer.render(dc);
        }
        catch (NullPointerException e)
        {
            fail("Layer does not check for null elements in Iterable");
        }
    }

    @Test
    public void testDisposeDoesNotClearRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();
        Iterable<Renderable> emptyRenderables = new ArrayList<Renderable>();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);
        layer.dispose();

        // Test that the layer contains the renderables.
        assertEquals("", emptyRenderables, layer.getRenderables());
    }

    /*************************************************************************************************************/
    /** Exceptional Condition Tests **/
    /** ****************************************************************************************************** */

    @Test(expected = IllegalStateException.class)
    public void testAddRenderableFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Expecting an IllegalStateException here.
        layer.addRenderable(new Polyline());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddRenderablesFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Expecting an IllegalStateException here.
        layer.addRenderables(renderables);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveRenderableFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Expecting an IllegalStateException here.
        layer.removeRenderable(new Polyline());
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAllRenderablesFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Expecting an IllegalStateException here.
        layer.removeAllRenderables();
    }

    @Test(expected = IllegalStateException.class)
    public void testDisposeFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        // Expecting an IllegalStateException here.
        layer.dispose();
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ****************************************************************************************************** */

    @SuppressWarnings({"JavaDoc"})
    private static void assertEquals(String message, Iterable<Renderable> expected, Iterable<Renderable> actual)
    {
        if (expected == null)
        {
            assertNull(message, actual);
        }
        else
        {
            Iterator<Renderable> expectedIter = expected.iterator(), actualIter = actual.iterator();
            // Compare the elements in each iterator, as long as they both have elements.
            while (expectedIter.hasNext() && actualIter.hasNext())
            {
                Assert.assertEquals(message, expectedIter.next(), actualIter.next());
            }
            // If either iterator has more elements, then their lengths are different.
            assertFalse(message, expectedIter.hasNext() || actualIter.hasNext());
        }
    }

    private static Iterable<Renderable> createExampleIterable()
    {
        //noinspection RedundantArrayCreation
        return Arrays.asList(new Renderable[] {
            new Polyline(),
            new Polyline(),
            new Polyline()});
    }
}
