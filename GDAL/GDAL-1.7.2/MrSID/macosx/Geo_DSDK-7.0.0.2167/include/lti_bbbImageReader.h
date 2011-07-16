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

#ifndef LTI_BBB_IMAGE_READER_H
#define LTI_BBB_IMAGE_READER_H

// lt_lib_mrsid_core
#include "lti_rawImageReader.h"

// system
#include <stdio.h>  // for FILE*

LT_BEGIN_NAMESPACE(LizardTech)

class LTIBBBHeaderReader;

/**
 * read an image from a BBB file
 *
 * This class provides support for reading BBB files, i.e. a raw file with a
 * BIL/BIP/BSQ-style header.
 *
 */
class LTIBBBImageReader : public LTIRawImageReader
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIBBBImageReader);
public:
   /**
    * intializer
    *
    * This function creates an image from a BBB file.
    *
    * @param fileSpec      the image file to read from
    * @param useWorldFile  use world file information if available
    */
   LT_STATUS initialize(const LTFileSpec& fileSpec, bool useWorldFile = true);

   /**
    * intializer
    *
    * This function creates an image from a BBB file.
    *
    * @param file          the image file to read from
    * @param useWorldFile  use world file information if available
    */
   LT_STATUS initialize(const char* file, bool useWorldFile = true);
};

#ifndef DOXYGEN_EXCLUDE

class LTIBBBHeaderReader
{
   LT_DISALLOW_COPY_CONSTRUCTOR(LTIBBBHeaderReader);

public:
   LTIBBBHeaderReader(const LTFileSpec&);
   ~LTIBBBHeaderReader();
   LT_STATUS read();

public:
   int            m_width;
   int            m_height;
   LTIDataType    m_dataType;
   LTIColorSpace  m_colorSpace;
   int            m_numBands;
   LTILayout      m_layout;
   char*          m_byteOrder;
   int            m_numBits;
   LTIEndian      m_endian;

   double         m_window;
   double         m_level;
   double         m_drMin;
   double         m_drMax;
   bool           m_hasWindow;
   bool           m_hasLevel;
   bool           m_hasDRMin;
   bool           m_hasDRMax;

   int            m_bandgapbytes;
   int            m_bandrowbytes;
   int            m_totalrowbytes;
   int            m_skipbytes;

   bool           m_hasUlxmap;
   bool           m_hasUlymap;
   bool           m_hasXdim;
   bool           m_hasYdim;
   double         m_ulxmap;
   double         m_ulymap;
   double         m_xdim;
   double         m_ydim;

   // Imagine header data
   bool        m_bFromImagine; //indicates whether to display Imagine Metadata
   char*       m_projname;
   char*       m_sphereName;
   char*       m_units;
   int         m_proZone;
   double*     m_projParams;
   double      m_sphereMajor;
   double      m_sphereMinor;
   double      m_sphereEccentricitySquared;
   double      m_sphereRadius;

private:
   LT_STATUS init();
   LT_STATUS finish();
   LT_STATUS readLines();
   LT_STATUS readLine(int& offset_last, bool& done);
   LT_STATUS nextLine();
   LT_STATUS readInt(int&);
   LT_STATUS readDouble(double&);
   LT_STATUS readString(char*);

   const LTFileSpec& m_fileSpec;
   FILE* m_fp;
};
#endif

LT_END_NAMESPACE(LizardTech)

#endif // LTI_BBB_IMAGE_READER_H
