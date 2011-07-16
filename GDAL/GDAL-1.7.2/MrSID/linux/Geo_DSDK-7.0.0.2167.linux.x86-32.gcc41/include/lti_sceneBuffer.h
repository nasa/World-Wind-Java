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

#ifndef LTI_SCENEBUFFER_H
#define LTI_SCENEBUFFER_H

// lt_lib_mrsid_core
#include "lti_types.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * class to hold data passed between image stages
 *
 * This class holds a buffer of data which is used as the target of decode
 * operations in LTIImageStage.
 *
 * The data within the buffer is always represented as an array of bytes in
 * BSQ (band-sequential) format.  Each band is stored separately, so that the
 * underlying data is an array of N pointers, each array element being a
 * buffer for one band of the image.
 *
 * The dimensions of the buffer are set in the constructor.  The \b total
 * number of rows and columns represents the actual extent of the data array
 * in memory.  However, it is often desirable to only expose a subset of the
 * full rectangle, e.g. to access a large buffer in a stripwise fashion or to
 * overlay a small image into a large buffer.  This \b window may also be set
 * via the constructor, by providing a second set of row/column dimensions
 * and giving an offset for the upper-left position of the window.
 *
 * If the data pointer passed to the constructor is NULL, the class will
 * internally allocate the required memory (and retain ownership of it).
 *
 * Functions are provided to access that data within the buffer in a variety
 * of ways, relative to both the total buffer and the exposed window within
 * it.  You may also construct a buffer which is relative to another buffer.
 *
 * For convenience, a number of functions are also provided which allow the
 * user to copy data to and from an LTISceneBuffer object, using a variety of
 * formats.  For example, there are functions to import and export the data
 * in the buffer to BIP (band-interleaved) format.
 *
 * @note The pixel properties of the LTISceneBuffer must exactly match the
 * pixel properties of the image being decoded.
 */
class LTISceneBuffer
{
   LT_DISALLOW_COPY_CONSTRUCTOR(LTISceneBuffer);
public:
   /**
    * @name Constructors and destructor
    */
   /*@{*/

   /**
    * constructor with default window
    *
    * Constructs an LTISceneBuffer with the window set to the total region of
    * the buffer.
    *
    * The \c data parameter may be NULL, in which case the memory will be
    * allocated internally.
    *
    * @param  pixelProps    pixel type to be used in the buffer
    * @param  totalNumCols  width of the buffer
    * @param  totalNumRows  height of the buffer
    * @param  data          pointer to the data array (may be NULL)
    */
   LTISceneBuffer(const LTIPixel& pixelProps,
                  lt_uint32 totalNumCols,
                  lt_uint32 totalNumRows,
                  void** data);

   /**
    * constructor with explicit window
    *
    * Constructs an LTISceneBuffer with the window set to the given size and
    * positioned at the given offset.  The offset is given relative to the
    * total region, and the window must lie entirely within the region.
    *
    * The \c data parameter may be NULL, in which case the memory will be
    * allocated internally.
    *
    * @param  pixelProps     pixel type to be used in the buffer
    * @param  totalNumCols   width of the buffer
    * @param  totalNumRows   height of the buffer
    * @param  colOffset      x-position of the window
    * @param  rowOffset      y-position of the window
    * @param  windowNumCols  width of the window
    * @param  windowNumRows  height of the window
    * @param  data           pointer to the data array (may be NULL)
    */
   LTISceneBuffer(const LTIPixel& pixelProps,
                  lt_uint32 totalNumCols,
                  lt_uint32 totalNumRows,
                  lt_uint32 colOffset,
                  lt_uint32 rowOffset,
                  lt_uint32 windowNumCols,
                  lt_uint32 windowNumRows,
                  void** data);

   /**
    * constructor to overlay existing LTISceneBuffer
    *
    * Constructs an LTISceneBuffer which is a window into the given existing
    * LTISceneBuffer object.  The window of the new buffer is set to cover the
    * full window of the original buffer, starting at the given offset
    * (which is relative to the window of the original buffer).
    *
    * @param  original   the existing buffer, to be overlaid
    * @param  colOffset  x-position of the new window
    * @param  rowOffset  y-position of the new window
    */
   LTISceneBuffer(const LTISceneBuffer& original,
                  lt_uint32 colOffset,
                  lt_uint32 rowOffset);

