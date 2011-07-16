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

#ifndef LTI_GEOCOORD_H
#define LTI_GEOCOORD_H

// lt_lib_mrsid_core
#include "lti_types.h"

#include <stdlib.h>

LT_BEGIN_NAMESPACE(LizardTech)

/**
 * represents a geographic coordinate
 *
 * The LTIGeoCoord class contains geographic coordinate information: x and
 * y resolution, upper left point, and x and y rotation.
 *
 * @note As per the conventions for georeferenced images, the Y resolution is
 * typically a negative value.
 */
class LTIGeoCoord
{
public:
   /**
    * constructor
    *
    * Creates an LTIGeoCoord object with the given coordinate information.
    *
    * @param xUL   upper-left x position
    * @param yUL   upper-left y position
    * @param xRes  x resolution
    * @param yRes  y resolution
    * @param xRot  x rotation
    * @param yRot  y rotation
    * @param wkt   Well Known Text string
    */
   LTIGeoCoord(double xUL, double yUL,
               double xRes, double yRes,
               double xRot, double yRot,
               const char *wkt = NULL);

   /**
    * default constructor
    */
   LTIGeoCoord();

   /**
    * copy constructor
    */
   LTIGeoCoord(const LTIGeoCoord&);

  /**
    * destructor
    */
   ~LTIGeoCoord();

   /**
    * assignment operator
    */
   LTIGeoCoord& operator=(const LTIGeoCoord&);

   /**
    * equality operator
    */
   bool operator==(const LTIGeoCoord&) const;

   /**
    * inequality operator
    */
   bool operator!=(const LTIGeoCoord&) const;

   /**
    * get the upper-left X position
    *
    * Returns the upper-left X position.
    *
    * @return the upper-left X position
    */
   double getX() const;

   /**
    * get the upper-left Y position
    *
    * Returns the upper-left Y position.
    *
    * @return the upper-left Y position
    */
   double getY() const;

   /**
    * get the X resolution
    *
    * Returns the X resolution
    *
    * @return the X resolution
    */
   double getXRes() const;

   /**
    * get the Y resolution
    *
    * Returns the Y resolution
    *
    * @return the Y resolution
    */
   double getYRes() const;

   /**
    * get the X rotation
    *
    * Returns the X rotation
    *
    * @return the X rotation
    */
   double getXRot() const;

   /**
    * get the Y rotation
    *
    * Returns the Y rotation
    *
    * @return the Y rotation
    */
   double getYRot() const;

   /**
    * get the Well Known Text (WKT)
    *
    * Returns the WKT
    *
    * @return the WKT
    */
   const char *getWKT() const;



   /**
    * get the coordinate information
    *
    * Returns all the coordinate information.  This is equivalent to calling
    * getXUL(), getXRes(), etc.
    *
    * @param xUL   upper-left x position
    * @param yUL   upper-left y position
    * @param xRes  x resolution
    * @param yRes  y resolution
    * @param xRot  x rotation
    * @param yRot  y rotation
    */
   void get(double& xUL, double& yUL,
            double& xRes, double& yRes,
            double& xRot, double& yRot) const;

   /**
    * convert a pixel coordinate to geo coordinate
    *
    * @param pixelX  x pixel position
    * @param pixelY  y pixel position
    * @param mag     magnification level (pixelX, pixelY) are in
    * @param geoX    x geo position
    * @param geoY    y geo position
    * @return        status code indicating success or failure
    */
   LT_STATUS pixelToGeo(double pixelX, double pixelY, double mag,
                        double &geoX, double &geoY) const;

   /**
    * convert a geo coordinate to pixel coordinate
    *
    * @param geoX    x geo position
    * @param geoY    y geo position
    * @param mag     magnification level (pixelX, pixelY) will be in
    * @param pixelX  x pixel position
    * @param pixelY  y pixel position
    * @return        status code indicating success or failure
    */
   LT_STATUS geoToPixel(double geoX, double geoY, double mag,
                        double &pixelX, double &pixelY) const;

   /**
    * get the coordinate information for a given scene
    *
    * Return a LTIGeoCoord object for the center of the upper-left
    *  pixel of the given scene.
    *
    * @param scene scene
    * @return a LTIGeoCoord object for scene
    */
   LTIGeoCoord getGeoCoordForScene(const LTIScene &scene) const;

   /**
    * set the upper-left X position
    *
    * Sets the upper-left X position.
    *
    * @param x the upper-left X position
    */
   void setX(double x);

