@echo off
set CONSOLE=false

set PWD=%cd%
rmdir /Q /S runner\cache
echo scan-dir:/%cd:\=/%/lib/ > comp
echo scan-dir:/%cd:\=/%/modules/ >> comp
echo scan-dir:/%cd:\=/%/framework/ >> comp

java  -cp pax-runner-1.6.1.jar org.ops4j.pax.runner.Run --vmo="-Dorg.daisy.pipeline.xproc.configuration=%PWD%\etc\conf_calabash.xml -Dlogback.configurationFile=%PWD%\etc\config.xml -DDP_LOG_DIR=%PWD%\log -Dorg.daisy.pipeline.mode=ws " --console=%CONSOLE% --log=none --platform=equinox --v=3.5.0 --bd=javax.naming,javax.naming.*,javax.management,javax.management.loading,javax.management.modelmbean,javax.net,javax.net.ssl,javax.crypto,javax.crypto.interfaces,javax.crypto.spec,javax.security.auth,javax.security.auth.spi,javax.security.auth.callback,javax.security.auth.login,javax.security.cert,javax.xml.parsers,javax.xml.xpath,javax.xml.transform.sax,javax.xml.transform.dom,javax.xml.namespace,javax.xml.transform,javax.xml.transform.stream,javax.xml.validation,org.xml.sax,org.xml.sax.helpers,org.xml.sax.ext,com.sun.org.apache.xalan.internal,com.sun.org.apache.xalan.internal.res,com.sun.org.apache.xml.internal.utils,com.sun.org.apache.xpath.internal,com.sun.org.apache.xpath.internal.jaxp,com.sun.org.apache.xpath.internal.objects,com.sun.org.apache.xml.internal,org.w3c.dom,org.w3c.dom.traversal,org.w3c.dom.ls,javax.sql,javax.transaction,sun.misc,javax.xml.stream,javax.xml.stream.events,javax.xml.stream.util,javax.annotation,org.apache.xerces.impl,org.apache.xerces.impl.validation,org.apache.xerces.impl.xpath.regex,org.apache.xerces.impl.xs,org.apache.xerces.parsers,org.apache.xerces.util,org.apache.xerces.xni,org.apache.xerces.xni.grammars,org.apache.xerces.xni.parser,javax.xml.datatype,javax.xml.parsers,javax.xml.transform,org.w3c.dom.events,org.w3c.dom.ranges,org.w3c.dom.xpath  scan-composite:file:comp

del comp
