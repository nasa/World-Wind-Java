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

#ifndef LT_UTILSTATUS_H
#define LT_UTILSTATUS_H

#include "lt_base.h"

// these should be in lt_define.h
LT_STATUSSTRING_ADD(LT_STS_Success, "Success")
LT_STATUSSTRING_ADD(LT_STS_Failure, "Failure")

// these are only used for testing
LT_STATUSSTRING_ADD(910, "status test uint32=%u")
LT_STATUSSTRING_ADD(911, "status test int32=%d")
LT_STATUSSTRING_ADD(912, "status test double=%f")
LT_STATUSSTRING_ADD(913, "status test string=%s")
LT_STATUSSTRING_ADD(914, "status test uint=%u uint=%u double=%f string=%s string=%s string=%s foo")
LT_STATUSSTRING_ADD(915, "status test filespec=%F")

/** Base value for util library error codes  */
#define LTUTIL_STATUS_BASE                         3000
LT_STATUSSTRING_ADD(LTUTIL_STATUS_BASE, "lt_lib_utils BASE")

/** Max value for util library error codes  */
#define LTUTIL_STATUS_MAX                          3999
LT_STATUSSTRING_ADD(LTUTIL_STATUS_MAX, "lt_lib_utils MAX")

#define LTUTIL_STS_NULL_POINTER                    3001
LT_STATUSSTRING_ADD(LTUTIL_STS_NULL_POINTER, "NULL pointer dereferenced")
#define LTUTIL_STS_NOT_FOUND                       3002
LT_STATUSSTRING_ADD(LTUTIL_STS_NOT_FOUND, "The specified resource was not found")

// caller failed to call initialize() (or called it twice)
#define LTUTIL_STS_CRITICAL_SECTION_INIT           3010
LT_STATUSSTRING_ADD(LTUTIL_STS_CRITICAL_SECTION_INIT, "critical section initialization failure")

// problem closing mutex
#define LTUTIL_STS_MUTEX_CLOSE_ERROR               3020
LT_STATUSSTRING_ADD(LTUTIL_STS_MUTEX_CLOSE_ERROR, "mutex close error")
#define LTUTIL_STS_MUTEX_LOCK_ERROR                3021
LT_STATUSSTRING_ADD(LTUTIL_STS_MUTEX_LOCK_ERROR, "mutex lock error")
#define LTUTIL_STS_MUTEX_UNLOCK_ERROR              3022
LT_STATUSSTRING_ADD(LTUTIL_STS_MUTEX_UNLOCK_ERROR, "mutex unlock error")

// LTFileUtils
#define LTUTIL_STS_DELETE_ERROR                    3030
LT_STATUSSTRING_ADD(LTUTIL_STS_DELETE_ERROR, "delete error")
#define LTUTIL_STS_MOVE_ERROR                      3031
LT_STATUSSTRING_ADD(LTUTIL_STS_MOVE_ERROR, "move error")
#define LTUTIL_STS_CREATEDIR_ERROR                 3032
LT_STATUSSTRING_ADD(LTUTIL_STS_CREATEDIR_ERROR, "createdir error")
#define LTUTIL_STS_INVALIDFILESPEC_ERROR           3033
LT_STATUSSTRING_ADD(LTUTIL_STS_INVALIDFILESPEC_ERROR, "invalid filespec error")
#define LTUTIL_STS_ERR_TEMPNAM_FAIL                3034
LT_STATUSSTRING_ADD(LTUTIL_STS_ERR_TEMPNAM_FAIL, "tempnam error")
#define LTUTIL_STS_FILE_NOT_FOUND                  3035
LT_STATUSSTRING_ADD(LTUTIL_STS_FILE_NOT_FOUND, "The file was not found")
#define LTUTIL_STS_DIR_NOT_FOUND                   3036
LT_STATUSSTRING_ADD(LTUTIL_STS_DIR_NOT_FOUND, "The directory was not found")
#define LTUTIL_STS_ERR_MKTEMP_FAIL                 3037
LT_STATUSSTRING_ADD(LTUTIL_STS_ERR_MKTEMP_FAIL, "mktemp error")

