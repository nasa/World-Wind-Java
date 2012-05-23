/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.DrawContextImpl;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.render.WWIcon;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author dcollins
 * @version $Id$
 */
@Ignore // **************** NOTE: THIS TEST IS CURRENTLY FAILING, PLEASE FIX.
public class IconLayerTest
{
    /*************************************************************************************************************/
    /** Basic Operation Tests **/
    /** ******************************************************************************************************** */

    @Test
    public void testConstructor()
    {
        IconLayer layer;

        // Test the parameterless constructor.
        layer = new IconLayer();
        assertNotNull("", layer);
    }

    @Test
    public void testAddIcon()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        for (WWIcon item : icons)
        {
            layer.addIcon(item);
        }

        // Test that the layer contains the icons.
        assertEquals("", icons, layer.getIcons());
    }

    @Test
    public void testAddIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);

        // Test that the layer contains the icons.
        assertEquals("", icons, layer.getIcons());
    }

    @Test
    public void testRemoveIcon()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        for (WWIcon item : icons)
        {
            layer.addIcon(item);
        }
        for (WWIcon item : icons)
        {
            layer.removeIcon(item);
        }

        // Test that the layer contains no icons.
        assertFalse("", layer.getIcons().iterator().hasNext());
    }

    @Test
    public void testRemoveAllIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);
        layer.removeAllIcons();

        // Test that the layer contains no icons.
        assertFalse("", layer.getIcons().iterator().hasNext());
    }

    @Test
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
    /** ******************************************************************************************************** */

    @Test
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

    @Test
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

    @Test
    public void testMaliciousGetIcons()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.addIcons(icons);

        Iterable<WWIcon> layerIcons = layer.getIcons();

        // Test that the returned list cannot be modified.
        try
        {
            if (layerIcons instanceof Collection)
            {
                Collection<WWIcon> collection = (Collection<WWIcon>) layerIcons;
                collection.clear();
            }
            else
            {
                Iterator<WWIcon> iter = layerIcons.iterator();
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

    @Test
    public void testMaliciousSetIcons()
    {
        // Create an Iterable with null elements.
        List<WWIcon> list = new ArrayList<WWIcon>();
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
    /** ******************************************************************************************************** */

    @Test(expected = IllegalStateException.class)
    public void testAddIconFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        // Expecting an IllegalStateException here.
        layer.addIcon(new UserFacingIcon("", Position.ZERO));
    }

    @Test(expected = IllegalStateException.class)
    public void testAddIconsFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        // Expecting an IllegalStateException here.
        layer.addIcons(icons);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveIconFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        // Expecting an IllegalStateException here.
        layer.removeIcon(new UserFacingIcon("", Position.ZERO));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAllIconsFail()
    {
        Iterable<WWIcon> icons = createExampleIterable();

        IconLayer layer = new IconLayer();
        layer.setIcons(icons);

        // Expecting an IllegalStateException here.
        layer.removeAllIcons();
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ******************************************************************************************************** */

    @SuppressWarnings({"JavaDoc"})
    private static void assertEquals(String message, Iterable<WWIcon> expected, Iterable<WWIcon> actual)
    {
        if (expected == null)
        {
            assertNull(message, actual);
        }
        else
        {
            // Since actual may contain duplicates, make a Set that eliminates duplicates.
            Set<WWIcon> actualSet = new HashSet<WWIcon>();
            for (WWIcon wwIcon : actual)
            {
                actualSet.add(wwIcon);
            }

            // Test that all the expected are in the actual. Order does not matter.
            int count = 0;
            for (WWIcon wwIcon : expected)
            {
                ++count;
                assertTrue(actualSet.contains(wwIcon));
            }

            // Test that actual and expected contain the same number of icons.
            assertTrue(actualSet.size() == count);
        }
    }

    private static Iterable<WWIcon> createExampleIterable()
    {
        //noinspection RedundantArrayCreation
        return Arrays.asList(new WWIcon[] {
                new UserFacingIcon("", Position.ZERO),
                new UserFacingIcon("", Position.ZERO),
                new UserFacingIcon("", Position.ZERO)});
    }
}
