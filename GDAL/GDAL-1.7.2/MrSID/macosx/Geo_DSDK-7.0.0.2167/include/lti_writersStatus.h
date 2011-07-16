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

#ifndef LTI_WRITERSSTATUS_H
#define LTI_WRITERSSTATUS_H

#include "lt_base.h"

#define LTI_STS_Writers_Base                                      50400
LT_STATUSSTRING_ADD(LTI_STS_Writers_Base, "lt_lib_mrsid_imageWriters base")

#define LTI_STS_Writers_CannotWriteFile                           50401
LT_STATUSSTRING_ADD(LTI_STS_Writers_CannotWriteFile, "Cannot write to file %F")

#define LTI_STS_Writers_UnsupColorSpace                           50402
LT_STATUSSTRING_ADD(LTI_STS_Writers_UnsupColorSpace, "unsupported colorspace")

#define LTI_STS_Writers_UnsupDataType                             50403
LT_STATUSSTRING_ADD(LTI_STS_Writers_UnsupDataType, "unsupported datatype")

#define LTI_STS_Writers_AllocFailed                               50404
LT_STATUSSTRING_ADD(LTI_STS_Writers_AllocFailed, "memory allocation failed")


#define LTI_STS_Writers_TIFFFieldInfoNotSet                       50410
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFFieldInfoNotSet, "TIFF: field info not set")

#define LTI_STS_Writers_TIFFLibraryError                          50411
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFLibraryError, "TIFF: library error - %s")

#define LTI_STS_Writers_TIFFLibraryWarning                        50412
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFLibraryWarning, "TIFF: library warning - %s")

#define LTI_STS_Writers_TIFFUnsupOutputFormat                     50413
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFUnsupOutputFormat, "TIFF: unsupported output format")

#define LTI_STS_Writers_TIFFWriteDataFailed                       50414
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFWriteDataFailed, "TIFF: write data failed")

#define LTI_STS_Writers_TIFFUnsupClutType                         50415
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFUnsupClutType, "TIFF: unsupported CLUT type")

#define LTI_STS_Writers_TIFFMetadataError                         50416
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFMetadataError, "TIFF: metadata error")

#define LTI_STS_Writers_TIFFToLarge                         50417
LT_STATUSSTRING_ADD(LTI_STS_Writers_TIFFToLarge, "TIFF: Resulting tiff file is too large: > 4GB")


#define LTI_STS_Writers_GDALErrorMessage                          50420
LT_STATUSSTRING_ADD(LTI_STS_Writers_GDALErrorMessage, "GDAL error: %s - %d")


#define LTI_STS_Writers_GeoTIFFBadKeyDims                         50430
LT_STATUSSTRING_ADD(LTI_STS_Writers_GeoTIFFBadKeyDims, "GeoTIFF: bad metadata key format")

#define LTI_STS_Writers_GeoTIFFUnableToSetKey                     504131
LT_STATUSSTRING_ADD(LTI_STS_Writers_GeoTIFFUnableToSetKey, "GeoTIFF: unable to set key")

#define LTI_STS_Writers_GeoTIFFOpenFileError                      50432
LT_STATUSSTRING_ADD(LTI_STS_Writers_GeoTIFFUnableToSetKey, "GeoTIFF: error opening file")


#define LTI_STS_Writers_BBBCannotWriteFile                        50440
LT_STATUSSTRING_ADD(LTI_STS_Writers_BBBCannotWriteFile, "BBB: cannot write to file")


#define LTI_STS_Writers_BMPCannotWriteFile                        50450
LT_STATUSSTRING_ADD(LTI_STS_Writers_BMPCannotWriteFile, "BMP: cannot write to file")


#define LTI_STS_Writers_JPEGErrorMessage                          50460
LT_STATUSSTRING_ADD(LTI_STS_Writers_JPEGErrorMessage, "JPEG error: %s")

#define LTI_STS_Writers_JPEGOpenFileError                         50461
LT_STATUSSTRING_ADD(LTI_STS_Writers_JPEGOpenFileError, "JPEG: error opening file")


#define LTI_STS_Writers_GIFCannotWriteFile                        50462
LT_STATUSSTRING_ADD(LTI_STS_Writers_GIFCannotWriteFile, "GIF: cannot write to file")

#define LTI_STS_Writers_GIFImageFormattingError                   50463
LT_STATUSSTRING_ADD(LTI_STS_Writers_GIFImageFormattingError, "GIF: error while composing file")

#define LTI_STS_Writers_PNGOutOfMemory                            50464
LT_STATUSSTRING_ADD(LTI_STS_Writers_PNGOutOfMemory, "PNG: out of memory")

#define LTI_STS_Writers_PNGLibraryError                           50465
LT_STATUSSTRING_ADD(LTI_STS_Writers_PNGLibraryError, "PNG: libpng error '%s'")

#define LTI_STS_Writers_Max                                       50499
LT_STATUSSTRING_ADD(LTI_STS_Writers_Max, "lt_lib_mrsid_imageWriters max")


#endif // LTI_WRITERSSTATUS_H
