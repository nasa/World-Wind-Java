/******************************************************************************
 * $Id: gdalconst_java.i 16316 2009-02-13 20:51:00Z rouault $
 *
 * Name:     gdalconst_java.i
 * Project:  GDAL SWIG Interface
 * Purpose:  Typemaps for Java bindings
 * Author:   Benjamin Collins, The MITRE Corporation
 *
 *
 * $Log$
 * Revision 1.2  2006/02/16 17:21:12  collinsb
 * Updates to Java bindings to keep the code from halting execution if the native libraries cannot be found.
 *
 * Revision 1.1  2006/02/02 20:56:07  collinsb
 * Added Java specific typemap code
 *
 *
 */

%pragma(java)jniclasscode=%{
private static boolean available=false;

static
{
   String nativeLibName = "gdalalljni";

   String osName = System.getProperty("os.name");
   String arch = System.getProperty("sun.arch.data.model");

   if( osName != null && !osName.toLowerCase().contains("mac") && arch != null )
   {
       nativeLibName += arch;
   }

   try
   {
       org.gdal.gdal.gdal.loadLibrary(nativeLibName);
       available=true;
   }
   catch(UnsatisfiedLinkError e)
   {
       available=false;
   }

   if( available == false && osName != null && osName.toLowerCase().contains("windows") && arch != null )
   {
       nativeLibName = "gdalminjni" + arch;

       try
       {
           org.gdal.gdal.gdal.loadLibrary(nativeLibName);
           available=true;
       }
       catch(UnsatisfiedLinkError e)
       {
           available=false;
       }
   }
}

public static boolean isAvailable()
{
   return available;
}
%}

    %pragma(java)modulecode=%{

/* Uninstanciable class */
private gdalconst()
    {
    }
    %}

    %include typemaps_java.i
