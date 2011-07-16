/* $Id$ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2004 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */
/* PUBLIC */

#ifndef LTI_SCENE_H
#define LTI_SCENE_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)


/**
 * representation of a scene
 *
 * This class represents a scene within an image.  A scene contains these
 * values:
 * \li an upper-left (x,y) position
 * \li a width and a height
 * \li a magnification
 *
 * The (X,Y) point is expressed in pixel-space (mag=1.0) coordinates, relative
 * to the given magnification.  The width and height are absolutes, i.e. they
 * correspond to the buffer size the user expects.
 *
 * The scene position and dimensions are maintained as high-precision values.
 * Accessors are supplied to get the high-precision values as well as the
 * properly rounded integral values (expressed in row/col form).
 *
 * A mag value of 1.0 indicates full-size resolution.  A mag greater than 1.0
 * indicates res-up (zoom in) by a factor of two: 2.0 gives an image 2x bigger,
 * 4.0 gives an image 4x bigger, etc.  A mag value less than 1.0 indicates
 * a lower resolution: 0.5 gives a half-sized image, 0.25 gives a quarter-sized
 * image, etc.
 *
 * An LTIImageStage may not support the requested magnification: most readers
 * only support mag=1.0, and MrSID readers only support power-of-two magnifications (0.25,
 * 0.5, 1.0, 2.0, 4.0, etc).  If the mag request cannot be returned the extraction
 * will return an error.
 *
 * An LTIScene is used to express the region of an image to be decoded via a
 * call to LTIImageStage::decodeBegin(), etc.
 *
 * As an example, consider an image which is 512x512 pixels.  Then, the
 * following scenes from the image will all contain the same upper-left point
 * and all will be 100x100 pixels.  The "granularity" (resolution) of each
 * pixel, however, will differ.
 * \li <tt>const LTIScene scene(256, 256, 100, 100, 1.0);     // center point</tt>
 * \li <tt>const LTIScene scene(512, 512, 100, 100, 2.0);</tt>
 * \li <tt>const LTIScene scene(128, 128, 100, 100, 0.5);</tt>
 *
 * @note Scenes may not require a buffer larger than 2GB.
 */
class LTIScene
{
public:
   /**
    * constructor
    *
    * This constructor produces a scene set to the given parameters, using double-precision.
    *
    * @param ulX            upper left X position of scene
    * @param ulY            upper left Y position of scene
    * @param width          width of scene
    * @param height         height of scene
    * @param magnification  resolution of scene
    */
   LTIScene(double ulX, double ulY,
            double width, double height,
            double magnification);

   /**
    * copy constructor
    */
   LTIScene(const LTIScene& copy);

   /**
    * default constructor
    *
    * This constructor will produce a scene with "invalid" default
    * coordinates.
    */
   LTIScene(void);

   /**
    * assignment operator
    */
   LTIScene& operator=(const LTIScene&);

   /**
    * equality operator
    */
   bool operator==(const LTIScene&) const;

   /**
    * equality operator
    */
   bool operator!=(const LTIScene&) const;


   /**
    * returns the magnification of the scene
    *
    * Returns the magnification of the scene.
    *
    * @return the magnification
    */
   double getMag() const;


   /**
    * @name High-precision point functions
    */
   /*@{*/

   /**
    * returns the x-position of the upper-left point
    *
    * Returns the x-position of the upper-left point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getUpperLeftX() const;

   /**
    * returns the y-position of the upper-left point
    *
    * Returns the y-position of the upper-left point.  This is a high-precision value.
    *
    * @return the y-position of the point
    */
   double getUpperLeftY() const;

   /**
    * returns the x-position of the lower-right point
    *
    * Returns the x-position of the lower-right point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getLowerRightX() const;

   /**
    * returns the y-position of the lower-right point
    *
    * Returns the y-position of the lower-right point.  This is a high-precision value.
    *
    * @return the y-position of the point
    */
   double getLowerRightY() const;

