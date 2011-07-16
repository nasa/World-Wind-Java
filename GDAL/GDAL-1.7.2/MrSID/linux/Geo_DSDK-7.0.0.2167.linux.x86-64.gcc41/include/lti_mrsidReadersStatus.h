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

#ifndef LTI_MRSIDREADERSSTATUS_H
#define LTI_MRSIDREADERSSTATUS_H

#include "lt_base.h"


#define LTI_STS_MrSIDReaders_Base                           50600
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_Base, "lt_lib_mrsid_mrsidReaders base")

#define LTI_STS_MrSIDReaders_UnsupColorSpace                50601
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_UnsupColorSpace, "unsupported colorspace")

#define LTI_STS_MrSIDReaders_UnsupDataType                  50602
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_UnsupDataType, "unsupported datatype")

#define LTI_STS_MrSIDReaders_InvalidMemoryModel             50603
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_InvalidMemoryModel, "invalid memory model")

#define LTI_STS_MrSIDReaders_InvalidMetadata                50604
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_InvalidMetadata, "invalid metadata")

#define LTI_STS_MrSIDReaders_MetadataReadError              50605
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_MetadataReadError, "error reading metadata")

#define LTI_STS_MrSIDReaders_InvalidStripHeight             50606
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_InvalidStripHeight, "invalid stripheight")

#define LTI_STS_MrSIDReaders_BadFileFormat                  50607
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_BadFileFormat, "invalid mrsid file format")


#define LTI_STS_MrSIDReaders_BadMG2letInit                  50608
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_BadMG2letInit, "mg2let init error")

#define LTI_STS_MrSIDReaders_BadMG3letInit                  50609
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_BadMG3letInit, "mg3let init error")

#define LTI_STS_MrSIDReaders_MG2Error                       50610
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_MG2Error, "error decoding MG2 file")

#define LTI_STS_MrSIDReaders_BadSidletFormat                50611
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_BadSidletFormat, "invalid sidlet message")

#define LTI_STS_MrSIDReaders_UnknownTileImageFormat         50612
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_UnknownTileImageFormat, "unknown tile image format")

#define LTI_STS_MrSIDReaders_CannotOpenFile                 50613
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_CannotOpenFile, "can't open file: %F")


#define LTI_STS_MrSIDReaders_Max                            50699
LT_STATUSSTRING_ADD(LTI_STS_MrSIDReaders_Max, "lt_lib_mrsid_mrsidReaders max")


#endif // LTI_MRSIDREADERSSTATUS_H
