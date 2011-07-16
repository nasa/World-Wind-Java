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

#ifndef LTI_WATERMARKFILTER_H
#define LTI_WATERMARKFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

class LTIEmbeddedImage;

/**
 * insert a watermark image onto an image
 *
 * Inserts a watermark image onto the current image stage.
 */
class LTIWatermarkFilter : public LTIImageFilter
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIWatermarkFilter);
public:
   /**
    * initializer
    *
    * Creates an image stage with the \a watermarkImage overlaid over the \a
    * srcImage at the position specified.
    *
    * The \a edgePadding argument is used to specify the distance (in pixels)
    * between the watermark and the edge of the base image specified by the \a
    * position argument.  (This argument is ignored if LTI_POSITION_CENTER is
    * used.)
    *
    * @param  srcImage     the base image
    * @param  watermarkImage  the watermark to be overlaid
    * @param  position        where to insert the watermark
    * @param  edgePadding     distance (in pixels) between the watermark and
    *                         the base image
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        LTIImageStage* watermarkImage,
                        LTIPosition position,
                        lt_uint32 edgePadding);


protected:
   LT_STATUS decodeBegin(const LTIScene &scene);
   LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer,
                         const LTIScene &stripScene);
   LT_STATUS decodeEnd(void);

private:
   LTIEmbeddedImage *m_embeddedWatermark;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_WATERMARKFILTER_H
