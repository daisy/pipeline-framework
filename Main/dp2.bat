@echo off
set JAVA=java
set DS_MODE="-Dorg.daisy.pipeline.mode=cmd"
set DP_LOG_DIR=%cd%/log
set FELIX_CONF="file:/%cd%/etc/config.properties"
set XPROC_CONF="file:/%cd:\=/%/etc/conf_calabash.xml"
set LOGBACK_CONF=etc/conf_logback.xml
%JAVA% %DS_MODE% -Dorg.daisy.pipeline.logdir="%DP_LOG_DIR%" -Dlogback.configurationFile="%LOGBACK_CONF%" -Dfelix.config.properties="%FELIX_CONF%" -Dorg.daisy.pipeline.xproc.configuration="%XPROC_CONF%" -Dorg.daisy.pipeline.cmdargs="%*" -jar fbin/felix.jar
