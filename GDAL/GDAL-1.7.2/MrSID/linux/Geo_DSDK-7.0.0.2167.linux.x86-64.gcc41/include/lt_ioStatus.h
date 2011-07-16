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

#ifndef LT_IOSTATUS_H
#define LT_IOSTATUS_H

#include "lt_base.h"

/** Base value for stream error codes  */
#define LT_STS_IOBas                            145000
LT_STATUSSTRING_ADD(LT_STS_IOBas, "lt_lib_mrsid_core base")

/**  Indicates that a stream has not been initialized with a data source */
#define LT_STS_IOStreamUninitialized            145003
LT_STATUSSTRING_ADD(LT_STS_IOStreamUninitialized, "stream not initialized")

/**  Indicates that a requested operation is not supported by a stream */
#define LT_STS_IOStreamUnsupported              145010
LT_STATUSSTRING_ADD(LT_STS_IOStreamUnsupported, "operation unsupported")

/**  Indicates that the supplied arguments are invalid or unintelligible */
#define LT_STS_IOInvalidArgs                     145011
LT_STATUSSTRING_ADD(LT_STS_IOInvalidArgs, "invalid arguments")

/**  Indicates that a stream is in a state that disallows the requested action */
#define LT_STS_IOStreamInvalidState               145012
LT_STATUSSTRING_ADD(LT_STS_IOStreamInvalidState, "stream in invalid state")

/**  Out of memory */
#define LT_STS_IOOutOfMemory                     145013
LT_STATUSSTRING_ADD(LT_STS_IOOutOfMemory, "out of memory")

/** Unable to get file lock */
#define LT_STS_IOFileLock                       145014
LT_STATUSSTRING_ADD(LT_STS_IOFileLock, "unable to get file lock")

/** Could not unlock file */
#define LT_STS_IOFileUnlock                     145015
LT_STATUSSTRING_ADD(LT_STS_IOFileUnlock, "could not unlock file")

/** Upper limit for stream error codes  */
#define LT_STS_IOMax                             145999
LT_STATUSSTRING_ADD(LT_STS_IOMax, "lt_lib_io max")


#endif // LT_IOSTATUS_H
