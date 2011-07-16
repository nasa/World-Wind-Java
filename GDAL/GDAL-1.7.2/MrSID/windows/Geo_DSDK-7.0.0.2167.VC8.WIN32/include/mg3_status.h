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

#ifndef MG3_STATUS_H
#define MG3_STATUS_H

// lt_lib_base
#include "lt_base.h"

//
// On the Usage of Status Codes
// ----------------------------
//
// Status codes are used to report failure modes of an "unnatural" or
// "unexpected" nature.  They are to be used when something happens that is
// not part of the normal course of events.  They often are used in places
// where other, more mature programmers would throw exceptions.
//
// Applications that don't otherwise handle failure conditions should feel
// free to return these status values as exit codes back out to the shell.
// The kernel sample apps follow this convention.
//
// These status values are to be generally applicable across the whole
// kernel.  They are not to be specific to a small set of cases ("the third
// parameter to this function was greater than 10"), nor are they to be
// overly generic ("an error occurred").
//
// The intent of status codes is to both signal to the user that something
// bad happened and to provide a mechanism for aborting execution gracefully.
// It is *not* the intent that these status codes would allow a caller to
// programmatically diagnose the error and retry the operation -- that would
// require a much richer semantics.  To paraphrase Nathan, the kernel is
// designed for non-dummies.
//
// Status codes are *not* to be used for indicating negative success, such as
// in a function that attempts to find within a file a packet of some
// specified type and return a pointer to that packet.  That function should
// return the "ok" status and use an "out" parameter to return the pointer,
// either set properly (packet was found) or as NULL (packet was not found).
// Finding packets in a file involves underlying I/O operations, and
// therefore is potentially a status-code-inducing operation.  (Contrast this
// with a function which is just looking for some element in a list -- a
// standard list search probably won't entail any operations that might fail
// in bad ways, so returning the pointer directly is okay.)
//
// By convention, status codes are only to be used as function return values,
// never as parameters.
//
// If a function returns a status code, it's value MUST be checked for
// success and handled appropriately.  If you are unsure how to handle the
// returned result, the least you should do is pass the status code back up
// the stack.
//
// Someday, we might unify these status codes with those used by Sparkle, the
// Core, the Stream library, etc.
//
// If you feel the urge to add a New status code, stop and consider your
// situation carefully.  Can you use one of the ones already there?  Could
// your New one replace an existing one?  Will your New one be useful in some
// case other than the current one you have in mind?  Have you talked it over
// with the other kernel folks yet?
//
// There is no "unknown" status code represented.  Such a value would defeat
// the whole purpose.  Lame-o.
//
// Recall that the kernel does not use explicit exception handling anywhere.
// Ever.  At all.  Period.  We use status codes instead.
//
// Only the "OK" status code is given a numeric value (zero).  Programmers
// should not rely on the numeric values or orderings of the enum values.
//
// The "OK()" function should always be used to test success.  The idiom of
// choice is:
//   stat = foo();
//   if (!OK(stat)) return foo;
//
// The "invalid" status is to be used only for initializing variables of type
// MG3Status.  No well-behaved function should ever return the "invalid"
// status.
//
// Note well that the meaning of each status code is documented.  We'd like
// to keep it that way.
//


// note "name()" function is declared in MG3Types class

#define MG3_STATUS_BASE  2000
LT_STATUSSTRING_ADD(MG3_STATUS_BASE, "mg3 BASE")

#define MG3_STATUS_MAX   2999
LT_STATUSSTRING_ADD(MG3_STATUS_MAX, "mg3 MAX")

   // A "read" I/O operation failed: you hit EOF unexpectedly because the
   // file was corrupt, someone unplugged your network cable on you, etc.
#define MG3_STATUS_READ_ERROR                      2001
LT_STATUSSTRING_ADD(MG3_STATUS_READ_ERROR, "mg3 read error")

   // A "write" I/O operation failed: someone unplugged your network cable on
   // you, you don't have write permission, the stream just ain't writable,
   // etc.
#define MG3_STATUS_WRITE_ERROR                     2002
LT_STATUSSTRING_ADD(MG3_STATUS_WRITE_ERROR, "mg3 write error")

   // An "open" I/O operation failed: someone unplugged your network cable
   // (again...), you don't have permission to create the file, you don't
   // have write access to the file, etc.
#define MG3_STATUS_OPEN_ERROR                      2003
LT_STATUSSTRING_ADD(MG3_STATUS_OPEN_ERROR, "mg3 open error")

   // An I/O operation failed, for some reason other than read, write, or
   // open: for example, seek() or close().
#define MG3_STATUS_IO_ERROR                        2004
LT_STATUSSTRING_ADD(MG3_STATUS_IO_ERROR, "mg3 IO error")

   // The format the file being read was somehow broken: a field containing
   // an enum had an illegal value, etc.  This is largely for use by those
   // functions which need to decode specific file formats, e.g. MG3.
