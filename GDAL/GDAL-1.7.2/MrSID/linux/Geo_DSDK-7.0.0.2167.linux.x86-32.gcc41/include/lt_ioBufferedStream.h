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

#ifndef LT_IO_BUFFERED_STREAM_H
#define LT_IO_BUFFERED_STREAM_H

#include "lt_ioStreamInf.h"

LT_BEGIN_NAMESPACE( LizardTech )

class LTIOBufPage;
class LTIOBufPageCache;

/**
 * Stream which wraps an array of bytes in memory.
 *
 * The buffer is of a fixed size and does not grow or shrink.
 */
class LTIOBufferedStream : public LTIOStreamInf
{
public:
   LTIOBufferedStream();
   virtual ~LTIOBufferedStream();

   /**
    * Initializes the stream
    *
    * @param   src       source stream; this instance takes ownership of it
    * @param   pageSize size of each page in bytes
    * @param   numPages number of pages to keep cached in memory
    *   @param   tempDir   temp directory to store unused pages; this instance
    *                     does not clean them up
    */
   virtual LT_STATUS initialize(   LTIOStreamInf* src,
                                 lt_uint32 pageSize=1024,
                                 lt_uint32 numPages = 10,
                                 const char* tempDir = 0L);


   virtual bool isEOF();
   virtual bool isOpen();
   virtual LT_STATUS open();
   virtual LT_STATUS close();
   virtual lt_uint32 read( lt_uint8 *pDest, lt_uint32 numBytes );
   virtual lt_uint32 write( const lt_uint8 *pSrc, lt_uint32 numBytes );
   virtual LT_STATUS seek( lt_int64 offset, LTIOSeekDir origin );
   virtual lt_int64 tell();
   virtual LTIOStreamInf* duplicate();
   virtual LT_STATUS getLastError() const;
   virtual const char* getID() const;

private:
   const LTIOBufPage* getPage( lt_uint64 pageIndex );

protected:
   lt_uint32      m_pageSize;
   lt_uint32      m_numPages;
   LTIOBufPageCache*      m_buffer;
   LTIOStreamInf*   m_src;
   lt_uint64      m_cur;
   bool             m_eof;
   char*            m_tempDir;
};



LT_END_NAMESPACE( LizardTech )


#endif   // LT_IO_BUFFERED_STREAM_H
