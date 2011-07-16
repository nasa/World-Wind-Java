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

#ifndef TIFFIMAGEWRITER_H
#define TIFFIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

struct tiff;


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTReusableBuffer;

/**
 * writes an image stage to a TIFF file
 *
 * This class writes an image stage to a TIFF file.
 */
class TIFFImageWriter : public LTIGeoFileImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(TIFFImageWriter);
public:
   /**
    * constructor
    *
    * Creates a writer for TIFF images.
    *
    * @param  image  the image to write from
    * @param  writeGeoTIFF  if true the writer will include GeoTIFF tags
    */
   TIFFImageWriter(LTIImageStage *image, bool writeGeoTIFF = false);

   virtual ~TIFFImageWriter();
   virtual LT_STATUS initialize();

   /**
    * Output resolution information.  Set this to false to prevent writing
    * normal TIFF resolution information.
    *
    * The default is to write this information.
    *
    * @param enable set to true to write resolution information
    */
   void setWriteResolution(bool enable);

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();

private:
   static LT_STATUS getLibtiffError(void);

   struct tiff *m_tiff;
   bool m_writeResolution;
   bool m_writeGeoTIFF;

   long m_currentRow;
   lt_int32 m_compression;
   LTReusableBuffer* m_stripBuffer;
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // TIFFIMAGEWRITER_H
