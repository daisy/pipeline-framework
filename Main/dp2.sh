#!/bin/bash
JAVA=java
DS_MODE="-Dorg.daisy.pipeline.mode=cmd"
FELIX_CONF="file:$PWD/etc/config.properties"
DP_LOG_DIR=$PWD/log
XPROC_CONF=$PWD/etc/conf_calabash.xml
LOGBACK_CONF=$PWD/etc/conf_logback.xml
ARGS="$(echo ${@})"
$JAVA $DS_MODE -Dorg.daisy.pipeline.logdir="$DP_LOG_DIR" -Dlogback.configurationFile="$LOGBACK_CONF" -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF" -Dorg.daisy.pipeline.cmdargs="$ARGS" -jar fbin/felix.jar





