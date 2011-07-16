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

#ifndef NITFIMAGEREADER_H
#define NITFIMAGEREADER_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_core
#include "lti_geoImageReader.h"

// local
#include "nitf_types.h"

LT_BEGIN_NAMESPACE(LizardTech)

class LTFileSpec;
class LTIOStreamInf;
class NITFFileHeader;
class NITFImageManager;
class NITFImageSegment;
class LTReusableBuffer;
class LTIReusableBSQBuffer;
class NITFImageSegmentMetadata;
class NITFSecurityMetadata;

/**
 * class for reading an NITF JPEG 2000 image segment
 *
 * This class provides support for reading an NITF
 * image segment.  NITFImageManager objects are not
 * to be created directly; the NITFImageManager class
 * contains a createReader() function for this purpose.
 *
 * THIS CLASS SUBJECT TO SIGNIFICANT CHANGE IN SUBSEQUENT RELEASES.
 */
class NITFImageReader : public LTIGeoImageReader
{
   LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(NITFImageReader);
public:
   /**
    * returns the IID1 field for the segment
    */
   const char* getIID1() const;

   lt_int64 getPhysicalFileSize() const;

   // not for general use
   NITFImageManager& getManager() const;
   /**
    * returns the compression format of the segment
    */
   NITFFormat getFormat() const;

   /**
    * returns the pixel layout of the segment
    */
   NITFLayout getLayout() const;

   /**
    * returns true iff the image segment is in blocked form
    */
   bool isBlocked() const;

   /**
    * returns true iff block masking is used in the image segment
    */
   bool isMasked() const;

   /**
    * returns the index of this image segment 
    */
  int getSegmentNumber() const;

   /**
    * returns the image segment metadata object for this segment
    */
  const NITFImageSegmentMetadata* getImageMetadata() const;

   /**
    * returns the secuirty metadata object for this segment
    */
  const NITFSecurityMetadata* getSecurityMetadata() const;

  LT_STATUS getDimsAtMag(double mag,
                         lt_uint32& width,
                         lt_uint32& height) const = 0;

  // does NOT take ownership of the tileList array
  // this version returns failure, only override is for JP2 image segments
  virtual LT_STATUS setTileMaskList(const lt_uint32* tileMaskList, lt_uint32 tileMaskListLen);

protected:
   LT_STATUS init(LTIOStreamInf *stream,
                  NITFImageManager *manager,
                  const NITFImageSegment *imageSegment,
                  bool useWorldFile);

   LT_STATUS addUnderlyingMetadata(const LTIImageStage &image);

   // blocked image support
   LTIScene computeBlockedScene(const LTIScene& scene) const;
   LT_STATUS copyIntoUserBuffer(const LTIScene& dstScene,
                                LTISceneBuffer& dstBuffer) const;
   bool activeSceneContains(const LTIScene& scene) const;
   void putBlockIntoBuffer_SEQ(LTISceneBuffer& cBuffer,
                               lt_uint8* buf,
                               lt_uint32 blockRow,
                               lt_uint32 blockCol,
                               lt_uint32 blockBand) const;
   void putBlockIntoBuffer_BLOCK(LTISceneBuffer& cBuffer,
                                 lt_uint8* buf,
                                 lt_uint32 blockRow,
                                 lt_uint32 blockCol) const;
   void putBlockIntoBuffer_PIXEL(LTISceneBuffer& cBuffer,
                               lt_uint8* buf,
                               lt_uint32 blockRow,
                               lt_uint32 blockCol) const;
   void putBlockIntoBuffer_ROW(LTISceneBuffer& cBuffer,
                               lt_uint8* buf,
                               lt_uint32 blockRow,
                               lt_uint32 blockCol) const;

   
   
   LTIOStreamInf *m_stream;
   NITFImageManager *m_manager;
   
   const NITFImageSegment *m_imageSegment;
   const NITFFileHeader *m_fileHeader;
   
   LTReusableBuffer *m_reusableBuffer;
   LTIScene *m_activeScene;
   LTISceneBuffer *m_activeSceneBuffer;
   LTIReusableBSQBuffer* m_activeReusableBuffer;
};

LT_END_NAMESPACE(LizardTech)

#endif // NITFIMAGEREADER_H
