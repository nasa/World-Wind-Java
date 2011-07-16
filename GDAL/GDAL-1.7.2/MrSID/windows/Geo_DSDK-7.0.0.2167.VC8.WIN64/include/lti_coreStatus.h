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

#ifndef LTI_CORESTATUS_H
#define LTI_CORESTATUS_H

#include "lt_base.h"

#define LTI_STS_Core_Base                          50000
LT_STATUSSTRING_ADD(LTI_STS_Core_Base, "lt_lib_mrsid_core base")


#define LTI_STS_Core_UnsupDataType                 50001
LT_STATUSSTRING_ADD(LTI_STS_Core_UnsupDataType, "unsupported datatype")

#define LTI_STS_Core_OperationNotSupported         50002
LT_STATUSSTRING_ADD(LTI_STS_Core_OperationNotSupported, "requested operation not supported")

#define LTI_STS_Core_InvalidArgument               50003
LT_STATUSSTRING_ADD(LTI_STS_Core_InvalidArgument, "invalid argument to function")

#define LTI_STS_Core_ErrorOpeningFile              50004    // NIT: add filename string
LT_STATUSSTRING_ADD(LTI_STS_Core_ErrorOpeningFile, "error opening file")

#define LTI_STS_Core_ErrorWritingFile              50005    // NIT: add filename string
LT_STATUSSTRING_ADD(LTI_STS_Core_ErrorWritingFile, "error writing file")

#define LTI_STS_Core_ErrorReadingFile              50006    // NIT: add filename string
LT_STATUSSTRING_ADD(LTI_STS_Core_ErrorReadingFile, "error reading file")

#define LTI_STS_Core_InternalError                 50007
LT_STATUSSTRING_ADD(LTI_STS_Core_InternalError, "internal error")

#define LTI_STS_Core_AllocFailed                   50008
LT_STATUSSTRING_ADD(LTI_STS_Core_AllocFailed, "memory allocation failed")


#define LTI_STS_Core_WorldFileNameError            50010    // NIT: add filename string
LT_STATUSSTRING_ADD(LTI_STS_Core_WorldFileNameError, "error in world file name")

#define LTI_STS_Core_OutputNotSpecified            50011
LT_STATUSSTRING_ADD(LTI_STS_Core_OutputNotSpecified, "output target not specified")

#define LTI_STS_Core_SceneTooLarge                 50012
LT_STATUSSTRING_ADD(LTI_STS_Core_SceneTooLarge, "scene larger than 2GB")

#define LTI_STS_Core_InvalidScene                  50013
LT_STATUSSTRING_ADD(LTI_STS_Core_InvalidScene, "invalid or empty scene specified")

#define LTI_STS_Core_SceneOutOfRange               50014
LT_STATUSSTRING_ADD(LTI_STS_Core_SceneOutOfRange, "specified scene out of range of image")

#define LTI_STS_Core_MagRangeError                 50015
LT_STATUSSTRING_ADD(LTI_STS_Core_MagRangeError, "scene magnification out of range")

#define LTI_STS_Core_ImpedanceMismatchDataType     50016
LT_STATUSSTRING_ADD(LTI_STS_Core_ImpedanceMismatchDataType, "impedance mismatch - datatype")

#define LTI_STS_Core_ImpedanceMismatchColorSpace   50017
LT_STATUSSTRING_ADD(LTI_STS_Core_ImpedanceMismatchColorSpace, "impedance mismatch - colorspace")

#define LTI_STS_Core_ImpedanceMismatchNumBands     50018
LT_STATUSSTRING_ADD(LTI_STS_Core_ImpedanceMismatchNumBands, "impedance mismatch - number of bands")

#define LTI_STS_Core_UnsupColorSpace               50019
LT_STATUSSTRING_ADD(LTI_STS_Core_UnsupColorSpace, "unsupported colorspace")

#define LTI_STS_Core_UnsupNumBands                 50020
LT_STATUSSTRING_ADD(LTI_STS_Core_UnsupNumBands, "unsupported number of bands")

