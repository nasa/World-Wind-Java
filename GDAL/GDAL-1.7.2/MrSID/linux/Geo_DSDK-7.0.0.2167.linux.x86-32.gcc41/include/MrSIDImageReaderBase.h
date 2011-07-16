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

#ifndef MRSIDIMAGEREADERBASE_H
#define MRSIDIMAGEREADERBASE_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)

class MrSIDPasswordDelegate;
class MrSIDSimplePasswordDelegate;

/*
 * memory settings for creating MrSID decoders
 *
 * "Small", "medium", and "large" refer to how much memory the
 * decoder will use when opening the image and constructing certain
 * internal data structures and tables.  In general, decode
 * performance will increase if more memory can be used.
 */
enum MrSIDMemoryUsage
{
   MRSID_MEMORY_USAGE_INVALID    = 0,
   MRSID_MEMORY_USAGE_DEFAULT    = 1,
   MRSID_MEMORY_USAGE_SMALL      = 2,
   MRSID_MEMORY_USAGE_MEDIUM     = 3,
   MRSID_MEMORY_USAGE_LARGE      = 4
};

/*
 * stream settings for creating MrSID decoders
 *
 * Normally, the stream used by the decoder is
 * only opened when doing actual decode work, as resources like
 * file handles can be a scarce resource in some environments.  This
 * obviously incurs a performance penalty; the "KeepStreamOpen" modes
 * can be used to change the behaviour.
 */
enum MrSIDStreamUsage
{
   MRSID_STREAM_USAGE_INVALID    = 0,
   MRSID_STREAM_USAGE_KEEPOPEN   = 2,
   MRSID_STREAM_USAGE_KEEPCLOSED = 3,
   MRSID_STREAM_USAGE_DEFAULT    = MRSID_STREAM_USAGE_KEEPCLOSED
};


/**
 * base class for MrSID image readers
 *
 * All the MrSID image readers (MrSIDImageReader, MG2ImageReader
 *  and MG3ImageReader) inherit from this class.
 */
class MrSIDImageReaderInterface
{
   LT_DISALLOW_COPY_CONSTRUCTOR(MrSIDImageReaderInterface);
public:
   virtual ~MrSIDImageReaderInterface();

   /**
    * get number of resolution levels
    *
    * Returns the number of resolution levels supported by the image.
    * This value returned corresponds to the LTIImage::getMinMagnification()
    * function.
    *
    * @return the number of resolution levels in the MrSID image
    */
   virtual lt_uint8 getNumLevels() const = 0;

   /**
    * image encryption query
    *
    * Returns true iff the image is password-protected.  If the image is
    * locked, the setPasswordDelegate() or setPassword() function must be
    * used to provide the decoder with information to decrypt the image
    * as decode requests are processed.
    *
    * @return true, if image is password-protected
    */
   virtual bool isLocked() const = 0;

   /**
    * get MrSID image version
    *
    * Returns detailed version information for the MrSID image.  Typical
    * version numbers will be 1.0.1 for MG2 (the \a letter value is not
    * used) and 3.0.26.q for MG3.  Most developers need only consider the
    * \a major number, which will be either 2 or 3..
    *
    * See also getSidVersion().
    *
    * @param major  the major version number
    * @param minor  the minor version number
    * @param tweak  the revision number
    * @param letter  the revision build number (MG3 only)
    */
   virtual void getVersion(lt_uint8& major, lt_uint8& minor,
                           lt_uint8& tweak, char& letter) const = 0;

   /**
    * set password handler
    *
    * This function is used to set up a password delegate, which will be
    * automatically called form within the internal decoder logic to obtain
    * a text password, if one is needed for decoded the image.
    *
    * Alternatively, the more direct setPassword() function may be used.
    *
    * See the isLocked() function for more details.
    * 
    * @param  passwordDelegate  the delegate to be called
    */
   void setPasswordDelegate(MrSIDPasswordDelegate* passwordDelegate);

   /**
    * set password handler
    *
    * This function is set the password used by the decoder logic
    * to decode the image, if one is needed.
    *
    * The password must be set prior to performing any decode (read)
    * requests; for more flexibility, the setPasswordDelegate() function
    * may be used.
    *
    * See the isLocked() function for more details.
    * 
    * @param  password  the password for the image
    */
   void setPassword(const lt_utf8* password);

public:
   /**
    * get MrSID version information
    *
    * Returns version information for a specific MrSID image.  This is
    * a static function, which is passed a filename; the getVersion()
    * function operates on the image represented by this MrSIDImageReaderBase
    * image and returns more detailed information.
    *
    * The \a ver value returned will be 2 (for MrSID/MG2), 3 (for MrSID/MG3),
    * or 0 (if error).
    *
    * @param fileSpec  the file to get the version of
    * @param ver       the major version number
    * @return status code indicating success or failure
    */
   static LT_STATUS getSidVersion(const LTFileSpec& fileSpec, lt_uint8& ver);

   /**
    * MrSID version information
    *
    * Returns version information for a specific MrSID image.  This is
    * a static function, which is passed a stream; the getVersion()
    * function operates on the image represented by this MrSIDImageReaderBase
    * image and returns more detailed information.
    *
    * The \a ver value returned will be 2 (for MrSID/MG2), 3 (for MrSID/MG3),
    * or 0 (if error).
    *
    * @param stream  the file to get the version of
    * @param ver       the major version number
    * @return status code indicating success or failure
    */
   static LT_STATUS getSidVersion(LTIOStreamInf& stream, lt_uint8 &ver);

protected:
   MrSIDImageReaderInterface();
   LT_STATUS init(MrSIDMemoryUsage memoryUsage,
                  MrSIDStreamUsage streamUsage);

   static LT_STATUS getGeoCoordFromMetadata(LTIMetadataDatabase &metadata,
                                            LTIGeoCoord &geoCoord,
                                            bool &hasGeo);

   static LTIOStreamInf *openWorldFileStream(const LTFileSpec &fileSpec,
                                             bool useWorldFile);

   MrSIDMemoryUsage m_memoryUsage;
   MrSIDStreamUsage m_streamUsage;

private:
   MrSIDPasswordDelegate* m_pwdDelegate;
   MrSIDSimplePasswordDelegate* m_localPwdDelegate;
};


LT_END_NAMESPACE(LizardTech)

#endif // MRSIDIMAGEREADERBASE_H
