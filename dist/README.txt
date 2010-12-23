###############################################################################
###             DAISY Pipeline 2 - Framework Demo                           ###
###############################################################################



About the framework demo
-------------------------------------------------------------------------------

The demo show a minimal version of what will be the Pipeline 2 runtime
framework. It is implemented with OSGi, a standard technology to develop
modular applications with Java.

The framework implementation allows flexible and dynamic management of modules
(pieces of cohesive functionality). It provides a module-aware URI resolver to
resolve behind the scene the dependencies of components developed with standard
XML technologies like XProc and XSLT.

Usage:

Option                                  Description                            
------                                  -----------                            
-h [converter]                          Show this help or the help for the     
                                          given converter                      
-i                                      list of input ports in the format      
                                          portName1:file1,portName2:file2      
                                          (only with -x modifier)              
-l                                      List of available converters           
-o                                      list of output ports in the format     
                                          portName1:file1,portName2:file2      
-p                                      list of parameters in the format port1:
                                          param1:value1,param1:param2:value2   
                                          (only with -x modifier)              
-x                                      xproc file to execute 



Demo
-------------------------------------------------------------------------------


   A) Execute an XProc script

It is possible to use the DAISY Pipeline 2 runtime to invoke an XProc
processor, just pass as arguments the needed ports for your pipeline to run
like in the example:

$ pipeline2.sh -x test/delete.xpl -i source:test/juicers.xml,xslt:test/createJuicerList.xsl -o result:out.html -p parameters:name:paco


    B) List available modules

The framework comes with a pre-installed modules (declared as EXPath packages).
Use the option -l to list the available components:

$ pipeline2.sh -l
Available URIs:
http://www.example.org/module-hello/helloizer.xsl


    C) Execute an XProc script depending on another module

The following example executes a xproc file which depends on the component
listed in the previous demo:

$ pipeline2.sh -x test/helloTest.xpl -o result:out.xml -p parameters:msg:world


Explanation of behind-the-scene URI resolution:

Inside helloTest.xpl the XSLT imports the module's XSLT as follows:

<p:input port="stylesheet">
    <p:document href="http://www.example.org/module-hello/helloizer.xsl"/>
</p:input>

The systems takes care of resolving the absolute URI to the XLST file located
in the available module.
