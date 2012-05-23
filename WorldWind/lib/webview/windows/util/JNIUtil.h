/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

/*
 * Version $Id$
 */

#ifndef JNI_UTIL_H
#define JNI_UTIL_H

#include <jni.h>

extern const char *ILLEGAL_ARGUMENT_EXCEPTION;
extern const char *WW_RUNTIME_EXCEPTION;

/**
 * Throw a Java exception.
 *
 * @param name Name of exception class.
 * @param msg  Exception message.
 */
void JNU_ThrowByName(JNIEnv *env, const char *name, const wchar_t *msg);

/**
 * Throw a Java exception.
 *
 * @param name      Name of exception class.
 * @param msg       Exception message.
 * @param errorCode Numeric error code.
 */
void JNU_ThrowByName(JNIEnv *env, const char *name, const wchar_t *msg, HRESULT errorCode);

#endif