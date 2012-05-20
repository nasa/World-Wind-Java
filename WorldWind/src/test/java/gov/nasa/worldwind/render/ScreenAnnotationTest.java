/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author dcollins
 * @version $Id$
 */
public class ScreenAnnotationTest
{
    /*************************************************************************************************************/
    /** Persistence Tests **/
    /** ******************************************************************************************************** */

    @Test
    public void testRestore_NewInstance()
    {
        ScreenAnnotation annotation = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(annotation);

        String stateInXml = annotation.getRestorableState();
        annotation = new ScreenAnnotation("", new Point(0, 0));
        annotation.restoreState(stateInXml);

        ScreenAnnotation expected = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(expected);

        assertEquals(expected, annotation);
    }

    @Test
    public void testRestore_SameInstance()
    {
        ScreenAnnotation annotation = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(annotation);

        String stateInXml = annotation.getRestorableState();
        assignNullValues(annotation);
        annotation.restoreState(stateInXml);

        ScreenAnnotation expected = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(expected);

        assertEquals(expected, annotation);
    }

    @Test
    public void testRestore_EmptyStateDocument()
    {
        ScreenAnnotation annotation = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(annotation);

        String emptyStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<emptyDocumentRoot/>";
        annotation.restoreState(emptyStateInXml);

        // No attributes should have changed.
        ScreenAnnotation expected = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(expected);

        assertEquals(expected, annotation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRestore_InvalidStateDocument()
    {
        String badStateInXml = "!!invalid xml string!!";
        ScreenAnnotation annotation = new ScreenAnnotation("", new Point(0, 0));
        annotation.restoreState(badStateInXml);
    }

    @Test
    public void testRestore_PartialStateDocument()
    {
        ScreenAnnotation annotation = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(annotation);

        String partialStateInXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<restorableState>" +
                "<stateObject name=\"text\">Hello, World!</stateObject>" +
                "<unknownElement name=\"unknownName\">unknownValue</unknownElement>" +
                "</restorableState>";
        annotation.restoreState(partialStateInXml);

        ScreenAnnotation expected = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(expected);
        expected.setText("Hello, World!");

        assertEquals(expected, annotation);
    }

    @Test
    public void testRestore_AnnotationSharing()
    {
        ScreenAnnotation annotation1 = new ScreenAnnotation("", new Point(0, 0));
        ScreenAnnotation annotation2 = new ScreenAnnotation("", new Point(0, 0));
        AnnotationAttributes sharedAttributes = new AnnotationAttributes();
        annotation1.setAttributes(sharedAttributes);
        annotation2.setAttributes(sharedAttributes);
        assignExampleValues(annotation1);

        String stateInXml = annotation1.getRestorableState();
        assignNullValues(annotation1);
        annotation1.restoreState(stateInXml);

        assertSame("Attributes are shared", annotation1.getAttributes(), annotation2.getAttributes());
        AnnotationAttributes expected = new AnnotationAttributes();
        assignExampleValues(expected);
        assertEquals(expected, annotation2.getAttributes());
    }

    @Test
    public void test_SaveScreenAnnotation_RestoreGlobeAnnotation()
    {
        ScreenAnnotation screenAnnotation = new ScreenAnnotation("", new Point(0, 0));
        assignExampleValues(screenAnnotation);

        String stateInXml = screenAnnotation.getRestorableState();

        GlobeAnnotation globeAnnotation = new GlobeAnnotation("", Position.fromDegrees(0.0, 0.0, 0.0));
        globeAnnotation.restoreState(stateInXml);

        //noinspection RedundantCast
        assertEquals((Annotation) screenAnnotation, (Annotation) globeAnnotation);
    }

    /*************************************************************************************************************/
    /** Helper Methods **/
    /** ******************************************************************************************************** */

    @SuppressWarnings({"JavaDoc"})
    private static void assignExampleValues(ScreenAnnotation annotation)
    {
        annotation.setText(
            "<p>\n<b><font color=\"#664400\">LA CLAPI\u00c8RE</font></b><br />\n<i>Alt: 1100-1700m</i>\n</p>\n<p>\n<b>Glissement de terrain majeur</b> dans la haute Tin\u00e9e, sur un flanc du <a href=\"http://www.mercantour.eu\">Parc du Mercantour</a>, Alpes Maritimes.\n</p>\n<p>\nRisque aggrav\u00e9 d'<b>inondation</b> du village de <i>Saint \u00c9tienne de Tin\u00e9e</i> juste en amont.\n</p>");
        annotation.setScreenPoint(new Point(321, 105));
        assignExampleValues(annotation.getAttributes());
    }

    private static void assignNullValues(ScreenAnnotation annotation)
    {
        annotation.setText("");
        annotation.setScreenPoint(new Point(0, 0));
        assignNullValues(annotation.getAttributes());
    }

    private static void assertEquals(Annotation expected, Annotation actual)
    {
        assertNotNull("Expected is null", expected);
        assertNotNull("Acutal is null", actual);
        Assert.assertEquals("text", expected.getText(), actual.getText());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

    private static void assertEquals(ScreenAnnotation expected, ScreenAnnotation actual)
    {
        assertNotNull("Expected is null", expected);
        assertNotNull("Acutal is null", actual);
        Assert.assertEquals("text", expected.getText(), actual.getText());
        Assert.assertEquals("screenPoint", expected.getScreenPoint(), actual.getScreenPoint());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

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
