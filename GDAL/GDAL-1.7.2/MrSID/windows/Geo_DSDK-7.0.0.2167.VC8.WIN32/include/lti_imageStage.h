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

#ifndef LTI_IMAGESTAGE_H
#define LTI_IMAGESTAGE_H

// lt_lib_mrsid_core
#include "lti_image.h"

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * abstract class for decoding from an image
 *
 * The LTIImageStage abstract class extends the LTIImage class by adding
 * decode functionality, including read methods and progress and interrupt
 * functions.
 */
class LTIImageStage : public LTIImage
{
   LTI_REFERENCE_COUNTED_BOILERPLATE_BASE(LTIImageStage);
public:
   // LTIImage
   lt_uint32 getWidth() const = 0;
   lt_uint32 getHeight() const = 0;
   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32 &width,
                          lt_uint32 &height) const = 0;
   const LTIPixel& getPixelProps() const = 0;
   const LTIPixel* getBackgroundPixel() const = 0;
   const LTIPixel* getNoDataPixel() const = 0;
   const LTIPixelLookupTable* getPixelLookupTable() const = 0;
   const LTIPixel &getMinDynamicRange() const = 0;
   const LTIPixel &getMaxDynamicRange() const = 0;
   const LTIGeoCoord& getGeoCoord() const = 0;
   bool isGeoCoordImplicit() const = 0;
   const LTIMetadataDatabase &getMetadata() const = 0;
   double getMinMagnification() const = 0;
   double getMaxMagnification() const = 0;
   lt_int64 getPhysicalFileSize() const = 0;

   /**
    * check if image supports "random access" decoding
    *
    * Some formats, notably JPEG, do not support "selective" decoding.
    * That is, they require that scenes being decoding must march
    * in order down the scanlines of the image.  Formats like TIFF
    * and MrSID, however, are "selective": any scene can be requested
    * at any time.
    *
    * @return true if and only if the image supports arbitrary scene requests
    */
   virtual bool isSelective() const = 0;
   
   // for LizardTech internal use only
   virtual LTIMaskSource *getMask() const = 0;
   
   
   /**
    * set progress delegate
    *
    * This function sets the progress delegate, which is used in a callback-like
    * fashion to report percent-complete of a read() operation back to the
    * calling application.
    *
    * Passing NULL to this function should remove the LTIImageStage's current delegate,
    * if any.
    *
    * Note this function does not take ownership of the delegate object.
    *
    * @param  delegate  a pointer to the delegate object to be used by the image reader
    */
   virtual void setProgressDelegate(LTIProgressDelegate* delegate) = 0;

   /**
    * get progress delegate
    *
    * This function returns the object's progress delegate.
    *
    * The function will return NULL if no delegate has been set.
    *
    * Derived classes should call this method from within their read()
    * methods so that they can inform the user of the progress of the read
    * operation.
    *
    * @return  a pointer to the delegate object (or NULL if no delegate has been set)
    */
   virtual LTIProgressDelegate* getProgressDelegate() const = 0;

   /**
    * set interrupt delegate
    *
    * This function sets the interrupt delegate, which is used in a callback-like
    * fashion by the calling application to asynchronously indicate that a read()
    * operation should be halted without completing.
    *
    * Passing NULL to this function should remove the LTIImageStage's current delegate,
    * if any.
    *
    * Note this function does not take ownership of the delegate object.
    *
    * @param  delegate  a pointer to the delegate object to be used by the image reader
    */
   virtual void setInterruptDelegate(LTIInterruptDelegate* delegate) = 0;

   /**
    * get interrupt delegate
    *
    * This function returns the object's interrupt delegate.
    *
    * The function will return NULL if no delegate has been set.
    *
    * Derived classes should call this method from within their read()
    * methods so that they can determine if the user has requested that the read
    * operation should be aborted.
    *
    * @return  a pointer to the delegate object (or NULL if no delegate has been set)
    */
   virtual LTIInterruptDelegate* getInterruptDelegate() const = 0;

   /**
    * read (decode) a scene from the image
    *
    * This function decodes a scene from the image and puts the pixels into the
    * given buffer.
    *
    * The scene may NOT extend beyond the boundaries of the image.
    *
    * This function calls readBegin(), then calls readStrip() repeatedly
    * until all the rows of the scene are done, then calls readEnd().
    *
    * Derived classes should not override this method.
    *
    * @param  scene  the region (and scale) of the image to be read
    * @param  buffer  the buffer to read the pixels into
    * @return status code indicating success or failure
    */
   LT_STATUS read(const LTIScene& scene,
                  LTISceneBuffer& buffer);

   /**
    * start strip-based read
    *
    * This function is called by read() before readStrip() is called.  It
    * should not be called directly except in certain rare circumstances.
    *
    * Derived classes should not override.
    *
    * @param scene  the full scene to be read
    * @return status code indicating success or failure
    */
   virtual LT_STATUS readBegin(const LTIScene& scene);

   /**
    * read a strip from the image
    *
    * This function decodes a scene from the image and puts the pixels into the
    * given buffer.  It is called by read(), and should not be called directly
    * except in certain rare circumstances.
    *
    * The scene must lie within the boundaries of the image.
    *
    * Derived classes should not override this method.
    *
    * Derived classes should use the progress and interrupt delegates when
    * the read operation can be expected to take a significant amount of time
    * to complete.
    *
    * @param   buffer      the buffer to read the pixels into
    * @param   stripScene  the scene for this strip being decoded
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS readStrip(LTISceneBuffer& buffer, const LTIScene& stripScene);

   /**
    * finish strip-based read
    *
    * This function is called by read() after readStrip() is called.  It
    * should not be called directly except in certain rare circumstances.
    *
    * Derived classes should not override.
    *
    * @return status code indicating success or failure
    */
   virtual LT_STATUS readEnd();

   /**
    * get strip height
    *
    * Returns the strip height used in read() calls.
    *
    * Reader classes should set this.  Filter classes should inherit the
    * stripheight of their pipeline predecessor.  A writer class will
    * force the stripheight of the pipeline to match its stripheight.
    *
    * @return the strip height
    */
   virtual lt_uint32 getStripHeight() const = 0;

   /**
    * set the strip height
    *
    * Sets the strip height to be used in decoding.  This is the number
    * of rows to be decoded in each strip of the read() sequence.
    *
    * Reader classes should implement this directly.  (LTIImageFilter
    * implements this as a call to setStripHeight() on the previous stage.)
    *
    * @param stripHeight the number of rows to decode at one time
    * @return status code indicating success or failure
    */
   virtual LT_STATUS setStripHeight(lt_uint32 stripHeight) = 0;

   /**
    * get the cost to encode this scene
    *
    * Returns the "cost" to encode this scene, for use by those image writers
    * which have usage metering enabled.  The typical cost is equal to the
    * nominal image size (width * height * numBands * bytesPerSample), but
    * this is overridden for special situations, e.g. the mosaic filter.
    *
    * @param  scene  the scene to be charged for
    * @return the cost to encode the given scene
    */
   virtual lt_int64 getEncodingCost(const LTIScene& scene) const = 0;

   /**
    * get the underlying scene to be used
    *
    * Get the scene that will be sent to the underlying LTIImageReader.  This
    * is useful in some complex pipelines.
    *
    * @param  decodeScene  the scene that would be given to read()
    * @param  readerScene  the scene that is the actual scene passed
    *                      the underlying image reader
    * @return true if readerScene is not empty
    */
   virtual bool getReaderScene(const LTIScene &decodeScene,
                               LTIScene &readerScene) const = 0;

   /**
    * get number of strips in scene
    *
    * After readBegin() has been called, this will return the number of
    * strips in the given scene.
    *
    * @return number of strips in the current scene
    */
   lt_uint32 getNumStrips() const;

   /**
    * get a strip for current scene
    *
    * After readBegin() has been called, this function can be used to return
    * the scene corresponding to the given strip number for the current scene
    * being decoded.
    *
    * @param   stripNumber  the strip to compute the scene for
    * @return  the scene representing the strip
    */
   LTIScene getStripScene(lt_uint32 stripNumber) const;

   /**
    * override the background color of the image
    *
    * This function is used to set the background color of the image.
    *
    * @param backgroundPixel  the data for the background color
    * @return status code indicating success or failure
    */
   virtual LT_STATUS overrideBackgroundPixel(const LTIPixel *backgroundPixel) = 0;

   /**
    * override the "no data" (transparency) color of the image
    *
    * This function is used to set the "no data" or transparency color of the
    * image.
    *
    * @param nodataPixel  the data for the transparency color
    * @return status code indicating success or failure
    */
   virtual LT_STATUS overrideNoDataPixel(const LTIPixel *nodataPixel) = 0;

   /**
    * override the geographic coordinates of the image
    *
    * This function is used to set the geographic coordinates of the image.
    *
    * @param geoCoord    the geographic coordinate information
    * @return status code indicating success or failure
    */
   virtual LT_STATUS overrideGeoCoord(const LTIGeoCoord &geoCoord) = 0;

   /**
    * override the dynamic range of the image
    */
   virtual LT_STATUS overrideDynamicRange(const LTIPixel& drmin,
                                          const LTIPixel& drmax) = 0;

   /**
    * override the bits-per-sample of the image's (sample's) datatype
    */
   virtual LT_STATUS overridePixelBPS(lt_uint8) = 0;

   /**
    * override the CLUT of the image
    */
   virtual LT_STATUS overridePixelLookupTable(const LTIPixelLookupTable* pixelLookupTable) = 0;

   /**
    * override the metadata of the image
    */
   virtual LT_STATUS overrideMetadata(const LTIMetadataDatabase &metadata) = 0;