   /**
    * constructor to overlay existing LTISceneBuffer
    *
    * Constructs an LTISceneBuffer which is a window into the given existing
    * LTISceneBuffer object.  The window of the new buffer is set to start at
    * the given offset (which is relative to the window of the original
    * buffer).  The dimensions of the new window are passed in, and the new
    * window must not extend beyond the dimensions of the original buffer.
    *
    * @param  original   the existing buffer, to be overlaid
    * @param  colOffset  x-position of the new window
    * @param  rowOffset  y-position of the new window
    * @param  windowNumCols  width of the window
    * @param  windowNumRows  height of the window
    */
   // exposes only a window of a different bsq buffer; window may not
   // extend outside of parent window; offset is relative to parent
   // window
   LTISceneBuffer(const LTISceneBuffer& original,
                  lt_uint32 colOffset,
                  lt_uint32 rowOffset,
                  lt_uint32 windowNumCols,
                  lt_uint32 windowNumRows);

   /** destructor */
   ~LTISceneBuffer();

   /*@}*/


   /**
    * @name Buffer property accessors
    */
   /*@{*/

   /**
   * get width of (entire) buffer
   *
   * Returns the total width of the buffer.
   *
   * @return the total width, in pixels
   */
   lt_int32 getTotalNumCols() const;

   /**
   * get height of (entire) buffer
   *
   * Returns the total height of the buffer.
   *
   * @return the total height, in pixels
   */
   lt_int32 getTotalNumRows() const;

   /**
   * get x-position of window
   *
   * Returns the x-position of the exposed window of the buffer.
   *
   * @return the x-position, relative to the whole buffer
   */
   lt_int32 getWindowColOffset() const;

   /**
   * get y-position of window
   *
   * Returns the y-position of the exposed window of the buffer.
   *
   * @return the y-position, relative to the whole buffer
   */
   lt_int32 getWindowRowOffset() const;

   /**
   * get width of exposed window
   *
   * Returns the width of the exposed window of the buffer.
   *
   * @return the window width, in pixels
   */
   lt_int32 getWindowNumCols() const;

   /**
   * get height of exposed window
   *
   * Returns the height of the exposed window of the buffer.
   *
   * @return the window height, in pixels
   */
   lt_int32 getWindowNumRows() const;

   /**
   * get size of (total) buffer
   *
   * Returns the total size of the buffer, in pixels.
   *
   * This is equal to getTotalNumCols() * getTotalNumRows().
   *
   * @return size of buffer
   */
   lt_int32 getTotalNumPixels() const;

   /**
   * get size of exposed window
   *
   * Returns the total size of the window of the buffer, in pixels.
   *
   * This is equal to getWindowNumCols() * getWindowNumRows().
   *
   * @return size of window in buffer
   */
   lt_int32 getWindowNumPixels() const;

   /**
   * get pixel type of buffer
   *
   * Returns the pixel type of the buffer.
   *
   * @return the pixel type
   */
   const LTIPixel& getPixelProps() const;

   /**
   * get number of bands
   *
   * Returns the number of bands of the pixel of the imager.
   *
   * This is the same as calling getPixelProps().getNumBands().
   *
   * @return the number of bands
   */
   lt_uint16 getNumBands() const;

   /*@}*/


   /**
    * @name Data buffer accessors
    */
   /*@{*/

   /**
    * get pointer to total buffer (for all bands)
    *
    * This function returns a pointer to the array of data buffers, one per
    * band.  This returns the "base" pointer for the total region, regardless
    * of the window setting.
    *
    * @return  a pointer to the array of data buffers
    */
   void** getTotalBSQData() const;

   /**
    * get pointer to total buffer (for 1 band)
    *
    * This function returns a pointer to the data buffer for the given band.
    * This returns the "base" pointer for the total region, regardless of the
    * window setting.
    *
    * @param   band  the band to access
    * @return  a pointer to the data buffer
    */
   void* getTotalBandData(lt_uint16 band) const;

