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

#ifndef LT_UTILSTATUSSTRINGS_H
#define LT_UTILSTATUSSTRINGS_H

// lt_lib_base
#include "lt_base.h"

/**
 * @file
 *
 * Status code / error string support.
 *
 * These functions allow the user to access strings which correspond to
 * status codes returned by LizardTech functions.
 */

#ifdef __cplusplus
extern "C" {
#endif

/**
 * return a "raw" status string
 *
 * This function returns the string for the given status code.  Any format
 * specifiers in the string will not be interpolated using the error stack
 * (see LTUtilStatusData).
 *
 * @param   code  the status code
 * @return  the string for the status code
 */
extern const char* getRawStatusString(LT_STATUS code);


/**
 * return a formatted status string
 *
 * This function returns the string for the given status code.  Any format
 * specifiers in the string will be expanded (interpolated) using the data
 * on the error stack (see LTUtilStatusData).
 *
 * @param   code  the status code
 * @return  the string for the status code
 */
extern const char* getLastStatusString(LT_STATUS code);


/**
  * initialize the status strings reporting system
  *
  * Applications should call this once prior to any other LizardTech
  * functions, to enable the error reporting system.  If not called, then
  * any calls to pushData() will be no-ops, and the integral status code
  * will map to an unintepreted string.
  *
  * @return success or failure status code
  */
extern LT_STATUS initializeStatusStrings();


/**
  * close out the status strings reporting system
  *
  * Applications should call this once after all other LizardTech functions
  * have been called, to clean up memory.
  *
  * @return success or failure success code
  */
extern LT_STATUS terminateStatusStrings();


#ifdef __cplusplus
}
#endif

#endif // LT_UTILSTATUSSTRINGS_H
