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

#ifndef LTI_DELEGATES_H
#define LTI_DELEGATES_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)


/**
 * interrupt delegate (callback) base class
 *
 * This abstract class is used for implementing a mechanism to determine if
 * a potentially long-running operation is to be interrupted, such as
 * LTIImageStage::read() or LTIImageWriter::write().  During these sorts of
 * operations, an object which has an interrupt delegate may periodically
 * call the delegate's getInterruptStatus() method to determine if the
 * operation should be aborted.  If this function returns a value other than
 * LT_STS_Success, then the object will abort the operation  and return that
 * status value.
 *
 * Interrupt delegates are typically used in environments such as GUI encoders.
 *
 * A "delegate" is simply an object-oriented version of a callback function.
 */
class LTIInterruptDelegate
{
public:
   /**
    * check for interrupt
    *
    * This function should be implemented to indicate whether some user-defined
    * event indicates that the operation should be terminated.  If an interrupt
    * is requested, a value other than LT_STS_Success should be returned.
    *
    * @return LT_STS_Success if no interrupt requested; any other (nonzero)
    *         value if an interrupt is requested
    */
   virtual LT_STATUS getInterruptStatus() = 0;
};


/**
 * progress delegate (callback) base class
 *
 * This abstract class is used for implementing a mechanism to report the
 * progress (percent complete) of a potentially long-running operation.
 * During these sorts of operations, an object which has a progress delegate
 * may periodically call the delegate's setProgressStatus() method to report
 * the percent of the operation completed.
 *
 * Progress delegates are typically used in GUI environments as a means of
 * displaying percent-complete or time-remaining.
 *
 * A "delegate" is simply an object-oriented version of a callback function.
 */
class LTIProgressDelegate
{
public:
   /**
    * set percent completed
    *
    * This function should be implemented to report to the client application
    * the progress of a long-running operation.
    *
    * @param percentComplete the percent complete; this must be a value in the
    *                        range 0.0 to 1.0 inclusive
    * @return LT_STS_Success if function succeeded, nonzero if some error occurred
    */
   virtual LT_STATUS setProgressStatus(float percentComplete) = 0;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_DELEGATES_H
