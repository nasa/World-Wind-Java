/* $Id$ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2005 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */     
/* PUBLIC */

#ifndef LT_CACHESTATUS_H
#define LT_CACHESTATUS_H

// lt_lib_base
#include "lt_base.h"


#define LT_STS_CACHE_BASE                        16000
LT_STATUSSTRING_ADD(LT_STS_CACHE_BASE, "lt_lib_cache BASE")


#define LT_STS_CACHE_MAX                        16999
LT_STATUSSTRING_ADD(LT_STS_CACHE_MAX, "lt_lib_cache MAX")


#endif // LT_CACHESTATUS_H
