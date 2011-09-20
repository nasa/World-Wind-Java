$Id$

This document provides links on getting started with the World Wind Android SDK, and provides instructions on building
and running the the an example World Wind application on Android.


Getting Started With the World Wind Android SDK
------------------------------------------------------------

Key files and folders in the World Wind Android SDK:
- build.xml: Apache ANT build file for the World Wind Android SDK.
- src: Contains all Java source files for the World Wind Android SDK.
- examples: Contains example applications that use the World Wind Android SDK.

Links to important World Wind sites that will help you get started using the World Wind Android SDK in your
application:

World Wind Android Developer's Guide:
http://goworldwind.org/android

World Wind Android Release Website:
http://worldwindserver.net/android/

World Wind Android Forum:
http://forum.worldwindcentral.com/forumdisplay.php?f=50

World Wind Android API Documentation:
http://builds.worldwind.arc.nasa.gov/wwandroid-releases/daily/docs/api/index.html


Running an Example Application on Android
------------------------------------------------------------

Start by configuring your Android development environment by following the instructions at:
http://goworldwind.org/android/getting-started-with-android-and-java. Then, connect your Android device to your
computer. Note that as of September 20, 2011, World Wind Android has been tested on the Samsung Galaxy TAB 10.1.

To run an example using IntelliJ IDEA:
    1) Open the WWAndroid project in IntelliJ IDEA.
    2) In the toolbar, select the SimplestPossibleExample run configuration.
    3) Click the run button.

To run an example from the command line:
    1) Open a terminal.
    2) Add the Android SDK folders "platform-tools" and "tools" to your PATH environment variable.
    3) cd to the WWAndroid project root folder.
    4) adb -d install examples/SimplestPossibleExample/SimplestPossibleExample.apk
