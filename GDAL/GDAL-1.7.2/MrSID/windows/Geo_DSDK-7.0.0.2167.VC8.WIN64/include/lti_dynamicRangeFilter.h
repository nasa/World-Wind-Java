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

#ifndef LTI_DYNAMIC_RANGE_FILTER_H
#define LTI_DYNAMIC_RANGE_FILTER_H

// lt_lib_mrsid_core
#include "lti_imageFilter.h"
#include "lti_imageStageOverrides.h"


LT_BEGIN_NAMESPACE(LizardTech)

class LTIReusableBSQBuffer;

/**
 * change dynamic range or datatype of the samples of the image
 *
 * Adjusts the sample values to fit the given dynamic range and datatype.
 */

class LTIDynamicRangeFilter : public LTIOverridePixelProps
                                     <LTIOverrideBackgroundPixel
                                     <LTIImageFilter> >
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIDynamicRangeFilter);
public:
   /**
    * initializer
    *
    * Creates an image stage with the sample data adjusted according to the
    * given dynamic range values.
    *
    * @param  srcImage    the base image
    * @param  srcDRMin       the min value of the srcImage (will be mapped to the min value of dstDataType)
    * @param  srcDRMax       the max value of the srcImage (will be mapped to the max value of dstDataType)
    * @param  dstDataType    the datatype of the new image stage
    *  @note If srcDRMin and srcDRMax are both 0 the filter will use srcImage->getDynamicRange{Min,Max}().
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        double srcDRMin,
                        double srcDRMax,
                        LTIDataType dstDataType);

   /**
    * initializer (for compatibly with old LTIDynamicRangeFilter)
    *
    * Creates an image stage with the sample data adjusted according to the
    * given dynamic range values.
    *
    * @param  srcImage    the base image
    * @param  window         the number of units or "width" of the desired range
    * @param  level          the midpoint of the window; this effectively defines
    *                        the min and max sample values
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        double window,
                        double level);

   /**
   * initializer  (for compatibly with old LTIDynamicRangeFilter)
   *
   * Creates an image stage with the sample data adjusted according to the
   * dynamic range values inherit in the image, e.g. in the metadata.
   *
   * @param  srcImage    the base image
   */
   LT_STATUS initialize(LTIImageStage* srcImage);

   /**
    * initializer  (for compatibly with LTIDataTypeTransformer)
    *
    * Creates an image stage with the given datatype.  The sample values are
    * scaled as required to meet the range of the new datatype; that is, a
    * value of 65535 for a 16-bit datatype will map to a value of 255 for an
    * 8-bit datatype, and a value of 127 for an 8-bit datatype will map to
    * a value of 32767 for a 16-bit datatype.
    *
    * @note Only uint8, uint16, and float32 datatypes are supported.
    *
    * @param  srcImage    the base image
    * @param  dstDataType    the datatype of the new image stage
    */
   LT_STATUS initialize(LTIImageStage* srcImage,
                        LTIDataType dstDataType);

   // for LizardTech internal use only
   LT_STATUS setSrcMinMax(double srcMin, double srcMax);

   // for LizardTech internal use only
   LT_STATUS setDstMinMax(double dstMin, double dstMax);

   // for LizardTech internal use only
   static double getValue(const LTIPixel &pixel);

   // for LizardTech internal use only
   static void compositeSrcRanges(double localMin, double localMax,
                                  double globalMin, double globalMax,
                                  double dstMin, double dstMax,
                                  double &compositeMin, double &compositeMax);

protected:
   LT_STATUS decodeStrip(LTISceneBuffer& stripBuffer,
                         const LTIScene& stripScene);

private:
   LT_STATUS transformBuffer(LTISceneBuffer &dstData,
                             LTISceneBuffer &srcData,
                             lt_uint32 numCols,
                             lt_uint32 numRows) const;
   LT_STATUS transformPixel(LTIPixel &newPixel,
                            const LTIPixel &oldPixel) const;

   double m_srcMin;
   double m_srcMax;
   double m_dstMin;
   double m_dstMax;

   LTIReusableBSQBuffer* m_buffer;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_DYNAMIC_RANGE_FILTER_H
