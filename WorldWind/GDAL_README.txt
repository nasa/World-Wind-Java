$Id$

GDAL and WWJ - Deployment scenarios and configuration guide


VERSION

We use GDAL 1.7.2 version along with LizardTech's Decode SDK version 7.0.0.2167.


SUPPORTED PLATFORMS:

We have compiled and built GDAL+PROJ4+MRSiD bundles for:
 - MacOSX (Snow Leopard, 64-bit),
 - Windows 32 and Windows 64
 - Linux 43 and Linux 64

WWJ GDAL BUNDLES LOCATION:

To simplify deployment, our GDAL+PRO4+MRSID bundles are compiled as a single dynamic library
that has all dependent libraries statically compiled.
As a result we have one dynamic library "gdalall" per OS / per platform.
They are located under the "lib-external/gdal/" folder.

GDAL and PROJ4 libraries require data tables located in the lib-external/gdal/data folder.
We recommend always put these tables in the "data" sub-folder.

WWJ attempts to locate GDAL bundles during the startup.
By default WWJ will first look for GDAL native binaries in the current path,
then in to the lib-external/gdal/ folder, and if no GDAL bundle was found,
will try to locate the GDAL bundle in the sub-folders.
Therefore we recommend one of two options:
keep GDAL bundle in the "lib-external/gdal" folder or in the current directory where you start your application.

DEPLOYING WITH JAVA WEB START

Here is an example of the WorldWindJ-based WebStart application that uses our GDAL bundle.

You will need these four files:
- gdal.jar  - GDAL Java Bindings - you can find it in the WorldWindJ root folder
- gdaldata.jar - jar file with entire GDAL data tables - create your own from lib-external/gdal/data folder
               or use our from http://worldwind.arc.nasa.gov/java/gdal/webstart/gdaldata.jar
- gdal.jnlp - Java Network Launching Protocol (JNLP) for our (or yours) OS and platform specific GDAL bundles
- worldwind.jar - WorldWind Java SDK

Our gdal.jnlp file is located in the "http://worldwind.arc.nasa.gov/java/gdal/webstart/gdal.jnlp" web page and
you can point your WWJ-based application to use prebuilt GDAL-bundle.
Remember, we support only Windows 32/64-bit, Linux 32/64-bit, and Mac OSX 64-bit for now.

Here is an example of a sample WWJ-based Java WebStart application that refers our GDAL-bundle:

<?xml version="1.0" encoding="utf-8"?>
<jnlp spec="1.0"
      codebase="http://yourcompany.website.com/applications/" href="MyDemoWWJApplication.jnlp" >
    <information>
        <title>My Demo WWJ app</title>
        <vendor>Your Company Name Here</vendor>
        <homepage href="http://yourcompany.website.com/"/>
        <description>My Demo WWJ appr</description>
        <description kind="short">My Demo WWJ appr</description>
        <offline-allowed/>
    </information>
    <security>
        <all-permissions/>
    </security>

    <resources>
        <j2se href="http://java.sun.com/products/autodl/j2se" version="1.6+" initial-heap-size="2048m"  max-heap-size="2048m"/>

        <property name="sun.java2d.noddraw" value="true"/>

        <property name="java.util.logging.config.file" value="logging.properties"/>

        <jar href="MyWWJDemo.jar" main="true"/>

        <extension name="worldwind" href="http://worldwind.arc.nasa.gov/java/1.3.0/webstart/worldwind.jnlp"/>

        <extension name="gdal" href="http://worldwind.arc.nasa.gov/java/gdal/webstart/gdal.jnlp"/>

    </resources>
    <application-desc main-class="com.YourCompanyName.applications.MyWWJDemoApp"/>
</jnlp>


