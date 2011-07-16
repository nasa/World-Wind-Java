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

#ifndef LTI_METADATASTATUS_H
#define LTI_METADATASTATUS_H

#include "lt_base.h"

#define LTI_STS_Metadata_Base                               50500
LT_STATUSSTRING_ADD(LTI_STS_Metadata_Base, "lt_lib_mrsid_metadata base")


#define LTI_STS_Metadata_InternalError                      50501
LT_STATUSSTRING_ADD(LTI_STS_Metadata_InternalError, "internal error")

#define LTI_STS_Metadata_UnsupDataType                      50502
LT_STATUSSTRING_ADD(LTI_STS_Metadata_UnsupDataType, "unsupported datatype")

#define LTI_STS_Metadata_TagNotFound                        50503
LT_STATUSSTRING_ADD(LTI_STS_Metadata_TagNotFound, "tag not found")

#define LTI_STS_Metadata_DuplicateTag                       50504
LT_STATUSSTRING_ADD(LTI_STS_Metadata_DuplicateTag, "duplicate tag")

#define LTI_STS_Metadata_OldMetadataError                   50505
LT_STATUSSTRING_ADD(LTI_STS_Metadata_OldMetadataError, "internal error - from old Metadata")

#define LTI_STS_Metadata_ScalarAsciiOnly                    50506
LT_STATUSSTRING_ADD(LTI_STS_Metadata_ScalarAsciiOnly, "only scalar ASCII records supported")

#define LTI_STS_Metadata_BadFormatForTag                    50507
LT_STATUSSTRING_ADD(LTI_STS_Metadata_BadFormatForTag, "bad format for given tag")

#define LTI_STS_Metadata_Max                                50599
LT_STATUSSTRING_ADD(LTI_STS_Metadata_Max, "lt_lib_mrsid_metadata max")


#endif // LTI_METADATASTATUS_H
