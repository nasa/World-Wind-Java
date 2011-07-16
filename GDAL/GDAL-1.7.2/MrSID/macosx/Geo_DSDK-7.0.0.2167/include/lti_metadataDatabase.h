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

#ifndef LTIMETADATADATABASE_H
#define LTIMETADATADATABASE_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_metadata
#include "lti_metadataTypes.h"


LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

class LTIMetadataRecord;


/**
 * class for storing metadata associated with an image
 *
 * This class implements a simple database which holds a set of
 * LTIMetadataRecords.  Each record has a unique tag associated with it.
 *
 * Each LTIImage object contains an LTIMetadataDatabase
 * for the metadata associated with that image.
 *
 * The database (and records) are stored in memory in a format-neutral
 * manner.  For importing or exporting metadata records to permanent
 * storage, see the LTIMetadataReader and LTIMetadataWriter classes.
 */
class LTIMetadataDatabase
{
public:
   /**
    * default constructor
    *
    * This constructor creates an empty metadata database.
    */
   LTIMetadataDatabase();

   /**
    * copy constructor
    */
   LTIMetadataDatabase(const LTIMetadataDatabase&);

   /**
    * destructor
    */
   virtual ~LTIMetadataDatabase();


   /**
    * @name Add functions
    */
   /*@{*/

   /**
    * add a record
    *
    * This function adds the given record to the metadata database.
    *
    * If the database already contains a record with the given tag,
    * a status code of LT_STS_LTIMetadata_DuplicateTag is returned.
    *
    * @param record the record to add 
    * @return status code indicating success or failure
    */
   LT_STATUS add(const LTIMetadataRecord& record);

   /**
    * add all the records from a database
    *
    * This function adds all the records from the given database to this
    * database.
    *
    * If the database already contains a record with the same tag as one
    * of the records in the database being added, no records are added and
    * a status code of LT_STS_LTIMetadata_DuplicateTag is returned.
    *
    * @param database the database containing the records to add 
    * @return status code indicating success or failure
    */
   LT_STATUS add(const LTIMetadataDatabase& database);

   /*@}*/


   /**
    * @name Retrieval functions
    */
   /*@{*/

   /**
    * retrieve a record, given a tag name
    *
    * This function returns a pointer to the (first) record in the database
    * that matches the given tag name.
    *
    * If the tag is not found, the pointer will be set to NULL and
    * a status of LT_STS_LTIMetadata_TagNotFound will be returned.
    *
    * @param   tagName  the name of the tag of the record to retrieve 
    * @param   record   pointer to the retrieved record 
    * @return  status code indicating success or failure
    */
   LT_STATUS get(const char* tagName,
                 const LTIMetadataRecord*& record) const;

   /**
    * retrieve a record, given a tag number
    *
    * This function returns a pointer to the (first) record in the database
    * that matches the given tag number.
    *
    * If the tag is not found, the pointer will be set to NULL and
    * a status of LT_STS_LTIMetadata_TagNotFound will be returned.
    *
    * @param   tag     the number of the tag of the record to retrieve 
    * @param   record  pointer to the retrieved record 
    * @return  status code indicating success or failure
    */
   LT_STATUS get(LTIMetadataTag tag,
                 const LTIMetadataRecord*& record) const;

   /**
    * lookup a record, given a tag name
    *
    * This function returns a boolean indicating whether the database
    * contains a record with the given tag.
    *
    * @param   tagName  the name of the tag of the record to look up 
    * @return  true if the tag is found, otherwise false
    */
   bool has(const char* tagName) const;

   /**
    * lookup a record, given a tag number
    *
    * This function returns a boolean indicating whether the database
    * contains a record with the given tag.
    *
    * @param   tag  the number of the tag of the record to look up 
    * @return  true if the tag is found, otherwise false
    */
   bool has(LTIMetadataTag tag) const;

   /**
    * retrieve a record, given an index number
    *
    * This function returns a pointer to the (first) record in the database
    * that matches the given index number.
    *
    * In combination with getIndexCount(), this function can be used
    * to iterate through all the records in the database.  (However, removing
    * a record will change the index numbers, so be careful if iterating
    * and removing in the same loop.)
    *
    * If the index number is not found, the pointer will be set to NULL and
    * a status of LT_STS_LTIMetadata_TagNotFound will be returned.
    *
    * @param   index  the index number of the record to retrieve 
    * @param   record  pointer to the retrieved record 
    * @return  status code indicating success or failure
    */
   LT_STATUS getDataByIndex(lt_uint32 index,
                            const LTIMetadataRecord*& record) const;

   /**
    * get number of records
    *
    * This function will return the number of records in the database.
    *
    * @return  the number of records
    */
   lt_uint32 getIndexCount() const;

   /*@}*/


   /**
    * @name Removal functions
    */
   /*@{*/

   /**
    * remove a record, given a tag name
    *
    * This function will remove the record with the given tag from the
    * database.
    *
    * If the tag is not found, a status of LT_STS_LTIMetadata_TagNotFound
    * will be returned.
    *
    * @param   tagName  the name of the tag of the record to remove 
    * @return  status code indicating success or failure
    */
   LT_STATUS remove(const char* tagName);

   /**
    * remove a record, given a tag number
    *
    * This function will remove the record with the given tag from the
    * database.
    *
    * If the tag is not found, a status of LT_STS_LTIMetadata_TagNotFound
    * will be returned.
    *
    * @param   tag  the number of the tag of the record to remove 
    * @return  status code indicating success or failure
    */
   LT_STATUS remove(LTIMetadataTag tag);

   /**
    * remove all records
    *
    * This function will remove all the records from the database.
    *
    * @return  status code indicating success or failure
    */
   LT_STATUS removeAll();

   /*@}*/

   /**
    * get size of metadata database
    *
    * This function will return a close-enough estimate of the size of the
    * metadata database, as if it were to be written directly to disk.
    *
    * @return  size in bytes of the metadata database
    */
   lt_int32 getApproximateSize() const;

   /**
    * Sorts the database records by tag name.  This can be useful in certain
    * debugging and validation scenarios.
    */
   void sort(void);

private:
   class RecordListX;
   RecordListX* m_recordList;

   // nope
   LTIMetadataDatabase& operator=(const LTIMetadataDatabase&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTIMETADATADATABASE_H
