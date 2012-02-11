@echo off
set DP2_HOME=%~d0%~p0
set JAVA=java
set DP_LOG_DIR=%DP2_HOME%log
set FELIX_CONF=file:%DP2_HOME:\=/%etc/config.properties
set XPROC_CONF=%DP2_HOME%etc/conf_calabash.xml
set LOGBACK_CONF=%DP2_HOME%etc\conf_logback.xml
set CLIENT_STORE_CONF=%DP2_HOME%etc\conf_client_store.xml
set TMP_IO_DIR=%DP2_HOME%data
%JAVA% -Xmx1024M -Xms512M -Dorg.daisy.pipeline.home="%DP2_HOME:~0,-1%" -Dorg.daisy.pipeline.iobase="%TMP_IO_DIR%" -Dorg.daisy.pipeline.logdir="%DP_LOG_DIR%" -Dlogback.configurationFile="%LOGBACK_CONF%" -Dfelix.config.properties="%FELIX_CONF%" -Dorg.daisy.pipeline.xproc.configuration="%XPROC_CONF%" -Dorg.daisy.pipeline.ws.clientstore="%CLIENT_STORE_CONF%" -jar "%DP2_HOME%fbin\felix.jar" -b "%DP2_HOME%bundle"
