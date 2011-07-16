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

#ifndef JPEGIMAGEWRITER_H
#define JPEGIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_geoFileImageWriter.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

struct jpeg_compress_struct;
struct jpeg_error_mgr;

LT_BEGIN_NAMESPACE(LizardTech)

class LTFileSpec;
class LTReusableBuffer;

/**
 * writes an image stage to a JPEG file
 *
 * This class writes an image stage to a JPEG file.
 *
 * NOTE: getEstimatedOutputSize() will return the nominal
 * size for the output, not the compression size (which
 * cannot easily be estimated).
 */
class JpegImageWriter : public LTIGeoFileImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(JpegImageWriter);
public:
   /**
    * constructor
    *
    * Creates a writer for JPEG images.
    *
    * @param  image      the image to write from
    * @param  quality    sets the JPEG "quality" encoding parameter;
    *                    this is a value between 0 and 100
    * @param  smoothing  sets the JPEG "smoothing" encoding parameter;
    *                    this is a value between 0 and 100
    */
   JpegImageWriter(LTIImageStage* image,
                   lt_int32 quality = 0,          // 0-100
                   lt_int32 smoothing = 0);       // 0-100
   virtual ~JpegImageWriter();
   LT_STATUS initialize();

   LT_STATUS writeBegin(const LTIScene& scene);
   LT_STATUS writeStrip(LTISceneBuffer& stripBuffer, const LTIScene& stripScene);
   LT_STATUS writeEnd();

private:
   LT_STATUS checkImpedance() const;

   //struct that contains the JPEG compression parameters
   struct jpeg_compress_struct* m_jpegObject;

   //struct to handle error messages 
   struct jpeg_error_mgr* m_jpegErr;

   //two parameters for setting compression quality
   lt_int32 m_quality;
   lt_int32 m_smoothingFactor;

   LTReusableBuffer* m_stripBuffer;
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // JPEGIMAGEWRITER_H
