@echo off
rem This is the path to Java. Edit this variable to suite your needs.
set JAVA=java

set CMD=%~n0
set MODULE_DIR=%~dp0

set CP="%MODULE_DIR%\lib\bndtools.launcher-0.0.0.20100513-0222.jar";"%MODULE_DIR%\lib\org.eclipse.osgi-3.6.1.jar"

rem echo %CP%
%JAVA%  -Dorg.daisy.pipeline.cmdargs="%*" -cp %CP%  bndtools.launcher.Main launch.properties