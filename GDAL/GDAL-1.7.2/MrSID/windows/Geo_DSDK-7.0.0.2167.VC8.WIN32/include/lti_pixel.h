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

#ifndef LTI_PIXEL_H
#define LTI_PIXEL_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)


/**
 * basic properties of a pixel
 *
 * This class stores the basic properties of a pixel:
 * \li the number of bands
 * \li the colorspace
 * \li the datatype
 *
 * This is done by representing the pixel as a set of samples.
 * Since the colorspace cannot in general be inferred from the
 * colors in the samples, the user must declare the colorspace
 * for the pixel.
 */
class LTIPixel
{
public:
   /**
    * constructor
    *
    * This constructor creates an LTIPixel object with the given
    * properties: all the samples will be of the same type.
    *
    * @param  colorSpace  the pixel colorspace
    * @param  numBands    the number of bands (samples per pixel)
    * @param  dataType    the datatype of the samples
    * @param  samples     optional sample objects (default is uint8 set to 0)
    */
   LTIPixel(LTIColorSpace colorSpace,
            lt_uint16 numBands,
            LTIDataType dataType,
            const LTISample* samples=0);

   /**
    * constructor
    *
    * This constructor creates an LTIPixel object made up of the
    * given sample types.  If the colorspace given is LTI_COLORSPACE_INVALID,
    * then the function will attempt to infer the colorspace from the
    * underlying samples; if there is no obvious colorspace, e.g. RGB,
    * the LTI_COLORSPACE_MULTISPECTRAL will be used.
    *
    * @param  samples     the samples of the pixel
    * @param  numBands    the number of bands (samples per pixel)
    * @param  colorSpace  the overall colorspace
    */
   LTIPixel(const LTISample* samples,
            lt_uint16 numBands,
            LTIColorSpace colorSpace=LTI_COLORSPACE_INVALID);

   /**
    * copy constructor
    */
   LTIPixel(const LTIPixel&);

   /**
    * destructor
    */
   virtual ~LTIPixel();

   /**
    * assignment operator
    */
   LTIPixel& operator=(const LTIPixel&);

   /**
    * equality operator
    */
   bool operator==(const LTIPixel&) const;

   /**
    * equality operator
    */
   bool operator!=(const LTIPixel&) const;

   /**
    * get the sample datatype
    *
    * This function returns the datatype of the samples.  Returns
    * LTI_DATATYPE_INVALID if the samples are not all the same
    * datatype.
    *
    * @return the datatype of the samples
    */
   LTIDataType getDataType() const;

   /**
    * get the colorspace
    *
    * This function returns the colorspace of the pixel.
    *
    * @return the colorspace of the pixel
    */
   LTIColorSpace getColorSpace() const;

   /**
    * get the number of bands
    *
    * This function returns the number of bands in the pixel (samples
    * per pixel).
    *
    * @return the number of bands
    */
   lt_uint16 getNumBands() const;

   /**
    * get the size of a pixel
    *
    * This function returns the size of a single pixel, in bytes.
    *
    * This is equivalent to the sum of getBytesPerSample() for each
    * of the samples.
    *
    * @return the number of bytes per pixel
    */
   lt_uint32 getNumBytes() const;

   /**
    * returns the largest size of any sample
    *
    * Returns the largest size of any sample.  This is equivalent to the
    * computing the maximum of getBytesPerSample() for each of the samples.
    *
    * @return  the number of bytes per sample
    */
   lt_uint32 getMaxBytesPerSample() const;

   /**
    * returns status code comparing two pixels
    *
    * Returns status code comparing two pixels.  This is just a different
    * version of operator==, which returns a status code instead of a bool.
    *
    * @param   pixel  the sample to compare this sample to
    * @return  a specific code indicating if impedance matches
    */
   LT_STATUS checkImpedance(const LTIPixel& pixel) const;

   LTISample* getSamples() const;
   LTISample& getSample(lt_uint16) const;

   /**
    * @name Helper functions to get sample values
    */
   /*@{*/

   /**
    * returns the address of the specified sample's value
    *
    * Returns the address of the specified sample's value.  The caller must
    * cast the pointer to the appropriate type before using.
    *
    * @param  band  the band number of the sample to use
    * @return  the address of the sample's value
    */
   const void* getSampleValueAddr(lt_uint16 band) const;

   /**
    * returns the specified sample's value
    *
    * Returns the specified sample's value.  The sample is assumed to be
    * known to have the UINT8 datatype.
    *
    * @param  band  the band number of the sample to use
    * @return  the sample's value
    */
   lt_uint8 getSampleValueUint8(lt_uint16 band) const;

   /**
    * returns the specified sample's value
    *
    * Returns the specified sample's value.  The sample is assumed to be
    * known to have the SINT8 datatype.
    *
    * @param  band  the band number of the sample to use
    * @return  the sample's value
    */
   lt_int8 getSampleValueSint8(lt_uint16 band) const;

   /**
    * returns the specified sample's value
    *
    * Returns the specified sample's value.  The sample is assumed to be
    * known to have the UINT16 datatype.
    *
    * @param  band  the band number of the sample to use
    * @return  the sample's value
    */
   lt_uint16 getSampleValueUint16(lt_uint16 band) const;

   /**
    * returns the specified sample's value
    *
    * Returns the specified sample's value.  The sample is assumed to be
    * known to have the SINT16 datatype.
    *
    * @param  band  the band number of the sample to use
    * @return  the sample's value
    */
   lt_int16 getSampleValueSint16(lt_int16 band) const;

   /**
    * returns the specified sample's value
    *
    * Returns the specified sample's value.  The sample is assumed to be
    * known to have the FLOAT32 datatype.
    *
    * @param  band  the band number of the sample to use
    * @return  the sample's value
    */
   float getSampleValueFloat32(lt_uint16 band) const;

