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

#ifndef LTI_PIXELLOOKUPTABLE_H
#define LTI_PIXELLOOKUPTABLE_H

// lt_lib_mrsid_core
#include "lti_pixel.h"


LT_BEGIN_NAMESPACE(LizardTech)

/**
 * color lookup table
 *
 * This class implements a color lookup table, for use with "indexed"
 * or "palletized" images.
 */
class LTIPixelLookupTable : public LTIPixel
{
public:
   /**
    * default constructor
    *
    * This constructor creates an LTIPixelLookupTable object consisting of
    * the given number of entries.
    *
    * @param  pixelProps  the basic properties of the pixel in this LUT
    * @param  tableSize   the number of elements in the table
    */
   LTIPixelLookupTable(const LTIPixel& pixelProps,
                       lt_uint32 tableSize);

   /**
    * copy constructor
    */
   LTIPixelLookupTable(const LTIPixelLookupTable&);

   /**
    * destructor
    */
   ~LTIPixelLookupTable();

   /**
    * get table size
    *
    * Returns the number of entries in the table.
    *
    * @return the number of entries in the table
    */
   lt_uint32 getTableSize() const;

   /**
    * set table entry
    *
    * This function adds an entry to the table.  The index must be less than
    * the number of entries.  The pixel data is copied into the table.
    *
    * If the table already contains an entry with the given index number, the
    * old entry is removed in favor of the new one.
    *
    * @param  index      the index number of the entry
    * @param  pixelData  the pixel data to be recorded for the index
    */
   void setTableEntry(lt_uint32 index,
                      const LTIPixel& pixelData);

   /** set all table entries
    *
    * This function sets all entries to the table.  The \a mapData parameter
    * is expected to be a one-dimensional array of length tablesize * numbands,
    * e.g. if the pixel datatype is UINT8 then the array should be of the form
    * "lt_uint8 data[3 * 256]" for a 3-banded, 256-entry table.  Entry i will be
    * referenced as data[i], data[i+256], data[i+512].
    *
    * @param mapData the colormap data
    */
   void setTableEntries(const void* mapData);

   /**
    * get table entry
    *
    * This function returns the entry with the given index number.
    * The caller takes ownership of the returned object.
    *
    * @param   index      the index number of the entry to look up
    * @return  pointer to the pixel data of the index (or NULL)
    */
   LTIPixel* getTableEntry(lt_uint32 index) const;

   /**
    * get all table entries
    *
    * This function returns the color map data as a one-dimensional
    * array, according to the format described in setAllEntries().
    */
   const void* getTableEntries() const;

private:
   LT_STATUS init(const void* data);

   lt_uint32 m_tableSize;

   // for an RGB map, entry I is formed by: R=data[I], G=data[I+256], B=data[I+512]
   void* m_data;

   // nope
   LTIPixelLookupTable& operator=(const LTIPixelLookupTable&);
};


LT_END_NAMESPACE(LizardTech)

#endif // LTI_PIXELLOOKUPTABLE_H
