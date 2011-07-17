REM Windows Batch file for Running a WorldWind Demo
REM $Id$

@echo Running %1
java -Xmx512m -Dsun.java2d.noddraw=true -classpath .\src;.\classes;.\worldwind.jar;.\worldwindx.jar;.\jogl.jar;.\gluegen-rt.jar;.\gdal.jar %*
