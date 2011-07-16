/* $Id$ */
/* //////////////////////////////////////////////////////////////////////////
//                                                                         //
// This code is Copyright (c) 2005 LizardTech, Inc, 1008 Western Avenue,   //
// Suite 200, Seattle, WA 98104.  Unauthorized use or distribution         //
// prohibited.  Access to and use of this code is permitted only under     //
// license from LizardTech, Inc.  Portions of the code are protected by    //
// US and foreign patents and other filings. All Rights Reserved.          //
//                                                                         //
////////////////////////////////////////////////////////////////////////// */     
/* PUBLIC */

#ifndef LTI_GEOMETADATASTATUS_H
#define LTI_GEOMETADATASTATUS_H

#include "lt_base.h"

#define LTI_STS_GeoMetadata_Base                        51300
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_Base, "lt_lib_mrsid_geoMetadata base")

#define LTI_STS_GeoMetadata_BadTIFFIFDRead              51301
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_BadTIFFIFDRead, "bad TIFF IFD read")

#define LTI_STS_GeoMetadata_BadTIFFIFDWrite             51302
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_BadTIFFIFDWrite, "bad TIFF IFD write")

#define LTI_STS_GeoMetadata_BadGeoTIFFDir               51303
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_BadGeoTIFFDir, "bad GeoTIFF Directory")

#define LTI_STS_GeoMetadata_InvalidSRS                  51304
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_InvalidSRS, "invalid SRS")

#define LTI_STS_GeoMetadata_InvalidDOQMetadata          51305
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_InvalidDOQMetadata, "invalid DOQ metadata")

#define LTI_STS_GeoMetadata_GDALDATANotSet              51306
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_GDALDATANotSet, "GDAL_DATA not set")

#define LTI_STS_GeoMetadata_PROJSONotSet                51307
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_PROJSONotSet, "PROJSO not set")

#define LTI_STS_GeoMetadata_NITFMGRSError               51308
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_NITFMGRSError, "MGRS conversion error")

#define LTI_STS_GeoMetadata_NITFUnsupIGEOLO             51309
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_NITFUnsupIGEOLO, "unsupported IGEOLO setting")

#define LTI_STS_GeoMetadata_CouldNotCreateTransformer   51310
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_CouldNotCreateTransformer, "could not create coordinate transformer")

#define LT_STS_GeoMetadata_ReprojectionErr              51311
LT_STATUSSTRING_ADD(LT_STS_GeoMetadata_ReprojectionErr, "coordinate reprojection error")

#define LTI_STS_GeoMetadata_InvalidWKT                  51312
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_InvalidWKT, "invalid WKT")

#define LTI_STS_GeoMetadata_Max                         51399
LT_STATUSSTRING_ADD(LTI_STS_GeoMetadata_Max, "lt_lib_mrsid_geoMetadata max")



#endif // LTI_GEOMETADATASTATUS_H
