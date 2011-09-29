/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import android.graphics.*;
import gov.nasa.worldwind.geom.Sector;

/** @version $Id$ */
public class ImageUtil
{
    /**
     * Merge an image into another image. This method is typically used to assemble a composite, seamless image from
     * several individual images. The receiving image, called here the canvas because it's analogous to the Photoshop
     * notion of a canvas, merges the incoming image according to the specified aspect ratio.
     *
     * @param canvasSector the sector defining the canvas' location and range.
     * @param imageSector  the sector defining the image's locaion and range.
     * @param aspectRatio  the aspect ratio, width/height, of the assembled image. If the aspect ratio is greater than
     *                     or equal to one, the assembled image uses the full width of the canvas; the height used is
     *                     proportional to the inverse of the aspect ratio. If the aspect ratio is less than one, the
     *                     full height of the canvas is used; the width used is proportional to the aspect ratio. <p/>
     *                     The aspect ratio is typically used to maintain consistent width and height units while
     *                     assembling multiple images into a canvas of a different aspect ratio than the canvas sector,
     *                     such as drawing a non-square region into a 1024x1024 canvas. An aspect ratio of 1 causes the
     *                     incoming images to be stretched as necessary in one dimension to match the aspect ratio of
     *                     the canvas sector.
     * @param image        the image to merge into the canvas.
     * @param canvas       the canvas into which the images are merged. The canvas is not changed if the specified image
     *                     and canvas sectors are disjoint.
     *
     * @throws IllegalArgumentException if the any of the reference arguments are null or the aspect ratio is less than
     *                                  or equal to zero.
     */
    public static void mergeImage(Sector canvasSector, Sector imageSector, double aspectRatio, Bitmap image,
        Bitmap canvas)
    {
        if (canvasSector == null || imageSector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(message);
            throw new IllegalStateException(message);
        }

        if (canvas == null || image == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.error(message);
            throw new IllegalStateException(message);
        }

        if (aspectRatio <= 0)
        {
            String message = Logging.getMessage("Util.AspectRatioInvalid", aspectRatio);
            Logging.error(message);
            throw new IllegalStateException(message);
        }

        if (!(canvasSector.intersects(imageSector)))
            return;

        // Create an image with the desired aspect ratio within an enclosing canvas of possibly different aspect ratio.
        int subWidth = aspectRatio >= 1 ? canvas.getWidth() : (int) Math.ceil((canvas.getWidth() * aspectRatio));
        int subHeight = aspectRatio >= 1 ? (int) Math.ceil((canvas.getHeight() / aspectRatio)) : canvas.getHeight();

        // yShift shifts image down to change origin from upper-left to lower-left
        float yShift = (float) (aspectRatio >= 1d ? (1d - 1d / aspectRatio) * canvas.getHeight() : 0d);

        float sh = (float) (((double) subHeight / (double) image.getHeight())
            * (imageSector.getDeltaLatDegrees() / canvasSector.getDeltaLatDegrees()));
        float sw = (float) (((double) subWidth / (double) image.getWidth())
            * (imageSector.getDeltaLonDegrees() / canvasSector.getDeltaLonDegrees()));

        float dh = (float) (subHeight *
            (-imageSector.maxLatitude.subtract(canvasSector.maxLatitude).degrees
                / canvasSector.getDeltaLat().degrees));
        float dw = (float) (subWidth *
            (imageSector.minLongitude.subtract(canvasSector.minLongitude).degrees
                / canvasSector.getDeltaLon().degrees));

        Canvas c = new Canvas(canvas);
        c.translate(dw, dh + yShift);
        c.scale(sw, sh);
        c.drawBitmap(image, 0, 0, null);
    }
}