   /**
    * returns the x-position of the upper-right point
    *
    * Returns the x-position of the upper-right point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getUpperRightX() const;

   /**
    * returns the y-position of the upper-left point
    *
    * Returns the y-position of the upper-left point.  This is a high-precision value.
    *
    * @return the y-position of the point
    */
   double getUpperRightY() const;

   /**
    * returns the x-position of the lower-left point
    *
    * Returns the x-position of the lower-left point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getLowerLeftX() const;

   /**
    * returns the y-position of the lower-left point
    *
    * Returns the y-position of the lower-left point.  This is a high-precision value.
    *
    * @return the y-position of the point
    */
   double getLowerLeftY() const;

   /**
    * returns the x-position of the center point
    *
    * Returns the x-position of the center point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getCenterX() const;

   /**
    * returns the x-position of the center point
    *
    * Returns the x-position of the center point.  This is a high-precision value.
    *
    * @return the x-position of the point
    */
   double getCenterY() const;

   /*@}*/


   /**
    * @name Low-precision point functions
    *
    * These functions return properly rounded integral values, to be used
    * only when discretizing the pixels (for example to map to a buffer in
    * memory).
    */
   /*@{*/

   /**
    * returns the x-position of the upper-left point
    *
    * Returns the x-position of the upper-left point.  This is a low-precision value.
    *
    * @return the x-position of the point
    */
   lt_int32 getUpperLeftCol() const;

   /**
    * returns the y-position of the upper-left point
    *
    * Returns the y-position of the upper-left point.  This is a low-precision value.
    *
    * @return the y-position of the point
    */
   lt_int32 getUpperLeftRow() const;

   /**
    * returns the x-position of the lower-right point
    *
    * Returns the x-position of the lower-right point.  This is a low-precision value.
    *
    * @return the x-position of the point
    */
   lt_int32 getLowerRightCol() const;

   /**
    * returns the y-position of the lower-right point
    *
    * Returns the y-position of the lower-right point.  This is a low-precision value.
    *
    * @return the y-position of the point
    */
   lt_int32 getLowerRightRow() const;

   /**
    * returns the x-position of the upper-right point
    *
    * Returns the x-position of the upper-right point.  This is a low-precision value.
    *
    * @return the x-position of the point
    */
   lt_int32 getUpperRightCol() const;

   /**
    * returns the y-position of the upper-right point
    *
    * Returns the y-position of the upper-right point.  This is a low-precision value.
    *
    * @return the y-position of the point
    */
   lt_int32 getUpperRightRow() const;

   /**
    * returns the x-position of the lower-left point
    *
    * Returns the x-position of the lower-left point.  This is a low-precision value.
    *
    * @return the x-position of the point
    */
   lt_int32 getLowerLeftCol() const;

   /**
    * returns the y-position of the lower-left point
    *
    * Returns the y-position of the lower-left point.  This is a low-precision value.
    *
    * @return the y-position of the point
    */
   lt_int32 getLowerLeftRow() const;

   /**
    * returns the x-position of the center point
    *
    * Returns the x-position of the center point.  This is a low-precision value.
    *
    * @return the x-position of the point
    */
   lt_int32 getCenterCol() const;

   /**
    * returns the y-position of the center point
    *
    * Returns the y-position of the center point.  This is a low-precision value.
    *
    * @return the y-position of the point
    */
   lt_int32 getCenterRow() const;

   /*@}*/


   /**
    * @name Dimension functions
    *
    */
   /*@{*/

   /**
    * returns the width of the scene
    *
    * Returns the width of the scene.  This is a high-precision value.
    *
    * @return the width
    */
   double getWidth() const;

   /**
    * returns the height of the scene
    *
    * Returns the height of the scene.  This is a high-precision value.
    *
    * @return the height
    */
   double getHeight() const;

   /**
    * returns the width of the scene
    *
    * Returns the width of the scene.  This is a low-precision value.
    *
    * @return the width
    */
   lt_int32 getNumCols() const;

