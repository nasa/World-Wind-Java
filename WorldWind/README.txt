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
http://OneWorldWind.org

World Wind Main Website:
http://worldwind.arc.nasa.gov/java/

World Wind Forum:
http://forum.worldwindcentral.com/forumdisplay.php?f=37

World Wind API Documentation:
http://builds.worldwind.arc.nasa.gov/worldwind-releases/1.2/docs/api/index.html

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


Changes from WWJ SDK 0.5 to 0.6 early access - March 6, 2009
------------------------------------------------------------

- New Airspace shapes and volumes, terrain conformant. See render.airspaces package and examples.Airspaces and AirspaceBuilder
- New rubber sheet images. See util.ImageUtil and examples.RubberSheetImage
- New measure tools. See util.measure package and examples.MeasureToolUsage
- New 'on-screen display' layers: ViewControlsLayer, LayerManagerLayer and StatusLayer
- New ContourLine renderable primitive. See examples.ContourLines
- New Web Service Catalog support. See applications.gio

- Data import and installation:
  - Local imagery and elevation import into WWJ cache format - see data.TiledImageProducer and data.TiledElevationProducer
  - Reading, writing, and discovery of file descriptors for WWJ cache format - see cache.FileStore and data.DataDescriptor
  - Discovery and conversion of WWJ.Net cache format - see data.WWDotNetDataLayerSetReader and data.WWDotNetLayerSetInstaller

- New DDS compressor with support for mipmaps - see formats.dds.DDSCompressor

- Icon layer and renderer allow to use absolute elevations, apply horizon and view clipping
- Applet package updated for Sun Next Generation Java plugin support
- Compound and local elevation models. See terrain package
- FlatWorld example updated with round/flat globe switching code
- Track markers updated - see render.markers package, layers.MarkerLayer and examples.MarkersOrder
- PlacenameLayer updated - see layers.placename package and examples.Placenames
- TerrainProfileLayer new 'follow path' mode - see examples.MeasureToolUsage
- Terrain intersection test for Line and elevation. See terrain.SectorGeometryList and render.ContourLine
- On-screen layers display location offset - see layers.Compass, WorldMap, Scalebar...
- New format support: tab, tiff and world file - see format package
- Tiled image layers alpha blending fixed and mipmap support.
- New examples.util package - browser launcher, audio and slides players, image viewer...

- Data driven navigation sensitivity settings - see awt.ViewInputAttributes.
- Redesigned OrbitViewInputBroker as ViewInputHandler - see awt.ViewInputHandler. Improved navigation near the terrain, and made it easier for applications to extend or modify the navigation behavior.
- Sharper image-based surface shapes. Experimental geometry based surface shapes.
- Experimental hybrid tessellator to better handle the poles.
- Improved WMS server
- Image transformation and reprojection utilities. See util.ImageUtil.
- New Ant build structure.
- Collision detection demo.
- New network status host available/unavailable events.
- Added a pre-render stage to the system, and implemented classes to create textures by drawing to the frame buffer or an FBO, most typically during the pre-render stage.

- Refactoring
  - Position is now a LatLon subclass

- On hold
- Fog layer produces artifacts with the new tiled image layer premultiplied alpha blending and has been removed
- Remote surface images are being reworked into a more generic scheme


Change Summary for 0.4 to 0.5:
-----------------------------
- Includes a WMS server.

- Major changes to the view code - the eye can now go very close to
  the ground, and underwater. New interface methods.
- New Restorable interface to save and restore objects state to/from an xml
  document. Implemented in UserFacingIcons, Annotations, Polyline, View...
- Flat Worlds with projection switching are now usable.
- Mars and Moon globes with elevations and full layersets from NASA servers.
- MGRS, UTM and TM coordinates classes and converters in geom.coords
- Tiled image layers will not wait for lower res tiles to load before
  showing the needed ones.
- New layers:
  - NAIPCalifornia.
  - BMNGWMSLayer gives access to any of the 12 BMNG 2004 layers.
  - OpenStreeMapLayer.
  - MGRSGraticuleLayer and UTMGraticuleLayer.
  - CrosshairLayer.
- All non Earth specific layers have been moved from layers.Earth to layers:
  CrosshairLayer, FogLayer, ScalebarLayer, SkyColorLayer, SkyGradientLayer,
  StarsLayer, TerrainProfileLayer and WorldMapLayer.
- StatusBar moved from examples to util.
- New GeographicText support - used for placenames.
- More accurate scalebar.
- Increased performance for Polyline.
- Icons can have a background image.
- WWJApplet example updated with new capabilities.
- Build script completely revised.
- SurfaceImage from an http source.
- Zoom with middle mouse button down and drag up/down.
- AlwaysOnTop property for icons and annotations.
- New Mipmap flag for TiledImageLayer
- Better TiledImageLayer image capture and composition.
- Enhanced NITFS/RPF support.
- Better gps tracks support
- New examples: AlarmIcons, BMNGTwelveMonth, FlatWorldEarthquakes, MGRSGraticule,
  RemoteSurfaceImage, ViewLookAround, Mars, Moon...
- Also includes an application for Search And Rescue support.

- Many other bug fixes and changes...

