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
 * Preprocessor symbols for canonical identification of OS, architecture,
 * and compiler.  Scrupulous use of these and only these symbols avoids
 * portability problems due to inconsistent platform tests.
 * 
 * For a given target platform XYZ, we define three symbols with the value 1:
 *
 * \li \c LT_OS_XYZ defines the operating system
 * \li \c LT_COMPILER_XYZ defines the compiler
 * \li \c LT_ARCH_XYZ defines the HW architecture
 *
 * Note for Windows, we treat WIN32, WIN64, and WinCE as distinct OS's, but
 * both will define LT_OS_WIN for the typical cases.
 *
 * See the file lt_platform.h for full details.
 *
 * @note This file is C-callable.
 */


#ifndef LT_PLATFORM_H
#define LT_PLATFORM_H

/*
 */


/*
 * Check for Cross Platform Macros
 */

/*  Check for GCC, Intel, of MSVC */
#if defined(__GNUC__) || defined(__GNUG__)   /* GNU's GCC */
   #define LT_COMPILER_GNU 1
   #if defined(__GNUC__)
      #define LT_COMPILER_GCC 1
   #endif
   #if defined(__GNUG__)
      #define LT_COMPILER_GXX 1
   #endif
   #if(__GNUC__ == 2)
      #define LT_COMPILER_GCC2 1
   #elif(__GNUC__ == 3)
      #define LT_COMPILER_GCC3 1
   #elif(__GNUC__ == 4)
      #define LT_COMPILER_GCC4 1
   #else
      #error PLATFORM ERROR: Unsupported version of GCC 
   #endif
#elif defined(__ECL) || defined(__LCL)  /* Intel's VTune */
   #define LT_COMPILER_INTEL 1
#elif defined(_MSC_VER)    /* MS Visual C/C++ */
   #define LT_COMPILER_MS 1
   #if _MSC_VER == 1300 || _MSC_VER == 1310
      #define LT_COMPILER_MS7 1
   #endif
   #if _MSC_VER == 1400 || _MSC_VER == 1410
      #define LT_COMPILER_MS8 1
      #ifndef _CRT_SECURE_NO_DEPRECATE
        #define _CRT_SECURE_NO_DEPRECATE
      #endif
      /* disable deprecation warnings (4996) */
      #pragma warning(disable:4996)
   #endif
   #if _MSC_VER == 1500
      #define LT_COMPILER_MS9 1
      #ifndef _CRT_SECURE_NO_DEPRECATE
        #define _CRT_SECURE_NO_DEPRECATE
      #endif
      /* disable deprecation warnings (4996) */
      #pragma warning(disable:4996)
   #endif
#endif

#if defined(_WIN64) || defined(WIN64)

   #define LT_OS_WIN 1
   #define LT_OS_WIN64 1

   #if defined(_M_IX86)
      #define LT_ARCH_IA64 1        /* Itanium */
   #elif defined(_M_AMD64)
      #define LT_ARCH_AMD64 1       /* x86 w/ 64-extensions ("x86-64") */
   #else
      #error PLATFORM ERROR: WIN64, but unknown architecture
   #endif

   #if !defined(LT_COMPILER_INTEL) && !defined(LT_COMPILER_MS)
      #error PLATFORM ERROR: WIN64, but unknown compiler
   #endif

#elif defined(_WIN32_WCE)

   #define LT_OS_WIN 1
   #define LT_OS_WINCE 1
  
   #if defined(ARM) || defined(_ARM_)  
      #define LT_ARCH_ARM 1
   #elif defined(MIPS) || defined(_MIPS_)
      #define LT_ARCH_MIPS 1
   #elif defined(SH3) || defined(_SH3_)
      #define LT_ARCH_SH3 1
   #elif defined(SH4) || defined(_SH4_)
      #define LT_ARCH_SH4 1
   #elif defined(_X86_) || defined(x86)
      #define LT_ARCH_IA32 1
   #else
      #error PLATFORM ERROR: WINCE, but unknown architecture
   #endif

   #if !defined(LT_COMPILER_MS)
      #error PLATFORM ERROR: WINCE, but unknown compiler
   #endif

