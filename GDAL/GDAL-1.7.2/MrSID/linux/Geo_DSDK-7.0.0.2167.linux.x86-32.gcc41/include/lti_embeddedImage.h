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

#ifndef LTI_EMBEDDED_IMAGE_H
#define LTI_EMBEDDED_IMAGE_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_imageStageOverrides.h"
#include "lti_scene.h"

LT_BEGIN_NAMESPACE(LizardTech)

class LTIReusableBSQBuffer;

/**
 * create a larger frame for the image
 *
 * Creates a new image stage of the given size, containing the input image
 * stage within it.
 *
 * This class is used to make an image stage "larger", e.g. to place an image
 * on a larger "canvas" for more flexible decoding.  This class is used
 * by the LTIMosaicFilter class to simplify certain computations by making
 * all the input images map to the same underlying grid shape and size.
 *
 * The embedding process honors the background and nodata pixel settings.
 *
 * The LTIGeoCoord information for the image stage is updated appropriately.
 */
class LTIEmbeddedImage : public LTIOverrideDimensions
                                <LTIOverrideBackgroundPixel
                                <LTIOverrideGeoCoord
                                <LTIImageFilter> > >
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIEmbeddedImage);
public:
   /**
    * initialize
    *
    * Creates an image stage whose input image is placed within a much larger
    * empty canvas.
    *
    * @param  srcImage         the base image
    * @param  newWidth         the width of the new image stage
    * @param  newHeight        the height of the new image stage
    * @param  newXPos          pixel x-position of the input image in the new image
    * @param  newYPos          pixel y-position of the input image in the new image
    * @param  backgroundPixel  background pixel color to use for the new image
    *                          (may be NULL, in which case the input image's
    *                          background is used)
    * @param  nodataPixel      nodata pixel color to use for the new image
    *                          (may be NULL, in which case the input image's
    *                          nodata is used)
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        lt_uint32 newWidth,
                        lt_uint32 newHeight,
                        double newXPos,
                        double newYPos,
                        const LTIPixel* backgroundPixel,
                        const LTIPixel* nodataPixel);


   // LTIImage
   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32 &width,
                          lt_uint32 &height) const;

   // LTIImageStage
   lt_int64 getEncodingCost(const LTIScene& scene) const;
   bool getReaderScene(const LTIScene &decodeScene,
                       LTIScene &readerScene) const;


   /**
    * control whether or not the background of the new "outer" image should be filled
    *
    * @param  fill         set to true to fill the background
    * @return success or failure
    */
   LT_STATUS setFillingBackground(bool fill);

   /**
    * query whether or not the background of the new "outer" image should be filled
    *
    * @return true if background to be filled
    */
   bool getFillingBackground(void) const;

   /**
    * Set the fill method which controls how noData pixels are matched.
    * @param method  fill method enum. See LTIPixelFillMethod
    */
    LT_STATUS setFillMethod(LTIPixelFillMethod method);
   
   /**
    * Get the fill method.  See setFillMethod().
    */
   LTIPixelFillMethod getFillMethod(void) const;


   // for LizardTech internal use only
   lt_int32 getChildXPosAtMag(double mag) const;
   // for LizardTech internal use only
   lt_int32 getChildYPosAtMag(double mag) const;
   // for LizardTech internal use only
   bool getChildScene(const LTIScene &parentScene, LTIScene &childScene) const;

   // for LizardTech internal use only
   LT_STATUS setWorkBuffer(LTIReusableBSQBuffer *buffer);
protected:
   LT_STATUS decodeBegin(const LTIScene &scene);
   LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer,
                         const LTIScene &stripScene);
   LT_STATUS decodeEnd(void);

private:
   double m_childXPos_P;
   double m_childYPos_P;

   LTIScene m_parentScene;
   LTIScene m_childScene;
   bool m_haveAnything;
   lt_int32 m_firstStrip;
   lt_int32 m_lastStrip;

   LTIReusableBSQBuffer *m_workBuffer;
   bool m_ownWorkBuffer;
   bool m_fillingBackground;
   LTIPixelFillMethod m_fillMethod;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_EMBEDDED_IMAGE_H
