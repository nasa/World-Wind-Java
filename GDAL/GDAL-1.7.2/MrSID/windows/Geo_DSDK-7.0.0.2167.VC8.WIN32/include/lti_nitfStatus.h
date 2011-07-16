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

#ifndef LTI_NITFSTATUS_H
#define LTI_NITFSTATUS_H

#include "lt_base.h"


#define LTI_STS_NITF_Base                                      51200
LT_STATUSSTRING_ADD(LTI_STS_NITF_Base, "lt_lib_mrsid_nitf base")


#define LTI_STS_NITF_UnsupDataType                             51201
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupDataType, "unsupported datatype")

#define LTI_STS_NITF_UnsupColorSpace                           51202
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupColorSpace, "unsupported colorspace")

#define LTI_STS_NITF_ErrorOpeningFile                          51203
LT_STATUSSTRING_ADD(LTI_STS_NITF_ErrorOpeningFile, "error opening file")

#define LTI_STS_NITF_ErrorReadingFile                          51204
LT_STATUSSTRING_ADD(LTI_STS_NITF_ErrorReadingFile, "error reading file")

#define LTI_STS_NITF_UnsupLUT                                  51205
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupLUT, "LUTs unsupported")

#define LTI_STS_NITF_InvalidImageSegment                       51206
LT_STATUSSTRING_ADD(LTI_STS_NITF_InvalidImageSegment, "invalid image segment number")

#define LTI_STS_NITF_AllocFailed                               51207
LT_STATUSSTRING_ADD(LTI_STS_NITF_AllocFailed, "memory allocation failed")

#define LTI_STS_NITF_InvalidNITF                               51210
LT_STATUSSTRING_ADD(LTI_STS_NITF_InvalidNITF, "file is not valid NITF 2.1 (2500C) file")

#define LTI_STS_NITF_UnsupEncrypt                              51211
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupEncrypt, "encrypted NITF not supported")

#define LTI_STS_NITF_UnsupStreaming                            51212
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupStreaming, "NITF STREAMING extension not supported")

#define LTI_STS_NITF_UnsupV11                                  51213
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupV11, "NITF v1.1 not supported")

#define LTI_STS_NITF_UnsupImageFormat                          51214
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormat, "NITF image format (IC) not supported")

#define LTI_STS_NITF_UnsupImageFormatBilevel                   51215
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatBilevel, "NITF bilevel image format not supported")

#define LTI_STS_NITF_UnsupImageFormatJpeg                      51216
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatJpeg, "NITF JPEG image format not supported")

#define LTI_STS_NITF_UnsupImageFormatVQ                        51217
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatVQ, "NITF VQ image format not supported")

#define LTI_STS_NITF_UnsupImageFormatJpegLS                    51218
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatJpegLS, "NITF lossless JPEG image format not supported")

#define LTI_STS_NITF_UnsupImageFormatJpegDS                    51219
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatJpegDS, "NITF downsampled JPEG image format not supported")

#define LTI_STS_NITF_GraphicUnsup                              51220
LT_STATUSSTRING_ADD(LTI_STS_NITF_GraphicUnsup, "Graphic segment version unsupported")

#define LTI_STS_NITF_UnsupUnalignedData                        51221
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupUnalignedData, "non-byte-aligned data is unsupported")

#define LTI_STS_NITF_ErrorParsingJPG                           51222
LT_STATUSSTRING_ADD(LTI_STS_NITF_ErrorParsingJPG, "error parsing JPEG segment")

#define LTI_STS_NITF_BadlyFormedString                         51223
LT_STATUSSTRING_ADD(LTI_STS_NITF_BadlyFormedString, "badly formed string")

#define LTI_STS_NITF_UnsupImageFormatJp2Block                  51224
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImageFormatJp2Block, "NITF blocked JP2 image format not supported")

#define LTI_STS_NITF_UnsupImagePJust                           51225
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupImagePJust, "PJUST=L not supported")

#define LTI_STS_NITF_UnsupScene                                51226
LT_STATUSSTRING_ADD(LTI_STS_NITF_UnsupScene, "specified scene must be entire image")

#define LTI_STS_NITF_TooManySegments                           51227
LT_STATUSSTRING_ADD(LTI_STS_NITF_TooManySegments, "maximum number of image segments exceeded")

#define LTI_STS_NITF_ILOCExceeded                              51228
LT_STATUSSTRING_ADD(LTI_STS_NITF_ILOCExceeded, "image segments out of file format range (ILOC)")

#define LTI_STS_NITF_SegmentTooLarge                           51229
LT_STATUSSTRING_ADD(LTI_STS_NITF_SegmentTooLarge, "image segment is larger than 10GB")

#define LTI_STS_NITF_StreamDupError                            51230
LT_STATUSSTRING_ADD(LTI_STS_NITF_StreamDupError, "error duplicating stream")

#define LTI_STS_NITF_Max                                       51299
LT_STATUSSTRING_ADD(LTI_STS_NITF_Max, "lt_lib_mrsid_nitf max")


#endif // LTI_NITFSTATUS_H
