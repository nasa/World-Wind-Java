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

#ifndef LTI_MULTIRESFILTER_H
#define LTI_MULTIRESFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_imageStageOverrides.h"
#include "lti_scene.h"

LT_BEGIN_NAMESPACE(LizardTech)

class LTIResampler;

/**
 * add resolutions to the image
 *
 * Extends the magnification range of an image, to allow decodes at different
 * resolutions than the image stage would normally allow.
 *
 * Note that this class is not the same as at the LTIStaticZoomFilter class,
 * which scales the magnification statically for the pipeline when initially
 * constructed.  This class allows for the zoom level to be extended for an
 * individual decode operation.
 */
class LTIMultiResFilter : public LTIOverrideDimensions
                                 <LTIOverrideGeoCoord
                                 <LTIOverrideMagnification
                                 <LTIImageFilter> > >
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIMultiResFilter);
public:
   /**
    * initializer
    *
    * Creates an image stage which can be decoded at arbitrary magnifications.
    *
    * Normally image stages will only support a limited set of magnification
    * values for the LTIScene passed to LTIImageStage::read() -- often, only
    * 1.0.  This class will perform any needed resampling on the fly so that
    * arbitrary (power-of-two) magnifications are supported.
    *
    * @param  srcImage     the base image
    */
   LT_STATUS initialize(LTIImageStage* srcImage);

   /**
    * initializer (for compatibly with the old LTIStaticZoomFilter)
    *
    * Magnifies the image by the scale factor given.  The image width, height,
    * geographic resolution, etc, are all updated accordingly.
    *
    * A positive scale factor performs a "res-up" operation, while a negative
    * scale factor will reduce the image.  That is, a scale factor of 2 will
    * double the image size, e.g. from a magnification of 1.0 to 2.0, while a
    * scale factor of -2 will halve the image size, e.g. from a magnification
    * of 1.0 to 0.5.
    *
    * @param  srcImage   the base image
    * @param  scaleFactor    the integer scale factor
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        lt_int8 scaleFactor);

   LT_STATUS initialize(LTIImageStage* srcImage,
                        double deltaMag,
                        double minMag,
                        double maxMag);

   // LTIImage
   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32 &width,
                          lt_uint32 &height) const;

   // LTIImageStage
   lt_int64 getEncodingCost(const LTIScene& scene) const;
   bool getReaderScene(const LTIScene &decodeScene,
                       LTIScene &readerScene) const;

   /**
    * Set resampling method.
    * @param resampleMethod resampling method See LTIResampleMethod
    */
   LT_STATUS setResampleMethod(LTIResampleMethod resampleMethod);

   // for LizardTech internal use only
   // call this before initialize() if you want non-sq. pixels
   // (the deltaMag in the constructor is ignored)
   void setDeltaMagXY(double deltaMagX, double deltaMagY);

   // for LizardTech internal use only
   static double magForIcon(const LTIImageStage &image,
                            lt_uint32 iconSize);

protected:
   LT_STATUS decodeBegin(const LTIScene &scene);
   LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer,
                         const LTIScene &stripScene);
   LT_STATUS decodeEnd(void);

protected:
   enum Mode
   {
      MODE_INVALID,
      MODE_RESAMPLE,
      MODE_PASSTHROUGH,
      MODE_DOWNSAMPLE_FULLREAD,
      MODE_ALL_AT_ONCE
   };

   bool getChildScene(const LTIScene &scene,
                      Mode &mode,
                      double &scaleX,
                      double &scaleY,
                      LTIScene &childScene) const;

   enum
   {
      // The largest possible mag is based on the 2gb
      // scene limitation. Thus the largest scene we
      // should ever expect is approximately the square
      // root of (2gb / 3) pixels on a  side. If we
      // assume the smallest image we'll ever encounter
      // is 32x32 then the largest magnification can
      // be calculated. It's big, but we need a real number!
      kMaxMagnification = 512   // 51200% zoom!
   };

private:
   struct StripCache;

   double m_deltaMagX;
   double m_deltaMagY;

   Mode m_mode;
   double m_scaleX;
   double m_scaleY;
   LTIScene m_childScene;
   double m_curY;

   lt_int32 m_childStrip;
   lt_int32 m_myStrip;

   StripCache *m_stripCache0;
   StripCache *m_stripCache1;

   LTIResampler *m_resampler;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_MULTIRESFILTER_H
