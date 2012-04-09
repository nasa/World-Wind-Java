/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.geom.Position;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author dcollins
 * @version $Id$
 */
public class PedestalTest
{
    /*************************************************************************************************************/
    /** Persistence Tests **/
    /** ******************************************************************************************************** */

    @Test
    public void testRestore_NewInstance()
    {
        Pedestal pedestal = new Pedestal("", null);
        assignExampleValues(pedestal);

        String stateInXml = pedestal.getRestorableState();
        pedestal = new Pedestal("", null);
        pedestal.restoreState(stateInXml);

        Pedestal expected = new Pedestal("", null);
        assignExampleValues(expected);

        assertEquals(expected, pedestal);
    }

    @Test
    public void testRestore_SameInstance()
    {
        Pedestal pedestal = new Pedestal("", null);
        assignExampleValues(pedestal);

        String stateInXml = pedestal.getRestorableState();
        assignNullValues(pedestal);
        pedestal.restoreState(stateInXml);

        Pedestal expected = new Pedestal("", null);
        assignExampleValues(expected);

        assertEquals(expected, pedestal);
    }

    @Test
    public void testRestore_EmptyStateDocument()
    {
        Pedestal pedestal = new Pedestal("", null);
        assignExampleValues(pedestal);

        String emptyStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<emptyDocumentRoot/>";
        pedestal.restoreState(emptyStateInXml);

        // No attributes should have changed.
        Pedestal expected = new Pedestal("", null);
        assignExampleValues(expected);

        assertEquals(expected, pedestal);
    }

    @Test
    public void testRestore_InvalidStateDocument()
    {
        try
        {
            String badStateInXml = "!!invalid xml string!!";
            Pedestal pedestal = new Pedestal("", null);
            pedestal.restoreState(badStateInXml);

            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testRestore_PartialStateDocument()
    {
        Pedestal pedestal = new Pedestal("", null);
        assignNullValues(pedestal);

        String partialStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<restorableState>" +
                "<stateObject name=\"highlighted\">true</stateObject>" +
                "<stateObject name=\"highlightScale\">3.141592</stateObject>" +
                "<stateObject name=\"spacingPixels\">3.5</stateObject>" +
                "<stateObject name=\"scale\">5.5</stateObject>" +
                "<unknownElement name=\"unknownName\">unknownValue</unknownElement>" +
                "</restorableState>";
        pedestal.restoreState(partialStateInXml);

        Pedestal expected = new Pedestal("", null);
        assignNullValues(expected);
        expected.setHighlighted(true);
        expected.setHighlightScale(3.141592);
        expected.setSpacingPixels(3.5);
        expected.setScale(5.5);

        assertEquals(expected, pedestal);
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ******************************************************************************************************** */

    @SuppressWarnings({"JavaDoc"})
    private static void assignExampleValues(Pedestal pedestal)
    {
        // WWIcon properties
        pedestal.setImageSource("path/to/image.ext");
        pedestal.setPosition(Position.fromDegrees(45.5, 55.5, 100.5));
        pedestal.setHighlighted(true);
        pedestal.setSize(new Dimension(255, 255));
        pedestal.setVisible(false);
        pedestal.setHighlightScale(3.141592);
        pedestal.setToolTipText("Hello World!");
        pedestal.setToolTipFont(new Font("Arial", Font.ITALIC, 24));
        pedestal.setShowToolTip(true);
        pedestal.setToolTipTextColor(Color.MAGENTA);
        pedestal.setAlwaysOnTop(false);
        // Pedestal properties
        pedestal.setSpacingPixels(3.5);
        pedestal.setScale(5.5);
    }

    private static void assignNullValues(Pedestal pedestal)
    {
        // WWIcon properties
        pedestal.setImageSource("");
        pedestal.setPosition(null);
        pedestal.setHighlighted(false);
        pedestal.setSize(null);
        pedestal.setVisible(false);
        pedestal.setHighlightScale(0.0);
        pedestal.setToolTipText(null);
        pedestal.setToolTipFont(null);
        pedestal.setShowToolTip(false);
        pedestal.setToolTipTextColor(null);
        pedestal.setAlwaysOnTop(false);
        // Pedestal properties
        pedestal.setSpacingPixels(0.0);
        pedestal.setScale(0.0);
    }

    private static void assertEquals(Pedestal expected, Pedestal actual)
    {
        // WWIcon equality
        assertNotNull("Expected is null", expected);
        assertNotNull("Actual is null", actual);
        Assert.assertEquals("imageSource", expected.getImageSource(), actual.getImageSource());
        if (expected.getPosition() != null && actual.getPosition() != null)
        {
            Assert.assertEquals("position.latitude",
                    expected.getPosition().getLatitude(),
                    actual.getPosition().getLatitude());
            Assert.assertEquals("position.longitude",
                    expected.getPosition().getLongitude(),
                    actual.getPosition().getLongitude());
            Assert.assertEquals("position.elevation",
                    expected.getPosition().getElevation(),
                    actual.getPosition().getElevation(),
                    0.0001);
        }
        else
        {
            assertNull("Expected position is not null", expected.getPosition());
            assertNull("Actual position is not null", actual.getPosition());
        }
        Assert.assertEquals("highlighted", expected.isHighlighted(), actual.isHighlighted());
        Assert.assertEquals("size", expected.getSize(), actual.getSize());
        Assert.assertEquals("visible", expected.isVisible(), actual.isVisible());
        Assert.assertEquals("highlightScale", expected.getHighlightScale(), actual.getHighlightScale(), 0.0001);
        Assert.assertEquals("toolTipText", expected.getToolTipText(), actual.getToolTipText());
        Assert.assertEquals("toolTipFont", expected.getToolTipFont(), actual.getToolTipFont());
        Assert.assertEquals("showToolTip", expected.isShowToolTip(), actual.isShowToolTip());
        Assert.assertEquals("toolTipTextColor", expected.getToolTipTextColor(), actual.getToolTipTextColor());
        Assert.assertEquals("alwaysOnTop", expected.isAlwaysOnTop(), actual.isAlwaysOnTop());
        // Pedestal equality
        Assert.assertEquals("spacingPixels", expected.getSpacingPixels(), actual.getSpacingPixels(), 0.0001);
        Assert.assertEquals("scale", expected.getScale(), actual.getScale(), 0.0001);
    }
}
