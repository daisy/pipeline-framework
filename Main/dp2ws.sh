#!/bin/bash
if [ -n $(uname | grep -v linux) ]; then 
	DP2_HOME=$(readlink -f $(dirname $0 ))
else
	pushd $(dirname $0) >/dev/null
	DP2_HOME=$(pwd)
	popd>/dev/null
fi
JAVA=java
DS_MODE="-Dorg.daisy.pipeline.mode=ws"
FELIX_CONF="file:$DP2_HOME/etc/config.properties"
DP_LOG_DIR=$DP2_HOME/log
XPROC_CONF=$DP2_HOME/etc/conf_calabash.xml
LOGBACK_CONF=$DP2_HOME/etc/conf_logback.xml
TMP_IO_DIR=$DP2_HOME/data
$JAVA -Dorg.daisy.pipeline.home="$DP2_HOME" $DS_MODE -Dorg.daisy.pipeline.iobase="$TMP_IO_DIR" -Dorg.daisy.pipeline.logdir="$DP_LOG_DIR" -Dlogback.configurationFile="$LOGBACK_CONF" -Dfelix.config.properties="$FELIX_CONF" -Dorg.daisy.pipeline.xproc.configuration="$XPROC_CONF" -jar $DP2_HOME/fbin/felix.jar -b $DP2_HOME/bundle


