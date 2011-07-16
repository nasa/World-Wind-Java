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

#ifndef LTI_J2KSTATUS_H
#define LTI_J2KSTATUS_H

#include "lt_base.h"

#define LTI_STS_J2K_Base                                          50900
LT_STATUSSTRING_ADD(LTI_STS_J2K_Base, "lt_lib_mrsid_j2k base")

#define LTI_STS_J2K_CannotWriteFile                               50901
LT_STATUSSTRING_ADD(LTI_STS_J2K_CannotWriteFile, "JP2: cannot write to file %F")

#define LTI_STS_J2K_UnsupColorSpace                               50902
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupColorSpace, "JP2: unsupported colorspace")

#define LTI_STS_J2K_UnsupDataType                                 50903
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupDataType, "JP2: unsupported datatype")

#define LTI_STS_J2K_LibraryError                                  50904
LT_STATUSSTRING_ADD(LTI_STS_J2K_LibraryError, "JP2: internal error")

#define LTI_STS_J2K_HandledError                                  50905
LT_STATUSSTRING_ADD(LTI_STS_J2K_HandledError, "JP2: internal error (%s)")

#define LTI_STS_J2K_InvalidRegion                                 50906
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidRegion, "JP2: invalid region")

#define LTI_STS_J2K_InvalidDims                                   50907
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidDims, "JP2: invalid/unsupported dimensions")

#define LTI_STS_J2K_InvalidDecodeScene                            50908
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidDecodeScene, "JP2: invalid scene for decode")

#define LTI_STS_J2K_BadDecodeParam                                50909
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadDecodeParam, "JP2: invalid decoder parameter setting")

#define LTI_STS_J2K_MetadataReadError                             50910
LT_STATUSSTRING_ADD(LTI_STS_J2K_MetadataReadError, "JP2: error reading metadata")

#define LTI_STS_J2K_MetadataUuidNotFound                          50911
LT_STATUSSTRING_ADD(LTI_STS_J2K_MetadataUuidNotFound, "JP2: uuid not found")

#define LTI_STS_J2K_BadPrecisionParam                             50912
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadPrecisionParam, "JP2: bad precision value")

#define LTI_STS_J2K_BadLayersParam                                50915
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadLayersParam, "JP2: bad quality layers value")

#define LTI_STS_J2K_CannotReadFile                                50916
LT_STATUSSTRING_ADD(LTI_STS_J2K_CannotReadFile, "JP2: cannot read file")

#define LTI_STS_J2K_BadTileParam                                  50917
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadTileParam, "JP2: bad tile size value (limit of 65535 tiles)")

#define LTI_STS_J2K_BadFlushParam                                 50918
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadFlushParam, "JP2: bad flush period value")

#define LTI_STS_J2K_BadPrecinctsParam                             50919
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadPrecinctsParam, "JP2: bad precincts values")

#define LTI_STS_J2K_BadProgressionParam                           50920
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadProgressionParam, "JP2: bad progression order")

#define LTI_STS_J2K_BadCodeblockParam                             50921
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadCodeblockParam, "JP2: bad codeblock value")

#define LTI_STS_J2K_BadTilePartParam                             50922
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadTilePartParam, "JP2: bad tile part value")

#define LTI_STS_J2K_BadMultiComponentParams                       50923
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadMultiComponentParams, "JP2: base image components differ from alpha component")

#define LTI_STS_J2K_UnsupSigned                                   50924
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupSigned, "JP2: unsupported datatype - signed")

#define LTI_STS_J2K_InvalidBandNumber                             50925
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidBandNumber, "JP2: invalid band number")

#define LTI_STS_J2K_IrregularImage                                50926
LT_STATUSSTRING_ADD(LTI_STS_J2K_IrregularImage, "JP2: band sizes/shapes are irregular")

#define LTI_STS_J2K_UnsupProfileVersion                           50927
LT_STATUSSTRING_ADD(LTI_STS_J2K_UnsupProfileVersion, "JP2: unsupported profile version")

#define LTI_STS_J2K_ProfileReadError                              50928
LT_STATUSSTRING_ADD(LTI_STS_J2K_ProfileReadError, "JP2: error reading profile")

#define LTI_STS_J2K_ProfileUnkElem                                50929
LT_STATUSSTRING_ADD(LTI_STS_J2K_ProfileUnkElem, "JP2: unknown element in profile")

#define LTI_STS_J2K_ProfileBadData                                50930
LT_STATUSSTRING_ADD(LTI_STS_J2K_ProfileBadData, "JP2: bad data/argument in profile")

#define LTI_STS_J2K_ProfileOpenError                              50931
LT_STATUSSTRING_ADD(LTI_STS_J2K_ProfileOpenError, "JP2: error opening profile")


