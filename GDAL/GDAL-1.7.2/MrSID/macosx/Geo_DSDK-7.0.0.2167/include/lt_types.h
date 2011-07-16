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

/**      
 * @file
 *
 * Declaration of standard types and limits in a C-interface.
 * 
 * @note This file is C-callable.
 */


#ifndef LT_TYPES_H
#define LT_TYPES_H

#include <limits.h>
#include <float.h>

#include "lt_platform.h"


#if defined(LT_COMPILER_MS)
   #pragma warning(push,4) 
#endif

/**
 * @name Fundamental types
 *
 * We provide typdefs for all the basic C datatypes.
 *
 * @note 80 and 128-bit floats are inherently unportable, so we do not use them.
 */
/*@{*/
/** signed 8-bit integer */
typedef signed char     lt_int8;
/** unsigned 8-bit integer */
typedef unsigned char   lt_uint8;
/** signed 16-bit integer */
typedef signed short    lt_int16;
/** unsigned 16-bit integer */
typedef unsigned short  lt_uint16;
/** signed 32-bit integer */
typedef signed int      lt_int32;
/** unsigned 32-bit integer */
typedef unsigned int    lt_uint32;
/** 32-bit floating point (\b DEPRECATED) */
typedef float           lt_float32;
/** 64-bit floating point (\b DEPRECATED) */
typedef double          lt_float64;

#if defined(LT_COMPILER_MS)
   /** signed 64-bit integer */
   typedef signed __int64     lt_int64;
   /** unsigned 64-bit integer */
   typedef unsigned __int64   lt_uint64;
#elif defined ( LT_COMPILER_GNU ) || \
      defined ( LT_COMPILER_SUN )
   /** signed 64-bit integer */
   typedef long long int              lt_int64;
   /** unsigned 64-bit integer */
   typedef unsigned long long int     lt_uint64;
#else
   #error NOT YET PORTED TO TARGET COMPILER
#endif
/*@}*/


#ifndef DOXYGEN_EXCLUDE

#define LT_CHAR_MAX ((char)CHAR_MAX) 
#define LT_CHAR_MIN ((char)CHAR_MIN) 
#define LT_SCHAR_MAX ((signed char)SCHAR_MAX) 
#define LT_SCHAR_MIN ((signed char)SCHAR_MIN) 
#define LT_UCHAR_MAX ((unsigned char)UCHAR_MAX) 

#define LT_SHRT_MAX ((short int)SHRT_MAX) 
#define LT_SHRT_MIN ((short int)SHRT_MIN) 
#define LT_USHRT_MAX ((unsigned short int)USHRT_MAX) 

#define LT_INT_MAX ((int)INT_MAX) 
#define LT_INT_MIN ((int)INT_MIN) 
#define LT_UINT_MAX ((unsigned int)UINT_MAX) 

#define LT_LONG_MAX ((long int)LONG_MAX) 
#define LT_LONG_MIN ((long int)LONG_MIN) 
#define LT_ULONG_MAX ((unsigned long int)ULONG_MAX) 

#define LT_FLT_MIN ((float)FLT_MIN)
#define LT_FLT_MAX ((float)FLT_MAX)
#define LT_FLOAT_MIN (LT_FLT_MIN)
#define LT_FLOAT_MAX (LT_FLT_MAX)

#define LT_DBL_MIN ((double)DBL_MIN)
#define LT_DBL_MAX ((double)DBL_MAX)
#define LT_DOUBLE_MIN (LT_DBL_MIN)
#define LT_DOUBLE_MAX (LT_DBL_MAX)

#define LT_INT8_MAX      ((lt_int8)127)                     /*  2^7 - 1  */
#define LT_INT8_MIN      ((lt_int8)(-LT_INT8_MAX - 1))      /*  -2^7     */
#define LT_UINT8_MAX     ((lt_uint8)255U)                   /*  2^8 - 1  */
#define LT_UINT8_MIN     (0)
#define LT_INT16_MAX     ((lt_int16)32767)                  /*  2^15 - 1 */
#define LT_INT16_MIN     ((lt_int16)-LT_INT16_MAX - 1)      /*  -2^15    */
#define LT_UINT16_MAX    ((lt_uint16)65535U)                /*  2^16 - 1 */
#define LT_UINT16_MIN    (0)
#define LT_INT32_MAX     ((lt_int32)2147483647)             /* 2^31 - 1 */
#define LT_INT32_MIN     ((lt_int32)(-LT_INT32_MAX - 1))    /* -2^31    */
#define LT_UINT32_MAX    ((lt_uint32)4294967295U)           /* 2^32 - 1 */
#define LT_UINT32_MIN    (0)
#define LT_INT64_MIN     ((lt_int64)-LT_INT64_MAX - 1)      /* -2^63    */
#define LT_UINT64_MIN    (0)

// GNU wants these constants suffixed, windows does not
#if defined(LT_COMPILER_GNU) || \
    defined(LT_COMPILER_SUN)
   #define LT_INT64_MAX     ((lt_int64)9223372036854775807LL)    /* 2^63 - 1 */
   #define LT_UINT64_MAX    ((lt_uint64)18446744073709551615ULL)  /* 2^64 - 1 */
#elif defined(LT_COMPILER_MS)
   #define LT_INT64_MAX     ((lt_int64)9223372036854775807)    /* 2^63 - 1 */
   #define LT_UINT64_MAX    ((lt_uint64)18446744073709551615)  /* 2^64 - 1 */
#else
   #error NOT YET PORTED TO TARGET COMPILER
#endif

#endif /* DOXYGEN_EXCLUDE */


/** (\b DEPRECATED) */
//typedef char lt_utf8;
//#define LT_UTF8STR(str) ((lt_utf8 *)(str))

/**
 * typedefs of basic character set related types
 */
//@{
typedef char      lt_ascii;
typedef lt_uint8   lt_utf8;
//typedef char   lt_utf8;
typedef lt_uint16   lt_utf16;
typedef lt_uint32   lt_utf32;
//@}


#if defined(LT_COMPILER_MS)
   #pragma warning(pop) 
#endif

#endif /* LT_TYPES_H */
