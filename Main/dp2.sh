#!/bin/bash

CLASSPATH=$CLASSPATH:lib/bndtools.launcher-0.0.0.20100513-0222.jar:lib/org.eclipse.osgi-3.6.1.jar
ARGS="$(echo ${@})"
java  -Dorg.daisy.pipeline.cmdargs="$ARGS" -cp $CLASSPATH  bndtools.launcher.Main launch.properties
