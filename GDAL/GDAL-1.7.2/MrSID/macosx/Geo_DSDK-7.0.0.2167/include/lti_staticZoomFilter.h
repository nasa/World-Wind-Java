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

#ifndef LTI_STATICZOOMFILTER_H
#define LTI_STATICZOOMFILTER_H

// lt_lib_mrsid_imageFilter
#include "lti_multiresFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * magnifies the image by a fixed amount
 *
 * This class magnifies the image by a fixed amount.  In effect this simply changes the
 * width and height of the image statically, i.e. for the life of the
 * pipeline.  The resampling is performed internally by the LTIMultiresFilter
 * class.
 */

typedef LTIMultiResFilter LTIStaticZoomFilter;

LT_END_NAMESPACE(LizardTech)

#endif // LTI_STATICZOOMFILTER_H
