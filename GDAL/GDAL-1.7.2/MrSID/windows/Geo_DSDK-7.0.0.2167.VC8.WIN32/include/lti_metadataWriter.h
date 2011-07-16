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

#ifndef LTI_METADATA_WRITER_H
#define LTI_METADATA_WRITER_H

// lt_lib_base
#include "lt_base.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


class LTIMetadataDatabase;


/**
 * abstract class for exporting a metadata database
 *
 * This abstract class provides an interface for exporting metadata records
 * from an LTIMetadataDatabase to a foreign source.
 *
 * This is used, for example, to provide a mechanism for writing the
 * format-neutral, in-memory database into binary TIFF tag format.  it
 * is also used to dump the database to plain-text format (for debugging).
 */
class LTIMetadataWriter
{
public:
   /**
    * default constructor
    *
    * This base constructor creates a writer object which can export
    * records from a database to some foreign format.
    *
    * @param database the database to be read into
    */
   LTIMetadataWriter(const LTIMetadataDatabase& database);

   /**
    * destructor
    */
   virtual ~LTIMetadataWriter();

   /**
    * write records out from database
    *
    * This function must be implemented in the derived class.  It should
    * write each of the LTIMetadataRecord objects in the database out to
    * the foreign format.
    *
    * @return status code indicating success or failure
    */
   virtual LT_STATUS write() const = 0;

protected:
   /**
    * the database to be written from
    *
    * This is the database to be written out from.  Derived classes may access
    * it directly.
    */
   const LTIMetadataDatabase& m_database;

private:
   // nope
   LTIMetadataWriter(const LTIMetadataWriter&);
   LTIMetadataWriter& operator=(const LTIMetadataWriter&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTI_METADATA_WRITER_H
