#!/bin/bash
JAVA=java
DS_MODE="-Dorg.daisy.pipeline.mode=ws"
FELIX_CONF="file:$PWD/etc/config.properties"
DP_LOG_DIR=$PWD/log
XPROC_CONF=$PWD/etc/conf_calabash.xml
LOGBACK_CONF=etc/conf_logback.xml

$JAVA $DS_MODE -Dorg.daisy.pipeline.logdir="$DP_LOG_DIR" -Dlogback.configurationFile="$LOGBACK_CONF" -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF" -jar fbin/felix.jar



