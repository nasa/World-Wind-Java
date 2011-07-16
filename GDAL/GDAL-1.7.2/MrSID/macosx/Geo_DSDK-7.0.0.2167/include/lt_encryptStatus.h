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


#ifndef ENCRYPTSTATUS_H
#define ENCRYPTSTATUS_H

// lt_lib_base
#include "lt_base.h"


#define LT_STS_EncryptBase                                     4000
LT_STATUSSTRING_ADD(LT_STS_EncryptBase, "BASE")

#define LT_STS_EncryptDISABLED                                 4001
LT_STATUSSTRING_ADD(LT_STS_EncryptDISABLED, "security disabled")

#define LT_STS_EncryptLOCK_ALREADY_LOCKED                      4002
LT_STATUSSTRING_ADD(LT_STS_EncryptLOCK_ALREADY_LOCKED, "can't lock an already-locked image")
#define LT_STS_EncryptUNLOCK_NOT_LOCKED                        4003
LT_STATUSSTRING_ADD(LT_STS_EncryptUNLOCK_NOT_LOCKED, "can't unlock an unlocked imaged")
#define LT_STS_EncryptLOCK_NEED_KEY                            4004
LT_STATUSSTRING_ADD(LT_STS_EncryptLOCK_NEED_KEY, "can't lock an image without a key")

#define LT_STS_EncryptUNLOCK_NEED_KEY                          4005
LT_STATUSSTRING_ADD(LT_STS_EncryptUNLOCK_NEED_KEY, "can't unlock a locked image without a key")
#define LT_STS_EncryptUNLOCK_WRONG_KEY                         4006
LT_STATUSSTRING_ADD(LT_STS_EncryptUNLOCK_WRONG_KEY, "key is valid but doesn't match image's lock")
#define LT_STS_EncryptINVALID_LICENSE                          4007
LT_STATUSSTRING_ADD(LT_STS_EncryptINVALID_LICENSE, "License is invalid")
#define LT_STS_EncryptBAD_PASSWORD                             4008
LT_STATUSSTRING_ADD(LT_STS_EncryptBAD_PASSWORD, "Password is invalid")
#define LT_STS_EncryptBAD_KEY                                  4009
LT_STATUSSTRING_ADD(LT_STS_EncryptBAD_KEY, "key is invalid (violates definition of key)")
#define LT_STS_EncryptLOCK_NO_KEY_PROVIDER                     4010
LT_STATUSSTRING_ADD(LT_STS_EncryptLOCK_NO_KEY_PROVIDER, "can't lock without a key provider")
#define LT_STS_EncryptUNLOCK_NO_KEY_PROVIDER                   4011
LT_STATUSSTRING_ADD(LT_STS_EncryptUNLOCK_NO_KEY_PROVIDER, "can't unlock without a key provider")
#define LT_STS_EncryptTYPE_KEY_PROVIDER_DEFINED                4012
LT_STATUSSTRING_ADD(LT_STS_EncryptTYPE_KEY_PROVIDER_DEFINED, "UNLOCK_WRONG_KEY with KeyProvider specific msg")
#define LT_STS_EncryptPromptToENCRYPT                          4013
LT_STATUSSTRING_ADD(LT_STS_EncryptPromptToENCRYPT, "Prompt to encrypt")
#define LT_STS_EncryptPromptToDECRYPT                          4014
LT_STATUSSTRING_ADD(LT_STS_EncryptPromptToDECRYPT, "Prompt to decrypt")
#define LT_STS_EncryptUnused4015                               4015

#define LT_STS_EncryptInternalError                            4100
LT_STATUSSTRING_ADD(LT_STS_EncryptInternalError, "Internal error")

#define LT_STS_EncryptMax                                      4999
LT_STATUSSTRING_ADD(LT_STS_EncryptMax, "MAX")


#endif // ENCRYPTSTATUS_H
