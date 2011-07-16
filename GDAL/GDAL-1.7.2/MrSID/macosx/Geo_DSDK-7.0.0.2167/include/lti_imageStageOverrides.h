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

#ifndef LTI_IMAGE_STAGE_OVERRIDES_H
#define LTI_IMAGE_STAGE_OVERRIDES_H

// lt_lib_mrsid_core
#include "lti_types.h"

#include <stddef.h>

LT_BEGIN_NAMESPACE(LizardTech)


////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideMetadataData
{
   LTIOverrideMetadataData();
   ~LTIOverrideMetadataData();
   
   LT_STATUS createMetadata(const LTIMetadataDatabase *metadata);
   LTIMetadataDatabase &getMetadata();
   
   LTIMetadataDatabase *m_metadata;
};


template<class BASE>
struct LTIOverrideMetadata : public BASE,
protected LTIOverrideMetadataData
{
   const LTIMetadataDatabase &getMetadata() const
   {
      return *m_metadata;
   }
   
   LT_STATUS overrideMetadata(const LTIMetadataDatabase &metadata)
   {
      return LTIOverrideMetadataData::createMetadata(&metadata);
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideDimensionsData
{
   LTIOverrideDimensionsData();

   /**
    * set width and height of the image
    *
    * This function is used to set the dimensions (width and height) of the image.
    *
    * Derived classes are \em required to call this function from within their
    * initialize() method.
    *
    * @param  width       the image width, in pixels
    * @param  height      the image height, in pixels
    * @return status code indicating success or failure
    */
   LT_STATUS setDimensions(lt_uint32 width, lt_uint32 height);

   lt_uint32 m_width;
   lt_uint32 m_height;
};

template<class BASE>
struct LTIOverrideDimensions : public BASE,
                               protected LTIOverrideDimensionsData

{
   lt_uint32 getWidth() const
   {
      return m_width;
   }

   lt_uint32 getHeight() const
   {
      return m_height;
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverridePixelPropsData
{
   LTIOverridePixelPropsData();
   ~LTIOverridePixelPropsData();

   /**
    * set pixel properties of the image
    *
    * This function is used to set the pixel properties of the image, namely
    * the datatype, colorspace, and number of bands.
    *
    * Derived classes are \em required to call this function from within their
    * initialize() method.
    *
    * @param pixelProps  the basic pixel properties of the image (number of
    *                    bands, colorspace, datatype)
    * @return status code indicating success or failure
    */
   LT_STATUS setPixelProps(const LTIPixel& pixelProps);
   LT_STATUS setPixelBPS(lt_uint8 bps);

   /**
    * set the dynamic range of the image
    *
    * This function is used to set the dynamic range of the image.  The
    * dynamic range consists of the minimum and maximum value for a given
    * sample.
    *
    * If both the minimum and maximum are NULL, the natural
    * range of the sample datatype will be used.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the dynamic range is not set, the natural
    * minimum and maximum of the datatype of the sample is used.
    *
    * @param drmin       the minimum dynamic range value
    * @param drmax       the maximum dynamic range value
    * @return status code indicating success or failure
    */
   LT_STATUS setDynamicRange(const LTIPixel& drmin,
                             const LTIPixel& drmax);

   /**
    * set the dynamic range of the image
    *
    * This function is used to set the dynamic range of the image.  The
    * dynamic range consists of the minimum and maximum value for a given
    * sample.  This function sets these values to the natural
    * range of the sample datatype.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the dynamic range is not set, the natural
    * minimum and maximum of the datatype of the sample is used.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setDefaultDynamicRange(void);

   LTIPixel *m_pixelProps;
   LTIPixel *m_drminPixel;
   LTIPixel *m_drmaxPixel;
};

template<class BASE>
struct LTIOverridePixelProps : public BASE,
                               protected LTIOverridePixelPropsData
{
   const LTIPixel &getPixelProps() const
   {
      return *m_pixelProps;
   }

   const LTIPixel &getMinDynamicRange() const
   {
      return *m_drminPixel;
   }

   const LTIPixel &getMaxDynamicRange() const
   {
      return *m_drmaxPixel;
   }

   LT_STATUS overridePixelBPS(lt_uint8 bps)
   {
      return setPixelBPS(bps);
   }

   LT_STATUS overrideDynamicRange(const LTIPixel& drmin,
                                  const LTIPixel& drmax)
   {
      return setDynamicRange(drmin, drmax);
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideBackgroundPixelData
{
   LTIOverrideBackgroundPixelData();
   ~LTIOverrideBackgroundPixelData();

   /**
    * set the background color of the image
    *
    * This function is used to set the background color of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the background color is not set, or if NULL
    * is passed in, a background color of black (sample values of 0) will
    * be used.  (Exception: for CMYK images, the background is set to white.)
    *
    * @param backgroundPixel  the data for the background color
    * @return status code indicating success or failure
    */
   LT_STATUS setBackgroundPixel(const LTIPixel* backgroundPixel);

   /**
    * set the "no data" (transparency) color of the image
    *
    * This function is used to set the "no data" or transparency color of the
    * image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the transparency color is not set, or if NULL
    * is passed in, the image will be assumed to have no transparent pixels.
    *
    * @param nodataPixel  the data for the transparency color
    * @return status code indicating success or failure
    */
   LT_STATUS setNoDataPixel(const LTIPixel* nodataPixel);

   LTIPixel *m_backgroundPixel;
   LTIPixel *m_nodataPixel;
};


template<class BASE>
struct LTIOverrideBackgroundPixel : public BASE,
                                    protected LTIOverrideBackgroundPixelData
{
public:
   const LTIPixel *getBackgroundPixel() const
   {
      return m_backgroundPixel;
   }
   const LTIPixel *getNoDataPixel() const
   {
      return m_nodataPixel;
   }

   LT_STATUS overrideBackgroundPixel(const LTIPixel *backgroundPixel)
   {
      return setBackgroundPixel(backgroundPixel);
   }
   LT_STATUS overrideNoDataPixel(const LTIPixel *nodataPixel)
   {
      return setNoDataPixel(nodataPixel);
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideGeoCoordData
{
   LTIOverrideGeoCoordData();
   ~LTIOverrideGeoCoordData();

   /**
    * set the geographic coordinates of the image
    *
    * This function is used to set the geographic coordinates of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the geographic coordinates are not set, the
    * default position is assumed.
    *
    * @param geoCoord    the geographic coordinate information
    * @return status code indicating success or failure
    */
   LT_STATUS setGeoCoord(const LTIGeoCoord& geoCoord);

   /**
    * set the geographic coordinates of the image
    *
    * This function is used to set the geographic coordinates of the image to
    * a reasonable default, when there are no other geographic coordinates to
    * use.
    *
    * The default coordinates used are:
    * \li upperleft: (0.0, height-1.0)
    * \li resolution: (1.0, -1.0)
    * \li rotation: (0.0, 0.0)
    *
    * Calling this function will cause isGeoCoordImplicit() to return true.
    *
    * @return status code indicating success or failure
    */
   LT_STATUS setDefaultGeoCoord(const LTIImage &image);

   LTIGeoCoord *m_geoCoord;
   bool m_geoCoordImplicit;
};

template<class BASE>
struct LTIOverrideGeoCoord : public BASE,
                             protected LTIOverrideGeoCoordData
{
   const LTIGeoCoord &getGeoCoord() const
   {
      return *m_geoCoord;
   }

   bool isGeoCoordImplicit() const
   {
      return m_geoCoordImplicit;
   }

   LT_STATUS overrideGeoCoord(const LTIGeoCoord &geoCoord)
   {
      return setGeoCoord(geoCoord);
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideMagnificationData
{
   LTIOverrideMagnificationData();

   /**
    * set the minimum and maximum magnifications of the image properties
    *
    * This function is used to set the minimum and maximum magnifications
    * of the image.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the minimum and maximum magnifications are not
    * set, values of 1.0 are used (indicating the image may not be scaled
                                   * larger or smaller).
    *
    * @param minMag  the minimum magnification of the image
    * @param maxMag  the maximum magnification of the image
    * @return status code indicating success or failure
    */
   LT_STATUS setMagnification(double minMag,
                              double maxMag);

   double m_minMag;
   double m_maxMag;
};

template<class BASE>
struct LTIOverrideMagnification : public BASE,
                                  protected LTIOverrideMagnificationData
{
   double getMinMagnification() const
   {
      return m_minMag;
   }

   double getMaxMagnification() const
   {
      return m_maxMag;
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideIsSelectiveData
{
   LTIOverrideIsSelectiveData();

   /**
    * set image to support "random access"
    *
    * This function is used to indicate the image supports "selective"
    * decoding.  See isSelective() for details.
    *
    * By default, all images support selective decoding.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.
    *
    * @param  enable  set to true if and only if the image supports selective decoding
    */
   void setIsSelective(bool enable);

   bool m_isSelective;
};

template<class BASE>
struct LTIOverrideIsSelective : public BASE,
                                protected LTIOverrideIsSelectiveData
{
   bool isSelective() const
   {
      return m_isSelective;
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideStripHeightData
{
   LTIOverrideStripHeightData();

   lt_uint32 m_stripHeight;
};

template<class BASE>
struct LTIOverrideStripHeight : public BASE,
                                protected LTIOverrideStripHeightData
{
   lt_uint32 getStripHeight() const
   {
      return m_stripHeight;
   }

   LT_STATUS setStripHeight(lt_uint32 stripHeight)
   {
      m_stripHeight = stripHeight;
      return LT_STS_Success;
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverrideDelegatesData
{
   LTIOverrideDelegatesData();

   LTIProgressDelegate  *m_progressDelegate;
   LTIInterruptDelegate *m_interruptDelegate;
};

template<class BASE>
struct LTIOverrideDelegates : public BASE,
                              protected LTIOverrideDelegatesData
{
   void setProgressDelegate(LTIProgressDelegate* delegate)
   {
      m_progressDelegate = delegate;
   }

   LTIProgressDelegate *getProgressDelegate() const
   {
      return m_progressDelegate;
   }

   void setInterruptDelegate(LTIInterruptDelegate* delegate)
   {
      m_interruptDelegate = delegate;
   }

   LTIInterruptDelegate *getInterruptDelegate() const
   {
      return m_interruptDelegate;
   }
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

struct LTIOverridePixelLookupTablesData
{
   LTIOverridePixelLookupTablesData();
   ~LTIOverridePixelLookupTablesData();

   /**
    * set the color lookup table
    *
    * This function is used to set the color used lookup table, used
    * for indexed color (palletized) images.
    *
    * Derived classes may choose to call this function from within their
    * initialize() method.  If the lookup table is not set, or if NULL
    * is passed in, the image will be assumed to have no lookup table.
    *
    * The LTIImageStage makes a local copy of the lookup table.
    *
    * @param  pixelLookupTable  pointer to the lookup table (or NULL)
    * @return status code indicating success or failure
    */
   LT_STATUS setPixelLookupTable(const LTIPixelLookupTable* pixelLookupTable);

   LTIPixelLookupTable *m_pixelLookupTable;
};

template<class BASE>
struct LTIOverridePixelLookupTables : public BASE,
                                      protected LTIOverridePixelLookupTablesData
{
   const LTIPixelLookupTable *getPixelLookupTable() const
   {
      return m_pixelLookupTable;
   }

   LT_STATUS overridePixelLookupTable(const LTIPixelLookupTable* pixelLookupTable)
   {
      return setPixelLookupTable(pixelLookupTable);
   }
};


////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

LT_END_NAMESPACE(LizardTech)


#endif // LTI_IMAGE_STAGE_OVERRIDES_H
