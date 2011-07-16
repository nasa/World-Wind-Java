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

#ifndef LTI_METADATATYPES_H
#define LTI_METADATATYPES_H

#include "lt_base.h"

#if defined(LT_COMPILER_MS)
   #pragma warning(push,4)
#endif

#ifdef LT_CPLUSPLUS
extern "C" {
#endif

/**
 * enums corresponding to tag strings
 *
 * These constants are enums that may be used in place of the text strings
 * used as tag names.
 */
typedef enum LTIMetadataTag
{
#ifndef DOXYGEN_EXCLUDE
   LTI_METADATA_TAG_INVALID                              = 0,
   LTI_METADATA_TAG_Unknown                              = 1,
   LTI_METADATA_TAG_TestScalar                           = 2,
   LTI_METADATA_TAG_TestVector                           = 3,
   LTI_METADATA_TAG_TestArray                            = 4,

   LTI_METADATA_TAG_IMAGE__SOM                           = 10,    /* short */
   LTI_METADATA_TAG_IMAGE__EOM                           = 11,    /* short */

   LTI_METADATA_TAG_IMAGE__INPUT_FORMAT                  = 100,   /* ascii */
   LTI_METADATA_TAG_IMAGE__CREATION_DATE                 = 101,   /* ascii */
   LTI_METADATA_TAG_IMAGE__INPUT_NAME                    = 102,   /* ascii */
   LTI_METADATA_TAG_IMAGE__NO_DATA_VALUE                 = 103,   /* byte */
   LTI_METADATA_TAG_IMAGE__INPUT_LUT                     = 104,   /* byte */
   LTI_METADATA_TAG_IMAGE__NO_DITHER                     = 105,   /* byte */
   LTI_METADATA_TAG_IMAGE__INPUT_FILE_SIZE               = 106,   /* double */
   LTI_METADATA_TAG_IMAGE__DYNAMIC_RANGE_WINDOW          = 107,   /* double */
   LTI_METADATA_TAG_IMAGE__DYNAMIC_RANGE_LEVEL           = 108,   /* double */
   LTI_METADATA_TAG_IMAGE__TARGET_COMPRESSION_RATIO      = 109,   /* float */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_WEIGHT            = 110,   /* float */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_KWEIGHT           = 111,   /* float */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_GAMMA             = 112,   /* float */
   LTI_METADATA_TAG_IMAGE__WIDTH                         = 113,   /* long */
   LTI_METADATA_TAG_IMAGE__HEIGHT                        = 114,   /* long */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_BLOCK_SIZE        = 115,   /* long */
   LTI_METADATA_TAG_IMAGE__COLOR_SCHEME                  = 116,   /* long */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_NLEV              = 117,   /* long */
   LTI_METADATA_TAG_IMAGE__COMPRESSION_VERSION           = 118,   /* slong */
   LTI_METADATA_TAG_IMAGE__DEFAULT_DATA_VALUE            = 119,   /* float or short or byte */
   LTI_METADATA_TAG_IMAGE__TRANSPARENT_DATA_VALUE        = 120,   /* float or short or byte */
   LTI_METADATA_TAG_IMAGE__FILE_IS_LOCKED                = 121,   /* byte */
   LTI_METADATA_TAG_IMAGE__KEY                           = 122,   /* byte */
   LTI_METADATA_TAG_IMAGE__SIGNATURE                     = 123,   /* byte */
   LTI_METADATA_TAG_IMAGE__XY_ORIGIN                     = 124,   /* double */
   LTI_METADATA_TAG_IMAGE__Z_ORIGIN                      = 125,   /* double */
   LTI_METADATA_TAG_IMAGE__X_RESOLUTION                  = 126,   /* double */
   LTI_METADATA_TAG_IMAGE__Y_RESOLUTION                  = 127,   /* double */
   LTI_METADATA_TAG_IMAGE__Z_RESOLUTION                  = 128,   /* double */
   LTI_METADATA_TAG_IMAGE__X_ROTATION                    = 129,   /* double */
   LTI_METADATA_TAG_IMAGE__BITS_PER_SAMPLE               = 130,   /* short */
   LTI_METADATA_TAG_IMAGE__HORIZONTAL_UNITS              = 131,   /* short */
   LTI_METADATA_TAG_IMAGE__VERTICAL_UNITS                = 132,   /* short */
   LTI_METADATA_TAG_IMAGE__DATA_TYPE                     = 133,   /* long */
   LTI_METADATA_TAG_IMAGE__Z_UNITS                       = 134,   /* (unknown type) */
   LTI_METADATA_TAG_IMAGE__Y_ROTATION                    = 135,   /* double */
   LTI_METADATA_TAG_IMAGE__WKT                           = 137,   /* ascii */
   LTI_METADATA_TAG_IMAGE__ENCODING_APPLICATION          = 138,   /* ascii */
   LTI_METADATA_TAG_IMAGE__LTI_ESDK_VERSION              = 139,   /* ascii */

   LTI_METADATA_TAG_GEO__PCSCitationGeoKey               = 200,   /* ascii */
   LTI_METADATA_TAG_GEO__HORIZONTAL_DATUM                = 201,   /* short */
   LTI_METADATA_TAG_GEO__ModelTypeGeoKey                 = 202,   /* short */
   LTI_METADATA_TAG_GEO__ProjectedCSTypeGeoKey           = 203,   /* short */
   LTI_METADATA_TAG_GEO__HORIZONTAL_COORDSYSTEMZONE      = 204,   /* (unknown type) */

   LTI_METADATA_TAG_USGS__QUADCOMMENT                    = 300,   /* ascii */
   LTI_METADATA_TAG_USGS__QUADRECT                       = 301,   /* double */

   LTI_METADATA_TAG_IMG__CLASS_NAME                      = 400,   /* ascii */
   LTI_METADATA_TAG_IMG__HORIZONTAL_UNITS                = 401,   /* ascii */
   LTI_METADATA_TAG_IMG__TIME_STAMP                      = 402,   /* ascii */
   LTI_METADATA_TAG_IMG__PROJECTION_NAME                 = 403,   /* ascii */
   LTI_METADATA_TAG_IMG__PROJECTION_TYPE                 = 404,   /* ascii */
   LTI_METADATA_TAG_IMG__PROJECTION_PARAMETERS           = 405,   /* ascii */
   LTI_METADATA_TAG_IMG__SPHEROID_NAME                   = 406,   /* ascii */
   LTI_METADATA_TAG_IMG__BYTEORDER                       = 407,   /* ascii */

   LTI_METADATA_TAG_IMG__SPHEROID_SEMI_MAJOR_AXIS        = 408,   /* double */
   LTI_METADATA_TAG_IMG__SPHEROID_SEMI_MINOR_AXIS        = 409,   /* double */
   LTI_METADATA_TAG_IMG__SPHEROID_ECCENTRICITY_SQUARED   = 410,   /* double */
   LTI_METADATA_TAG_IMG__SPHEROID_RADIUS                 = 411,   /* double */
   LTI_METADATA_TAG_IMG__STATISTICS_MEAN                 = 412,   /* double */
   LTI_METADATA_TAG_IMG__STATISTICS_MEDIAN               = 413,   /* double */
   LTI_METADATA_TAG_IMG__STATISTICS_MODE                 = 414,   /* double */
   LTI_METADATA_TAG_IMG__STATISTICS_STDDEV               = 415,   /* double */

   LTI_METADATA_TAG_IMG__PROJECTION_NUMBER               = 416,   /* short */
   LTI_METADATA_TAG_IMG__PROJECTION_ZONE                 = 417,   /* short */

   LTI_METADATA_TAG_ICC__Profile                         = 500,   /* (unknown type) */
   LTI_METADATA_TAG_PShop__ImageResources                = 501,   /* (unknown type) */

   LTI_METADATA_TAG_USER__COMPANYNAME                    = 600,   /* ascii */
   LTI_METADATA_TAG_USER__COPYRIGHT                      = 601,   /* ascii */
   LTI_METADATA_TAG_USER__CREDIT                         = 602,   /* ascii */
   LTI_METADATA_TAG_USER__SUMMARY                        = 603,   /* ascii */
   LTI_METADATA_TAG_USER__KEYWORDS                       = 604,   /* ascii */
   LTI_METADATA_TAG_USER__COMMENTS                       = 605,   /* ascii */
   LTI_METADATA_TAG_USER__IMAGEID                        = 606,   /* ascii */
   LTI_METADATA_TAG_USER__IMAGINGDATE                    = 607,   /* ascii */
   LTI_METADATA_TAG_USER__IMAGINGTIME                    = 608,   /* ascii */
   LTI_METADATA_TAG_USER__SOURCEDEVICE                   = 609,   /* ascii */
   LTI_METADATA_TAG_USER__SCANINFO                       = 610,   /* ascii */
   LTI_METADATA_TAG_USER__GEOGRAPHICLOCATION             = 611,   /* ascii */

   LTI_METADATA_TAG_ASCII__COMMENT                       = 700,   /* ascii */

   LTI_METADATA_TAG_GDAL__COORDINATE_SYSTEM              = 800,   /* 6 doubles */

   LTI_METADATA_TAG_IMAGE__STATISTICS_MIN                = 900,   /* vector (pixel type) */
   LTI_METADATA_TAG_IMAGE__STATISTICS_MAX                = 901,   /* vector (pixel type) */
   LTI_METADATA_TAG_IMAGE__STATISTICS_MEAN               = 902,   /* double vector */
   LTI_METADATA_TAG_IMAGE__STATISTICS_STANDARD_DEVIATION = 903,   /* double vector */

   LTI_METADATA_TAG_LAST                                 = 10000
#endif
} LTIMetadataTag;


/**
 * datatypes used in database records
 *
 * These enums are used to represent the datatypes of values stored
 * in LTIMetadataRecords.
 */
typedef enum LTIMetadataDataType
{
  LTI_METADATA_DATATYPE_INVALID           =   0,

  LTI_METADATA_DATATYPE_UINT8             =   1,
  LTI_METADATA_DATATYPE_SINT8             =   2,
  LTI_METADATA_DATATYPE_UINT16            =   3,
  LTI_METADATA_DATATYPE_SINT16            =   4,
  LTI_METADATA_DATATYPE_UINT32            =   5,
  LTI_METADATA_DATATYPE_SINT32            =   6,
  LTI_METADATA_DATATYPE_UINT64            =   7,
  LTI_METADATA_DATATYPE_SINT64            =   8,
  LTI_METADATA_DATATYPE_FLOAT32           =   9,
  LTI_METADATA_DATATYPE_FLOAT64           =  10,
  LTI_METADATA_DATATYPE_ASCII             =  11,
  
  LTI_METADATA_TYPE_LAST                  = 0xffffffff
} LTIMetadataDataType;


typedef enum LTIClassicalMetadataConst
{
   LTI_CLASSICAL_METADATA_VERSION_MAJOR = 1,
   LTI_CLASSICAL_METADATA_VERSION_MINOR = 0,
   LTI_CLASSICAL_METADATA_MAX_KEY_NAME_SIZE = 100,
   LTI_CLASSICAL_METADATA_MAX_DIMENSIONS = 10
} LTIClassicalMetadataConst;


#ifdef LT_CPLUSPLUS
}
#endif

#if defined(LT_COMPILER_MS)
   #pragma warning(pop)
#endif

#endif
