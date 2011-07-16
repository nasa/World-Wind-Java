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

#ifndef MG2_STATUS_H
#define MG2_STATUS_H

// lt_lib_base
#include "lt_base.h"


#define MG2D_STATUS_BASE                              8000
LT_STATUSSTRING_ADD(MG2D_STATUS_BASE, "mg2d BASE")

#define MG2D_STATUS_Error1Array2DCtor3                8001
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Array2DCtor3, "mg2d internal error")

#define MG2D_STATUS_NewerSID                          8002
LT_STATUSSTRING_ADD(MG2D_STATUS_NewerSID, "MrSID version error")

#define MG2D_STATUS_InvalidImageSupport               8008
LT_STATUSSTRING_ADD(MG2D_STATUS_InvalidImageSupport, "mg2d internal error")

#define MG2D_STATUS_IncompatibleScaleGreater          8009
LT_STATUSSTRING_ADD(MG2D_STATUS_IncompatibleScaleGreater, "mg2d internal error")

#define MG2D_STATUS_IncompatibleScaleLess             8010
LT_STATUSSTRING_ADD(MG2D_STATUS_IncompatibleScaleLess, "mg2d internal error")

#define MG2D_STATUS_Error1SubbandCopy                 8011
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1SubbandCopy, "mg2d internal error")

#define MG2D_STATUS_Error1SubbandIntersect            8012
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1SubbandIntersect, "mg2d internal error")

#define MG2D_STATUS_Error2SubbandIntersect            8013
LT_STATUSSTRING_ADD(MG2D_STATUS_Error2SubbandIntersect, "mg2d internal error")

#define MG2D_STATUS_Error3SubbandIntersect            8014
LT_STATUSSTRING_ADD(MG2D_STATUS_Error3SubbandIntersect, "mg2d internal error")

#define MG2D_STATUS_Error4SubbandIntersect            8015
LT_STATUSSTRING_ADD(MG2D_STATUS_Error4SubbandIntersect, "mg2d internal error")

#define MG2D_STATUS_Error5SubbandIntersect            8016
LT_STATUSSTRING_ADD(MG2D_STATUS_Error5SubbandIntersect, "mg2d internal error")

#define MG2D_STATUS_Error1Array2DSetBand              8017
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Array2DSetBand, "mg2d internal error")

#define MG2D_STATUS_Error1Array2DInsert               8018
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Array2DInsert, "mg2d internal error")

#define MG2D_STATUS_Error2Array2DInsert               8019
LT_STATUSSTRING_ADD(MG2D_STATUS_Error2Array2DInsert, "mg2d internal error")

#define MG2D_STATUS_Error1Array2DBSQIteratorPlus      8020
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Array2DBSQIteratorPlus, "mg2d internal error")

#define MG2D_STATUS_UnexpectedMarker                  8021
LT_STATUSSTRING_ADD(MG2D_STATUS_UnexpectedMarker, "mg2d internal error")

#define MG2D_STATUS_ErrorDecompressing                8022
LT_STATUSSTRING_ADD(MG2D_STATUS_ErrorDecompressing, "Error decoding image")

#define MG2D_STATUS_Error2SubbandCopy                 8023
LT_STATUSSTRING_ADD(MG2D_STATUS_Error2SubbandCopy, "mg2d internal error")

#define MG2D_STATUS_Error1Merge2sub                   8024
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Merge2sub, "mg2d internal error")

#define MG2D_STATUS_Error2Merge2sub                   8025
LT_STATUSSTRING_ADD(MG2D_STATUS_Error2Merge2sub, "mg2d internal error")

#define MG2D_STATUS_Error3Merge2sub                   8026
LT_STATUSSTRING_ADD(MG2D_STATUS_Error3Merge2sub, "mg2d internal error")

#define MG2D_STATUS_Error4Merge2sub                   8027
LT_STATUSSTRING_ADD(MG2D_STATUS_Error4Merge2sub, "mg2d internal error")

#define MG2D_STATUS_Error5Merge2sub                   8028
LT_STATUSSTRING_ADD(MG2D_STATUS_Error5Merge2sub, "mg2d internal error")

