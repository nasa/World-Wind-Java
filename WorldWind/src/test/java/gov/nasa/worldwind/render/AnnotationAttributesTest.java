/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author dcollins
 * @version $Id$
 */
public class AnnotationAttributesTest
{
    /*************************************************************************************************************/
    /** Persistence Tests **/
    /** ******************************************************************************************************** */

    @Test
    public void testRestore_NewInstance()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignExampleValues(attrib);

        String stateInXml = attrib.getRestorableState();
        attrib = new AnnotationAttributes();
        attrib.restoreState(stateInXml);

        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);

        assertEquals(expected, attrib);
    }

    @Test
    public void testRestore_SameInstance()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignExampleValues(attrib);

        String stateInXml = attrib.getRestorableState();
        assignNullValues(attrib);
        attrib.restoreState(stateInXml);

        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);

        assertEquals(expected, attrib);
    }

    @Test
    public void testRestore_EmptyStateDocument()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignExampleValues(attrib);

        String emptyStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<emptyDocumentRoot/>";
        attrib.restoreState(emptyStateInXml);

        // No attributes should have changed.        
        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);

        assertEquals(expected, attrib);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRestore_InvalidStateDocument()
    {
        String badStateInXml = "!!invalid xml string!!";
        AnnotationAttributes attrib = new AnnotationAttributes();
        attrib.restoreState(badStateInXml);
    }

    @Test
    public void testRestore_PartialStateDocument()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignExampleValues(attrib);

        String partialStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<restorableState>" +
                "<stateObject name=\"scale\">10.5</stateObject>" +
                "<stateObject name=\"cornerRadius\">11</stateObject>" +
                "<unknownElement name=\"unknownName\">unknownValue</unknownElement>" +
                "</restorableState>";
        attrib.restoreState(partialStateInXml);

        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);
        expected.setScale(10.5);
        expected.setCornerRadius(11);

        assertEquals(expected, attrib);
    }

    @Test
    public void testRestore_LegacyStateDocument()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignExampleValues(attrib);
        String partialStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<restorableState>" +
                "<stateObject name=\"textAlign\">2</stateObject>" +
                "<stateObject name=\"imageRepeat\">render.Annotation.RepeatXY</stateObject>" +
                "<stateObject name=\"adjustWidthToText\">render.Annotation.SizeFitText</stateObject>" +
                "</restorableState>";
        attrib.restoreState(partialStateInXml);

        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);
        expected.setTextAlign(AVKey.RIGHT); // The integer 2 corresponds to RIGHT text alignment.
        expected.setImageRepeat(AVKey.REPEAT_XY); // render.Annotation.RepeatXY corresponds to AVKey.REPEAT_XY
        expected.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);

        assertEquals(expected, attrib);
    }

    @Test
    public void testRestore_PartialSave()
    {
        AnnotationAttributes attrib = new AnnotationAttributes();
        assignPartialExampleValues(attrib);

        // Only those values assigned to should be saved.
        String stateInXml = attrib.getRestorableState();
        attrib = new AnnotationAttributes();
        attrib.restoreState(stateInXml);

        AnnotationAttributes expected = new AnnotationAttributes();
        assignPartialExampleValues(expected);

        assertEquals(expected, attrib);
    }

    @Test
    public void testRestore_CustomDefaults()
    {
        AnnotationAttributes defaults = new AnnotationAttributes();
        assignExampleValues(defaults);
        AnnotationAttributes attrib = new AnnotationAttributes();
        attrib.setDefaults(defaults);

        String stateInXml = attrib.getRestorableState();
        attrib = new AnnotationAttributes();
        attrib.restoreState(stateInXml);

        AnnotationAttributes expectedDefaults = new AnnotationAttributes();
        assignExampleValues(expectedDefaults);
        AnnotationAttributes expected = new AnnotationAttributes();
        expected.setDefaults(expectedDefaults);

        // "expected" and "attrib" will return values from their defaults.
        assertEquals(expected, attrib);
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ******************************************************************************************************** */

    @SuppressWarnings({"JavaDoc"})
    private static void assignExampleValues(AnnotationAttributes attrib)
    {
        attrib.setFrameShape(AVKey.SHAPE_ELLIPSE);
        attrib.setHighlighted(true);
        attrib.setHighlightScale(2.5);
        attrib.setSize(new Dimension(255, 255));
        attrib.setScale(3.5);
        attrib.setOpacity(0.5);
        attrib.setLeader(AVKey.SHAPE_NONE);
        attrib.setLeaderGapWidth(100);
        attrib.setCornerRadius(4);
        attrib.setAdjustWidthToText(AVKey.SIZE_FIXED);
        attrib.setDrawOffset(new Point(-3, -3));
        attrib.setInsets(new Insets(11, 11, 11, 11));
        attrib.setBorderWidth(5.5);
        attrib.setBorderStippleFactor(6);
        attrib.setBorderStipplePattern((short) 0xFC0C);
        attrib.setAntiAliasHint(Annotation.ANTIALIAS_NICEST);
        attrib.setVisible(false);
        attrib.setFont(Font.decode("Arial-ITALIC-24"));
        attrib.setTextAlign(AVKey.CENTER);
        attrib.setTextColor(Color.PINK);
        attrib.setBackgroundColor(Color.MAGENTA);
        attrib.setBorderColor(Color.CYAN);
        attrib.setImageSource("path/to/image.ext");
        attrib.setImageScale(7.5);
        attrib.setImageOffset(new Point(-4, -4));
        attrib.setImageOpacity(0.4);
        attrib.setImageRepeat(AVKey.REPEAT_Y);
        attrib.setDistanceMaxScale(0.1);
        attrib.setDistanceMaxScale(8.5);
        attrib.setEffect(AVKey.TEXT_EFFECT_OUTLINE);
    }

    private static void assignNullValues(AnnotationAttributes attrib)
    {
        attrib.setFrameShape(null);
        attrib.setHighlighted(false);
        attrib.setHighlightScale(-1);
        attrib.setSize(null);
        attrib.setScale(-1);
        attrib.setOpacity(-1);
        attrib.setLeader(null);
        attrib.setLeaderGapWidth(-1);
        attrib.setCornerRadius(-1);
        attrib.setAdjustWidthToText(null);
        attrib.setDrawOffset(null);
        attrib.setInsets(null);
        attrib.setBorderWidth(-1);
        attrib.setBorderStippleFactor(-1);
        attrib.setBorderStipplePattern((short) 0x0000);
        attrib.setAntiAliasHint(-1);
        attrib.setVisible(false);
        attrib.setFont(null);
        attrib.setTextAlign(null);
        attrib.setTextColor(null);
        attrib.setBackgroundColor(null);
        attrib.setBorderColor(null);
        attrib.setImageSource(null);
        attrib.setImageScale(-1);
        attrib.setImageOffset(null);
        attrib.setImageOpacity(-1);
        attrib.setImageRepeat(null);
        attrib.setDistanceMaxScale(-1);
        attrib.setDistanceMaxScale(-1);
        attrib.setEffect(null);
    }

    private static void assignPartialExampleValues(AnnotationAttributes attrib)
    {
        attrib.setFrameShape(AVKey.SHAPE_ELLIPSE);
        attrib.setHighlighted(true);
        attrib.setHighlightScale(2.5);
        attrib.setSize(new Dimension(255, 255));
        attrib.setScale(3.5);
        attrib.setOpacity(0.5);
        attrib.setLeader(AVKey.SHAPE_NONE);
    }

    private static void assertEquals(AnnotationAttributes expected, AnnotationAttributes actual)
    {
        assertNotNull("Expected is null", expected);
        assertNotNull("Acutal is null", actual);
        Assert.assertEquals("frameShape", expected.getFrameShape(), actual.getFrameShape());
        Assert.assertEquals("highlighted", expected.isHighlighted(), actual.isHighlighted());
        Assert.assertEquals("highlightScale", expected.getHighlightScale(), actual.getHighlightScale(), 0.0001);
        Assert.assertEquals("size", expected.getSize(), actual.getSize());
        Assert.assertEquals("scale", expected.getScale(), actual.getScale(), 0.0001);
        Assert.assertEquals("opacity", expected.getOpacity(), actual.getOpacity(), 0.0001);
        Assert.assertEquals("leader", expected.getLeader(), actual.getLeader());
        Assert.assertEquals("leaderGapWidth", expected.getLeaderGapWidth(), actual.getLeaderGapWidth());
        Assert.assertEquals("cornerRadius", expected.getCornerRadius(), actual.getCornerRadius());
        Assert.assertEquals("adjustWidthToText", expected.getAdjustWidthToText(), actual.getAdjustWidthToText());
        Assert.assertEquals("drawOffset", expected.getDrawOffset(), actual.getDrawOffset());
        Assert.assertEquals("insets", expected.getInsets(), actual.getInsets());
        Assert.assertEquals("borderWidth", expected.getBorderWidth(), actual.getBorderWidth(), 0.0001);
        Assert.assertEquals("borderStippleFactor", expected.getBorderStippleFactor(), actual.getBorderStippleFactor());
        Assert.assertEquals("borderStipplePattern", expected.getBorderStipplePattern(), actual.getBorderStipplePattern());
        Assert.assertEquals("antiAliasHint", expected.getAntiAliasHint(), actual.getAntiAliasHint());
        Assert.assertEquals("visible", expected.isVisible(), actual.isVisible());
        Assert.assertEquals("font", expected.getFont(), actual.getFont());
        Assert.assertEquals("textAlign", expected.getTextAlign(), actual.getTextAlign());
        Assert.assertEquals("textColor", expected.getTextColor(), actual.getTextColor());
        Assert.assertEquals("backgroundColor", expected.getBackgroundColor(), actual.getBackgroundColor());
        Assert.assertEquals("borderColor", expected.getBorderColor(), actual.getBorderColor());
        Assert.assertEquals("imageSource", expected.getImageSource(), actual.getImageSource());
        Assert.assertEquals("imageScale", expected.getImageScale(), actual.getImageScale(), 0.0001);
        Assert.assertEquals("imageOffset", expected.getImageOffset(), actual.getImageOffset());
        Assert.assertEquals("imageOpacity", expected.getImageOpacity(), actual.getImageOpacity(), 0.0001);
        Assert.assertEquals("imageRepeat", expected.getImageRepeat(), actual.getImageRepeat());
        Assert.assertEquals("distanceMinScale", expected.getDistanceMinScale(), actual.getDistanceMinScale(), 0.0001);
        Assert.assertEquals("distanceMaxScale", expected.getDistanceMaxScale(), actual.getDistanceMaxScale(), 0.0001);
        Assert.assertEquals("distanceMinOpacity", expected.getDistanceMinOpacity(), actual.getDistanceMinOpacity(), 0.0001);
        Assert.assertEquals("effect", expected.getEffect(), actual.getEffect());
    }
}
