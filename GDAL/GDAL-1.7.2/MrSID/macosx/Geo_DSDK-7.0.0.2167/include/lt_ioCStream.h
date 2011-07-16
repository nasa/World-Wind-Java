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
/* PUBLIC - C */

#ifndef LT_IO_C_STREAM_H
#define LT_IO_C_STREAM_H

#include "lt_base.h"
#include "lt_lib_io.h"
#include "lt_ioCallbackStreamTypes.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

#ifdef LT_CPLUSPLUS
extern "C" {
#endif

/**
 * @name C functions for operating on streams
 *
 * These functions are C-callable analogues to the member
 * functions in the LTIOStreamInf class.
 */
/*@{*/

/**
 * destructor for C stream
 *
 * This function must be called once the stream is no longer needed
 * to free up the stream's allocated resources, i.e. it calls the
 * destructor of the underlying LTIOCallbackStream.
 *
 * @param  stream  stream to be freed
 * @return success/failure code
 */
LT_STATUS lt_ioCStreamDestroy(LTIOStreamH stream);


/**
 * open C stream
 *
 * Opens a previously-created C stream.
 *
 * This function is equivalent to LTIOStreamInf::open().
 *
 * @param  stream  stream to be opened
 * @return success/failure code
 */
LT_STATUS lt_ioCStreamOpen(LTIOStreamH stream);


/**
 * close C stream
 *
 * Closes a C stream.  Note that lt_ioCStreamDestroy() must be
 * called once the stream is no longer needed.
 *
 * This function is equivalent to LTIOStreamInf::close().
 *
 * @param  stream  stream to be closed
 * @return success/failure code
 */
LT_STATUS lt_ioCStreamClose(LTIOStreamH stream);


/**
 * read C stream
 *
 * Read from a C stream.
 *
 * This function is equivalent to LTIOStreamInf::read().
 *
 * @param  stream  stream to read from
 * @param  buf     buffer to read into
 * @param  len     number of bytes to read
 * @return number of bytes read
 */
lt_uint32 lt_ioCStreamRead(LTIOStreamH stream, lt_uint8* buf, lt_uint32 len);


/**
 * write C stream
 *
 * Write to a C stream.
 *
 * This function is equivalent to LTIOStreamInf::write().
 *
 * @param  stream  stream to be written to
 * @param  buf     buffer to write from
 * @param  len     number of bytes to write
 * @return number of bytes written
 */
lt_uint32 lt_ioCStreamWrite(LTIOStreamH stream, const lt_uint8* buf, lt_uint32 len);


/**
 * seek for C stream
 *
 * Seek on a C stream.
 *
 * This function is equivalent to LTIOStreamInf::seek().
 *
 * @param  stream  stream to be seek on
 * @param  offset  distance to seek
 * @param  dir     seek direction
 * @return success/failure code
 */
LT_STATUS lt_ioCStreamSeek(LTIOStreamH stream, lt_int64 offset, LTIOSeekDir dir);


/**
 * tell for C stream
 *
 * Tell on a C stream.
 *
 * This function is equivalent to LTIOStreamInf::tell().
 *
 * @param  stream  stream to get offset of
 * @return stream's current offset
 */
lt_int64 lt_ioCStreamTell(LTIOStreamH stream);


/**
 * is end-of-file? for C stream
 *
 * Check for EOF on a C stream.
 *
 * This function is equivalent to LTIOStreamInf::isEOF().
 *
 * @param  stream  stream to query
 * @return true (1), iff the stream is at EOF
 */
lt_uint8 lt_ioCStreamIsEOF(LTIOStreamH stream);


/**
 * is open? for C stream
 *
 * Check for open on a C stream.
 *
 * This function is equivalent to LTIOStreamInf::isOpen().
 *
 * @param  stream  stream to query
 * @return true (1) iff the stream is open
 */
lt_uint8 lt_ioCStreamIsOpen(LTIOStreamH stream);


/**
 * duplicate C stream
 *
 * Duplicate a C stream.
 *
 * This function is equivalent to LTIOStreamInf::duplicate().
 *
 * @param  stream  stream to duplicate
 * @return the new stream (or NULL if cannot duplicate)
 */
LTIOStreamH lt_ioCStreamDuplicate(LTIOStreamH stream);


/**
 * create callback stream
 *
 * Create an LTIOCallbackStream, via C API.  The parameters correspond
 * to those used in LTIOCallbackStream::initialize().
 *
 * @param  open       user's open function
 * @param  close      user's close function
 * @param  read       user's read function
 * @param  write      user's write function
 * @param  seek       user's seek function
 * @param  tell       user's tell function
 * @param  isEOF      user's isEOF function
 * @param  isOpen     user's isOpen function
 * @param  duplicate  user's duplicate function
 * @param  userData   user's stream data
 * @return the created stream
 */
LTIOStreamH lt_ioCallbackStreamCreate(LTIOCallbackStream_Open open,
                                      LTIOCallbackStream_Close close,
                                      LTIOCallbackStream_Read read,
                                      LTIOCallbackStream_Write write,
                                      LTIOCallbackStream_Seek seek,
                                      LTIOCallbackStream_Tell tell,
                                      LTIOCallbackStream_IsEOF isEOF,
                                      LTIOCallbackStream_IsOpen isOpen,
                                      LTIOCallbackStream_Duplicate duplicate,
                                      void* userData);

/*@}*/

#ifdef LT_CPLUSPLUS
}
#endif

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif
