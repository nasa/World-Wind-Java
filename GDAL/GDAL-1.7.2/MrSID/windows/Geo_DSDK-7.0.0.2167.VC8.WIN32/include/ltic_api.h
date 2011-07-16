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
/* PUBLIC - C */

#ifndef LTI_CAPI_H
#define LTI_CAPI_H

#include "lti_types.h"
#include "lti_metadataTypes.h"
#include "lt_ioCStream.h"

#ifdef LT_CPLUSPLUS
extern "C" {
#endif

#if defined(LT_COMPILER_MS)
	#pragma warning(push,4)
#endif


/**      
 * @file  ltic_api.h
 *
 * This file contains a simple C API to the Decode SDK.  It is essentially
 * just a wrapper around the LTIImage class and its member functions.
 */

   
/**
 * C API status codes
 */
/*@{*/
#define LT_STS_CAPI_BASE            ((LT_STATUS)52000)
#define LT_STS_CAPI_BadParam        ((LT_STATUS)52001)
#define LT_STS_CAPI_MAX             ((LT_STATUS)52099)
/*@}*/


/**
 * opaque pointer (handle) to an LTIImage
 */
typedef void* LTICImageH;


/**
 * get SDK version (C API)
 *
 * Returns the full version number of the SDK, e.g. "4.0.8.673".
 *
 * This method is part of the C API.  It is equivalent to the
 * LTIUtils::getVersionInfo() function.
 *
 * @param major     address to hold major version number
 * @param minor     address to hold minor version number
 * @param revision  address to hold revision number
 * @param build     address to hold build number
 * @param branch    address to hold string giving the branch name
 * @return status code indicating success or failure
 */
LT_STATUS ltic_getVersion(lt_uint32* major,
                          lt_uint32* minor,
                          lt_uint32* revision,
                          lt_uint32* build,
                          const char** branch);

/**
 * open MrSID image via filename (C API)
 *
 * Given the filename of a MrSID image, this function will open the image
 * and return a handle which can be used to access image information and
 * perform decodes.
 *
 * This method is part of the C API.  It is equivalent to constructing
 * a MrSIDImageReader object.
 *
 * @param image     address to hold image handle 
 * @param fileName  name of file to open
 * @return status code indicating success or failure
 */
LT_STATUS ltic_openMrSIDImageFile(LTICImageH* image,
                                  const char* fileName);

/**
 * open MrSID image via stream (C API)
 *
 * Given a C stream containing a MrSID image, this function will open the image
 * and return a handle which can be used to access image information and
 * perform decodes.
 *
 * This method is part of the C API.  It is equivalent to constructing
 * a MrSIDImageReader object.
 *
 * @param image     address to hold image handle 
 * @param stream    C stream handle
 * @return status code indicating success or failure
 */
LT_STATUS ltic_openMrSIDImageStream(LTICImageH* image,
                                    LTIOStreamH stream);

/**
 * open JPEG 2000 image via filename (C API)
 *
 * Given the filename of a JPEG 2000 image, this function will open the image
 * and return a handle which can be used to access image information and
 * perform decodes.
 *
 * This method is part of the C API.  It is equivalent to constructing
 * a J2KImageReader object.
 *
 * @param image     address to hold image handle 
 * @param fileName  name of file to open
 * @return status code indicating success or failure
 */
LT_STATUS ltic_openJP2ImageFile(LTICImageH* image,
                                const char* fileName);

/**
 * open JPEG 2000 image via stream (C API)
 *
 * Given a C stream containing a JPEG 2000 image, this function will open the image
 * and return a handle which can be used to access image information and
 * perform decodes.
 *
 * This method is part of the C API.  It is equivalent to constructing
 * a J2KImageReader object.
 *
 * @param image     address to hold image handle 
 * @param stream    C stream handle
 * @return status code indicating success or failure
 */
LT_STATUS ltic_openJP2ImageStream(LTICImageH* image,
                                  LTIOStreamH stream);

/**
 * open NITF image via filename (C API)
 *
 * Given the filename of a NITF image, this function will open the image
 * and return a handle which can be used to access image information and
 * perform decodes.
 *
 * This method is part of the C API.  It is equivalent to constructing
 * a NITFImageReader (and NITFImageManager) object.
 *
 * @param image     address to hold image handle 
 * @param fileName  name of file to open
 * @return status code indicating success or failure
 */
LT_STATUS ltic_openNITFImageFile(LTICImageH* image,
                                 const char* fileName);

/**
 * close an image (C API)
 *
 * This function will close the given image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * the LTIImage destructor.
 *
 * @param image     image to be closed
 * @return status code indicating success or failure
 */
LT_STATUS ltic_closeImage(LTICImageH image);

/**
 * get image width (C API)
 *
 * This function will return the width of the given image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getWidth().
 *
 * @param   image     image to query
 * @return  width in pixels
 */
lt_uint32 ltic_getWidth(const LTICImageH image);

/**
 * get image height (C API)
 *
 * This function will return the height of the given image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getHeight().
 *
 * @param   image     image to query
 * @return  height of image
 */
lt_uint32 ltic_getHeight(const LTICImageH image);

/**
 * get dimensions of an image at a given magnification (C API)
 *
 * This function returns the projected dimensions of an image at a given
 * magnification level.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getDimsAtMag()
 */
LT_STATUS ltic_getDimsAtMag(LTICImageH image,
                            double magnification,
                            lt_uint32 *width,
                            lt_uint32 *height);

/**
 * get image colorspace (C API)
 *
 * This function will return the colorspace of the given image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getColorSpace().
 *
 * @param   image     image to query
 * @return  colorspace of image
 */
LTIColorSpace ltic_getColorSpace(const LTICImageH image);

/**
 * get number of bands in trhe image (C API)
 *
 * This function will return the numbe rof bands in the given image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getNumBands().
 *
 * @param   image     image to query
 * @return  number of bands in the image
 */
lt_uint16 ltic_getNumBands(const LTICImageH image);

/**
 * get image datatype (C API)
 *
 * This function will return the datatype of the pixels of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getDataType().
 *
 * @param   image     image to query
 * @return  datatype of the image
 */
LTIDataType ltic_getDataType(const LTICImageH image);

/**
 * get image minimum magnifaction (C API)
 *
 * This function will return the minimum magnification of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getMinMagnification().
 *
 * @param   image     image to query
 * @return  minimum magnification of image
 */
double ltic_getMinMagnification(const LTICImageH image);

/**
 * get image maximum magnifacation (C API)
 *
 * This function will return the maximum magnification of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getMaxMagnification().
 *
 * @param   image     image to query
 * @return  maximum magnification of image
 */
double ltic_getMaxMagnification(const LTICImageH image);

/**
 * query if MrSID image is locked (C API)
 *
 * This function will return whether or not the given image is
 * locked, i.e. password protected.  The \a image handle must
 * be a MrSID image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * MrSIDImageReader::isLocked().
 *
 * @param   image     MrSID image to query
 * @return  1 if locked, otherwise 0
 */
lt_uint8 ltic_isMrSIDLocked(const LTICImageH image);

/**
 * set password for decoding MrSID image (C API)
 *
 * This function will set the passward used to decode a locked MrSID
 * image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * MrSIDImageReader::setPassword().
 *
 * @param   image     MrSID image to unlock
 * @param   passwd    password string for image
 * @return  success or failure status code
 */
LT_STATUS ltic_setMrSIDPassword(LTICImageH image, const lt_utf8* passwd);

/**
 * get geo X position of image (C API)
 *
 * This function will return the upper-left X position of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getX().
 *
 * @param   image     image to query
 * @return  upperleft X geo position
 */
double ltic_getGeoXOrigin(const LTICImageH image);

/**
 * get geo Y position of image (C API)
 *
 * This function will return the upper-left Y position of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getY().
 *
 * @param   image     image to query
 * @return  upperleft Y geo position
 */
double ltic_getGeoYOrigin(const LTICImageH image);

/**
 * get geo X resolution of image (C API)
 *
 * This function will return the X resolution of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getXRes().
 *
 * @param   image     image to query
 * @return  X resolution
 */
double ltic_getGeoXResolution(const LTICImageH image);

/**
 * get geo Y resolution of image (C API)
 *
 * This function will return the Y resolution of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getYRes().
 *
 * @param   image     image to query
 * @return  Y resolution
 */
double ltic_getGeoYResolution(const LTICImageH image);

/**
 * get geo X rotation term of image (C API)
 *
 * This function will return the X rotation term of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getXRot().
 *
 * @param   image     image to query
 * @return  X rotation
 */
double ltic_getGeoXRotation(const LTICImageH image);

/**
 * get geo Y rotation term of image (C API)
 *
 * This function will return the Y rotation term of the image.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getGeoCoord().getYRot().
 *
 * @param   image     image to query
 * @return  Y rotation
 */
double ltic_getGeoYRotation(const LTICImageH image);

/**
 * decode a scene from the image (C API)
 *
 * This function decodes a scene from the image.  The output
 * is written to the given band buffers in packed form.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImageStage::read().  The \a xUpperLeft, \a yUpperLeft,
 * \a width, \a height, and \a magnification parameters correspond
 * to the LTIScene used in the read() call.
 *
 * @param   image     image to decode
 * @param   xUpperLeft upperleft x position of scene
 * @param   yUpperLeft upperleft y position of scene
 * @param   width      width of scene
 * @param   height     height of scene
 * @param   magnification manification of scene
 * @param   buffers   array of buffers to write to, one per band
 * @return status code indicating success or failure
 */
LT_STATUS ltic_decode(LTICImageH image,
                      double xUpperLeft,
                      double yUpperLeft,
                      double width,
                      double height,
                      double magnification,
                      void** buffers);
   
/**
 * get number of metadata records (C API)
 *
 * This function returns the number of metadata records in the
 * image.  This number defines the range of values used with
 * ltic_metadataRecord().
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getMetadata().getIndexCount().
 *
 * @param   image     image to query
 * @return  number of records in image
 */
lt_uint32 ltic_getNumMetadataRecords(LTICImageH image);

/**
 * get a metadata record from an image (C API)
 *
 * This function returns the data contained in a given metadata
 * record.
 *
 * This method is part of the C API.  It is equivalent to calling
 * LTIImage::getMetadata().getDataByIndex().
 *
 * @param   image     image to query
 * @param   recordNum  index of record to retrieve
 * @param   tag       address to hold name of metadata tag
 * @param   datatype  address to hold datatype of record
 * @param   numDims   address to hold number of dimension in record data
 * @param   dims      address to record's dimesnion arrays
 * @param   data      address to record's data
 * @return status code indicating success or failure
 */
LT_STATUS ltic_getMetadataRecord(LTICImageH image,
                                 lt_uint32 recordNum,
                                 const char** tag,
                                 LTIMetadataDataType* datatype,
                                 lt_uint32* numDims,
                                 const lt_uint32** dims,
                                 const void** data);

#ifdef LT_CPLUSPLUS
}
#endif

#if defined(LT_COMPILER_MS)
	#pragma warning(pop)
#endif

#endif
