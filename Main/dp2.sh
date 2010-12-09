#!/bin/bash

CLASSPATH=$CLASSPATH:lib/bndtools.launcher-0.0.0.20100513-0222.jar:lib/org.eclipse.osgi-3.6.1.jar

java  -Dorg.daisy.pipeline.cmdargs=$@ -cp $CLASSPATH  bndtools.launcher.Main launch.properties
