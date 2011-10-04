#!/bin/bash
pushd $(dirname $0) >/dev/null
DP2_HOME=$(pwd)
popd>/dev/null
JAVA=java
DS_MODE="-Dorg.daisy.pipeline.mode=cmd"
FELIX_CONF="file:$DP2_HOME/etc/config.properties"
DP_LOG_DIR=$DP2_HOME/log
XPROC_CONF=$DP2_HOME/etc/conf_calabash.xml
LOGBACK_CONF=$DP2_HOME/etc/conf_logback.xml
ARGS="$(echo ${@})"
$JAVA -Dorg.daisy.pipeline.home="$DP2_HOME" $DS_MODE -Dorg.daisy.pipeline.logdir="$DP_LOG_DIR" -Dlogback.configurationFile="$LOGBACK_CONF" -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF" -Dorg.daisy.pipeline.cmdargs="$ARGS" -jar $DP2_HOME/fbin/felix.jar -b $DP2_HOME/bundle






