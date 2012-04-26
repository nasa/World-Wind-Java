$Id$

This document provides links on getting started with the World Wind Java SDK, provides instructions on running the a
World Wind demo application, and outlines the key changes between each World Wind Java SDK release.


Getting Started With the World Wind Java SDK
------------------------------------------------------------

Key files and folders in the World Wind Java SDK:
- build.xml: Apache ANT build file for the World Wind Java SDK.
- src: Contains all Java source files for the World Wind Java SDK, except the World Wind WMS Server.
- server: Contains the World Wind WMS Server Java source files, build file, and deployment files.
- lib-external/gdal: Contains the GDAL native binaries libraries that may optionally be distributed with World Wind.

Links to important World Wind sites that will help you get started using the World Wind Java SDK in your
application:

World Wind Developer's Guide:
http://goworldwind.org/

World Wind Main Website:
http://worldwind.arc.nasa.gov/java/

World Wind Forum:
http://forum.worldwindcentral.com/forumdisplay.php?f=37

World Wind API Documentation:
http://builds.worldwind.arc.nasa.gov/worldwind-releases/1.3/docs/api/index.html

World Wind Bug Base:
http://issues.worldwind.arc.nasa.gov/secure/IssueNavigator.jspa?reset=true&pid=10021&status=1


Running a Basic Demo Application
------------------------------------------------------------

To run the basic demo on Mac OS X or Linux:
    1) Open a terminal.
    2) cd to the World Wind release folder.
    3) chmod +x run-demo.bash
    4) ./run-demo.bash gov.nasa.worldwindx.examples.ApplicationTemplate

To run the basic demo on Windows:
    1) Open a command prompt.
    2) cd to the World Wind release folder.
    3) run-demo.bat gov.nasa.worldwindx.examples.ApplicationTemplate

Your computer must have a modern graphics card with an up-to-date driver.  The source of most getting-up-and-running
problems is an out-of-date graphics driver. To get an updated driver, visit your graphics card manufacturer's web site.
This will most likely be either NVIDIA, ATI or Intel. The drivers are typically under a link named "Downloads" or
"Support". If your computer is a laptop, then updated drivers are probably at the laptop manufacturer's web site rather
than the graphics card manufacturer's.


New features and improvements in World Wind Java SDK 1.3.0 - April 27, 2012
------------------------------------------------------------
- 2525C Symbology
- KML NetworkLinkControl and Update


New features and improvements in World Wind Java SDK 1.2.0 - July 19, 2011
------------------------------------------------------------

- KML file parsing and display.
- Improved Shapefile parsing and display performance.
- GeoJSON file parsing and display.
- Vector Product Format (VPF) database parsing and display

- Web-browser balloons on Mac OS X and Windows that display HTML5 and JavaScript content.
- 3D polygon and extruded polygon shapes.
- 3D rigid shapes: ellipsoid, wedge, cylinder, cone, pyramid, box.
- Improved 3D path/polyline shape.
- Editors for 3D polygon and extruded polygon shapes.
- Editors for 3D rigid shapes.
- Line of sight intersections with 3D shapes and high-resolution terrain.
- Improved on-screen layer manager. Integrated with KML file parsing and display.

- Image and elevation data import using GDAL.
  - Adds support for most common image and elevation data formats.
  - Reduces the hard drive footprint and time required to import large datasets.

- Bulk download / cache building support for surface image layers, place name layer, and selevation models.
- Screen credit support for surface image layers.
- New high resolution topographic maps for the United States.
- High resolution NAIP imagery for the United States.
- Improved image layer rendering performance.
- Improved XML parsing performance.

- WorldWindow application framework.
- Simplified project structure and build scripts.
- Applications and examples separated into a new JAR file: worldwindx.jar.
- WMS server integrated into the World Wind project.