#elif defined(_WIN32) || defined(WIN32)

   #define LT_OS_WIN 1
   #define LT_OS_WIN32 1

   #if defined(_M_IX86)
      #define LT_ARCH_IA32 1
   #else
      #error PLATFORM ERROR: WIN32, but unknown architecture
   #endif

   #if !defined(LT_COMPILER_INTEL) && !defined(LT_COMPILER_MS)
      #error PLATFORM ERROR: WIN32, but unknown compiler
   #endif

#elif defined(__APPLE__) && defined(__MACH__)

   #define LT_OS_UNIX 1
   #define LT_OS_DARWIN 1

   #if defined(__ppc__)
      #define LT_ARCH_PPC 1
   #elif defined(__ppc64__)
      #define LT_ARCH_PPC64 1
   #elif defined(__i386__)
      #define LT_ARCH_IA32 1
   #elif defined(__x86_64__)
      #define LT_ARCH_AMD64 1
   #else
      #error PLATFORM ERROR: DARWIN, but unknown architecture
   #endif

   #if !defined(LT_COMPILER_GNU)
      #error PLATFORM ERROR: DARWIN, but unknown compiler
   #endif

#elif defined(__hpux) 

   #define LT_OS_UNIX 1
   #define LT_OS_HPUX 1
   #if defined(__hppa__)
      #define LT_ARCH_PARISC 1
   #else
      #error PLATFORM ERROR: HPUX, but unknown architecture
   #endif

#elif defined (__sun)

   #define LT_OS_UNIX 1
   #define LT_OS_SUNOS 1

   #if defined(__sparcv9)
      #define LT_ARCH_SPARC64 1
   #elif defined(__sparc)
      #define LT_ARCH_SPARC 1
   #elif defined(i386)
      #define LT_ARCH_IA32 1
   #else
      #error PLATFORM ERROR: SUNOS, but unknown architecture
   #endif

   #if defined (__SUNPRO_CC)
      #define LT_COMPILER_SUN 1
      #define LT_COMPILER_SUNPRO_CC 1
   #elif defined(__SUNPRO_C)
      #define LT_COMPILER_SUN 1
      #define LT_COMPILER_SUNPRO_C 1
   #elif !defined(LT_COMPILER_GNU)
      #error PLATFORM ERROR: SUNOS, but unknown compiler
   #endif

#elif defined (linux) || defined (__linux__) || defined (__linux)

   #define LT_OS_UNIX 1
   #define LT_OS_LINUX 1

   #if defined(i386)
      #define LT_ARCH_IA32 1
   #elif defined(__amd64) || defined(__amd64__)
      #define LT_ARCH_AMD64 1       /* x86 w/ 64-extensions ("x86-64") */
   #elif defined(__x86_64) || defined(__x86_64__) 
      #define LT_ARCH_AMD64 1
   #else
      #error PLATFORM ERROR: LINUX, but unknown architecture
   #endif

   #if !defined(LT_COMPILER_GNU)
      #error PLATFORM ERROR: LINUX, but unknown compiler
   #endif

#else
   #error PLATFORM ERROR: unsupported target platform
#endif

#if !defined(LT_OS_WIN) && !defined(LT_OS_UNIX)
   #error PLATFORM ERROR: set LT_OS_WIN or LT_OS_UNIX
#endif

/*
 * Endian-ness
 */

#if defined(LT_ARCH_IA32) || defined(LT_ARCH_IA64) || defined(LT_ARCH_ARM) || defined(LT_ARCH_AMD64)
   #define LT_LITTLE_ENDIAN
#elif defined(LT_ARCH_PPC) || defined(LT_ARCH_PPC64) || defined(LT_ARCH_SPARC) || defined(LT_ARCH_SPARC64) || (defined LT_ARCH_PARISC)
   /* We do not define LT_BIG_ENDIAN, as that would just confuse things;
      either you're little endian, or you're not. */
#else
   #error PLATFORM ERROR: unknown architecture
#endif

/*
 * backwards compatability, deprecated stuff
 */

#if defined(LT_LITTLE_ENDIAN) && !defined(_LITTLE_ENDIAN)
   /* this is deprecated! */
   #define _LITTLE_ENDIAN
#endif

#ifdef LT_OS_UNIX
   #define LT_UNIX 1
#endif

#endif /* LT_PLATFORM_H */