   /**
    * get pointer to window data (for all bands)
    *
    * This function returns a pointer to the array of data buffers, one per
    * band.  The buffer pointers are set to the start of the window within
    * the total buffer.
    *
    * @return  a pointer to the array of data buffers
    */
   void** getWindowBSQData() const;

   /**
    * get pointer to window data (for 1 band)
    *
    * This function returns a pointer to the data buffer for the given band.
    * The buffer pointer is set to the start of the window within
    * the total buffer.
    *
    * @param   band  the band to access
    * @return  a pointer to the array of data buffers
    */
   void* getWindowBandData(lt_uint16 band) const;

   /**
    * get pointer to sample (total buffer)
    *
    * This function returns a pointer to the data for the given band of the
    * specified pixel.  The offset is relative to the total buffer.
    *
    * @param   x     the x-position of the pixel
    * @param   y     the y-position of the pixel
    * @param   band  the band to access
    * @return  a pointer to the sample
    */
   void* getTotalSample(lt_uint32 x, lt_uint32 y, lt_uint16 band) const;

   /**
    * get pointer to sample (windowed)
    *
    * This function returns a pointer to the data for the given band of the
    * specified pixel.  The offset is relative to the window into the buffer.
    *
    * @param   x     the x-position of the pixel
    * @param   y     the y-position of the pixel
    * @param   band  the band to access
    * @return  a pointer to the sample
    */
   void* getWindowSample(lt_uint32 x, lt_uint32 y, lt_uint16 band) const;
   /*@}*/


   /**
    * query pixel position
    *
    * This function returns true if and only if the given position lies
    * within the exposed window of the buffer.  (The position is relative
    * to the total buffer.)
    *
    * @param   x     the x-position of the pixel
    * @param   y     the y-position of the pixel
    */
   bool inWindow(lt_uint32 x, lt_uint32 y) const;


   /**
    * @name Import functions
    *
    * These functions provide an easy way to copy data from a variety of
    * layouts into an LTISceneBuffer object, in an efficient manner.
    *
    * The copying is performed relative to the exposed window of the buffer.
    */
   /*@{*/

   /**
    * import from another LTISceneBuffer
    *
    * This function copies data from one source LTISceneBuffer object into
    * another destination LTISceneBuffer object.
    *
    * @param   sourceData   the data to be imported
    * @return  status code indicating success or failure
    */
    LT_STATUS importData(const LTISceneBuffer &sourceData);

   /**
    * import one band from another LTISceneBuffer
    *
    * This function copies just one band of data from one source LTISceneBuffer object into
    * another destination LTISceneBuffer object.
    *
    * @param   dstBand      the band number of this buffer to be written to
    * @param   sourceData   the data to be imported
    * @param   srcBand      the band number of \a sourceData to be read from
    * @return  status code indicating success or failure
    */
    LT_STATUS importDataBand(lt_uint16 dstBand,
                            const LTISceneBuffer& sourceData,
                            lt_uint16 srcBand);

    /**
    * import from another LTISceneBuffer, respecting approximate NoData
    *
    * This function copies data from one source LTISceneBuffer object into
    * another destination LTISceneBuffer object.  If the \c nodata parameter
    * is set, any pixel in the source that matches the nodata pixel will not
    * be copied to the corresponding destination pixel position.
    *
    * @param   fillMethod   method for determining if the pixel will be copied
    * @param   sourceData   the data to be imported
    * @param   nodataPixel  source pixel will not be copied if matches; may be NULL
    * @param   mag          scene magnification
    * @return  status code indicating success or failure
    */
   LT_STATUS importData(LTIPixelFillMethod fillMethod,
                        const LTISceneBuffer& sourceData,
                        const LTIPixel *nodataPixel,
                        double mag);

   /**
    * import from another LTISceneBuffer, respecting NoData
    *
    * This function copies data from one source LTISceneBuffer object into
    * another destination LTISceneBuffer object.  If the \c nodata parameter
    * is set, any pixel in the source that matches the nodata pixel will not
    * be copied to the corresponding destination pixel position.  This is the same
    * as calling importData(LTI_PIXELFILL_HARD, sourceData, nodataPixel, 1.0).
    *
    * @param   sourceData   the data to be imported
    * @param   nodataPixel  source pixel will not be copied if matches; may be NULL
    * @return  status code indicating success or failure
    */
   LT_STATUS importData(const LTISceneBuffer& sourceData,
                        const LTIPixel* nodataPixel);

