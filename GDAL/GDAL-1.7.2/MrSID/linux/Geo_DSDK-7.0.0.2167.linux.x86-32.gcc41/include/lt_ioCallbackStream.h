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

#ifndef LT_IO_CALLBACK_STREAM_H
#define LT_IO_CALLBACK_STREAM_H

#include "lt_ioCallbackStreamTypes.h"
#include "lt_ioStreamInf.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

LT_BEGIN_NAMESPACE( LizardTech )


/**
 * Stream driven entirely by user-defined callbacks
 *
 * This class implements a stream whose operations -- read(), write(), open(),
 * close(), etc -- are all defined by functions passed in from the user.
 *
 * This class can be used as an alternative to deriving custom stream classes
 * from LTIOStreamInf.  This class also forms the basis for the C-callable
 * stream defined in lt_ioCStream.h.
 */
class LTIOCallbackStream : public LTIOStreamInf
{
public:
   LTIOCallbackStream();

   virtual ~LTIOCallbackStream();

   /**
    * initialize
    *
    * The parameters to this function are a set of function pointers
    * which implement all the required operations needed to support
    * a stream.  Their semantics exactly mirror the semantics of the
    * corresponding member functions in LTIOStreamInf.
    *
    * The \a user parameter is a pointer to a user-defined area
    * containing information about the stream itself, e.g. the
    * filename and a FILE*.  This pointer is passed as the first
    * argument to each of the user's stream functions.  This class
    * never attempts to interpret this data directly.
    *
    * Note that the implementation of the \a duplicate function
    * is required to create a new copy of this user-defined data by
    * whatever means appropriate.  The user retains ownership of the
    * copied user data.
    *
    * @param  open       pointer to user's open function
    * @param  close      pointer to user's close function
    * @param  read       pointer to user's read function
    * @param  write      pointer to user's write function
    * @param  seek       pointer to user's seek function
    * @param  tell       pointer to user's tell function
    * @param  isEOF      pointer to user's isEOF function
    * @param  isOpen     pointer to user's isOpen function
    * @param  duplicate  pointer to user's duplicate function
    * @param  user       pointer to user-defined stream data
    * @return status code indicating success or failure
    */
   virtual LT_STATUS initialize(LTIOCallbackStream_Open open,
                                LTIOCallbackStream_Close close,
                                LTIOCallbackStream_Read read,
                                LTIOCallbackStream_Write write,
                                LTIOCallbackStream_Seek seek,
                                LTIOCallbackStream_Tell tell,
                                LTIOCallbackStream_IsEOF isEOF,
                                LTIOCallbackStream_IsOpen isOpen,
                                LTIOCallbackStream_Duplicate duplicate,
                                void* user);

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
   LTIOCallbackStream_Open m_open;
   LTIOCallbackStream_Close m_close;
   LTIOCallbackStream_Read m_read;
   LTIOCallbackStream_Write m_write;
   LTIOCallbackStream_Seek m_seek;
   LTIOCallbackStream_Tell m_tell;
   LTIOCallbackStream_IsEOF m_isEOF;
   LTIOCallbackStream_IsOpen m_isOpen;
   LTIOCallbackStream_Duplicate m_duplicate;

   void* m_user;
};

LT_END_NAMESPACE( LizardTech )

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LT_IO_CALLBACK_STREAM_H
