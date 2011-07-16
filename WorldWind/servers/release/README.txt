To run the WMS:

1. Edit the file WEB-INF/config.xml to configure the listening port number, and an optional path to where GDAL
   utilities are stored on the local system.

   To be useful as a WMS, sources of map data will also need to be configured into this file.  See the javadoc
   overview for details.

2. Edit the file WEB-INF/capabilities_template.xml replace to all occurances of the string "@HOSTNAME@:@PORT@" with
   the name or IP-address of the machine on which the WMS is installed, along with the listening port number
   as configured in step 1. Note that this file serves as the basis for the WMS "GetCapabilities" response,
   and thus the name or IP must be something that can be resolved by any remote machines that the WMS is to serve.
   For example, if the WMS sits behind a firewall, the name/IP must be what is visible to machines outside the
   local network, not the name/IP of the machine as known on the LAN.

3. To run, from a command-line, enter:
    Windows:            startWMS.bat
    Linux/MacOSX/Unix:  bash startWMS.sh

