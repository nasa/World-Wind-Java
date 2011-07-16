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

#ifndef LTI_CROPFILTER_H
#define LTI_CROPFILTER_H

// lt_lib_mrsid_imageFilters
#include "lti_embeddedImage.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * crops the image stage to a smaller width and height
 *
 * This class crops the image stage to a smaller width and height.
 */
class LTICropFilter : public LTIEmbeddedImage
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTICropFilter);
public:
   /**
    * initializer
    *
    * Creates an image stage which corresponds to a cropped subsection of the
    * input image, according to the given offset, width, and height.
    *
    * @param  srcImage    the base image
    * @param  xOffset        x-position of the origin of the new image
    *                        (specified relative to the input image)
    * @param  yOffset        y-position of the origin of the new image
    *                        (specified relative to the input image)
    * @param  newWidth       width of the new image
    * @param  newHeight      height of the new image
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        lt_int32 xOffset,
                        lt_int32 yOffset,
                        lt_int32 newWidth,
                        lt_int32 newHeight);
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_CROPFILTER_H
