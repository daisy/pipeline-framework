@echo off
set JAVA=java
set DS_MODE="-Dorg.daisy.pipeline.mode=ws"
set DP_LOG_DIR=%cd%/log
set FELIX_CONF="file:/%cd%/etc/config.properties"
set XPROC_CONF="file:/%cd:\=/%/etc/conf_calabash.xml"
set LOGBACK_CONF=etc/conf_logback.xml
set TMP_IO_DIR=%cd%/data
%JAVA% %DS_MODE% -Dorg.daisy.pipeline.iobase="%TMP_IO_DIR%" -Dorg.daisy.pipeline.logdir="%DP_LOG_DIR%" -Dlogback.configurationFile="%LOGBACK_CONF%" -Dfelix.config.properties="%FELIX_CONF%" -Dorg.daisy.pipeline.xproc.configuration="%XPROC_CONF%" -jar fbin/felix.jar

