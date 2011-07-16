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


#ifndef LT_IO_DYNAMIC_MEM_STREAM_H
#define LT_IO_DYNAMIC_MEM_STREAM_H

#include "lt_ioStreamInf.h"
#include <stdlib.h>

LT_BEGIN_NAMESPACE( LizardTech )


/**
 *
 * holds a memory buffer of variable size
 *
 * Unlike LTIOMemStream, which uses a fixed-size buffer, this class will
 * grow its buffer as you write past the end of it.  It is most suitable
 * for use as an internal "temp stream".  The stream is initially empty.
 *
 * The stream cannot hold more than 2^32 bytes of data.
 */
class LTIODynamicMemStream : public LTIOStreamInf
{
public:
   /** @name Memory Allocation and Deallocation Functions */
   //@{
#if defined(LT_OS_WINCE)
   typedef void* (__cdecl * Allocator)(size_t);
   typedef void(__cdecl *Deallocator)(void*);
   typedef void*(__cdecl *Reallocator)(void*,size_t);
#else
   typedef void*(*Allocator)(size_t);
   typedef void(*Deallocator)(void*);
   typedef void*(*Reallocator)(void*,size_t);
#endif
   //@}

public:
   LTIODynamicMemStream();
   virtual ~LTIODynamicMemStream();

   /** 
    * @name Initialization functions
    */
   /*@{*/
   /**
    * Initializes the stream
    * 
    * @param   size  initial size of buffer in bytes
    * @param   growthRate factor of growth Valid Values: [1-2)
    */
   virtual LT_STATUS initialize( lt_uint32 size = 4096, float growthRate=2 );

   /**
    * Initializes the stream, with custom memory management
    * 
    * The allocator function should return NULL in the event of failure,
    * as opposed to throwing an exception -- that is, be like malloc()
    * and not like new.
    *
    * @param   size    initial size of buffer in bytes
    * @param   allo    user-defined memory allocator
    * @param   deallo  user-defined memory deallocator
    * @param   reallo  user-defined memory reallocator
    * @param   growthRate factor of growth Valid Values: [1-2)
    */
   virtual LT_STATUS initialize( lt_uint32 size,
                                 Allocator allo, Deallocator deallo,
                                 Reallocator reallo=NULL, float growthRate=2 );

   /*@}*/

   virtual bool isEOF();
   virtual bool isOpen();
   virtual LT_STATUS open();
   virtual LT_STATUS close();
   virtual lt_uint32 read( lt_uint8 *pDest, lt_uint32 numBytes );
   virtual lt_uint32 write( const lt_uint8 *pSrc, lt_uint32 numBytes );
   virtual LT_STATUS seek( lt_int64 offset, LTIOSeekDir origin );
   virtual lt_int64 tell();


   /** 
    * @name Memory Management
    */
   /*@{*/
   /**
    * Returns the allocator function pointer
    */
   Allocator getAllocator() const         {  return m_alloc; }
   
   /**
    * Returns the deallocator function pointer
    */
   Deallocator getDeallocator() const    {  return m_dealloc; } 

   /**
    * Returns the reallocator function pointer
    */
   Reallocator getReallocator() const    {  return m_realloc; } 

   /**
    * Get the underlying buffer
    *
    * The pointer is guaranteed to be good until
    * the next call to write(), or close()
    *
    * @return  pointer to underlying buffer
    */
   const lt_uint8* getData() const  {  return m_data; }

   
   /**
    * Detach the underlying buffer from the stream,
    * and closes the stream. The caller is responsible 
    * for deallocating the buffer.  (Note that you must
    * use the Deallocator method, not delete[].) 
    *
    * @param data receives pointer to underlying buffer
    *
    * @return  LT_STS_Success upon success
    */
   LT_STATUS detachAndClose(lt_uint8*& data);

   /**
    * Returns the number of bytes which are in the
    * stream. This may be smaller than the amount
    * actually allocated.
    *
    * @return  size (in bytes) of stream
    */
   lt_uint64 size() const           {  return m_userSize; }

   /*@}*/

   virtual LTIOStreamInf* duplicate();
   virtual LT_STATUS getLastError() const;
   virtual const char* getID() const;
 
protected:
   /**   extend size of data buffer if required */
   bool grow(lt_uint32 numBytes);

   /**   pointer to buffer */
   lt_uint8*   m_data;

   /**   initial size of buffer */
   lt_uint32   m_initialSize;

   /**   size of buffer as allocated */
   lt_uint32   m_bufferSize;

   /**   size of buffer as accessible by user */
   lt_uint32   m_userSize;

   /**   current position  */
   lt_uint32   m_cur;

   /**   openness */
   bool        m_isOpen; 

   /**   allocator function */
   Allocator m_alloc;

   /**   deallocator */
   Deallocator m_dealloc;

   /**   reallocator */
   Reallocator m_realloc;

   void* defaultRealloc(void*,size_t);

   LT_STATUS m_lastError;

   bool m_isEOF;
   
   /** the factor of growth of the memory [1-2): */
   float m_growthRate;
};


LT_END_NAMESPACE( LizardTech )


#endif   // LT_IO_DYNAMIC_MEM_STREAM_H
