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

#ifndef GEOTIFFIMAGEWRITER_H
#define GEOTIFFIMAGEWRITER_H

// lt_lib_mrsid_imageWriters
#include "TIFFImageWriter.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * writes an image stage to a GeoTIFF file
 *
 * This class writes an image stage to a GeoTIFF file.
 */
class GeoTIFFImageWriter : public TIFFImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(GeoTIFFImageWriter);
public:
   /**
    * constructor
    *
    * Creates a writer for GeoTIFF images.
    *
    * @param  image  the image to write from
    */
   GeoTIFFImageWriter(LTIImageStage* image);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // GEOTIFFIMAGEWRITER_H
