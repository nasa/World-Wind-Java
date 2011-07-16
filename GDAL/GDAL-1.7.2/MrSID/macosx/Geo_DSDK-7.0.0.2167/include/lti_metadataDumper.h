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

#ifndef LTIMETADATADUMPER_H
#define LTIMETADATADUMPER_H

// lt_lib_mrsid_metadata
#include "lti_metadataWriter.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIOStreamInf;
class LTIMetadataRecord;


/**
 * dumps contents of database in text form
 *
 * This class writes the given database in ASCII text, readable format
 * to a stream or stdout.
 */
class LTIMetadataDumper: public LTIMetadataWriter
{
public:
   /**
    * constructor
    *
    * This constructor creates an LTIMetadataWriter which will pretty-print
    * the records in the database to a stream or to stdout.
    *
    * @param database the database to write from
    * @param stream the stream to write to; if NULL, will write to stdout
    * @param abbreviated dump only the first several elements of each record
    */
   LTIMetadataDumper(const LTIMetadataDatabase& database,
                     LTIOStreamInf* stream,
                     bool abbreviated=false);

   /**
    * destructor
    */
   ~LTIMetadataDumper();

   /**
    * write records to stream
    */
   LT_STATUS write() const;

   /**
    * write a single record to stream
    *
    * This function writes (pretty-prints) the given record to the stream.
    *
    * It is public and static because it is useful on its own for debugging.
    *
    * @param record the record to write
    * @param stream the stream to write to
    * @param abbreviated dump only the first several elements of the record
    * @return status code indicating success or failure
    */
   static LT_STATUS writeRecord(const LTIMetadataRecord& record,
                                LTIOStreamInf& stream,
                                bool abbreviated=false);


   /**
    * INPUT_NAME metadata control
    *
    * Control if the IMAGE::INPUT_NAME tag is written out.
    * This can useful for certain debugging and validation situations.
    *
    * The default is to always write the INPUT_NAME tag.
    *
    * @param  enable  set to true to not skip the INPUT_NAME tag
    */
   void setWriteInputFilename(bool enable); 

private:
   LTIOStreamInf* m_stream;
   bool m_ownsStream;
   bool m_writeInputFilename;
   const bool m_abbreviated;

   // nope
   LTIMetadataDumper(const LTIMetadataDumper&);
   LTIMetadataDumper& operator=(const LTIMetadataDumper&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTIMETADATADUMPER_H
