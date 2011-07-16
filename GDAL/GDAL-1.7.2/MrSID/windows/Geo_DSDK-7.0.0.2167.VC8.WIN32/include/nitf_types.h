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

#ifndef NITF_TYPES_H
#define NITF_TYPES_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_core

LT_BEGIN_NAMESPACE(LizardTech)



/**
 * version
 *
 * This enum is used to represent the version of the NITF file.
 */
enum NITFVersion
{
   NITF_VERSION_INVALID,
   NITF_VERSION_11, 
   NITF_VERSION_20,
   NITF_VERSION_21
};


/**
 * image/compression format
 *
 * This enum is used to represent the compression format of an image segment.
 */
enum NITFFormat    // IC field
{
   NITF_FORMAT_INVALID,
   NITF_FORMAT_RAW,
   NITF_FORMAT_BILEVEL,
   NITF_FORMAT_JPEG,
   NITF_FORMAT_VQ,
   NITF_FORMAT_JPEGLS,
   NITF_FORMAT_JPEGDS,
   NITF_FORMAT_JP2
};


/**
 * layout
 *
 * This enum is used to represent the data layout of an image segment.
 */
enum NITFLayout    // IMODE field
{
   NITF_LAYOUT_INVALID,
   NITF_LAYOUT_BLOCK,      // B: interleaved by block
   NITF_LAYOUT_PIXEL,      // P: interleaved by pixel
   NITF_LAYOUT_ROW,        // R: interleaved by row (line)
   NITF_LAYOUT_SEQ         // S: sequential (not interleaved)
};

LT_END_NAMESPACE(LizardTech)

#endif // NITF_TYPES_H
