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

#ifndef LTI_DATATYPETRANSFORMER_H
#define LTI_DATATYPETRANSFORMER_H

// lt_lib_mrsid_imageFilters
#include "lti_dynamicRangeFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

class LTIReusableBSQBuffer;

#if defined LT_COMPILER_GCC
#warning "** LTIDataTypeTransformer is deprecated -- use LTIDynamicRangeFilter"
#elif defined LT_COMPILER_MS
#pragma message("*******************************************")
#pragma message("*  LTIDataTypeTransformer is deprecated   *")
#pragma message("*       use LTIDynamicRangeFilter         *")
#pragma message("*******************************************")
#endif

typedef LTIDynamicRangeFilter LTIDataTypeTransformer;


LT_END_NAMESPACE(LizardTech)

#endif // LTI_DATATYPETRANSFORMER_H