   /**
    * are all samples equal to the minimum value
    *
    * Returns true if all samples are the the minimum value of the datatype.
    */
   bool areSampleValuesMin() const;

   /**
    * are all samples equal to the maximum value
    *
    * Returns true if all samples are the the maximum value of the datatype.
    */
   bool areSampleValuesMax() const;


   /*@}*/

   /**
    * @name Helper functions to set sample values
    */
   /*@{*/

   /**
    * sets all samples to minimum
    *
    * Sets all samples to the minimum value of the datatype.
    */
   void setSampleValuesToMin();

   /**
    * sets all samples to maximum
    *
    * Sets all samples to the maximum value of the datatype.
    */
   void setSampleValuesToMax();

   /**
    * sets sample value by address
    *
    * Sets sample value to value pointed to.
    *
    * @param  band  which sample to set
    * @param  data  value to use
    */
   void setSampleValueAddr(lt_uint16 band, const void* data) const;

   /**
    * sets all samples to the given value
    *
    * Sets all samples to the given value of the datatype.  The samples are
    * assumed to be known to have the UINT8 datatype.
    *
    * @param  value  the value to set the samples to
    */
   void setSampleValuesUint8(lt_uint8 value);

   /**
    * sets all samples to the given value
    *
    * Sets all samples to the given value of the datatype.  The samples are
    * assumed to be known to have the SINT8 datatype.
    *
    * @param  value  the value to set the samples to
    */
   void setSampleValuesSint8(lt_int8 value);

   /**
    * sets all samples to the given value
    *
    * Sets all samples to the given value of the datatype.  The samples are
    * assumed to be known to have the UINT16 datatype.
    *
    * @param  value  the value to set the samples to
    */
   void setSampleValuesUint16(lt_uint16 value);

   /**
    * sets all samples to the given value
    *
    * Sets all samples to the given value of the datatype.  The samples are
    * assumed to be known to have the SINT16 datatype.
    *
    * @param  value  the value to set the samples to
    */
   void setSampleValuesSint16(lt_int16 value);

   /**
    * sets all samples to the given value
    *
    * Sets all samples to the given value of the datatype.  The samples are
    * assumed to be known to have the FLOAT32 datatype.
    *
    * @param  value  the value to set the samples to
    */
   void setSampleValuesFloat32(float value);

   /**
    * sets the given sample to the given value
    *
    * Sets the given sample to the given value of the datatype.  The sample is
    * assumed to be known to have the UINT8 datatype.
    *
    * @param  band   the band number of the sample to use
    * @param  value  the value to set the samples to
    */
   void setSampleValueUint8(lt_uint16 band, lt_uint8 value);

   /**
    * sets the given sample to the given value
    *
    * Sets the given sample to the given value of the datatype.  The sample is
    * assumed to be known to have the SINT8 datatype.
    *
    * @param  band   the band number of the sample to use
    * @param  value  the value to set the samples to
    */
   void setSampleValueSint8(lt_uint16 band, lt_int8 value);

   /**
    * sets the given sample to the given value
    *
    * Sets the given sample to the given value of the datatype.  The sample is
    * assumed to be known to have the UINT16 datatype.
    *
    * @param  band   the band number of the sample to use
    * @param  value  the value to set the samples to
    */
   void setSampleValueUint16(lt_uint16 band, lt_uint16 value);

   /**
    * sets the given sample to the given value
    *
    * Sets the given sample to the given value of the datatype.  The sample is
    * assumed to be known to have the SINT16 datatype.
    *
    * @param  band   the band number of the sample to use
    * @param  value  the value to set the samples to
    */
   void setSampleValueSint16(lt_uint16 band, lt_int16 value);

   /**
    * sets the given sample to the given value
    *
    * Sets the given sample to the given value of the datatype.  The sample is
    * assumed to be known to have the FLOAT32 datatype.
    *
    * @param  band   the band number of the sample to use
    * @param  value  the value to set the samples to
    */
   void setSampleValueFloat32(lt_uint16 band, float value);

   /**
    * sets all samples to the given values
    *
    * Sets all samples to the given values of the datatype.  The sample is
    * assumed to be known to have the UINT8 datatype.
    *
    * @param  values  the values to set the samples to
    */
   void setSampleValuesUint8(const lt_uint8* values);

   /**
    * sets all samples to the given values
    *
    * Sets all samples to the given values of the datatype.  The sample is
    * assumed to be known to have the SINT8 datatype.
    *
    * @param  values  the values to set the samples to
    */
   void setSampleValuesSint8(const lt_int8* values);

   /**
    * sets all samples to the given values
    *
    * Sets all samples to the given values of the datatype.  The sample is
    * assumed to be known to have the UINT16 datatype.
    *
    * @param  values  the values to set the samples to
    */
   void setSampleValuesUint16(const lt_uint16* values);

   /**
    * sets all samples to the given values
    *
    * Sets all samples to the given values of the datatype.  The sample is
    * assumed to be known to have the SINT16 datatype.
    *
    * @param  values  the values to set the samples to
    */
   void setSampleValuesSint16(const lt_int16* values);

   /**
    * sets all samples to the given values
    *
    * Sets all samples to the given values of the datatype.  The sample is
    * assumed to be known to have the FLOAT32 datatype.
    *
    * @param  values  the values to set the samples to
    */
   void setSampleValuesFloat32(const float* values);
   /*@}*/

private:
   void createSamples(LTIDataType dt);
   void createSamples(const LTISample*);
   void deleteSamples();

   LTISample* m_samples;
   lt_uint16 m_numBands;
   LTIColorSpace m_colorSpace;
};


LT_END_NAMESPACE(LizardTech)


#endif // LTI_PIXEL_H
