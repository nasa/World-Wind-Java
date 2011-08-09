/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

import java.util.ArrayList;

/**
 * @author dcollins
 * @version $Id$
 */
public class RenderableLayerTest extends junit.framework.TestCase
{
    /*************************************************************************************************************/
    /** Basic Operation Tests **/
    /** ********************************************************************************************************* */

    @org.junit.Test
    public void testConstructor()
    {
        RenderableLayer layer;

        // Test the parameterless constructor.
        layer = new RenderableLayer();
        assertNotNull("", layer);
    }

    @org.junit.Test
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

    @org.junit.Test
    public void testAddRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);

        // Test that the layer contains the renderables.
        assertEquals("", renderables, layer.getRenderables());
    }

    @org.junit.Test
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

    @org.junit.Test
    public void testRemoveAllRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);
        layer.removeAllRenderables();

        // Test that the layer contains no renderables.
        assertFalse("", layer.getRenderables().iterator().hasNext());
    }

    @org.junit.Test
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
    /** ********************************************************************************************************* */

    @org.junit.Test
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

    @org.junit.Test
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

    @org.junit.Test
    public void testMaliciousGetRenderables()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderables(renderables);

        Iterable<? extends Renderable> layerRenderables = layer.getRenderables();

        // Test that the returned list cannot be modified.
        try
        {
            if (layerRenderables instanceof java.util.Collection)
            {
                java.util.Collection collection = (java.util.Collection) layerRenderables;
                collection.clear();
            }
            else
            {
                java.util.Iterator<? extends Renderable> iter = layerRenderables.iterator();
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
        assertEquals("", renderables, layerRenderables);
    }

    @org.junit.Test
    public void testMaliciousSetRenderables()
    {
        // Create an Iterable with null elements.
        java.util.List<Renderable> list = new java.util.ArrayList<Renderable>();
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

    @org.junit.Test
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
    /** ********************************************************************************************************* */

    @org.junit.Test
    public void testAddRenderableFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        try
        {
            // Expecting an IllegalStateException here.
            layer.addRenderable(new Polyline());
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testAddRenderablesFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        try
        {
            // Expecting an IllegalStateException here.
            layer.addRenderables(renderables);
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testRemoveRenderableFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        try
        {
            // Expecting an IllegalStateException here.
            layer.removeRenderable(new Polyline());
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testRemoveAllRenderablesFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        try
        {
            // Expecting an IllegalStateException here.
            layer.removeAllRenderables();
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testDisposeFail()
    {
        Iterable<Renderable> renderables = createExampleIterable();

        RenderableLayer layer = new RenderableLayer();
        layer.setRenderables(renderables);

        try
        {
            // Expecting an IllegalStateException here.
            layer.dispose();
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ********************************************************************************************************* */
//
//    @SuppressWarnings( {"JavaDoc"})
//    private static void assertEquals(String message, Iterable<Renderable> expected, Iterable<Renderable> actual)
//    {
//        if (expected == null)
//        {
//            assertNull(message, actual);
//        }
//        else
//        {
//            java.util.Iterator<Renderable> expectedIter = expected.iterator(), actualIter = actual.iterator();
//            // Compare the elements in each iterator, as long as they both have elements.
//            while (expectedIter.hasNext() && actualIter.hasNext())
//            {
//                assertEquals(message, expectedIter.next(), actualIter.next());
//            }
//            // If either iterator has more elements, then their lengths are different.
//            assertFalse(message, expectedIter.hasNext() || actualIter.hasNext());
//        }
//    }

    private static Iterable<Renderable> createExampleIterable()
    {
        //noinspection RedundantArrayCreation
        return java.util.Arrays.asList(new Renderable[] {
            new Polyline(),
            new Polyline(),
            new Polyline()});
    }

    public static void main(String[] args)
    {
        new junit.textui.TestRunner().doRun(new junit.framework.TestSuite(RenderableLayerTest.class));
    }
}