// lt_winUtils.cpp
#define LT_STS_UTIL_ExtractRegistryText                     3040
LT_STATUSSTRING_ADD(LT_STS_UTIL_ExtractRegistryText, "error extracting registry text - %s")


#define LTUTIL_STS_InvalidProtocol                          3050
LT_STATUSSTRING_ADD(LTUTIL_STS_InvalidProtocol, "Invalid protocol")

// lt_utilShell.cpp
#define LT_STS_UTIL_ShellRedirectCannotOpenFile             3060
LT_STATUSSTRING_ADD(LT_STS_UTIL_ShellRedirectCannotOpenFile, "ShellRedirectCannotOpenFile")
#define LT_STS_UTIL_ShellRedirectDUPError1                  3061
LT_STATUSSTRING_ADD(LT_STS_UTIL_ShellRedirectDUPError1, "ShellRedirectDUPError1")
#define LT_STS_UTIL_ShellRedirectDUPError2                  3062
LT_STATUSSTRING_ADD(LT_STS_UTIL_ShellRedirectDUPError2, "ShellRedirectDUPError2")
#define LT_STS_UTIL_ShellRedirectDUPError3                  3063
LT_STATUSSTRING_ADD(LT_STS_UTIL_ShellRedirectDUPError3, "ShellRedirectDUPError3")
#define LT_STS_UTIL_ShellRedirectCannotCloseFile            3064
LT_STATUSSTRING_ADD(LT_STS_UTIL_ShellRedirectCannotCloseFile, "ShellRedirectCannotCloseFile")

// lt_utilStatusData.cpp
/** indicates StatusData initialization error */
#define LT_STS_UTIL_StatusDataInitialized                   3070
LT_STATUSSTRING_ADD(LT_STS_UTIL_StatusDataInitialized, "StatusData initialization error")
/** indicates StatusData data type error */
#define LT_STS_UTIL_StatusDataTypeError                     3071
LT_STATUSSTRING_ADD(LT_STS_UTIL_StatusDataTypeError, "StatusData data type error")

// lt_packageInfo.cpp
#define LT_STS_UTIL_PackageHomeNotFound                     3080
LT_STATUSSTRING_ADD(LT_STS_UTIL_PackageHomeNotFound, "package home not found")
#define LT_STS_UTIL_PackageDirNotFound                      3081
LT_STATUSSTRING_ADD(LT_STS_UTIL_PackageDirNotFound, "package directory not found")
#define LT_STS_UTIL_PackageFileNotFound                     3082
LT_STATUSSTRING_ADD(LT_STS_UTIL_PackageFileNotFound, "package file not found")
#define LT_STS_UTIL_PackageHomeRegKeyNotFound               3083
LT_STATUSSTRING_ADD(LT_STS_UTIL_PackageHomeRegKeyNotFound, "package home registry key not found")
#define LT_STS_UTIL_PackageHomeEnvVarUndefined              3084
LT_STATUSSTRING_ADD(LT_STS_UTIL_PackageHomeEnvVarUndefined, "package home environment variable not defined")

// lt_utilTimer.cpp
#define LT_STS_UTIL_TimeUnknown                             3090
LT_STATUSSTRING_ADD(LT_STS_UTIL_TimeUnknown, "time could not be determined")

// lt_utilLocale.h
#define LT_STS_UTIL_LocaleNotSet                            3100
LT_STATUSSTRING_ADD(LT_STS_UTIL_LocaleNotSet, "locale could not be set")

//   lt_utilThread.h
#define LT_STS_UTIL_ThreadAlreadyRunning                    3110
LT_STATUSSTRING_ADD(LT_STS_UTIL_ThreadAlreadyRunning,      "thread is already running")

#endif // LT_UTILSTATUS_H
