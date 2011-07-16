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

#ifndef BMPIMAGEWRITER_H
#define BMPIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

// lt_lib_utils
#include "lti_imageWriter.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTReusableBuffer;
class LTIBandSelectFilter;

/**
 * writes an image stage to a Windows BMP file
 *
 * This class writes an image stage to a Windows BMP file.
 */
class BMPImageWriter : public LTIGeoFileImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(BMPImageWriter);
public:
   /**
    * constructor
    *
    * Creates a writer for Windows BMP images.
    *
    * @param  image  the image to write from
    */
   BMPImageWriter(LTIImageStage* image);
   ~BMPImageWriter();
   
   LT_STATUS initialize();

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();

private:
   LT_STATUS checkImpedance() const;

   LT_STATUS normalizeProperties(lt_uint32 width, lt_uint32 height);
   LT_STATUS initFile(lt_uint32 width, lt_uint32 height);
   LT_STATUS writeHeaders(lt_uint32 width, lt_uint32 height);
   LT_STATUS initBitmapHeader(lt_uint32 width, lt_uint32 height);
   LT_STATUS initEndian();
   LT_STATUS writeStrip(lt_uint32 stripHeight,
                        const lt_uint8* buf);

   lt_uint32 m_alignedRowBytes;
   lt_uint32 m_unalignedRowBytes;
   lt_uint8* m_padding;
   lt_uint32 m_padsize;

   struct bitmapFileHeaderType;
   struct bitmapHeaderType;

   bitmapFileHeaderType* m_bmpFileHeader;
   bitmapHeaderType* m_bmpHeader;

   //  This will provide a marker so the image doesn't write over itself
   // I need to know how far back to seek.
   lt_int32 m_seekBackTo;

   LTReusableBuffer* m_stripBuffer;
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // BMPIMAGEWRITER_H
