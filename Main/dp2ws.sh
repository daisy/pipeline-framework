#!/bin/bash
pushd $(dirname $0) >/dev/null
DP2_HOME=$(pwd)
popd>/dev/null
JAVA=java
FELIX_CONF="file:$DP2_HOME/etc/config.properties"
DP_LOG_DIR=$DP2_HOME/log
XPROC_CONF=$DP2_HOME/etc/conf_calabash.xml
LOGBACK_CONF=$DP2_HOME/etc/conf_logback.xml
CLIENT_STORE_CONF=$DP2_HOME/etc/conf_client_store.xml
TMP_IO_DIR=$DP2_HOME/data
$JAVA -Xmx1024M -Xms512M -Dorg.daisy.pipeline.home="$DP2_HOME" -Dorg.daisy.pipeline.iobase="$TMP_IO_DIR" -Dorg.daisy.pipeline.logdir="$DP_LOG_DIR" -Dlogback.configurationFile="$LOGBACK_CONF" -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF"  -Dorg.daisy.pipeline.ws.clientstore="$CLIENT_STORE_CONF" -jar "$DP2_HOME/fbin/felix.jar" -b "$DP2_HOME/bundle"