#define MG2D_STATUS_Error1Dth                         8029
LT_STATUSSTRING_ADD(MG2D_STATUS_Error1Dth, "mg2d internal error")

#define MG2D_STATUS_Interrupt                         8040
LT_STATUSSTRING_ADD(MG2D_STATUS_Interrupt, "Interrupt")


#define LT_STS_MG2_readError                          8101
LT_STATUSSTRING_ADD(LT_STS_MG2_readError, "mg2 read error")

#define LT_STS_MG2_writeError                         8102
LT_STATUSSTRING_ADD(LT_STS_MG2_writeError, "mg2 write error")

#define LT_STS_MG2_ioError                            8103
LT_STATUSSTRING_ADD(LT_STS_MG2_ioError, "mg2 i/o error")

#define LT_STS_MG2_notMrSIDFile                       8104
LT_STATUSSTRING_ADD(LT_STS_MG2_notMrSIDFile, "not a MG2 file")

#define LT_STS_MG2_unsupportedVersion                 8105
LT_STATUSSTRING_ADD(LT_STS_MG2_unsupportedVersion, "unsupported version of mg2")

#define LT_STS_MG2_badMarker                          8106
LT_STATUSSTRING_ADD(LT_STS_MG2_badMarker, "bad mg2 file marker")

#define LT_STS_MG2_blockSizeTooLarge                  8107
LT_STATUSSTRING_ADD(LT_STS_MG2_blockSizeTooLarge, "blocksize too large")

#define LT_STS_MG2_blockSizeTooSmall                  8108
LT_STATUSSTRING_ADD(LT_STS_MG2_blockSizeTooSmall, "blocksize too small")

#define LT_STS_MG2_binCountTooLarge                   8109
LT_STATUSSTRING_ADD(LT_STS_MG2_binCountTooLarge, "binCount too large")

#define LT_STS_MG2_binCountTooSmall                   8110
LT_STATUSSTRING_ADD(LT_STS_MG2_binCountTooSmall, "binCount too small")

#define LT_STS_MG2_huffmanDecodeError                 8111
LT_STATUSSTRING_ADD(LT_STS_MG2_huffmanDecodeError, "huffman decode error")

#define LT_STS_MG2_huffmanEncodeError                 8112
LT_STATUSSTRING_ADD(LT_STS_MG2_huffmanEncodeError, "huffman encode error")

#define LT_STS_MG2_outOfMemory                        8113
LT_STATUSSTRING_ADD(LT_STS_MG2_outOfMemory, "mg2 out of memory")

#define LT_STS_MG2_error                              8114
LT_STATUSSTRING_ADD(LT_STS_MG2_error, "mg2 internal error: unknown exception")

#define LT_STS_MG2_rasterBlockNotCompatible           8115
LT_STATUSSTRING_ADD(LT_STS_MG2_rasterBlockNotCompatible, "mg2 raster block not compatible")

#define LT_STS_MG2_badLevel                           8116
LT_STATUSSTRING_ADD(LT_STS_MG2_badLevel, "level is larger than the number of levels in image")

#define LT_STS_MG2_badSupport                         8117
LT_STATUSSTRING_ADD(LT_STS_MG2_badSupport, "support is larger than the image")

#define LT_STS_MG2_badColorSpaceConvertion            8118
LT_STATUSSTRING_ADD(LT_STS_MG2_badColorSpaceConvertion, "bad color space conversion")


#define MG2D_STATUS_MAX                               8999
LT_STATUSSTRING_ADD(MG2D_STATUS_MAX, "mg2d MAX")


#define LT_STS_Image_MG2E_BASE                  9000
LT_STATUSSTRING_ADD(LT_STS_Image_MG2E_BASE, "mg2e BASE")

#define LT_STS_MG2EError1SInitSub               9001
LT_STATUSSTRING_ADD(LT_STS_MG2EError1SInitSub, "mg2 Error1SInitSub")

#define LT_STS_MG2EErrorBlockSmallGivenNlev     9002
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorBlockSmallGivenNlev, "block size too small for given scale")

#define LT_STS_MG2EError1ZeroOverlapSub         9003
LT_STATUSSTRING_ADD(LT_STS_MG2EError1ZeroOverlapSub, "mg2 Error1ZeroOverlapSub")

