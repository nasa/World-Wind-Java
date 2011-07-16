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

#ifndef LTI_NAVIGATOR_H
#define LTI_NAVIGATOR_H

// lt_lib_mrsid_core
#include "lti_scene.h"
#include "lti_geoCoord.h"


LT_BEGIN_NAMESPACE(LizardTech)


/**
 * provides LTIScene movement control
 *
 * Class for navigating around an image.  This class extends LTIScene by
 * adding functions for moving the scene, resizing, zooming, etc.
 */
class LTINavigator : public LTIScene
{
   LT_DISALLOW_COPY_CONSTRUCTOR(LTINavigator);
public:
   /**
    * Styles for scene-setting operations.  These constants are used to
    * define how to treat out-of-bounds conditions for scene-setting operations,
    * i.e. when a calculated scene or magnification is not valid for our image.
    */
   typedef enum
   {
      STYLE_LAX    = 1,    /**< if invalid scene, return success anyway */
      STYLE_STRICT = 2,    /**< if invalid scene, return an error code */
      STYLE_CLIP   = 3     /**< if invalid scene, clip scene to image and return success */
   } Style;

public:
   /**
    * default constructor
    *
    * This constructor creates an LTINavigator whose scene corresponds
    * to the entire given image at magnification 1.0.
    *
    * @param image the image to be navigated within
    */
   LTINavigator(const LTIImage& image);

   /**
    * constructor
    *
    * This constructor creates an LTINavigator with the given scene.
    *
    * @param  image  the image to be navigated within
    * @param  scene  the initial scene
    */
   LTINavigator(const LTIImage& image,
                const LTIScene& scene);

   /**
    * destructor
    */
   ~LTINavigator();

   /**
    * Returns the image the navigator is attached to.
    *
    * @return the navigator's image
    */
   const LTIImage& getImage() const;

   /**
    * is current scene is legal for the image
    *
    * Returns true if the current scene is legal for the image.  If any part
    * of the scene is outside the boundaries of the image or if the
    * magnification is not supported by the image, false will be returned.
    *
    * @return the navigator's image
    */
   bool isSceneValid() const;

   /**
    * move by specified amount
    *
    * Modifies the current scene by moving by the specified amount.
    * (See also moveTo().)
    *
    * @param   xDelta  the number of pixels to move horizontally
    * @param   yDelta  the number of pixels to move vertically
    * @param   style   how to handle boundary conditions
    * @return  status code indicating success or failure
    */
   LT_STATUS moveBy(double xDelta, double yDelta, Style style);

   /**
    * move to specified amount
    *
    * Modifies the current scene by moving to the specified (x,y) position.
    * (See also moveBy().)
    *
    * @param   x      the x-position to move to
    * @param   y      the y-position to move to
    * @param   style  how to handle boundary conditions
    * @return  status code indicating success or failure
    */
   LT_STATUS moveTo(double x, double y, Style style);

   /**
    * zoom by specified amount
    *
    * Modifies the current scene by zooming by the specified scale
    * factor.  (See also zoomTo().)
    * The other scene parameters (x, y, width,
    * height) are unaffected, unless the \a style parameter is used.
    *
    * For example, a call zoomBy(2.0) will double the magnification.
    *
    * @param   delta  the zoom factor
    * @param   style  how to handle boundary conditions
    * @return  status code indicating success or failure
    */
   LT_STATUS zoomBy(double delta, Style style);

   /**
    * zoom to specified scale
    *
    * Modifies the current scene by zooming to the specified scale.
    * (See also zoomBy().)  The other scene parameters (x, y, width,
    * height) are unaffected, unless the \a style parameter is used.
    *
    * For example, a call zoomBy(2.0) will double the magnification.
    *
    * @param   mag    the magnification to change to
    * @param   style  how to handle boundary conditions
    * @return  status code indicating success or failure
    */
   LT_STATUS zoomTo(double mag, Style style);

   /**
    * return scene large enough to fit window
    *
    * Returns a scene which fits the image to the given size.  The scene will
    * be the largest such that the entire image fits completely within the
    * specified dimensions.  A failure code will be returned if the request
    * cannot be honored.
    *
    * @note This function does NOT stretch, upsample, or downsample the image;
    * it will only determine the best fit within the bounds of the resolutions
    * that the image supports, e.g. power-of-two.
    *
    * @param   maxWidth   the maximum width of the scene to return
    * @param   maxHeight  the maximum height of the scene to return
    * @param   newScene   the scene which fits the request
    * @return  status code indicating success or failure
    */
   LT_STATUS bestFit(double maxWidth, double maxHeight, LTIScene& newScene);

   /**
    * round scene to integral values
    *
    * Modifies the current scene by performing proper integral rounding on
    * the (x,y) position and the dimensions.
    */
   void roundScene();

