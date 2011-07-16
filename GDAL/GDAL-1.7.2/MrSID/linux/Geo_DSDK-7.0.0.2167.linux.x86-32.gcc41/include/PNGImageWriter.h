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

#ifndef PNGIMAGEWRITER_H
#define PNGIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

struct png_struct_def;
struct png_info_struct;

LT_BEGIN_NAMESPACE(LizardTech)

class LTFileSpec;
class LTReusableBuffer;

/**
 * writes an image stage to a PNG file
 *
 * This class writes an image stage to a PNG file.
 */
class PNGImageWriter : public LTIGeoFileImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(PNGImageWriter);
public:
   PNGImageWriter(LTIImageStage* image);
   ~PNGImageWriter(void);

   /**
    * Initialize the writer
    */
   LT_STATUS initialize();


   /** 
    * Set the library compression level.  Currently, valid values range from
    * 0 - 9, corresponding directly to the zlib compression levels 0 - 9
    * (0 - no compression, 9 - "maximal" compression).  Note that tests have
    * shown that zlib compression levels 3-6 usually perform as well as level 9
    * for PNG images, and do considerably fewer caclulations.  In the future,
    * these values may not correspond directly to the zlib compression levels.
    * @note - call this before writing the image, after initialize()
    * @note - default is no compression (0)
    */   
   void setCompressionLevel( lt_uint16 level );
   
   /**
    * If true, and the image has a transparency/nodata value,
    * then this pixel value will be flagged as such in the PNG
    * output image
    * @note - call this before writing the image, after initialize()
    */
   LT_STATUS setWriteTransparencyColor(bool write);

   /**
    * @see LTIGeoFileImageWriter
    */
   //@{
   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();
   //@}

private:
   LT_STATUS checkImpedance() const;

   struct png_struct_def *m_png;
   struct png_info_struct *m_info;

   bool m_writeTransparencyColor;
   lt_int16 m_compressionLevel;

   char *m_errorMessage;

   LTReusableBuffer *m_stripBuffer;
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // PNGIMAGEWRITER_H
