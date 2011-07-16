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

#ifndef LTIVIEWERIMAGEFILTER_H
#define LTIVIEWERIMAGEFILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * performs datatype and colorspace transforms on an image to make it displayable
 *
 * This class wraps the LTIDataType, LTIColorTransformer, and
 * LTIDynamicRangeFilter classes in order to transform the input image into
 * an unsigned 8-bit datatype with colorspace greyscale or RGB, as is required
 * by most display engines.
 */
class LTIViewerImageFilter : public LTIImageFilter
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIViewerImageFilter);
public:
   /**
    * initializer
    *
    * Transforms the input image to unsigned 8-bit samples and a colorspace
    * of either greyscale or RGB.  This allows the image's pixels to be more
    * easily passed to command rendering engines.
    *
    * The class also allows for the dynamic range of the image to be adjusted
    * to make the image's histogram fit the datatype width.  RGB pixels can
    * also be remapped to be in BGR format (as is required for Windows
    * bitmaps).
    *
    * @param  srcImage      the input image
    * @param  useDynamicRange  if set, the image data will be scaled
    *                          according to the dynamic range metadata in the
    *                          image (if any)
    * @param  useBGR           if set, RGB samples will be remapped to BGR
    *                          format
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        bool useDynamicRange,
                        bool useBGR);
};


LT_END_NAMESPACE(LizardTech)

#endif // LTIVIEWERIMAGEFILTER_H
