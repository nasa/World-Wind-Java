/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.RestorableSupport;
import junit.framework.*;
import junit.textui.TestRunner;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class ShapeAttributesTest
{
    public static void main(String[] args)
    {
        TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(ShapeAttributeTests.class);
        testSuite.addTestSuite(BalloonAttributeTests.class);
        new TestRunner().doRun(testSuite);
    }

    public static class ShapeAttributeTests extends TestCase
    {
        protected static final ShapeAttributes exampleAttributes = new BasicShapeAttributes();

        static
        {
            exampleAttributes.setUnresolved(true);
            exampleAttributes.setDrawInterior(false);
            exampleAttributes.setDrawOutline(false);
            exampleAttributes.setEnableAntialiasing(false);
            exampleAttributes.setEnableLighting(false);
            exampleAttributes.setInteriorMaterial(Material.RED);
            exampleAttributes.setOutlineMaterial(Material.GREEN);
            exampleAttributes.setInteriorOpacity(0.5);
            exampleAttributes.setOutlineOpacity(0.75);
            exampleAttributes.setOutlineWidth(10.0);
            exampleAttributes.setOutlineStippleFactor(256);
            exampleAttributes.setOutlineStipplePattern((short) 0xABAB);
            exampleAttributes.setImageSource("images/pushpins/plain-black.png");
            exampleAttributes.setImageScale(2.0);
        }

        protected ShapeAttributes createDefaultAttributes()
        {
            return new BasicShapeAttributes();
        }

        protected ShapeAttributes createExampleAttributes()
        {
            return exampleAttributes.copy();
        }

        @org.junit.Test
        public void testBasicSaveRestore()
        {
            RestorableSupport rs = RestorableSupport.newRestorableSupport();

            ShapeAttributes expected = this.createExampleAttributes();
            expected.getRestorableState(rs, null);

            ShapeAttributes actual = this.createDefaultAttributes();
            actual.restoreState(rs, null);

            assertEquals(expected, actual);
        }

        @org.junit.Test
        public void testRestoreSameInstance()
        {
            RestorableSupport rs = RestorableSupport.newRestorableSupport();

            ShapeAttributes expected = this.createExampleAttributes();

            ShapeAttributes actual = this.createExampleAttributes();
            actual.getRestorableState(rs, null);
            actual.copy(this.createDefaultAttributes());
            actual.restoreState(rs, null);

            assertEquals(expected, actual);
        }

        @org.junit.Test
        public void testRestoreNullDocument()
        {
            try
            {
                ShapeAttributes attrs = this.createDefaultAttributes();
                attrs.restoreState(null, null);
                fail("Expected an IllegalArgumentException");
            }
            catch (Exception e)
            {
            }
        }

        @org.junit.Test
        public void testRestoreEmptyDocument()
        {
            ShapeAttributes expected = this.createExampleAttributes();

            // Restoring an empty state document should not change any attributes.
            ShapeAttributes actual = this.createExampleAttributes();
            String emptyStateInXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<emptyDocumentRoot/>";
            RestorableSupport rs = RestorableSupport.parse(emptyStateInXml);
            actual.restoreState(rs, null);

            assertEquals(expected, actual);
        }

        @org.junit.Test
        public void testRestoreOneAttribute()
        {
            ShapeAttributes expected = this.createExampleAttributes();
            expected.setOutlineWidth(11);

            ShapeAttributes actual = this.createExampleAttributes();
            String partialStateInXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<restorableState>" +
                    "<stateObject name=\"outlineWidth\">11</stateObject>" +
                    "<unknownElement name=\"unknownName\">unknownValue</unknownElement>" +
                    "</restorableState>";
            RestorableSupport rs = RestorableSupport.parse(partialStateInXml);
            actual.restoreState(rs, null);

            assertEquals(expected, actual);
        }
    }

    public static class BalloonAttributeTests extends ShapeAttributeTests
    {
        protected static final BalloonAttributes exampleBalloonAttrs = new BasicBalloonAttributes();

        static
        {
            exampleBalloonAttrs.setUnresolved(true);
            exampleBalloonAttrs.setDrawInterior(false);
            exampleBalloonAttrs.setDrawOutline(false);
            exampleBalloonAttrs.setEnableAntialiasing(false);
            exampleBalloonAttrs.setEnableLighting(false);
            exampleBalloonAttrs.setInteriorMaterial(Material.RED);
            exampleBalloonAttrs.setOutlineMaterial(Material.GREEN);
            exampleBalloonAttrs.setInteriorOpacity(0.5);
            exampleBalloonAttrs.setOutlineOpacity(0.75);
            exampleBalloonAttrs.setOutlineWidth(10.0);
            exampleBalloonAttrs.setOutlineStippleFactor(256);
            exampleBalloonAttrs.setOutlineStipplePattern((short) 0xABAB);
            exampleBalloonAttrs.setSize(new Size(Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION,
                Size.EXPLICIT_DIMENSION, 100.0, AVKey.PIXELS));
            exampleBalloonAttrs.setOffset(new Offset(0.5, 0.0, AVKey.FRACTION, AVKey.PIXELS));
            exampleBalloonAttrs.setInsets(new Insets(5, 10, 15, 20));
            exampleBalloonAttrs.setBalloonShape(AVKey.SHAPE_ELLIPSE);
            exampleBalloonAttrs.setLeaderShape(AVKey.SHAPE_NONE);
            exampleBalloonAttrs.setLeaderWidth(100);
            exampleBalloonAttrs.setCornerRadius(5);
            exampleBalloonAttrs.setFont(Font.decode("Arial-BOLD-24"));
            exampleBalloonAttrs.setTextColor(Color.BLUE);
            exampleBalloonAttrs.setImageSource("images/pushpins/plain-black.png");
            exampleBalloonAttrs.setImageScale(2.0);
            exampleBalloonAttrs.setImageOffset(new Point(5, 10));
            exampleBalloonAttrs.setImageOpacity(0.5);
            exampleBalloonAttrs.setImageRepeat(AVKey.REPEAT_NONE);
        }

        @Override
        protected ShapeAttributes createDefaultAttributes()
        {
            return new BasicBalloonAttributes();
        }

        @Override
        protected ShapeAttributes createExampleAttributes()
        {
            return exampleBalloonAttrs.copy();
        }
    }
}
