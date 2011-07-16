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

#ifndef LTI_BBBIMAGEWRITER_H
#define LTI_BBBIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_rawImageWriter.h"

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * writes an image stage to a BBB file
 *
 * This class provides support for writing BBB files, i.e. a raw file with a BBB-style
 * header.
 */
class LTIBBBImageWriter : public LTIRawImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(LTIBBBImageWriter);
public:
   /**
    * constructor
    *
    * Creates a writer for BBB images.
    *
    * @param  image   the image to write from
    */
   LTIBBBImageWriter(LTIImageStage* image);

   virtual ~LTIBBBImageWriter();

   LT_STATUS writeBegin(const LTIScene& scene);

   static LT_STATUS writeHeader(const LTFileSpec& fileSpec,
                                const LTIImage& image,
                                const LTIScene* userScene,
                                LTIEndian byteOrder,
                                LTILayout layout);

private:
   LT_STATUS checkImpedance() const;

   LT_STATUS writeHeader(const LTIScene&);

};


LT_END_NAMESPACE(LizardTech)


#endif // LTI_BBBIMAGEWRITER_H
