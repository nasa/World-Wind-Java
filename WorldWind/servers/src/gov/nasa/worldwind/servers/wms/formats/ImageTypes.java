/* Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

public enum ImageTypes
{
    GEOTIFF("image/geotiff", "gtiff", "gtiff"),
    TIFF("image/tiff", "gtiff", "gtiff"),
    DDS("image/dds", "dds", ""),
    PNG("image/png", "png", "png"),
    JPEG("image/jpeg", "jpg", "jpeg"),
    GIF("image/gif", "gif", "gif"),
    BIL("image/bil", "", "");

    static public ImageTypes getGDALTypeFromMime(String mimeType)
    {
        ImageTypes[] types = ImageTypes.class.getEnumConstants();
        for (ImageTypes t : types)
        {
            if (t.mimeType.equalsIgnoreCase(mimeType))
            {
                return t;
            }
        }
        return null;
    }

    private ImageTypes(String mimeType, String fileSuffix, String gdalType)
    {
        this.mimeType = mimeType;
        this.fileSuffix = fileSuffix;
        this.gdalType = gdalType;
    }

    public String mimeType;
    public String fileSuffix;
    public String gdalType;
}
