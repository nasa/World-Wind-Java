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
 
/**       
 * @file
 * 
 * Support for the IO classes.
 */
#ifndef LT_LIB_IO_H
#define LT_LIB_IO_H

#include "lt_base.h"

#ifdef LT_CPLUSPLUS
extern "C" {
#endif

/**
 *   Stream seek directions.
 */
typedef enum LTIOSeekDir
{
   /**   error */
   LTIO_SEEK_DIR_ERROR = 1,
   
   /** Offset from the beginning of the file */
   LTIO_SEEK_DIR_BEG = 2,

   /** Offset from the current read/write position */
   LTIO_SEEK_DIR_CUR = 3,

   /** Offset from the end of the file (offsets need to be negative to be valid) */
   LTIO_SEEK_DIR_END = 4,

   /** Marker */
   LTIO_SEEK_DIR_MAX = 5
} LTIOSeekDir;

//   status codes
#include "lt_ioStatus.h"

//   macros
#define LTIO_HR_FAILED(hr)  ((long)(hr)<0)

#ifdef LT_CPLUSPLUS
}
#endif

#endif