protected:
   /**
    * fill the background of the scene
    *
    * This function sets the buffer to the background pixel, if any.
    *
    * Derived classes may choose to override this.
    *
    * @param   scene   the region (and scale) of the image to be read
    * @param   buffer  the buffer to read the pixels into
    * @return  status code indicating success or failure
    */
   LT_STATUS fillBackground(const LTIScene& scene,
                            LTISceneBuffer& buffer);

   /**
    * fill the scene to given pixel
    *
    * This function sets the buffer to the given pixel.
    *
    * Derived classes may choose to override this.
    *
    * @param   scene   the region (and scale) of the image to be read
    * @param   buffer  the buffer to read the pixels into
    * @return  status code indicating success or failure
    */
   LT_STATUS fillBackground(const LTIScene& scene,
                            LTISceneBuffer& buffer,
                            const LTIPixel&);

   /**
    * start strip-based read
    *
    * This function is called by readBegin() to start the actual
    * class-specific work for decoding a scene.
    *
    * Derived classes must implement this function.
    *
    * This function should never be called directly.
    *
    * @param scene  the full scene to be read
    * @return status code indicating success or failure
    */
   virtual LT_STATUS decodeBegin(const LTIScene &scene) = 0;

   /**
    * read a strip from the image
    *
    * This function is called by readStrip() to implement the actual
    * class-specific work for decoding a strip of the scene.
    *
    * Derived classes must implement this function.
    *
    * This function should never be called directly.
    *
    * @param   stripBuffer  the buffer to read the pixels into
    * @param   stripScene   the scene for this strip being decoded
    * @return  status code indicating success or failure
    */
   virtual LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer,
                                 const LTIScene &stripScene) = 0;

   /**
    * finish strip-based read
    *
    * This function is called by readEnd() to complete the actual
    * class-specific work for decoding a scene.
    *
    * Derived classes must implement this function.
    *
    * This function should never be called directly.
    *
    * @return status code indicating success or failure
    */
   virtual LT_STATUS decodeEnd(void) = 0;

   /**
    * get full scene
    *
    * This function returns the object's scene that was passed to readBegin().
    * Only use this function in decode{Begin, Strip, End}().
    *
    * @return  the scene
    */
   const LTIScene &getFullScene() const;

private:
   LT_STATUS validateReadRequestScene(const LTIScene& fullScene) const;
   LT_STATUS validateReadRequestBuffer(const LTIScene& fullScene,
                                       LTISceneBuffer& fullData) const;

   LT_STATUS checkDelegates(const LTIScene*, bool);

   struct StripMarcher;
   StripMarcher* m_stripMarcher;
};

LT_END_NAMESPACE(LizardTech)

#endif // LTI_IMAGESTAGE_H