   /**
    * clip scene to fit image
    *
    * This function clips the current scene to the boundaries of the image at
    * the current magnification.
    *
    * @return true if the resulting scene contains any part of the image
    */
   bool clipToImage();

   /**
    * clip scene to fit given scene
    *
    * This function clips the current scene to the boundaries of the given
    * scene.
    *
    * @return true if the resulting scene contains any part of the image
    */
   bool clipToScene(const LTIScene& scene);

   /**
    * reset scene
    *
    * This function manually resets the navigator scene to the given scene.
    *
    * @param scene the new scene
    * @param style boundary condition mode
    * @return status code indicating success or failure
    */
   LT_STATUS setScene(const LTIScene& scene, Style style);


   /**
    * set scene to "icon"
    *
    * This function sets the navigator scene to the icon (the smallest
    * magnification this image can be represented at).
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneToIcon();


   /**
    * set scene to full image
    *
    * This function sets the navigator scene to the entire image at full
    * resolution.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneToFull();


   /**
    * get current scene
    *
    * This function returns the current navigator scene, e.g. to be used in
    * a call to LTIImageStage::read().
    *
    * Note the returned object is volatile; caller should use copy ctor as needed.
    *
    * @return the navigator scene
    */
   const LTIScene& getScene() const;

   /**
    * returns the geographic coordinates of scene
    *
    * Returns the geographic coordinates of the current scene.
    *
    * @return the geo coordinates
    */
   LTIGeoCoord getGeoCoord() const;

   /**
    * @name Functions to set scene explicitly
    */
   /*@{*/

   /**
    * set scene to given position
    *
    * Sets the scene to the given position.  The position is specified using
    * upper-left/width-height values.
    *
    * @param   upperLeftX     the upper-left x-position of the new scene
    * @param   upperLeftY     the upper-left y-position of the new scene
    * @param   width          the width of the new scene
    * @param   height         the height of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsULWH(double upperLeftX, double upperLeftY,
                            double width, double height,
                            double magnification);

   /**
    * set scene to given position
    *
    * Sets the scene to the given position.  The position is specified using
    * upper-left/lower-right values.
    *
    * @param   upperLeftX     the upper-left x-position of the new scene
    * @param   upperLeftY     the upper-left y-position of the new scene
    * @param   lowerRightX    the lower-right x-position of the new scene
    * @param   lowerRightY    the lower-right y-position of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsULLR(double upperLeftX, double upperLeftY,
                            double lowerRightX, double lowerRightY,
                            double magnification);

   /**
    * sets scene to the given position
    *
    * Sets the scene to the given position.  The position is specified using
    * center/width-height values.
    *
    * @param   centerX        the center x-position of the new scene
    * @param   centerY        the center y-position of the new scene
    * @param   width          the width of the new scene
    * @param   height         the height of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsCWH(double centerX, double centerY,
                           double width, double height,
                           double magnification);
   /*@}*/


   /**
    * @name Functions to set scene explicitly, using geographic coordinates
    */
   /*@{*/

   /**
    * set scene to given position
    *
    * Sets the scene to the given position.  The position is specified using
    * upper-left/width-height values.  The values are in "geo" space, i.e.
    * that of the LTIGeoCoord space.
    *
    * @param   upperLeftX     the upper-left x-position of the new scene
    * @param   upperLeftY     the upper-left y-position of the new scene
    * @param   width          the width of the new scene
    * @param   height         the height of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsGeoULWH(double upperLeftX, double upperLeftY,
                               double width, double height,
                               double magnification);

   /**
    * set scene to given position
    *
    * Sets the scene to the given position.  The position is specified using
    * upper-left/lower-right values.  The values are in "geo" space, i.e.
    * that of the LTIGeoCoord space.
    *
    * @param   upperLeftX     the upper-left x-position of the new scene
    * @param   upperLeftY     the upper-left y-position of the new scene
    * @param   lowerRightX    the lower-right x-position of the new scene
    * @param   lowerRightY    the lower-right y-position of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsGeoULLR(double upperLeftX, double upperLeftY,
                               double lowerRightX, double lowerRightY,
                               double magnification);

   /**
    * set scene to given position
    *
    * Sets the scene to the given position.  The position is specified using
    * center/width-height values.  The values are in "geo" space, i.e.
    * that of the LTIGeoCoord space.
    *
    * @param   centerX        the center x-position of the new scene
    * @param   centerY        the center y-position of the new scene
    * @param   width          the width of the new scene
    * @param   height         the height of the new scene
    * @param   magnification  the magnification of the new scene
    * @return status code indicating success or failure
    */
   LT_STATUS setSceneAsGeoCWH(double centerX, double centerY,
                              double width, double height,
                              double magnification);
   /*@}*/

protected:
   const LTIImage& m_image;
};


LT_END_NAMESPACE(LizardTech)


#endif // LTI_NAVIGATOR_H
