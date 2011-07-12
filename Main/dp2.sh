#!/bin/bash
JAVA=java
DS_MODE="-Dorg.daisy.pipeline.mode=cmd"

ARGS="$(echo ${@})"
DP_ARGS=-Dorg.daisy.pipeline.cmdargs=\"$ARGS\"
FELIX_CONF="file:$PWD/etc/config.properties"
XPROC_CONF=$PWD/etc/conf_calabash.xml
$JAVA $DS_MODE -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF" -Dorg.daisy.pipeline.cmdargs="$ARGS" -jar bin/felix.jar





