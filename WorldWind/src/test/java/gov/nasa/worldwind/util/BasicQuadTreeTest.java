/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.geom.Sector;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author tag
 * @version $Id$
 */
public class BasicQuadTreeTest
{
    protected int countItemsInTree(BasicQuadTree<Integer> tree)
    {
        // Counts only unique items.
        Set<Integer> map = new HashSet<Integer>();

        for (Integer i : tree)
        {
            map.add(i);
        }

        return map.size();
    }

    @Test
    /**
    * Tests incremental removal of all items from the tree.
    */
    public void testFullRemoval()
    {
        int numItems = 1000;
        BasicQuadTree<Integer> tree = new BasicQuadTree<Integer>(5, Sector.FULL_SPHERE, null);

        for (int i = 1; i <= numItems; i++)
        {
            tree.add(i, new double[] {i % 90, i % 180}, Integer.toString(i));
        }
        assertEquals("Item count incorrect at start ", countItemsInTree(tree), numItems);

        // Remove icons one at a time then verify the count.
        for (int i = numItems; i > 0; i--)
        {
            tree.remove(i);
            assertEquals("Item count incorrect ", countItemsInTree(tree), i - 1);
        }
    }

    @Test
    /**
    * Tests removal of named items from the tree.
    */
    public void testIndividualRemoval()
    {
        int numItems = 1000;
        BasicQuadTree<Integer> tree = new BasicQuadTree<Integer>(5, Sector.FULL_SPHERE, null);

        for (int i = 1; i <= numItems; i++)
        {
            tree.add(i, new double[] {i % 90, i % 180}, Integer.toString(i));
        }

        // Remove icons one at a time and verify removal.
        for (int i = numItems; i > 0; i--)
        {
            tree.removeByName(Integer.toString(i));
            Integer item = tree.getByName(Integer.toString(i));
            assertNull("Item not fully removed from tree ", item);
        }
    }
}