   /**
    * import from memory (BSQ)
    *
    * This function copies data from a buffer in memory.  The source pointer
    * is assumed to be organized as an array of pointers to BSQ buffers, one
    * per band.
    *
    * @param   data  the source data
    * @return  status code indicating success or failure
    */
   LT_STATUS importDataBSQ(void** data);

   /**
    * import from memory (BSQ)
    *
    * This function copies data from a buffer in memory.  The source pointer
    * is assumed to be organized as one large buffer in BSQ format.
    *
    * @param   data  the source data
    * @return  status code indicating success or failure
    */
   LT_STATUS importDataBSQ(void* data);

   /**
    * import from memory (BIP)
    *
    * This function copies data from a buffer in memory.  The source pointer
    * is assumed to be organized as one large buffer in BIP format.
    *
    * @param   data  the source data
    * @return  status code indicating success or failure
    */
   LT_STATUS importDataBIP(void* data);

   /**
    * import from stream (BSQ)
    *
    * This function copies data from a buffer contained in the given stream.
    * The data is assumed to be organized as one large buffer in BSQ format.
    *
    * @param   stream  the source data
    * @return  status code indicating success or failure
    */
   LT_STATUS importDataBSQ(LTIOStreamInf& stream);

   /**
    * import from stream (BIP)
    *
    * This function copies data from a buffer contained in the given stream.
    * The data is assumed to be organized as one large buffer in BIP format.
    *
    * @param   stream  the source data
    * @return  status code indicating success or failure
    */
   LT_STATUS importDataBIP(LTIOStreamInf& stream);
   /*@}*/

   // for LizardTech internal use only
   LT_STATUS importData(const LTISceneBuffer &sourceData,
                         const LTIMask &binaryMask);

   // for LizardTech internal use only
   LT_STATUS importDataBand(lt_uint16 dstBand,
                            const LTISceneBuffer& sourceData,
                            lt_uint16 srcBand,
                            const LTIMask &binaryMask);

   // for LizardTech internal use only
   LT_STATUS importData(LTIPixelFillMethod fillMethod,
                        const LTISceneBuffer& sourceData,
                        const LTIPixel *nodataPixel,
                        LTIMaskSource &mask,
                        LTIScene &scene);

   /**
    * @name Export functions
    *
    * These functions provide an easy way to copy data from an LTISceneBuffer
    * object to a variety of layouts, in an efficient manner.
    *
    * The copying is performed relative to the exposed window of the buffer.
    */
   /*@{*/

   /**
    * export to memory (BSQ)
    *
    * This function copies data to a buffer in memory.  The destination
    * pointer is assumed to be organized as an array of pointers to BSQ
    * buffers, one per band.
    *
    * If the \c data parameter is NULL, the function will allocate it (but not
    * retain ownership).  In this case it is the caller's responsibility to
    * deallocate each band using the delete[] operator.
    *
    * @param   data  the destination data (may be NULL) [in/out]
    * @return  status code indicating success or failure
    */
   LT_STATUS exportDataBSQ(void**& data) const;

   /**
    * export to memory (BSQ)
    *
    * This function copies data to a buffer in memory.  The destination
    * pointer is assumed to be organized as one large buffer in BSQ
    * format.
    *
    * If the \c data parameter is NULL, the function will allocate it (but not
    * retain ownership).  In this case it is the caller's responsibility to
    * deallocate the buffer using the delete[] operator.
    *
    * @param   data  the destination data (may be NULL) [in/out]
    * @return  status code indicating success or failure
    */
   LT_STATUS exportDataBSQ(void*& data) const;

   /**
    * export to memory (BIP)
    *
    * This function copies data to a buffer in memory.  The destination
    * pointer is assumed to be organized as one large buffer in BIP
    * format.
    *
    * If the \c data parameter is NULL, the function will allocate it (but not
    * retain ownership).  In this case it is the caller's responsibility to
    * deallocate the buffer using the delete[] operator.
    *
    * @param   data  the destination data (may be NULL) [in/out]
    * @return  status code indicating success or failure
    */

