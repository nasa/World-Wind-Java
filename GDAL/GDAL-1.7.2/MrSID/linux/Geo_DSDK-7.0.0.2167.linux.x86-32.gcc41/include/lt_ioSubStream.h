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


#ifndef LT_IOSUBSTREAM_H
#define LT_IOSUBSTREAM_H

#include "lt_ioStreamInf.h"
#include <stdio.h>

LT_BEGIN_NAMESPACE( LizardTech )



/**
 * LTIOSubStream
 *
 * A SubStream is a stream which wraps another stream, but provides access
 * to only a (contiguous) subset of the bytes of the parent stream -- without
 * revealing any of the surrounding bytes.
 *
 * Consider the following situation: the MG2 decoder always assumes byte 0 of
 * the image stream given to it is the start of the image, but you have an
 * image embedded in some larger stream -- say from byte offset 100 to 2100.
 * By creating a substream from the larger stream and giving that to the
 * decoder, the decoder will be doing the right thing when it naively issues
 * a "seek-to-beginning" request.
 *
 * Note that the SubStream does NOT take ownership of the stream given to it.
 * The caller should duplicate() the main stream first, if needed.  Closing
 * the substream will close the parent stream, but deleting the substream will
 * not delete the parent stream.
 *
 * Note that the results might be undefined if the parent or child stream is
 * modified at any point -- try to avoid such situations.
 */
class LTIOSubStream : public LTIOStreamInf
{
public:

   /** 
    * @name Construction, destruction, initialization
    */
   //@{

   /**   
    * Default Constructor
    */
   LTIOSubStream();

   /**   
    * Destructor
    */
   virtual ~LTIOSubStream();

   /**
    * Initializes the stream
    *
    * @param   stream  the parent stream to be subsetted
    * @param   start   the byte offset to be byte 0 of the subset stream 
    * @param   end     the byte offset to be the last byte of the subset
    *                  stream: reading or writing past this byte will be
    *                  handled as an EOF condition
    * @param   takeOwnership   if true, this object will take ownership
    *                  of \a stream and delete it
    */
   virtual LT_STATUS initialize(LTIOStreamInf* stream,
                                lt_int64 start,
                                lt_int64 end,
                                bool takeOwnership=false);

   /** 
    * @name Status accessors
    */
   //@{

   /**
    * Indicates whether the stream is at the end of its data or not.
    *
    *   @return  true     the stream is valid and at the end of its data.  
    * @retval false     otherwise
    */
   virtual bool isEOF();


   /** 
    * @name Opening and closing
    */
   //@{

   /**
    * Is the stream open?
    *
    *   @retval  true  the stream is valid and in a state that allows data access.
    * @retval  false otherwise
    */
   virtual bool isOpen();

   /**
    *   Opens the stream.
    *
    * Opening a stream puts it in a state that allows data access based on cached
    *   initialization parameters.
    *
    * @retval  LT_STS_IOStreamUninitialized  The stream has not been initialized with enough
    *                                          information to open the stream
    *   @retval  LT_STS_IOStreamInvalidState   The the stream is already open
    *   @retval  LT_STS_Success                On success.
    *   @retval  LT_STS_Failure                Otherwise.
    */
   virtual LT_STATUS open();
   
   /**
    *   Closes the stream.
    *
    *   Puts the stream in a state that does not allow data access.  May
    *   free up resources, but only in such a way that doesn't inhibit
    *   successful future calls to open()
    *
    *   @retval  LT_STS_Success                On success, or if the stream is already closed.
    *   @retval  LT_STS_Failure                Otherwise.
    */
   virtual LT_STATUS close();

   //@}


   /** 
    * @name Data access
    */
   //@{

   /**
    * Retrieve the specified number of bytes from the data source and
    *   place them in pDest.
    *   
    *   @param   pDest         buffer in which to store read data
    *   @param   numBytes      number of bytes to read from stream
    *
    *   @retval numBytes        The number of bytes actually read
    */
   virtual lt_uint32 read( lt_uint8 *pDest, lt_uint32 numBytes );
   
   /**
    * Store the specified number of bytes in the data source.  
    *
    *   @param   pSrc        buffer from which to store data
    *   @param   numBytes    number of bytes to write to stream
    *
    *   @retval  numBytes    number of bytes actually written
    */
   virtual lt_uint32 write( const lt_uint8 *pSrc, lt_uint32 numBytes );

   //@}

   /** 
    * @name Positioning
    */
   //@{

   /**
    *   Moves the the data access position to origin + offset
    * 
    *   @param   offset   number of bytes from origin at which to the next read or write will take place
    *   @param   origin   place in stream from which to seek
    *
    *   @retval  LT_STS_IOStreamUnsupported    The stream is not seekable
    *   @retval  LT_STS_IOStreamInvalidArgs    The offset and origin do not specify a valid location in the stream
    *   @retval  LT_STS_Success                On success
    *   @retval  LT_STS_Failure                Otherwise
   */
   virtual LT_STATUS seek( lt_int64 offset, LTIOSeekDir origin );

   /**
    *   Returns the current data access position as an offset from the start of the data
    *
    *   @retval  postion     Number of bytes from the start of the data  
    *   @retval  -1          On error.  
    */
   virtual lt_int64 tell();

   //@}

   /** 
    * @name Other operations
    */
   //@{

   /**
    * @brief   Clone the stream
    *
    * Create new stream of the same type with the same initialization parameters.  
    *   The transmission of these parameters is the responsibility of the derived type.
    *   The new stream should initially return false for isOpen().
    *   
    *   @retval  NULL  the stream could not be duplicated; valid LTIOStreamInf* otherwise.
   */
   virtual LTIOStreamInf* duplicate();

   virtual LT_STATUS getLastError() const;

   virtual const char* getID() const;

   //@}


protected:
   /**   cleanup method */
   void cleanup();

   LTIOStreamInf* m_stream;
   bool m_ownsStream;
   lt_int64 m_startOffset;
   lt_int64 m_endOffset;

   bool m_isEOF;
};



LT_END_NAMESPACE( LizardTech )


#endif   // LT_IOSUBSTREAM_H