#define LT_STS_MG2EError1PurgeBorderSub         9004
LT_STATUSSTRING_ADD(LT_STS_MG2EError1PurgeBorderSub, "mg2 Error1PurgeBorderSub")

#define LT_STS_MG2EError1CopyDataToBordersSub   9005
LT_STATUSSTRING_ADD(LT_STS_MG2EError1CopyDataToBordersSub, "mg2 Error1CopyDataToBordersSub")

#define LT_STS_MG2EError2PurgeBorderSub         9006
LT_STATUSSTRING_ADD(LT_STS_MG2EError2PurgeBorderSub, "mg2 Error2PurgeBorderSub")

#define LT_STS_MG2EErrorSetOverlap              9007
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorSetOverlap, "mg2 ErrorSetOverlap")

#define LT_STS_MG2EErrorSubbandSplit            9008
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorSubbandSplit, "mg2 ErrorSubbandSplit")

// 9009 unused

#define LT_STS_MG2EUnknownException             9010
LT_STATUSSTRING_ADD(LT_STS_MG2EUnknownException, "mg2 UnknownException")

#define LT_STS_MG2ENlevLessEqual9               9011
LT_STATUSSTRING_ADD(LT_STS_MG2ENlevLessEqual9, "scale too large")

#define LT_STS_MG2EImageSmallGivenNLev          9012
LT_STATUSSTRING_ADD(LT_STS_MG2EImageSmallGivenNLev, "scale too small")

#define LT_STS_MG2EErrorMaxIconSize             9013
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorMaxIconSize, "mg2 icon size error")

#define LT_STS_MG2EMemAlloc                     9014
LT_STATUSSTRING_ADD(LT_STS_MG2EMemAlloc, "mg2 mem alloc error")

#define LT_STS_MG2EErrorOpenOutputFile          9015
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorOpenOutputFile, "error opening output file")

#define LT_STS_MG2EErrorWriteOutputFile         9016
LT_STATUSSTRING_ADD(LT_STS_MG2EErrorWriteOutputFile, "error writing output file")

#define LT_STS_MG2ETempFileError                9017
LT_STATUSSTRING_ADD(LT_STS_MG2ETempFileError, "temp file error (%F)")

#define LT_STS_MG2ETempDirError                 9018
LT_STATUSSTRING_ADD(LT_STS_MG2ETempDirError, "temp dir error")

#define LT_STS_MG2EBlockSmallGivenNlev          9019
LT_STATUSSTRING_ADD(LT_STS_MG2EBlockSmallGivenNlev, "block size too small for scale")

#define LT_STS_MG2EIllegalSampleType            9020
LT_STATUSSTRING_ADD(LT_STS_MG2EIllegalSampleType, "illegal sample type")

#define LT_STS_MG2EInterrupt                    9021
LT_STATUSSTRING_ADD(LT_STS_MG2EInterrupt, "mg2 interrupt")

#define LT_STS_MG2EWriteErrorCheckTempDir       9022
LT_STATUSSTRING_ADD(LT_STS_MG2EWriteErrorCheckTempDir, "temp dir write error")

#define LT_STS_MG2EFileOffsetErrorDecodeIndices 9023
LT_STATUSSTRING_ADD(LT_STS_MG2EFileOffsetErrorDecodeIndices, "mg2 FileOffsetErrorDecodeIndices")

#define LT_STS_MG2EInvalidLockingKey            9024
LT_STATUSSTRING_ADD(LT_STS_MG2EInvalidLockingKey, "Invalid key")

#define LT_STS_MG2EInvalidLockingPassword       9025
LT_STATUSSTRING_ADD(LT_STS_MG2EInvalidLockingPassword, "Invalid password")

#define LT_STS_RFIInvalid                       9100
LT_STATUSSTRING_ADD(LT_STS_RFIInvalid, "RFI invalid error")


