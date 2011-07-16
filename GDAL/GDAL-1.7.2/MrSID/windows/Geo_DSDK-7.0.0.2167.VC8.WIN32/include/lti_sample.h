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

#ifndef LTI_SAMPLE_H
#define LTI_SAMPLE_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * basic properties of a sample
 *
 * This class stores the basic properties of a sample: the color and the
 * datatype.  A set of one or more (possibly dissimilar) LTISample objects is
 * used in the representation of a pixel (LTIPixel).
 */
class LTISample
{
public:
   /**
    * default constructor
    *
    * This constructor creates an LTISample object with the given
    * properties.
    *
    * @param  color       the sample's color
    * @param  dataType    the datatype of the sample
    * @param  data        initial data for sample value (defaults to 0)
    */
   LTISample(LTIColor color,
             LTIDataType dataType,
             const void* data=0);

   LTISample();

   /**
    * copy constructor
    */
   LTISample(const LTISample&);

   /**
    * destructor
    */
   virtual ~LTISample();

   /**
    * assignment operator
    */
   virtual LTISample& operator=(const LTISample&);

   /**
    * equality operator
    */
   bool operator==(const LTISample&) const;

   /**
    * equality operator
    */
   bool operator!=(const LTISample&) const;

   /**
    * sets the precision of the sample
    *
    * Sets the precision of the sample.  By default, the number of bits of
    * precision is equal to the number of bits in the underlying datatype.
    *
    * @note The data is assumed to be justified to the least significant bit
    * of the word.
    *
    * @param  precision  the number of bits
    */
   void setPrecision(lt_uint32 precision);

   /**
    * returns the precision of the sample
    *
    * Returns the precision of the sample.
    *
    * @return  the number of bits used in the datatype
    */
   lt_uint32 getPrecision() const;

   /**
    * get the sample datatype
    *
    * This function returns the datatype of the sample.
    *
    * @return the datatype of the sample
    */
   LTIDataType getDataType() const;

   /**
    * get the color
    *
    * This function returns the color of the sample.
    *
    * @return the color of the sample
    */
   LTIColor getColor() const;

   /**
    * get the size of a sample
    *
    * This function returns the size of the sample, in bytes.
    *
    * This is equivalent to calling LTITypes::getNumBytes(getDataType()).
    *
    * @return the number of bytes in the sample
    */
   lt_uint32 getNumBytes() const;

   /**
    * returns status code comparing two samples
    *
    * Returns status code comparing two samples.  This is just a different
    * version of operator==, which returns a status code instead of a bool.
    *
    * @param   sample  the sample to compare this sample to
    * @return  a specific code indicating if impedance matches
    */
   LT_STATUS checkImpedance(const LTISample& sample) const;

   /**
    * is the sample value minimum
    *
    * Returns true if the sample's value is the datatype's minimum.
    */
   bool isValueMin() const;

   /**
    * is the sample value maximum
    *
    * Returns true if the sample's value is the datatype's maximum.
    */
   bool isValueMax() const;

   /**
    * sets the sample value minimum
    *
    * Sets the sample's value to the datatype's minimum.
    */
   void setValueToMin();

   /**
    * sets the sample value maximum
    *
    * Sets the sample's value to the datatype's maximum.
    */
   void setValueToMax();

   /**
    * sets the sample value minimum
    *
    * Sets the sample's value to the min of itself and the argument
    * (The argument sample assumed to have the same datatype.)
    */
   void setValueToMin(const LTISample&);

   /**
    * sets the sample value maximum
    *
    * Sets the sample's value to max of itself and the argument.
    * (The argument sample assumed to have the same datatype.)
    */
   void setValueToMax(const LTISample&);

   /**
    * @name Sample value functions
    *
    * Sets the sample's value to the value given.  The sample is assumed to
    * be known to have the given datatype.  (For the void* "addr" functions,
    * the caller must assure datatype correctness underneath the void* pointer.)
    */
   /*@{*/
   void setValueAddr(const void*);
   const void* getValueAddr() const;

   void setValueUint8(lt_uint8);
   void setValueUint16(lt_uint16);
   void setValueUint32(lt_uint32);
   void setValueUint64(lt_uint64);
   void setValueSint8(lt_int8);
   void setValueSint16(lt_int16);
   void setValueSint32(lt_int32);
   void setValueSint64(lt_int64);
   void setValueFloat32(float);
   void setValueFloat64(double);

   lt_uint8 getValueUint8() const;
   lt_uint16 getValueUint16() const;
   lt_uint32 getValueUint32() const;
   lt_uint64 getValueUint64() const;
   lt_int8 getValueSint8() const;
   lt_int16 getValueSint16() const;
   lt_int32 getValueSint32() const;
   lt_int64 getValueSint64() const;
   float getValueFloat32() const;
   double getValueFloat64() const;

   /*@}*/

private:
   union ValueType
   {
      lt_uint8 uint8;
      lt_uint16 uint16;
      lt_uint32 uint32;
      lt_uint64 uint64;

      lt_int8 sint8;
      lt_int16 sint16;
      lt_int32 sint32;
      lt_int64 sint64;

      float float32;
      double float64;

      //float complex32[2];
      //double complex64[2];
   };

   ValueType m_value;
   LTIDataType m_dataType;
   LTIColor m_color;

   lt_uint32 m_numBytes;
   lt_uint32 m_precision;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_SAMPLE_H
