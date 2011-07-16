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

#ifndef LT_IO_FILE_STREAM_H
#define LT_IO_FILE_STREAM_H

#include "lt_ioStreamInf.h"
#include "lt_fileSpec.h"
#include <stdio.h>

LT_BEGIN_NAMESPACE( LizardTech )


/**
 * File stream
 *
 * This class implements a file-based stream.
 *
 */
class LTIOFileStream : public LTIOStreamInf
{
public:

   /** 
    * @name Construction, destruction, initialization
    */
   //@{

   /**   
    * Default Constructor
    */
   LTIOFileStream(void);

   /**   
    * Destructor
    */
   virtual ~LTIOFileStream(void);

   /**
    * Initializes the stream from a file spec
    *
    * @param   fs      file spec
    * @param   mode  mode (see stdio.h)
    */
   virtual LT_STATUS initialize(const LTFileSpec& fs, const char* mode);
   LT_STATUS initialize(const char* path, const char* mode);
   //@}

   // LTIOStreamInf overrides
   virtual bool isEOF();
   virtual bool isOpen();
   virtual LT_STATUS open();
   virtual LT_STATUS close();
   virtual lt_uint32 read(lt_uint8 *pDest, lt_uint32 numBytes);
   virtual lt_uint32 write(const lt_uint8 *pSrc, lt_uint32 numBytes);
   virtual LT_STATUS seek(lt_int64 offset, LTIOSeekDir origin);
   virtual lt_int64 tell();
   virtual LTIOStreamInf *duplicate();
   virtual LT_STATUS getLastError() const;
   virtual const char* getID() const;

   /** 
    * @name Status accessors
    */
   //@{
   /**
    * Returns underlying stdio error code
    */
   int stdio_ferror();

   /**
    * Clears underlying stdio error code
    */
   void stdio_clearerr();
   //@}

   /**
    *   Set buffering - may be called only after open() but before
    *   the first read/write operation.
    *   @param   buf   buffer to use; if NULL then one is allocated
    *   @param   mode   one of the following:
    *                  _IONBF (unbuffered)
    *                  _IOLBF (line buffered)
    *                  _IOFBF (fully buffered)
    *   @param   size   size of buffer
    */
   int stdio_setvbuf( lt_uint8* buf, lt_uint32 mode, lt_uint32 size  );

private:

   void cleanup();

protected:
   FILE* m_file;
   
   enum
   {
      unknown_state = 1,
      reading_state = 2,
      writing_state = 3
   } m_state;

   LTFastFileSpec   m_path;
   char *m_mode;
   char *m_uri;
};

LT_END_NAMESPACE( LizardTech )

#endif   // LT_STREAMINF_H
