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

#ifndef LTI_METADATAUTILS_H
#define LTI_METADATAUTILS_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_metadata
#include "lti_metadataTypes.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif



/**
 * useful metadata type and tag functions
 *
 * This class contains several static utility functions useful for
 * operating with tag names, tag numbers, and datatypes.
 */
class LTIMetadataUtils
{
public:
   /**
    * get numeric value of tag name
    *
    * This function returns the enum associated with a given tag name.
    * It will return LTI_METADATA_TAG_Unknown if the string is not known.
    *
    * @param tagName the tagName
    * @return the enum corresponding to \a tagName
    */
   static LTIMetadataTag lookupTag(const char* tagName);

   /**
    * get string value of numeric tag name
    *
    * This function returns the string associated with a given tag enum.
    * It will return NULL if the tag enum is not known.
    *
    * @param tag the tag number
    * @return the string corresponding to \a tag
    */
   static const char* lookupName(LTIMetadataTag tag);

   /**
    * get string representation of datatype
    *
    * This function returns a string representation of the given datatype,
    * useful for debugging.
    *
    * @param dataType the datatype to use
    * @return the string version of the given datatype
    */
   static const char* name(LTIMetadataDataType dataType);
};



LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTI_METADATAUTILS_H
