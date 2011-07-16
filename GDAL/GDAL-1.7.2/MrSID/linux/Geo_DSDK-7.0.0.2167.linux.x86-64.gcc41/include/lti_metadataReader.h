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

#ifndef LTIMETADATAREADER_H
#define LTIMETADATAREADER_H

// lt_lib_base
#include "lt_base.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


class LTIMetadataDatabase;

/**
 * abstract class for populating a metadata database
 *
 * This abstract class provides an interface for importing metadata records
 * from a foreign source into an LTIMetadataDatabase.
 *
 * This is used, for example, to provide a mechanism for reading the TIFF
 * tag style metadata from a MrSID file into the format-neutral, in-memory
 * database.
 */
class LTIMetadataReader
{
public:
   /**
    * default constructor
    *
    * This base constructor creates a reader object which can insert
    * records into a database from some foreign source.
    *
    * @param database the database to be read into
    */
   LTIMetadataReader(LTIMetadataDatabase& database);

   /**
    * destructor
    */
   virtual ~LTIMetadataReader();

   /**
    * read records into database
    *
    * This function must be implemented in the derived class.  It should
    * read the metadata content from the foreign metadata source, express
    * it as LTIMetadataRecord objects, and add them to the database.
    *
    * @return status code indicating success or failure
    */
   virtual LT_STATUS read() = 0;

protected:
   /**
    * the database to be read into
    *
    * This is the database to be read into.  Derived classes may access it
    * directly.
    */
   LTIMetadataDatabase& m_database;

private:
   // nope
   LTIMetadataReader(const LTIMetadataReader&);
   LTIMetadataReader& operator=(const LTIMetadataReader&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTIMETADATAREADER_H
