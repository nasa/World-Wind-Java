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

#ifndef LTI_GEOFILEIMAGEWRITER_H
#define LTI_GEOFILEIMAGEWRITER_H

// lt_lib_mrsid_core
#include "lti_imageWriter.h"
#include "lti_scene.h"

LT_BEGIN_NAMESPACE(LizardTech)


/**
 * class for writing geographic images to files or streams
 *
 * This abstract class extends LTIImageWriter by adding functions for
 * controlling the output form (either a file or a stream) and world file
 * generation.
 */
class LTIGeoFileImageWriter : public LTIImageWriter
{
   LT_DISALLOW_COPY_CONSTRUCTOR(LTIGeoFileImageWriter);
public:
   /**
    * constructor
    *
    * Constructs a writer which will write to files or streams.  The
    * \c supportsStreams parameter is used to indicate whether the
    * derived class can write directly to a stream or only to files.
    *
    * @param  image            the source of image data to be written
    * @param  supportsStreams  set to true if output to streams is allowed
    */
   LTIGeoFileImageWriter(LTIImageStage* image,
                         bool supportsStreams);

   virtual ~LTIGeoFileImageWriter();

   virtual LT_STATUS writeBegin(const LTIScene& scene);
   virtual LT_STATUS writeStrip(LTISceneBuffer& stripBuffer,
                                const LTIScene& stripScene) = 0;
   virtual LT_STATUS writeEnd();


   /**
    * set output file name
    *
    * Sets the output target to the given filename.  Must be called prior to
    * calling write().
    *
    * @param fileSpec the name of the file to write to
    * @return status code indicating success or failure
    */
   virtual LT_STATUS setOutputFileSpec(const LTFileSpec& fileSpec);

   /**
    * set output file name
    *
    * Sets the output target to the given filename.  Must be called prior to
    * calling write().
    *
    * @param fileSpec the name of the file to write to
    * @return success or failure
    */
   virtual LT_STATUS setOutputFileSpec(const char* fileSpec);

   /**
    * set output file stream
    *
    * Sets the output target to the given stream.  Must be called prior to
    * calling write().
    *
    * This operation will only succeed if the derived class has set the
    * \c supportsStream parameter to the LTIGeoFileImageWriter ctor to true.
    *
    * @param stream the stream to write to (may not be NULL)
    * @return status code indicating success or failure
    */
   virtual LT_STATUS setOutputStream(LTIOStreamInf* stream);

   /**
    * enable writing of world file
    *
    * Enables or disables automatic generation of a world file.  Only
    * applicable when the output target is a filename.
    *
    * The default is to not generate a world file.
    *
    * @param enabled set to true for world file generation
    */
   virtual void setWorldFileSupport(bool enabled);

protected:
   /**
    * get underlying stream
    *
    * Returns the stream being written to.
    *
    * If the output target is a filename, the returned stream will  be NULL
    * until the write() sequence has begun.
    *
    * @return the stream (may be NULL)
    */
   LTIOStreamInf* getStream() const;

   /**
    * get target filename
    *
    * Returns the name of the file being written to.
    *
    * If the output target is set by the user to be a stream, the returned filename
    * will  be NULL.
    *
    * @return the filename (may be NULL)
    */
   LTFileSpec* getFileSpec() const;

private:
   bool m_supportsStreams;
   LTFileSpec* m_fileSpec;
   LTIOStreamInf* m_stream;
   bool m_ownStream;

   bool m_worldFileEnabled;

   LTIScene m_fullScene;

};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_GEOFILEIMAGEWRITER_H