#define LT_STS_MG2E_TableGen_MemAlloc           9200
LT_STATUSSTRING_ADD(LT_STS_MG2E_TableGen_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_Subband_MemAlloc            9201
LT_STATUSSTRING_ADD(LT_STS_MG2E_Subband_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_SetOverlap_MemAlloc         9202
LT_STATUSSTRING_ADD(LT_STS_MG2E_SetOverlap_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_IconBitRate_MemAlloc        9203
LT_STATUSSTRING_ADD(LT_STS_MG2E_IconBitRate_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_CreateTempFile              9204
LT_STATUSSTRING_ADD(LT_STS_MG2E_CreateTempFile, "cannot create temp file")

#define LT_STS_MG2E_TempName_MemAlloc           9205
LT_STATUSSTRING_ADD(LT_STS_MG2E_TempName_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_ArchUtils_MemAlloc          9206
LT_STATUSSTRING_ADD(LT_STS_MG2E_ArchUtils_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_Dump_MemAlloc               9207
LT_STATUSSTRING_ADD(LT_STS_MG2E_Dump_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_BlkNode_MemAlloc            9208
LT_STATUSSTRING_ADD(LT_STS_MG2E_BlkNode_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_Convert_MemAlloc            9209
LT_STATUSSTRING_ADD(LT_STS_MG2E_Convert_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_HuffD_MemAlloc              9210
LT_STATUSSTRING_ADD(LT_STS_MG2E_HuffD_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_HuffE_MemAlloc              9211
LT_STATUSSTRING_ADD(LT_STS_MG2E_HuffE_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_HuffE2_MemAlloc             9212
LT_STATUSSTRING_ADD(LT_STS_MG2E_HuffE2_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_OutIcon_MemAlloc            9213
LT_STATUSSTRING_ADD(LT_STS_MG2E_OutIcon_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_Purge_MemAlloc              9214
LT_STATUSSTRING_ADD(LT_STS_MG2E_Purge_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_QGen_MemAlloc               9215
LT_STATUSSTRING_ADD(LT_STS_MG2E_QGen_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_SQ_MemAlloc                 9216
LT_STATUSSTRING_ADD(LT_STS_MG2E_SQ_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_SbInit_MemAlloc             9217
LT_STATUSSTRING_ADD(LT_STS_MG2E_SbInit_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_TDWTQ_MemAlloc              9218
LT_STATUSSTRING_ADD(LT_STS_MG2E_TDWTQ_MemAlloc, "memory allocation error")

#define LT_STS_MG2E_TDWTQ_ColorSpace            9219
LT_STATUSSTRING_ADD(LT_STS_MG2E_TDWTQ_ColorSpace, "invalid colorspace")

#define LT_STS_MG2E_Convert_ReadError           9220
LT_STATUSSTRING_ADD(LT_STS_MG2E_Convert_ReadError, "internal mg2 read error")

#define LT_STS_MG2E_HuffD_ReadError             9221
LT_STATUSSTRING_ADD(LT_STS_MG2E_HuffD_ReadError, "internal mg2 read error")

#define LT_STS_MG2E_Split_MemAlloc              9222
LT_STATUSSTRING_ADD(LT_STS_MG2E_Split_MemAlloc, "memory allocation error")

#define LT_STS_MG2EAllocFailed                  9223
LT_STATUSSTRING_ADD(LT_STS_MG2EAllocFailed, "MG2 encode memory allocation failed")

#define LT_STS_Image_MG2E_MAX                   9999
LT_STATUSSTRING_ADD(LT_STS_Image_MG2E_MAX, "mg2 MAX")


#define LT_STS_MG2LET_BEGIN                  32000 

#define LT_STS_SLT_Error                     32001
#define LT_STS_SLT_ExtractError              32002 
#define LT_STS_SLT_SidletClientZeroSupport   32005
#define LT_STS_SLT_SidletClientAllDataCached 32006
#define LT_STS_SLT_SidletClientExceptionCaught 32007
#define LT_STS_SLT_IOError                   32008
#define LT_STS_SLT_ErrGenRequest             32009
#define LT_STS_SLT_ErrorParsingSidletRequest 32013
#define LT_STS_SLT_MessageHeaderSectionCorrupt              32014
#define LT_STS_SLT_MarkerMismatchWhileParsingSidletMessage  32016
#define LT_STS_SLT_UnrecognizedSidletMessageSection         32017

#define LT_STS_MG2LET_END 32999;


#endif /* MG2_STATUS_H */
