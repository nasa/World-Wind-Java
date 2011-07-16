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
 * Preprocessor definitions used throughout LizardTech sources.  This file
 * should not be included directly; use lt_base.h instead.
 *
 * @note This file is C-callable.
 */

#ifndef LT_DEFINE_H
#define LT_DEFINE_H

#include "lt_platform.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4) 
#endif


/**
 * @name Language settings
 */
/*@{*/

#if defined(__cplusplus)
   /** compiler supports C++ (as opposed to straight C) */
   #define LT_CPLUSPLUS

   #define LT_DISALLOW_COPY_CONSTRUCTOR(classname) \
      private: \
         classname(const classname &); \
         classname &operator=(const classname &)

#endif

/*}@*/


/**
 * @name C++ namespace settings
 */
/*@{*/

#if defined(__cplusplus) && \
    ( defined(LT_COMPILER_MS) || \
      defined(LT_COMPILER_GNU) || \
      defined(LT_COMPILER_SUN) || \
      defined(_USE_NAMESPACE_) )
   /** compiler supports namespaces */
   #define LT_NAMESPACE_SUPPORT
#else
   /** compiler does not support namespaces */
   #undef LT_NAMESPACE_SUPPORT
#endif

#ifdef LT_NAMESPACE_SUPPORT
   /** declare start of namespace, for declarations */
   #define LT_BEGIN_NAMESPACE( theNameSpace ) namespace theNameSpace {
   /** declare end of namespace, for declarations */
   #define LT_END_NAMESPACE( theNameSpace )   }

   /** declare start of nameless namespace, for declarations */
   #define LT_BEGIN_NAMELESS_NAMESPACE namespace {
   /** declare end of nameless namespace, for declarations */
   #define LT_END_NAMELESS_NAMESPACE   }

   /** declare use of namespace */
   #define LT_USE_NAMESPACE( theNameSpace ) using namespace theNameSpace;
   /** declare use of name */
   #define LT_USE_NAME( theName ) using theName;
#else
   #define LT_BEGIN_NAMESPACE( theNameSpace )
   #define LT_END_NAMESPACE( theNameSpace )

   #define LT_BEGIN_NAMELESS_NAMESPACE 
   #define LT_END_NAMELESS_NAMESPACE  

   #define LT_USE_NAMESPACE( theNameSpace ) 
   #define LT_USE_NAME( theName ) 
#endif

/*@}*/


/**
 * @name Debug settings
 */
/*@{*/

#if (defined(LT_OS_WIN) && defined(_DEBUG))
   /** symbol for enabling debug code */
   #define LT_DEBUG
#endif

/*@}*/


/**
 * @name Miscellaneous macros
 */
/*@{*/

/** macro turns its argument into a quoted string */
#define LT_STRINGIFY(x) #x
/** macro expands its argument, and it into a quoted string */
#define LT_XSTRINGIFY(x) LT_STRINGIFY(x)

/** return lesser of two parameters */
#define LT_MIN(A,B) ((A) < (B) ? (A) : (B))
/** return greater of two parameters */
#define LT_MAX(A,B) ((A) > (B) ? (A) : (B))

/*@}*/





/**
 * @name Support for status string generation
 */
/*@{*/

#if defined(LT_STATUSSTRING_GENERATE)
   #define LT_STATUSSTRING_ADD(NUM,STR)    { NUM, STR },
#else
   #define LT_STATUSSTRING_ADD(NUM,STR)
#endif

typedef struct { int code; const char* str; } LTStatusStringTable;
/*@}*/


#if defined(LT_COMPILER_MS)
   #pragma warning(pop) 
#endif


/* globally disable spurious VC++ warning resulting from use of templates  */
#ifdef LT_COMPILER_MS
   #pragma warning(disable:4786)
#endif


/* other includes to provide users of lt_define.h */
#include "lt_types.h"

#endif /* LT_DEFINE_H */
