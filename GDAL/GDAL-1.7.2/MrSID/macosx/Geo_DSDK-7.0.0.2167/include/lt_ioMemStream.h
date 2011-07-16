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


#ifndef LT_IO_MEM_STREAM_H
#define LT_IO_MEM_STREAM_H

#include "lt_ioStreamInf.h"

LT_BEGIN_NAMESPACE( LizardTech )


/**
 * Stream which wraps an array of bytes in memory.
 *
 * The buffer is of a fixed size and does not grow or shrink.
 */
class LTIOMemStream : public LTIOStreamInf
{
public:
   LTIOMemStream();
   virtual ~LTIOMemStream();

   /** 
    * @name Initialization functions
    */
   /*@{*/
   /**
    * Initializes the stream
    *
    * @param   data  pointer to start of buffer
    * @param   size  size of buffer in bytes
    */
   virtual LT_STATUS initialize( void* data, lt_uint32 size );

   /**
    * Initializes the stream
    *
    * This version internally allocates a buffer of the specified size.
    * 
    * @param   size  size of buffer in bytes
    */
   virtual LT_STATUS initialize( lt_uint32 size );
   /*@}*/

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

protected:

   /**   pointer to buffer */
   lt_uint8*   m_data;

   /**   size of buffer */
   lt_uint32   m_size;

   /**   current position  */
   lt_uint32   m_cur;

   /**   data ownership */
   bool        m_ownsData;

   /**   openness */
   bool        m_isOpen;

   bool m_isEOF;
};


LT_END_NAMESPACE( LizardTech )


#endif   // LT_IO_MEM_STREAM_H