#define MG3_STATUS_FORMAT_ERROR                    2005
LT_STATUSSTRING_ADD(MG3_STATUS_FORMAT_ERROR, "mg3 format error")

   // A versioning problem has occurred: you're trying to read a version that
   // you don't have support for.  [I don't like this one much -- probably
   // should be diagnosed more explicitly and handled more formally. BUG.]
#define MG3_STATUS_BAD_VERSION                     2006
LT_STATUSSTRING_ADD(MG3_STATUS_BAD_VERSION, "mg3 bad version")

   // The user has requested an interrupt, e.g. via a signal from our friends
   // as LTProcessCallback::hasBeenTerminated().
#define MG3_STATUS_INTERRUPT                       2007
LT_STATUSSTRING_ADD(MG3_STATUS_INTERRUPT, "mg3 interrupt")

#define MG3_STATUS_RESERVED_2008                   2008

   // One of the arguments to the function was incorrect: a value was out of
   // range, a pointer was NULL, etc.  (Compare this to the "bad context"
   // error, which is more of an implicit problem.)
#define MG3_STATUS_BAD_ARGUMENT                    2009
LT_STATUSSTRING_ADD(MG3_STATUS_BAD_ARGUMENT, "mg3 bad argument")

   // The context in which the function was called is incorrect: for example,
   // trying to add a certain unique packet type to the database when there
   // is already one of that type in there -- the caller of function is not
   // supposed to do that.  Similarly, calling initialize() twice is not
   // allowed, nor is calling execute() with calling initialize() first.
   // (Compare this to the "bad argument" error, which is more of an explicit
   // problem.)
#define MG3_STATUS_BAD_CONTEXT                     2010
LT_STATUSSTRING_ADD(MG3_STATUS_BAD_CONTEXT, "mg3 bad context")

   // We tried to do something that required a password or some such, and
   // the operation didn't succeed.  This should only happen around calls
   // into secuirty packets and the encryption library and those sorts of
   // places.
#define MG3_STATUS_SECURITY_ERROR                  2011
LT_STATUSSTRING_ADD(MG3_STATUS_SECURITY_ERROR, "mg3 security error")

   // Unlikely, but could happen.  Typically would be used by wrapping a call
   // to a potentially large malloc() call in a try region and using this
   // value if the catch region finds an out of memory exception.
#define MG3_STATUS_OUT_OF_MEMORY                   2012
LT_STATUSSTRING_ADD(MG3_STATUS_OUT_OF_MEMORY, "mg3 out of memory")

   // A C++ exception occurred that we did not expect.  In some cases, the
   // kernel will wrap a call to a foreign library in a try region so as to
   // manually catch any errors it may throw.  This status value is used when
   // the resulting exception is not something we can readily deal with.
#define MG3_STATUS_UNHANDLED_EXCEPTION             2013
LT_STATUSSTRING_ADD(MG3_STATUS_UNHANDLED_EXCEPTION, "mg3 unhandled exception")

   // A theoretically unreachable piece of code was reached.  This should
   // be used for things like the default case of a switch statement in which
   // all possible legal values have already been handled explicitly.  This
   // value may be used in conjunction with LT_ASSERT(0).
#define MG3_STATUS_NOTREACHED                      2014
LT_STATUSSTRING_ADD(MG3_STATUS_NOTREACHED, "mg3 NOTREACHED")

   // Use only for initializing a variable.  Should never be returned as an
   // actual status value.
#define MG3_STATUS_INVALID                         2015
LT_STATUSSTRING_ADD(MG3_STATUS_INVALID, "mg3 status invalid")

   // No MSEs in the image: the image cannot be optimized or streamed.
#define MG3_STATUS_NO_MSES                         2016
LT_STATUSSTRING_ADD(MG3_STATUS_NO_MSES, "mg3 no MSEs")

   // The streaming client made a bad request
   // on the server side still send the reply
#define MG3_STATUS_BAD_CLIENT_REQUEST              2017
LT_STATUSSTRING_ADD(MG3_STATUS_BAD_CLIENT_REQUEST, "mg3 bad client request")

   // The streaming server had any error: call getServerError()
   // on the server side still send the reply
#define MG3_STATUS_SERVER_ERROR                    2018
LT_STATUSSTRING_ADD(MG3_STATUS_SERVER_ERROR, "mg3 server error")

   // The MG3 image could not be added the client plane cache
   // because the imageInfo does not match.
#define MG3_STATUS_IMAGE_NOT_COMPATIBLE            2019
LT_STATUSSTRING_ADD(MG3_STATUS_IMAGE_NOT_COMPATIBLE, "mg3 image not compatible")

   // Singed integer overflowed
#define MG3_STATUS_OVERFLOW                        2020
LT_STATUSSTRING_ADD(MG3_STATUS_OVERFLOW, "mg3 integer overflow")

#endif // MG3_STATUS_H
