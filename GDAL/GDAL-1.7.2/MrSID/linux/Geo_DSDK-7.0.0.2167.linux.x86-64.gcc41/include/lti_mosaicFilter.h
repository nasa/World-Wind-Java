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

#ifndef LTI_MOSAIC_FILTER_H
#define LTI_MOSAIC_FILTER_H

// lt_lib_mrsid_core
#include "lti_imageStage.h"
#include "lti_imageStageOverrides.h"
#include "lti_imageStageManager.h"


LT_BEGIN_NAMESPACE(LizardTech)

class LTIRTree;
class LTIEmbeddedImage;
class LTIReusableBSQBuffer;
class LTIPipelineBuilder;

/**
 * create a single mosaicked image from a set of images
 *
 * This class create a single mosaicked image from a set of images.
 *
 * The set of input images are all assumed to be in the same coordinate
 * space.  In general, all the images must have the same resolution;
 * differences that are within a small epsilon or exactly a power of two
 * are optionally allowed.
 */
class LTIMosaicFilter : public LTIOverrideDimensions
                               <LTIOverridePixelProps
                               <LTIOverrideBackgroundPixel
                               <LTIOverrideGeoCoord
                               <LTIOverrideMagnification
                               <LTIOverrideIsSelective
                               <LTIOverrideStripHeight
                               <LTIOverrideDelegates
                               <LTIOverridePixelLookupTables
                               <LTIOverrideMetadata
                               <LTIImageStage> > > > > > > > > >
{
   LTI_REFERENCE_COUNTED_BOILERPLATE(LTIMosaicFilter);
public:
   /**
    * initializer
    *
    * Creates an image stage which is a mosaic of the set of input images.
    *
    * The "res correct" feature allows images with slightly different
    * resolutions to be mosaicked.  This allows for proper handling of
    * situations where one image has a resolution of 0.5000 and another has a
    * resolution of 0.4999.
    *
    * The "multires" feature allows images whose resolutions which differ by
    * a power of two to be mosaicked together.
    *
    * NoData and background pixel settings are honored by the mosaic process.
    *
    * @param  imageStageManager manages the set of input images
    * @param  backgroundPixel   color of the background pixel for the new image stage
    * @param  useResCorrect     allow images to have slightly different resolutions
    * @param  useMultires       allow images whose resolutions differ by a power of two
    */
   LT_STATUS initialize(LTIImageStageManager *imageStageManager,
                        const LTIPixel* backgroundPixel,
                        bool useResCorrect,
                        bool useMultires);

   /**
    * Check if a set of images can be mosaicked together.  The parameters to
    * this function mirror those of the constructor: this function will
    * return LT_STS_Success if and only if the images' resolutions are such
    * that a mosaic can be produced.
    *
    * @param  imageStageManager manages the set of input images
    * @param  useResCorrect     allow images to have slightly different resolutions
    * @param  useMultires       allow images whose resolutions differ by a power of two
    * @return status code indicating success or failure
    */
   static LT_STATUS checkResolutionConformance(LTIImageStageManager *imageStageManager,
                                               bool useResCorrect,
                                               bool useMultires);

   // LTIImage
   LT_STATUS getDimsAtMag(double mag,
                          lt_uint32 &width,
                          lt_uint32 &height) const;
   lt_int64 getPhysicalFileSize() const;
   LTIMaskSource *getMask() const;

   // LTIImageStage
   lt_int64 getEncodingCost(const LTIScene& scene) const;
   bool getReaderScene(const LTIScene &decodeScene,
                       LTIScene &readerScene) const;

   /**
    * Set the fill method which controls how noData pixels are matched.
    * @param method  fill method enum. See LTIPixelFillMethod
    */
   void setFillMethod(LTIPixelFillMethod method);
   
   /**
    * Get the fill method.  See setFillMethod().
    */
   LTIPixelFillMethod getFillMethod(void) const;

   // for LizardTech internal use only
   bool getReaderScene(lt_uint32 child,
                       const LTIScene &decodeScene,
                       LTIScene &mosaicScene,
                       LTIScene &readerScene) const;

   // for LizardTech internal use only
   class InSceneCallback
   {
   public:
      virtual LT_STATUS found(const LTIScene &scene,
                              lt_uint32 imageNum,
                              LTIEmbeddedImage &embedded,
                              LTIImageStage &image) = 0;
   };

   // for LizardTech internal use only
   LT_STATUS forEachImageStageInScene(const LTIScene &scene,
                                      InSceneCallback &callback);

   // for LizardTech internal use only
   void setDeleteImages(bool deleteImages);
   // for LizardTech internal use only
   bool getDeleteImages(void) const;

   // for LizardTech internal use only
   LT_STATUS loadImage(lt_uint32 i,
                       LTIEmbeddedImage *&embedded,
                       LTIImageStage *&raw);
   // for LizardTech internal use only
   LT_STATUS closeImage(lt_uint32 i);

   // for LizardTech internal use only
   // does not take ownship of pipelineBuilder
   // don't call this function in inside a decodeBegin()/decodeStrip()/decodeEnd() loop
   LT_STATUS setPipelineBuilder(LTIPipelineBuilder *pipelineBuilder);

   // for LizardTech internal use only
   const LTIRTree &getRTree(void) const;
   // for LizardTech internal use only
   LTIImageStageManager &getImageStageManager(void) const;

protected:
   LT_STATUS decodeBegin(const LTIScene &scene);
   LT_STATUS decodeStrip(LTISceneBuffer &stripBuffer,
                         const LTIScene &stripScene);
   LT_STATUS decodeEnd(void);

private:
   class ListImageStagesInSceneCallback;

   LTIImageStageManager *m_imageStageManager;

   lt_int64 m_physicalFileSize;

   LTIRTree *m_rtree;

   // open image book keeping
   LTIImageStage **m_rImage;
   LTIEmbeddedImage **m_fImage;
   int *m_sImage;
   LTIPixelFillMethod m_fillMethod;
   bool m_deleteImages;
   LTIPixel *m_zeroPixel;
   LTIReusableBSQBuffer *m_workBuffer;

   LTIPipelineBuilder *m_pipelineBuilder;

   // imageInScene
   lt_uint32 *m_inSceneList;
};

#ifndef DOXYGEN_EXCLUDE

class LTIPipelineBuilder
{
public:
   virtual LT_STATUS buildPipeline(lt_uint32 tileId,
                                   LTIImageStage *&pipeline) = 0;
};

#endif

LT_END_NAMESPACE(LizardTech)


#endif // LTI_MOSAIC_FILTER_H