#define LTI_STS_J2K_LibraryErrorB                                 50940
LT_STATUSSTRING_ADD(LTI_STS_J2K_LibraryErrorB, "JP2: internal error")

#define LTI_STS_J2K_LibraryErrorL                                 50941
LT_STATUSSTRING_ADD(LTI_STS_J2K_LibraryErrorL, "JP2: internal error")

#define LTI_STS_J2K_LibraryErrorE                                 50942
LT_STATUSSTRING_ADD(LTI_STS_J2K_LibraryErrorE, "JP2: internal error")

#define LTI_STS_J2K_ROIScene                                      50943
LT_STATUSSTRING_ADD(LTI_STS_J2K_ROIScene, "JP2: invalid scene for ROI encoding")

#define LTI_STS_J2K_AllocFailed                                   50944
LT_STATUSSTRING_ADD(LTI_STS_J2K_AllocFailed, "JP2: memory allocation failed")

#define LTI_STS_J2K_InternalROI1                                  50945
LT_STATUSSTRING_ADD(LTI_STS_J2K_InternalROI1, "JP2: internal error processing ROI")

#define LTI_STS_J2K_InternalROI2                                  50946
LT_STATUSSTRING_ADD(LTI_STS_J2K_InternalROI2, "JP2: internal error processing ROI")

#define LTI_STS_J2K_InternalSamp16                                50947
LT_STATUSSTRING_ADD(LTI_STS_J2K_InternalSamp16, "JP2: internal error processing sample")

#define LTI_STS_J2K_FileNotFound                                  50950
LT_STATUSSTRING_ADD(LTI_STS_J2K_FileNotFound, "JP2: file not found")

#define LTI_STS_J2K_ReadError                                     50951
LT_STATUSSTRING_ADD(LTI_STS_J2K_ReadError, "JP2: error reading file")

#define LTI_STS_J2K_FileNotJ2K                                    50952
LT_STATUSSTRING_ADD(LTI_STS_J2K_FileNotJ2K, "JP2: not valid JPEG 2000 file")

#define LTI_STS_J2K_NotPersistent                                 50953
LT_STATUSSTRING_ADD(LTI_STS_J2K_NotPersistent, "JP2: attempted second decode with isPersistent==false")

#define LTI_STS_J2K_InvalidCodestreamNumber                       50954
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidCodestreamNumber, "JP2: invalid codestream (image number)")


#define LTI_STS_J2K_BadDBVersion                                  50960
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadDBVersion, "JP2: incorrect version in database record")

#define LTI_STS_J2K_InvalidDBBlockSize                            50961
LT_STATUSSTRING_ADD(LTI_STS_J2K_InvalidDBBlockSize, "JP2: invalid database block size")

#define LTI_STS_J2K_BadDBImportFile                               50962
LT_STATUSSTRING_ADD(LTI_STS_J2K_BadDBImportFile, "JP2: error reading import file")

#define LTI_STS_J2K_DBReadError                                   50963
LT_STATUSSTRING_ADD(LTI_STS_J2K_DBReadError, "JP2: error reading database")

#define LTI_STS_J2K_DBWriteError                                  50964
LT_STATUSSTRING_ADD(LTI_STS_J2K_DBWriteError, "JP2: error writing database")

#define LTI_STS_J2K_DBRecordNotFound                              50965
LT_STATUSSTRING_ADD(LTI_STS_J2K_DBRecordNotFound, "JP2: georaster record not found")


#define LTI_STS_J2K_ServerError                                   50970
LT_STATUSSTRING_ADD(LTI_STS_J2K_ServerError, "JP2 server error")

#define LTI_STS_J2K_JPIPDownloadFailed                            50971
LT_STATUSSTRING_ADD(LTI_STS_J2K_JPIPDownloadFailed, "JPIP download thread dead (bad host, bad filename, server timeout")

#define LTI_STS_J2K_MSIChunkError1                                50980
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError1, "JP2: internal error 1")

#define LTI_STS_J2K_MSIChunkError2                                50981
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError2, "JP2: internal error 2")

#define LTI_STS_J2K_MSIChunkError3                                50982
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError3, "JP2: internal error 3")

#define LTI_STS_J2K_MSIChunkError4                                50983
LT_STATUSSTRING_ADD(LTI_STS_J2K_MSIChunkError4, "JP2: internal error 4")

#define LTI_STS_J2K_Max                                           50999
LT_STATUSSTRING_ADD(LTI_STS_J2K_Max, "lt_lib_mrsid_j2k max")

#endif // LTI_J2KSTATUS_H
