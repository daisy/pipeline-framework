@echo off
set DP2_HOME=%~p0
set JAVA=java
set DS_MODE="-Dorg.daisy.pipeline.mode=cmd"
set DP_LOG_DIR=%DP2_HOME%/log
set FELIX_CONF=file:/%DP2_HOME:\=/%/etc/config.properties
set XPROC_CONF=%DP2_HOME%/etc/conf_calabash.xml
set LOGBACK_CONF=%DP2_HOME%/etc/conf_logback.xml
%JAVA% -Dorg.daisy.pipeline.home="%DP2_HOME%" %DS_MODE% -Dorg.daisy.pipeline.logdir="%DP_LOG_DIR%" -Dlogback.configurationFile="%LOGBACK_CONF%" -Dfelix.config.properties="%FELIX_CONF%" -Dorg.daisy.pipeline.xproc.configuration="%XPROC_CONF%" -Dorg.daisy.pipeline.cmdargs="%*" -jar %DP2_HOME%/fbin/felix.jar -b %DP2_HOME%/bundle
