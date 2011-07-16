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

#ifndef LTI_TRANSLATIONFILTER_H
#define LTI_TRANSLATIONFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_imageStageOverrides.h"

LT_BEGIN_NAMESPACE(LizardTech)


/**
 * translates (moves) the geo coordinates of the image
 *
 * This class translates (moves) the geo coordinates of the image.
 */
class LTITranslationFilter : public LTIOverrideGeoCoord
                                    <LTIImageFilter>
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTITranslationFilter);
public:
   /**
    * initialize
    *
    * This class shifts the geographic coordinates of the image by the given
    * amount.
    *
    * @param  srcImage   the base image
    * @param  xOffset       amount to shift in the X direction
    * @param  yOffset       amount to shift in the Y direction
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        double xOffset,
                        double yOffset);

   LT_STATUS initialize(LTIImageStage* srcImage,
                        const LTIGeoCoord &geoCoord);
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_TRANSLATIONFILTER_H