   /**
    * set the upper-left Y position
    *
    * Sets the upper-left Y position.
    *
    * @param y the upper-left Y position
    */
   void setY(double y);

   /**
    * set the X resolution
    *
    * Sets the X resolution
    *
    * @param xRes the X resolution
    */
   void setXRes(double xRes);

   /**
    * set the Y resolution
    *
    * Sets the Y resolution
    *
    * @param yRes the Y resolution
    */
   void setYRes(double yRes);

   /**
    * set the X rotation
    *
    * Sets the X rotation
    *
    * @param xRot the X rotation
    */
   void setXRot(double xRot);

   /**
    * set the Y rotation
    *
    * Sets the Y rotation
    *
    * @param yRot the Y rotation
    */
   void setYRot(double yRot);

   /**
    * set the coordinate information
    *
    * Sets the coordinate information.  This is equivalent to calling
    * setX(), setXRes(), etc.
    *
    * @param xUL   upper-left x position
    * @param yUL   upper-left y position
    * @param xRes  x resolution
    * @param yRes  y resolution
    * @param xRot  x rotation
    * @param yRot  y rotation
    */
   void set(double xUL, double yUL,
            double xRes, double yRes,
            double xRot, double yRot);

   /**
    * set the WKT
    *
    * Sets the WKT
    *
    * @param wkt the WKT
    */
   void setWKT(const char *wkt);

   /**
    * write data to world file
    *
    * This function writes the geographic data out in "world file" format
    * to the specified file.  If \a extension is not NULL, the extension
    * will be used to replace the extension in the filename.
    *
    * @param fileSpec the name of the world file to write to
    * @param   determineExtension  if true, replace extension in \a fileSpec; otherwise, just use \a fileSpec
    * @return status code indicating success or failure
    */
   LT_STATUS writeWorldFile(const LTFileSpec& fileSpec,
                            bool determineExtension) const;

   /**
    * read data from world file
    *
    * This function reads the geographic data in from the given "world file", if file present.  If no world
    * file found, \a fileFound set to false and returns LT_STS_Success.
    *
    * @param   fileSpec   the name of the world file to read from
    * @param   determineExtension  if true, replace extension in \a fileSpec; otherwise, just use \a fileSpec
    * @param   fileFound  will be if true, if world file was found and read
    * @return  status code indicating success or failure
    */
   LT_STATUS readWorldFile(const LTFileSpec& fileSpec,
                           bool determineExtension,
                           bool& fileFound);

   /**
    * read data from world file, via a stream
    *
    * This function reads the geographic data in from the given "world file",
    * using an LTIOStreamInf interface.
    *
    * @param   stream   the world file data read from
    * @return  status code indicating success or failure
    */
   LT_STATUS readWorldFile(LTIOStreamInf& stream);

   /**
    * get file extension text
    *
    * This function computes the "official" world file extension for the
    * given image file name.  Typically this is formed by concatenating the
    * first and last letters of the image file extension and a 'w', e.g.
    * "foo.jpg" returns "jgw".
    *
    * If the first and last characters of the extension are both uppercase,
    * then the 'W' is uppercase, e.g. "foo.JPG" return "JPW".
    *
    * The \c ext parameter must be allocated to be at least 4 bytes prior to
    * the call.
    *
    * @param   fileSpec the name of the image file
    * @param   ext       an array of (at least) 4 bytes
    * @return  status code indicating success or failure
    */
   static LT_STATUS getWorldFileExtension(const LTFileSpec& fileSpec,
                                          char* ext);


   /**
    * write data to metadata
    *
    * This function writes the geographic data out to the metadata.
    *
    * @param metadata the metadata database to write to
    * @return status code indicating success or failure
    */
   LT_STATUS writeMetadata(LTIMetadataDatabase &metadata) const;

   /**
    * read data from metadata
    *
    * This function reads the geographic data in from the given metadata database.
    *
    * @param   metadata   the metadata database to read from
    * @param   found  will be if true, if metadata has geographic data
    * @return  status code indicating success or failure
    */
   LT_STATUS readMetadata(const LTIMetadataDatabase &metadata, bool &found);

private:
   double m_xUL;
   double m_yUL;
   double m_xRes;
   double m_yRes;
   double m_xRot;
   double m_yRot;

   char *m_wkt;
};

LT_END_NAMESPACE(LizardTech)


#endif // LTI_GEOCOORD_H
