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

#ifndef LTI_REFERENCE_COUNTED_OBJECT_H
#define LTI_REFERENCE_COUNTED_OBJECT_H

// lt_lib_mrsid_core
#include "lti_types.h"
#include <stddef.h> // NULL

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * LTIReferenceCountedObject is a base class for implementing reference 
 * counting.
 *
 * Call retain() when keeping a pointer to the object and call 
 * release() when the object is no longer needed.
 *
 * Derived classes will need to supply a static create() function to
 * allocates a new object.  The newly created object will have a reference
 * count of 1.  Constructors and destructors should be protected so users must
 * go through the create/retain/release functions.
 */
class LTIReferenceCountedObject
{
   /**
    * Macros for defining boilerplate parts of derived LTIReferenceCountedObject
    * classes.
    *
    * LTI_REFERENCE_COUNTED_BOILERPLATE_BASE: shoudl be used when deriving class
    * that will not be instantiated directly.  (TYPE::create() is not declared.)
    *
    * LTI_REFERENCE_COUNTED_BOILERPLATE: should be used when deriving classes
    * that are concrete.
    */
#define LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(classname) \
   LT_DISALLOW_COPY_CONSTRUCTOR(classname); \
   protected: \
      classname(void); \
      virtual ~classname(void)

#define LTI_REFERENCE_COUNTED_BOILERPLATE(classname) \
   LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(classname); \
   public: \
      static classname *create(void) 

   LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(LTIReferenceCountedObject);
public:

   /** increment reference counter */
   virtual void retain(void) const;

   /** decrement the reference counter and delete the object when the counter is zero */
   virtual void release(void) const;

private:
   mutable lt_int32 m_referenceCount;
};

/**
 * Helper functions that test if the object is NULL before calling
 * retian() and release().
 */

/** Call retain() on non-NULL objects */
template<typename TYPE> inline TYPE &
LTI_RETAIN(TYPE &object)
{
   if(object != NULL)
      object->retain();
   return object;
}

/** Call release() on non-NULL objects and NULL out the pointer */
template<typename TYPE> inline void
LTI_RELEASE(TYPE *&object)
{
   if(object != NULL)
      object->release();
   object = NULL;
}


/**
 * RC<> is a wrapper class around LTIReferenceCountedObject that gives the
 * wrapped object block scoping.
 *
 * RC<TYPE> tries to look like a TYPE *.
 */
template<typename TYPE>
class RC
{
public:
   /** releases the object when RC<> goes out of scope */
   ~RC(void) { LTI_RELEASE(m_object); }

   /**
    * create an object on the heap
    *
    * Note: caller should test if it is NULL before using it.
    */
   RC(void) : m_object(TYPE::create()) {}
   
   /**
    * manage an existing object
    *
    * Note: use RC<TYPE> object(NULL) to get an empty wrapper
    */
   RC(TYPE *object) : m_object(LTI_RETAIN(object)) {}
   RC &operator=(TYPE *object)
   {
      // retain first because object may equal m_object
      LTI_RETAIN(object);
      LTI_RELEASE(m_object);
      m_object = object;
      return *this;
   }

   RC(const RC &object) : m_object(LTI_RETAIN(object.m_object)) {}
   RC &operator=(const RC &object)
   {
      // retain first because object may equal m_object
      LTI_RETAIN(object.m_object);
      LTI_RELEASE(m_object);
      m_object = object.m_object;
      return *this;
   }

   /** make the wrapper look like a pointer to TYPE */
   TYPE *operator->(void) { return m_object; }
   /** make the wrapper look like a pointer to TYPE */
   TYPE &operator*(void) { return *m_object; }
   /** make the wrapper look like a pointer to TYPE */
   operator TYPE *&(void) { return m_object; }

private:
   TYPE *m_object;
};

LT_END_NAMESPACE(LizardTech)

#endif // LTI_REFERENCE_COUNTED_OBJECT_H
