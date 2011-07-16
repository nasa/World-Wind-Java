/* $Id$ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2005 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */
/* PUBLIC */

/**      
 *  @file  lt_system.h
 *
 *  @brief Declaration of standard system headers in a C-interface.
 * 
 *  $Date: 2008/03/18 19:52:58 $
 *  $Revision: 1.8 $
 */


#ifndef LT_SYSTEM_H
#define LT_SYSTEM_H

#include "lt_platform.h"

/*
 * justifications for inclusions are that we want:
 *
 *   NULL
 *   memcpy() ...
 *   malloc() ...
 *   strlen() ...
 *   isdigit() ...
 *   printf() ...
 *   posix
 */


/* common to all platforms */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <stddef.h>
#include <ctype.h>
#include <limits.h>
#include <float.h>


#if defined(LT_OS_LINUX)

   #include <unistd.h>
   #include <strings.h>
   #include <time.h>
   #include <wchar.h>

#elif defined(LT_OS_SUNOS)

   #include <unistd.h>
   #include <strings.h>
   #include <time.h>
   #include <wchar.h>

#elif defined(LT_OS_HPUX)

   #include <unistd.h>
   #include <strings.h>
   #include <time.h>
   #include <wchar.h>

#elif defined(LT_OS_DARWIN)

   #include <unistd.h>
   #include <strings.h>
   #include <time.h>
   
   #if defined(LT_OS_DARWIN6)
      #include <wcstring.h>
   #else
      #include <wchar.h>
   #endif

#elif defined(LT_OS_WINCE)

   /* (nothing special for now) */

#elif defined(LT_OS_WIN32) || defined(LT_OS_WIN64)

   #include <time.h>
   #include <wchar.h>

#else

   #error Port me!

#endif


#endif /* LT_SYSTEM_H */