   LT_STATUS exportDataBIP(void*& data) const;

   /**
    * export to stream (BSQ)
    *
    * This function copies data to a stream.  The destination is organized as
    * one large buffer in BSQ format.
    *
    * @param   stream  the destination stream
    * @return  status code indicating success or failure
    */
   LT_STATUS exportDataBSQ(LTIOStreamInf& stream) const;

   /**
    * export to stream (BIP)
    *
    * This function copies data to a stream.  The destination is organized as
    * one large buffer in BIP format.
    *
    * @param   stream  the destination stream
    * @return  status code indicating success or failure
    */
   LT_STATUS exportDataBIP(LTIOStreamInf& stream) const;

   /**
    * export to (arbitrary) memory
    *
    * This function copies data to a buffer.  The layout of the destination
    * is determined by the input parameters.
    *
    * For example, assuming RGB/uint8 data and WxH pixels:
    *
    * \li BIP format: pixelBytes=3, rowBytes=W*3, bandBytes=1
    * \li BIL: pixelBytes=1, rowBytes=W*3, bandBytes=W*1
    * \li BSQ: pixelBytes=1, rowBytes=W*1, bandBytes=W*H*1
    *
    * @param   data        the destination buffer (may not be NULL)
    * @param   pixelBytes  width of pixel, in bytes (e.g. distance from "red" to "red")
    * @param   rowBytes    width of buffer, in bytes
    * @param   bandBytes   distance from sample to the next, in bytes (e.g. distance from "red" to "blue")
    * @return  status code indicating success or failure
    */
   LT_STATUS exportData(void* data,
                        lt_uint32 pixelBytes,
                        lt_uint32 rowBytes,
                        lt_uint32 bandBytes) const;
   /*@}*/

   LT_STATUS byteSwap();

   // for LizardTech internal use only
   LT_STATUS applyMask(const LTIMask &binaryMask,
                       const LTIPixel *pixel);

   LT_STATUS fill(const LTIPixel &pixelColor);
   LT_STATUS zero(void);


   /**
    * compute alignment constraint
    *
    * This utility function returns a value which is equal to or greater than
    * the given value, when aligned to the given constraint.  This is useful
    * for determining proper row widths for certain applications.
    *
    * For example, given the value 99 and an alignment of 4, the function
    * will return 100.  Given a value of 128 and an alignment of 8, the
    * function will return 128.
    *
    * @param   value          the nominal buffer width
    * @param   byteAlignment  the alignment required
    * @return  the aligned width
    */
   static lt_uint32 addAlignment(lt_uint32 value, lt_uint32 byteAlignment);

   // for LizardTech internal use only
   static LT_STATUS buildMask(LTIPixelFillMethod fillMethod, double mag,
                              const LTISceneBuffer &dstBuffer, int dstBand, 
                              const LTISceneBuffer &srcBuffer, int srcBand,
                              const LTIPixel *nodataPixel,
                              LTIMask &binaryMask);

private:
   void init(const LTIPixel& pixelProps,
             lt_uint32 totalWidth,
             lt_uint32 totalHeight,
             lt_uint32 xOffset,
             lt_uint32 yOffset,
             lt_uint32 windowWidth,
             lt_uint32 windowHeight,
             void** data);

   void** m_data;
   void** m_windowData;
   bool m_ownsData;

   mutable void** m_tmpBands; // only used by import/export functions

   lt_int32* m_sampleSizes; // size of each sample, in bytes

   LTIPixel* m_pixelProps;
   lt_uint16 m_numBands;

   lt_uint32 m_totalNumCols;
   lt_uint32 m_totalNumRows;
   lt_uint32 m_windowColOffset;
   lt_uint32 m_windowRowOffset;
   lt_uint32 m_windowNumCols;
   lt_uint32 m_windowNumRows;

   bool operator==(const LTISceneBuffer&) const;
   bool operator!=(const LTISceneBuffer&) const;
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_SCENEBUFFER_H
