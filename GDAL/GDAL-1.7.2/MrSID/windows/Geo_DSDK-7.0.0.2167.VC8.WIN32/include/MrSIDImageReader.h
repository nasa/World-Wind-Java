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

#ifndef MRSIDIMAGEREADER_H
#define MRSIDIMAGEREADER_H

// lt_lib_mrsid_mrsidReader
#include "MrSIDImageReaderBase.h"
#include "lti_imageFilter.h"


LT_BEGIN_NAMESPACE(LizardTech)

class MrSIDImageStageManager;
class LTIMosaicFilter;
class MG2ImageReader;
class MG3SingleImageReader;

/**
 * reader for MrSID images (MG2 and MG3)
 *
 * This class supports reading MrSID/MG2 and MrSID/MG3 images, including
 * composite MG3 images.
 */
class MrSIDImageReader : public LTIImageFilter,
                         public MrSIDImageReaderInterface
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(MrSIDImageReader);
public:
   /**
    * initializer
    *
    * Construct a MrSID reader from the given file.
    *
    * @param  fileSpec      file containing MrSID image
    * @param  useWorldFile  incorporate world file data when reading image
    * @param  memoryUsage   control memory resource usage
    * @param  streamUsage   control stream resource usage
    */
   LT_STATUS initialize(const LTFileSpec& fileSpec,
                        bool useWorldFile = false,
                        MrSIDMemoryUsage memoryUsage = MRSID_MEMORY_USAGE_DEFAULT,
                        MrSIDStreamUsage streamUsage = MRSID_STREAM_USAGE_DEFAULT);

   /**
    * initializer
    *
    * Construct a MrSID reader from the given stream.
    *
    * @param  stream        stream containing MrSID image (may not be NULL)
    * @param  worldFileStream  stream containing world file data (may be NULL)
    * @param  memoryUsage   control memory resource usage
    * @param  streamUsage   control stream resource usage
    */
   LT_STATUS initialize(LTIOStreamInf* stream,
                        LTIOStreamInf* worldFileStream = NULL,
                        MrSIDMemoryUsage memoryUsage = MRSID_MEMORY_USAGE_DEFAULT,
                        MrSIDStreamUsage streamUsage = MRSID_STREAM_USAGE_DEFAULT);

   // MrSIDImageReaderBase overrides
   lt_uint8 getNumLevels(void) const;
   bool isLocked(void) const;

   void getVersion(lt_uint8& major, lt_uint8& minor,
                   lt_uint8& tweak, char& letter) const;
   
   // for LizardTech internal use only
   const MrSIDImageStageManager &getManager(void) const;
   // for LizardTech internal use only
   const LTIMosaicFilter *getMosaicFilter(void) const;
   // for LizardTech internal use only
   const MG2ImageReader *getMG2ImageReader(void) const;
   // for LizardTech internal use only
   const MG3SingleImageReader *getMG3ImageReader(void) const;

protected:
   LT_STATUS protectedInit(LTIOStreamInf *worldfile,
                           bool deleteImages);

   MrSIDImageStageManager *m_manager;
   LTIMosaicFilter *m_mosaicFilter;
   LTIImageStage *m_mrsidReader;
   lt_uint8 m_numLevels;
   bool m_isLocked;
};


LT_END_NAMESPACE(LizardTech)

#endif // MRSIDIMAGEREADER_H
