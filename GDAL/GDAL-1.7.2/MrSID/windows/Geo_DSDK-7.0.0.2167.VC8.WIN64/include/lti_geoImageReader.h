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

#ifndef LTI_GEO_IMAGE_READER_H
#define LTI_GEO_IMAGE_READER_H

// lt_lib_mrsid_core
#include "lti_imageReader.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * abstract class for implementing a geo image reader
 *
 * The LTIGeoImageReader abstract class extends the LTIImageReader so that it
 * allows whether world files are used or ignored for setting up geo
 * information.
 */
class LTIGeoImageReader : public LTIImageReader
{
   LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(LTIGeoImageReader);
protected:
   LT_STATUS init(bool useWorldFile);

   bool getUseWorldFile() const;

   LT_STATUS readWorldFile(const LTFileSpec &file,
                           bool &changed, bool doUseWorldFileTest = true);
   LT_STATUS readWorldFile(LTIOStreamInf &stream,
                           bool &changed, bool doUseWorldFileTest = true);
private:
   bool m_useWorldFile;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_GEO_IMAGE_READER_H
