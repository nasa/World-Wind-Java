This readme describes the following:

1. The purpose of the JAR file _vpf-symbols.jar_.
2. When it is necessary to use it.
3. How to build it.

Purpose of vpf-symbols.jar
==========================
The JAR file vpf-symbols.jar found in the World Wind Java SDK contains style definitions and PNG icons for Vector
Product Format (VPF) shapes. vpf-symbols.jar is 1.8 MB in size, and is therefore distributed separately to avoid
increasing the size of worldwind.jar for the sake of supporting VPF, an uncommonly used feature.

For details on the VPF format specification and the corresponding GeoSym style specification, see the following
National Geospatial-Intelligence Agency (NGA) websites: [VPF](http://earth-info.nga.mil/publications/specs/)
[GeoSym](http://www.gwg.nga.mil/pfg_documents.php)

Using vpf-symbols.jar
=====================
The JAR file vpf-symbols.jar must be distributed and included in the runtime class-path by applications using the World
Wind class gov.nasa.worldwind.formats.vpf.VPFLayer. When added to an application's class-path, World Wind VPF shapes
automatically find and locate style and icon resources contained within this JAR file.

If vpf-symbols.jar is not in the Java class-path, VPFLayer outputs the following message in the World Wind log:
`WARNING: GeoSym style support is disabled`. In this case, VPF shapes are displayed as gray outlines, and icons are
displayed as a gray question mark.

Building vpf-symbols.jar
========================
To build or reconstruct the VPF symbols JAR file for the World Wind Java SDK, follow these six steps:

1. Download and extract the GeoSym Second Edition package.
Download the GeoSym archive from the [National Geospatial-Intelligence Agency (NGA)](http://www.gwg.nga.mil/pfg_documents.php),
then extract it to a folder named _GeoSymEd2Final_:
2. Create a new directory structure to hold the contents of the VPF symbols JAR file as follows:
```
geosym/
geosym/graphics/bin
geosym/symasgn/ascii
```
3. Convert all Geosym images from CGW format to PNG format.
Convert all images under the folder _GeoSymEd2Final/GRAPHICS/BIN_ from CGM to PNG format. Pad the PNG files to the next
largest power-of-two, adding extra transparent pixels out from the image's center. Place the PNG files under the
following directory created earlier: _geosym/graphics/bin_
The PNG images in the current version of vpf-symbols.jar were created by (a) converting the original CGM images to TIFF
images using CorelDRAW Graphics Suite X4, (b) converting the TIFF images to PNG images using the open source application
tiff2png version 0.91, and (c) padding the images to the next largest power-of-two using a simple Java application built 
on ImageIO.
4. Copy all GeoSym style tables.
Copy all files under the directory _GeoSymEd2Final/SYMASGN/ASCII_ to the directory _geosym/symasgn/ascii_.
5. Convert GeoSym line and area styles to World Wind shape attributes.
Run the Java application gov.nasa.worldwind.formats.vpf.GeoSymAttributeConverter from the shell, passing the full path
to _GeoSymEd2Final/GRAPHICS/TEXT_ as the application's only parameter. Copy the output PNG files under
_gsac-out/geosym/graphics/bin_ to the following directory created earlier: _geosym/graphics/bin_. Copy the output CSV
files under _gsac-out/geosym/symasgn/ascii_ to the following directory created earlier: _geosym/symasgn/ascii_.
6. Create the VPF symbols JAR file:
Execute the following shell command from the parent directory of the _geosym_ directory structure created earlier,
making sure that a Java SDK is on the path: `jar -cf vpf-symbols.jar geosym`