#define LTI_STS_Core_DatatypeMismatch              50021
LT_STATUSSTRING_ADD(LTI_STS_Core_DatatypeMismatch, "datatype mismatch")

#define LTI_STS_Core_Unused1                       50022
#define LTI_STS_Core_Unused2                       50023
#define LTI_STS_Core_Unused3                       50024

#define LTI_STS_Core_RawImageSizeError             50025
LT_STATUSSTRING_ADD(LTI_STS_Core_RawImageSizeError, "actual size of raw image file not equal to expected size")

#define LTI_STS_Core_MetadataReadError             50026
LT_STATUSSTRING_ADD(LTI_STS_Core_MetadataReadError, "error reading metadata")

#define LTI_STS_Core_SceneBufferMismatch           50027
LT_STATUSSTRING_ADD(LTI_STS_Core_SceneBufferMismatch, "specified scene size differs from buffer size")

#define LTI_STS_Core_MetadataFormatError           50028
LT_STATUSSTRING_ADD(LTI_STS_Core_MetadataFormatError, "metadata format error")

#define LT_STS_Core_BBBImageDimsIncorrect            50100
LT_STATUSSTRING_ADD(LT_STS_Core_BBBImageDimsIncorrect, "incorrect BBB image dimensions")

#define LT_STS_Core_BBBDomainBitsIncorrect           50101
LT_STATUSSTRING_ADD(LT_STS_Core_BBBDomainBitsIncorrect, "incorrect BBB domain bits")

#define LT_STS_Core_BBBWordLength                    50102
LT_STATUSSTRING_ADD(LT_STS_Core_BBBWordLength, "incorrect BBB word length")

#define LT_STS_Core_BBBNot1Or3Banded                 50103
LT_STATUSSTRING_ADD(LT_STS_Core_BBBNot1Or3Banded, "BBB image not 1 or 3 bands")

#define LT_STS_Core_BBBUnknownLayout                 50104
LT_STATUSSTRING_ADD(LT_STS_Core_BBBUnknownLayout, "unknown BBB image layout")

#define LT_STS_Core_BBBUnknownByteOrder              50105
LT_STATUSSTRING_ADD(LT_STS_Core_BBBUnknownByteOrder, "incorrect BBB byte order")

#define LT_STS_Core_BBBMissingNBANDS                 50106
LT_STATUSSTRING_ADD(LT_STS_Core_BBBMissingNBANDS, "BBB missing NBANDS")

#define LT_STS_Core_BBBMissingNCOLS                  50107
LT_STATUSSTRING_ADD(LT_STS_Core_BBBMissingNCOLS, "BBB missing NCOLS")

#define LT_STS_Core_BBBMissingNROWS                  50108
LT_STATUSSTRING_ADD(LT_STS_Core_BBBMissingNROWS, "BBB missing NROWS")

#define LT_STS_Core_BBBBadFormat                     50109
LT_STATUSSTRING_ADD(LT_STS_Core_BBBBadFormat, "invalid BBB header format")

#define LT_STS_Core_BBBInvalidLayout                 50110
LT_STATUSSTRING_ADD(LT_STS_Core_BBBInvalidLayout, "BBB incorrect layout")

#define LT_STS_Core_BBBBadColorSpace                 50111
LT_STATUSSTRING_ADD(LT_STS_Core_BBBBadColorSpace, "BBB bad colorspace")

#define LT_STS_Core_AOINotRectangular                50112
LT_STATUSSTRING_ADD(LT_STS_Core_AOINotRectangular, "area of interest (AOI) not rectangular")

#define LTI_STS_Core_UnsupMaskDataType              50113
LT_STATUSSTRING_ADD(LTI_STS_Core_UnsupMaskDataType, "unsupported datatype in shape mask")

#define LTI_STS_Core_Max                           50199
LT_STATUSSTRING_ADD(LTI_STS_Core_Base, "lt_lib_mrsid_core max")


#endif // LTI_CORESTATUS_H