   /**
    * returns the height of the scene
    *
    * Returns the height of the scene.  This is a low-precision value.
    *
    * @return the height
    */
   lt_int32 getNumRows() const;

   /*@}*/


   /**
    * @name Helper functions
    *
    */
   /*@{*/

   /**
    * returns the x-position of the upper-left point
    *
    * Returns the x-position of the upper-left point, as a high-precision
    * value.  This returns the same value as calling getUpperLeftX().
    *
    * @return the x-position of the point
    */
   double getX() const;

   /**
    * returns the y-position of the upper-left point
    *
    * Returns the y-position of the upper-left point, as a high-precision
    * value.  This returns the same value as calling getUpperLeftY().
    *
    * @return the y-position of the point
    */
   double getY() const;

   /**
    * returns the (x,y) positions of the upper-left and lower-right points
    *
    * Returns the (x,y) positions of the upper-left and lower-right points,
    * as high-precision values.
    *
    * @param  ulX  the upper-left x-position
    * @param  ulY  the upper-left y-position
    * @param  lrX  the lower-right x-position
    * @param  lrY  the lower-right y-position
    */
   void getPoints(double& ulX, double& ulY,
                  double& lrX, double& lrY) const;

   /**
    * returns the (x,y) positions of the center and corner points
    *
    * Returns the (x,y) positions of the center and corner points,
    * as high-precision values.
    *
    * @param  ulX  the upper-left x-position
    * @param  ulY  the upper-left y-position
    * @param  urX  the upper-right x-position
    * @param  urY  the upper-right y-position
    * @param  llX  the lower-left x-position
    * @param  llY  the lower-left y-position
    * @param  lrX  the lower-right x-position
    * @param  lrY  the lower-right y-position
    * @param  cX   the center x-position
    * @param  cY   the center y-position
    */
   void getPoints(double& ulX, double& ulY,
                  double& urX, double& urY,
                  double& llX, double& llY,
                  double& lrX, double& lrY,
                  double& cX, double& cY) const;

   /**
    * returns the (x,y) positions of the upper-left and lower-right points
    *
    * Returns the (x,y) positions of the upper-left and lower-right points,
    * as low-precision values.
    *
    * @param  ulX  the upper-left x-position
    * @param  ulY  the upper-left y-position
    * @param  lrX  the lower-right x-position
    * @param  lrY  the lower-right y-position
    */
   void getPoints(lt_int32& ulX, lt_int32& ulY,
                  lt_int32& lrX, lt_int32& lrY) const;

   /**
    * returns the (x,y) positions of the center and corner points
    *
    * Returns the (x,y) positions of the center and corner points,
    * as low-precision values.
    *
    * @param  ulX  the upper-left x-position
    * @param  ulY  the upper-left y-position
    * @param  urX  the upper-right x-position
    * @param  urY  the upper-right y-position
    * @param  llX  the lower-left x-position
    * @param  llY  the lower-left y-position
    * @param  lrX  the lower-right x-position
    * @param  lrY  the lower-right y-position
    * @param  cX   the center x-position
    * @param  cY   the center y-position
    */
   void getPoints(lt_int32& ulX, lt_int32& ulY,
                  lt_int32& urX, lt_int32& urY,
                  lt_int32& llX, lt_int32& llY,
                  lt_int32& lrX, lt_int32& lrY,
                  lt_int32& cX, lt_int32& cY) const;
   /*@}*/

   /**
    * return a clip scene to fit an image with size (width, height)
    *
    * @param  width   max x value
    * @param  height  max y value
    */
   LTIScene clip(double width, double height) const;

protected:
   double m_ulX, m_ulY;
   double m_width, m_height;
   double m_magnification;

private:
   lt_int32 getHalfWidth_i() const;
   lt_int32 getHalfHeight_i() const;
};


LT_END_NAMESPACE(LizardTech)


#endif // LTI_SCENE_H
