#! /bin/bash
#
#
#
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib

java -Xmx512M -server -cp lib/worldwind-servers.jar:lib/worldwind.jar:lib/jogl.jar:.: \
     -Djava.library.path=`pwd`/lib  \
     -Djava.awt.headless=true \
     -Dsun.java2d.noddraw=true \
     -Djava.util.logging.config.file=wms.logging.properties \
     gov.nasa.worldwind.servers.app.ApplicationServerLauncher 2>err.log >out.log &
echo $! >wms.pid