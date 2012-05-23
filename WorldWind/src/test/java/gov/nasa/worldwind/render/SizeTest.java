package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.RestorableSupport;

import java.awt.Dimension;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class SizeTest
{
    @Test
    public void testSize()
    {
        // Test with native width and fractional height
        Size size = new Size(Size.NATIVE_DIMENSION, 0, AVKey.PIXELS, Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION);
        Dimension dim = size.compute(70, 10, 100, 100);
        assertTrue("Dimension should be 70 x 50", dim.equals(new Dimension(70, 50)));

        // Test with maintain aspect ratio
        size = new Size(Size.MAINTAIN_ASPECT_RATIO, 0, AVKey.PIXELS, Size.EXPLICIT_DIMENSION, 50, AVKey.PIXELS);
        dim = size.compute(20, 10, 100, 100);
        assertTrue("Dimension should be 100 x 50", dim.equals(new Dimension(100, 50)));
    }

    @Test
    public void testZeroSizeContainer()
    {
        Size size = new Size(Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION,
            Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION);

        Dimension dim = size.compute(100, 100, 0, 0);

        assertTrue("Dimension != null", dim != null);
        assertTrue("Dimension should be zero", dim.equals(new Dimension(0, 0)));
    }

    @Test
    public void testZeroSizeRect()
    {
        // Test with fractional dimensions
        Size size = new Size(Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION, Size.EXPLICIT_DIMENSION, 0.5,
            AVKey.FRACTION);
        Dimension dim = size.compute(0, 0, 100, 100);
        assertTrue("Dimension should be 50 x 50", dim.equals(new Dimension(50, 50)));

        // Test with pixel dimensions
        size = new Size(Size.EXPLICIT_DIMENSION, 50, AVKey.PIXELS, Size.EXPLICIT_DIMENSION, 50, AVKey.PIXELS);
        dim = size.compute(0, 0, 100, 100);
        assertTrue("Dimension should be 50 x 50", dim.equals(new Dimension(50, 50)));

        // Test with maintain aspect radio 
        size = new Size(Size.MAINTAIN_ASPECT_RATIO, 0, AVKey.PIXELS, Size.MAINTAIN_ASPECT_RATIO, 0, AVKey.PIXELS);
        dim = size.compute(0, 0, 100, 100);
        assertTrue("Dimension should be 0 x 0", dim.equals(new Dimension(0, 0)));

        // Test with native dimension
        size = new Size(Size.NATIVE_DIMENSION, 0, AVKey.PIXELS, Size.NATIVE_DIMENSION, 0, AVKey.PIXELS);
        dim = size.compute(0, 0, 100, 100);
        assertTrue("Dimension should be 0 x 0", dim.equals(new Dimension(0, 0)));
    }

    @Test
    public void testRestorableStateExplicit()
    {
        // Test with fractional dimensions
        Size expected = new Size(Size.EXPLICIT_DIMENSION, 0.5, AVKey.FRACTION, Size.EXPLICIT_DIMENSION, 0.5,
            AVKey.FRACTION);

        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        expected.getRestorableState(rs, null);

        Size actual = new Size();
        actual.restoreState(rs, null);

        assertEquals(expected, actual);
    }

    @Test
    public void testRestorableStateNative()
    {
        // Test with fractional dimensions
        Size expected = new Size(Size.NATIVE_DIMENSION, 0, null, Size.NATIVE_DIMENSION, 0, null);

        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        expected.getRestorableState(rs, null);

        Size actual = new Size();
        actual.restoreState(rs, null);

        assertEquals(expected, actual);
    }

    @Test
    public void testRestorableStateAspectRatio()
    {
        // Test with fractional dimensions
        Size expected = new Size(Size.MAINTAIN_ASPECT_RATIO, 0, null, Size.MAINTAIN_ASPECT_RATIO, 0, null);

        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        expected.getRestorableState(rs, null);

        Size actual = new Size();
        actual.restoreState(rs, null);

        assertEquals(expected, actual);
    }

    @Test
    public void testRestorableStateLegacy()
    {
        // Test with fractional dimensions
        Size input = new Size("MaintainAspectRatio", 0, null, "ExplicitDimension", 100, AVKey.PIXELS);
        Size expected = new Size(Size.MAINTAIN_ASPECT_RATIO, 0, null, Size.EXPLICIT_DIMENSION, 100, AVKey.PIXELS);

        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        input.getRestorableState(rs, null);

        Size actual = new Size();
        actual.restoreState(rs, null);

        assertEquals(expected, actual);
    }
}
