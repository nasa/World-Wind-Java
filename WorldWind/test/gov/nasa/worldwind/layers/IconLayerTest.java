/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

/**
 * @author dcollins
 * @version $Id$
 */
public class IconLayerTest extends junit.framework.TestCase
{
    /*************************************************************************************************************/
    /** Basic Operation Tests **/
    /*************************************************************************************************************/

    @org.junit.Test
    public void testConstructor()
    {
        IconLayer layer;

        // Test the parameterless constructor.
        layer = new IconLayer();
        assertNotNull("", layer);
    }

    @org.junit.Test
    public void testAddIcon()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        for (WWIcon item : icons)
            layer.addIcon(item);

        // Test that the layer contains the icons.
        assertEquals("", icons, layer.getIcons());
    }

    @org.junit.Test
    public void testAddIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);

        // Test that the layer contains the icons.
        assertEquals("", icons, layer.getIcons());
    }

    @org.junit.Test
    public void testRemoveIcon()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        for (WWIcon item : icons)
            layer.addIcon(item);
        for (WWIcon item : icons)
            layer.removeIcon(item);

        // Test that the layer contains no icons.
        assertFalse("", layer.getIcons().iterator().hasNext());
    }

    @org.junit.Test
    public void testRemoveAllIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);
        layer.removeAllIcons();

        // Test that the layer contains no icons.
        assertFalse("", layer.getIcons().iterator().hasNext());
    }

    @org.junit.Test
    public void testSetIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        // Test that the layer points to the Iterable.
        assertSame("", icons, layer.getIcons());
    }

    /*************************************************************************************************************/
    /** Edge Case Tests **/
    /*************************************************************************************************************/

    @org.junit.Test
    public void testSetIconsClearsIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);
        layer.setIcons(icons);
        layer.setIcons(null);

        // Test that the layer does not point to the Iterable.
        assertNotSame("", icons, layer.getIcons());
        // Test that the layer contains no icons.
        assertFalse("", layer.getIcons().iterator().hasNext());
    }

    @org.junit.Test
    public void testSetIconsThenAddIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);
        layer.setIcons(null);
        layer.addIcons(icons);

        // Test that the layer does not point to the Iterable.
        assertNotSame("", icons, layer.getIcons());
        // Test that the layer contains the icons.
        assertEquals("", icons, layer.getIcons());
    }

    @org.junit.Test
    public void testMaliciousGetIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);

        Iterable<WWIcon> layerIcons = layer.getIcons();

        // Test that the returned list cannot be modified.
        try
        {
            if (layerIcons instanceof java.util.Collection)
            {
                java.util.Collection<WWIcon> collection = (java.util.Collection<WWIcon>) layerIcons;
                collection.clear();
            }
            else
            {
                java.util.Iterator<WWIcon> iter = layerIcons.iterator();
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
        assertEquals("", icons, layerIcons);
    }

    @org.junit.Test
    public void testMaliciousSetIcons()
    {
        // Create an Iterable with null elements.
        java.util.List<WWIcon> list = new java.util.ArrayList<WWIcon>();
        list.add(null);

        IconLayer layer = new IconLayer()
        {
            // Override to avoid View initialization issues.
            public boolean isLayerActive(DrawContext dc)
            {
                return true;
            }
        };
        layer.setIcons(list);

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

    /*************************************************************************************************************/
    /** Exceptional Condition Tests **/
    /*************************************************************************************************************/

    @org.junit.Test
    public void testAddIconFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        try
        {
            // Expecting an IllegalStateException here.
            layer.addIcon(new UserFacingIcon("", Position.ZERO));
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testAddIconsFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        try
        {
            // Expecting an IllegalStateException here.
            layer.addIcons(icons);
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testRemoveIconFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        try
        {
            // Expecting an IllegalStateException here.
            layer.removeIcon(new UserFacingIcon("", Position.ZERO));
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    @org.junit.Test
    public void testRemoveAllIconsFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        try
        {
            // Expecting an IllegalStateException here.
            layer.removeAllIcons();
            fail("");
        }
        catch (IllegalStateException e)
        {
        }
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /*************************************************************************************************************/

    @SuppressWarnings({"JavaDoc"})
    private static void assertEquals(String message, Iterable<WWIcon> expected, Iterable<WWIcon> actual)
    {
        if (expected == null)
        {
            assertNull(message, actual);
        }
        else
        {
            java.util.Iterator<WWIcon> expectedIter = expected.iterator(), actualIter = actual.iterator();
            // Compare the elements in each iterator, as long as they both have elements.
            while (expectedIter.hasNext() && actualIter.hasNext())
                assertEquals(message, expectedIter.next(), actualIter.next());
            // If either iterator has more elements, then their lengths are different.
            assertFalse(message, expectedIter.hasNext() || actualIter.hasNext());
        }
    }

    private static Iterable<WWIcon> createExampleIterable()
    {
        //noinspection RedundantArrayCreation
        return java.util.Arrays.asList(new WWIcon[] {
                new UserFacingIcon("", Position.ZERO),
                new UserFacingIcon("", Position.ZERO),
                new UserFacingIcon("", Position.ZERO)});
    }

    public static void main(String[] args)
    {
        new junit.textui.TestRunner().doRun(new junit.framework.TestSuite(IconLayerTest.class));
    }
}