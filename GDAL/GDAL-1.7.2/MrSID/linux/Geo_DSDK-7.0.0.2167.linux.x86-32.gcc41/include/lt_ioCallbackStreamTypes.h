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

#ifndef LT_IO_CALLBACK_STREAM_TYPES_H
#define LT_IO_CALLBACK_STREAM_TYPES_H

#include "lt_base.h"
#include "lt_lib_io.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

#ifdef LT_CPLUSPLUS
extern "C" {
#endif


/**
 * opaque pointer to an LTIOStreamInf
 */
typedef void* LTIOStreamH;

/**
 * @name types specifically for use with LTIOCallbackStream
 */
/*@{*/

/**
 * typedef for callback stream open function
 *
 * The parameter is a void* (user data).
 *
 * A status code is returned.
 */
typedef LT_STATUS (*LTIOCallbackStream_Open)(void*);

/**
 * typedef for callback stream close function
 *
 * The parameter is a void* (user data).
 *
 * A status code is returned.
 */
typedef LT_STATUS (*LTIOCallbackStream_Close)(void*);

/**
 * typedef for callback stream read function
 *
 * The parameters are a void* (user data), a pointer to the buffer to
 * read from, and the number of bytes to read.
 *
 * The number of bytes actually read is returned.
 */
typedef lt_uint32 (*LTIOCallbackStream_Read)(void*, lt_uint8*, lt_uint32);

/**
 * typedef for callback stream write function
 *
 * The parameters are a void* (user data), a pointer to the buffer to
 * write to, and the number of bytes to write.
 *
 * The number of bytes actually written is returned.
 */
typedef lt_uint32 (*LTIOCallbackStream_Write)(void*, const lt_uint8*, lt_uint32);

/**
 * typedef for callback stream seek function
 *
 * The parameters are a void* (user data), the number of bytes to
 * seek, and the seek direction.
 *
 * A status code is returned.
 */
typedef LT_STATUS (*LTIOCallbackStream_Seek)(void*, lt_int64, LTIOSeekDir);

/**
 * typedef for callback stream tell function
 *
 * The parameter is a void* (user data).
 *
 * The current offset is returned.
 */
typedef lt_int64 (*LTIOCallbackStream_Tell)(void*);

/**
 * typedef for callback stream isEOF function
 *
 * The parameter is a void* (user data).
 *
 * A boolean value (0 or 1) is returned.
 */
typedef lt_uint8 (*LTIOCallbackStream_IsEOF)(void*);

/**
 * typedef for callback stream isOpen function
 *
 * The parameter is a void* (user data).
 *
 * A boolean value (0 or 1) is returned.
 */
typedef lt_uint8 (*LTIOCallbackStream_IsOpen)(void*);

/**
 * typedef for callback stream duplicate function
 *
 * The parameter is a void* (user data).
 *
 * A pointer to the new stream is returned.
 */
typedef LTIOStreamH (*LTIOCallbackStream_Duplicate)(void*);

/*@}*/


#ifdef LT_CPLUSPLUS
}
#endif

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif
