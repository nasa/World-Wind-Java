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

#ifndef LTIMETADATARECORD_H
#define LTIMETADATARECORD_H

// lt_lib_base
#include "lt_base.h"

// lt_lib_mrsid_metadata
#include "lti_metadataTypes.h"

LT_BEGIN_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif


/**
 * representation of a metadata record
 *
 * This class stores the data associated with a single record in the database.
 * The data consists of:
 *   - the tag (a string), used to uniquely identify the record
 *   - the type of the data, e.g. string, byte, etc
 *   - the dimensionality of the data, e.g. scalar, 2x3 array, etc
 *   - the actual data
 *
 * It is assumed each record has only a single set of data of a single datatype.
 * (That is, a record can't contain both a float and two ints.)
 *
 * Some of the functions for operating on records are cast in the guise
 * of scalar, vector, or array data.  This is for notational convenience only;
 * the internal representation does not distinguish and treats everything as
 * a (possibly degenerate) array.
 */
class LTIMetadataRecord
{
public:
   /**
    * default constructor for scalar data
    *
    * The constructor creates a record containing a scalar data value.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tag the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    */
   LTIMetadataRecord(LTIMetadataTag tag,
                     LTIMetadataDataType type,
                     const void* data);

   /**
    * default constructor for scalar data
    *
    * The constructor creates a record containing a scalar data value.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tagName the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    */
   LTIMetadataRecord(const char* tagName,
                     LTIMetadataDataType type,
                     const void* data);
   
   /**
    * default constructor for vector data
    *
    * The constructor creates a record containing a vector of data values.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tag the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    * @param vecLen the length of the data vector 
    */
   LTIMetadataRecord(LTIMetadataTag tag,
                     LTIMetadataDataType type,
                     const void* data,
                     lt_uint32 vecLen);
   /**
    * default constructor for vector data
    *
    * The constructor creates a record containing a vector of data values.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tagName the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    * @param vecLen the length of the data vector 
    */
   LTIMetadataRecord(const char* tagName,
                     LTIMetadataDataType type,
                     const void* data,
                     lt_uint32 vecLen);

   /**
    * default constructor for array data
    *
    * The constructor creates a record containing an array of data values.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tag the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    * @param numDims the length of the \a dims array 
    * @param dims the dimensionality of the data 
    */
   LTIMetadataRecord(LTIMetadataTag tag,
                     LTIMetadataDataType type,
                     const void* data,
                     lt_uint32 numDims,
                     const lt_uint32* dims);

   /**
    * default constructor for array data
    *
    * The constructor creates a record containing an array of data values.
    *
    * Note that the object makes a local copy of the data passed into the
    * record.
    *
    * @param tagName the tag number of the record 
    * @param type datatype of the data in the record 
    * @param data pointer to the data to insert into the record 
    * @param numDims the length of the \a dims array 
    * @param dims the dimensionality of the data 
    */
   LTIMetadataRecord(const char* tagName,
                     LTIMetadataDataType type,
                     const void* data,
                     lt_uint32 numDims,
                     const lt_uint32* dims);

   /**
    * copy constructor
    */
   LTIMetadataRecord(const LTIMetadataRecord&);

   /**
    * destructor
    */
   ~LTIMetadataRecord();

   /**
    * get tag
    *
    * This function returns the tag number (enum) associated with the record.
    *
    * @return the tag
    */
   LTIMetadataTag getTag() const;

   /**
    * get tag name
    *
    * This function returns the tag name (string) associated with the record.
    *
    * @return the tag name
    */
   const char* getTagName() const;

   /**
    * get datatype
    *
    * This function returns the datatype of the data in the record.
    *
    * @return the datatype
    */
   LTIMetadataDataType getDataType() const;

   /**
    * get number of dimensions of the data set
    *
    * This function returns the number of dimensions in the dimensionality
    * of the dataset.
    *
    * This is the length of the array returned by the getDims() function.
    *
    * @return the number of dimensions
    */
   lt_uint32 getNumDims() const;

   /**
    * get dimensionality of the data set
    *
    * This function returns an array with the length of each dimension of the
    * dataset.
    *
    * For example, a 2x3 dataset would return an array of length two with the
    * values {2,3}.
    *
    * This is the length of the returned array is equal to getNumDims().
    *
    * @return the number of dimensions
    */
   const lt_uint32* getDims() const;

   /**
    * is dataset a scalar?
    *
    * This function returns true iff the record's dataset consists only of a
    * single element.
    *
    * @return true if record data is a scalar value
    */
   bool isScalar() const;

   /**
    * is dataset a vector?
    *
    * This function returns true iff the record's dataset is a 1-D array.
    *
    * @return true if record data is a vector of values
    */
   bool isVector() const;

   /**
    * is dataset an array?
    *
    * This function returns true iff the record's dataset is neither
    * a scalar nor a vector.
    *
    * @return true if record data is not scalar or vector
    */
   bool isArray() const;

   /**
    * get scalar data value
    *
    * This function returns the record's data as a scalar value.  It is
    * the caller's responsibility to assure that the record does indeed
    * contain a scalar dataset.
    *
    * The user is responsible for casting the returned value to the correct
    * datatype.
    *
    * @return a pointer to the scalar data value
    */
   const void* getScalarData() const;

   /**
    * get vector data values
    *
    * This function returns the record's data as a vector of values.  It is
    * the caller's responsibility to assure that the record does indeed
    * contain a vector dataset.
    *
    * The user is responsible for casting the returned value to an array
    * of the correct datatype.
    *
    * @param vecLen the length of the returned vector 
    * @return a pointer to the vector of data values
    */
   const void* getVectorData(lt_uint32& vecLen) const;

   /**
    * get array data values
    *
    * This function returns the record's data as an array of values.
    *
    * Note the function returns via its parameters the same information as
    * getNumDims() and getDims().
    *
    * The user is responsible for casting the returned value to an array
    * of the correct datatype.
    *
    * @param numDims the number of dimensions in the \a dims parameter 
    * @param dims the dimensionality array 
    * @return a pointer to the array of data values
    */
   const void* getArrayData(lt_uint32& numDims, const lt_uint32*& dims) const;

   /**
    * get size of metadata record
    *
    * This function will return a close-enough estimate of the size of the
    * record, as if it were to be written directly to disk.
    *
    * @return  size in bytes of the record
    */
   lt_int32 getApproximateSize() const;

private:
   void initialize(LTIMetadataTag tag,
                   const void* data, const lt_uint32* dims);
   void initialize(const char* tagName,
                   const void* data, const lt_uint32* dims);

   lt_uint32 computeLen() const;

   char* m_tagName;
   LTIMetadataDataType m_type;
   lt_uint32 m_numDims;
   lt_uint32* m_dims;
   lt_uint8* m_data;
   
   // nope
   LTIMetadataRecord& operator=(const LTIMetadataRecord&);
};


LT_END_NAMESPACE(LizardTech)

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif // LTIMETADATARECORD_H
