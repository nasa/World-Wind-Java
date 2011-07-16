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

#ifndef LT_STREAMINF_H
#define LT_STREAMINF_H

#include "lt_lib_io.h"


LT_BEGIN_NAMESPACE( LizardTech )



/**
 * Abstract definition of a stream.
 *
 * This class is intentionally very sparse, and completely abstract to 
 * facilitate COM usage.  The semantics of the LTIOStreamInf class are
 * very similar to the unix stdio model.
 */
class LTIOStreamInf
{
public:
   // The following is a pure virtual destructor with an empty definition.
   // It might look broken to you, but this is a well-known practice and
   // is explicitly allowed by the standard for very good, technical reasons.
   // (Do a google search for "pure virtual destructor" for details.)
#if defined(LT_COMPILER_GNU) || defined(LT_COMPILER_SUN)
   // gcc doesn't allow the declaration to be in the class body,
   // so we put the member function "definition" in lt_ioStreamInf.cpp
   virtual ~LTIOStreamInf() =0;
#else
   virtual ~LTIOStreamInf() =0 {}
#endif
  
   /** 
    * @name Status accessors
    */
   //@{

   /**
    * Test for end-of-stream
    *
    * Returns true after the first read operation that attempts to 
    * read past the end of the stream. It returns false if the current 
    * position is not end of stream.
    *
    *   @retval  true     end of stream  
    * @retval  false    otherwise
    */
   virtual bool isEOF() =0;

   /**
    * Test for stream openness.
    *
    *   @retval  true     The stream is open
    * @retval  false    otherwise
    */
   virtual bool isOpen() =0;

   //@}


   /** 
    * @name Opening and closing
    */
   //@{

   /**
    *   Opens the stream.
    *
    * Opening a stream puts it in a state that allows data access based on cached
    *   initialization parameters. 
    *
    * @retval  LT_STS_IOStreamUninitialized  The stream has not been initialized with enough
    *                                          information to open the stream
    *   @retval  LT_STS_IOStreamInvalidState   The stream is already open
    *   @retval  LT_STS_Success                On success.
    *   @retval  LT_STS_Failure                Failure.
    * @retval  other                         Implementations may return other codes
    */
   virtual LT_STATUS open() =0;
   
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
   virtual LT_STATUS close() =0;

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
   virtual lt_uint32 read( lt_uint8 *pDest, lt_uint32 numBytes ) = 0;
   
   /**
    * Store the specified number of bytes in the data source.  
    *
    *   @param   pSrc        buffer from which to store data
    *   @param   numBytes    number of bytes to write to stream
    *
    *   @retval  numBytes    number of bytes actually written
    */
   virtual lt_uint32 write( const lt_uint8 *pSrc, lt_uint32 numBytes ) = 0;

   //@}

   /** 
    * @name Positioning
    */
   //@{

   /**
    *   Moves the data access position to origin + offset
    * 
    *   @param   offset   number of bytes from origin at which to the next read or write will take place
    *   @param   origin   place in stream from which to seek
    *
    *   @retval  LT_STS_IOStreamUnsupported    The stream is not seekable
    *   @retval  LT_STS_IOStreamInvalidArgs    The offset and origin do not specify a valid location in the stream
    *   @retval  LT_STS_Success                On success
    *   @retval  LT_STS_Failure                Otherwise
    * @retval  other                         Implementations may return other codes
    */
   virtual LT_STATUS seek( lt_int64 offset, LTIOSeekDir origin ) =0;

   /**
    *   Returns the current data access position as an offset from the start of the data
    *
    *   @retval  postion     Number of bytes from the start of the data  
    *   @retval  -1          On error.  
    * @retval  other       Implementations may return other codes
    */
   virtual lt_int64 tell() =0;

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
   virtual LTIOStreamInf* duplicate() =0;


   /**
    * @brief   Get status code of last error event.
    *
    * read(), write(), tell(), and duplicate() do not explicitly return status codes
    * in the event of an error.  When an error has occurred, this function returns
    * the appropriate status code.  Note calling this function after a successful
    * I/O operation will return an undefined value. 
    *   
    *   @retval  status  the error code
   */
   virtual LT_STATUS getLastError() const =0;


   /**
    * @brief   Get a URI describing the stream object.
    *
    * This function returns a UTF-8, null-terminated string which is a
    * URI describing the origin of the stream object -- for example,
    * "file://foo.txt" or "lt_memstream:".  This string is only intended
    * for diagnostic purposes, i.e. it may not be valid to pass it
    * to the ctor in an attempt to reopen the stream. 
    *   
    *   @retval  uri  the uri string
   */
   virtual const char* getID() const =0;

   //@}

};



LT_END_NAMESPACE( LizardTech )


#endif   // LT_STREAMINF_H
