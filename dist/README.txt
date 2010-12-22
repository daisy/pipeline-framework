
============
 EXECUTION
============


Execute dp2.sh without any argument or dp2.sh -h to get the available options:

javi@chaos:~/dedicon/dist$ ./dp2.sh -h
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


--FRAMEWORK AS XPROC PROCESSOR--

	It is possible to use the daisy pipeline 2 framework as a xproc processor, just pass as arguments the needed ports for your pipeline to run like in the example:

./dp2.sh -x test/delete.xpl -i source:test/juicers.xml,xslt:test/createJuicerList.xsl -o result:out.html -p parameters:name:paco

--USING PREBUILT EXPATH PACKAGES--

 	The framework comes with a pre installed expath package, using the option -l the available components inside the system are shown:

./dp2.sh -l
Available URIs:
http://www.example.org/module-hello/helloizer.xsl

	The following example executes a xproc file which depends on the component shown before:

./dp2.sh -x test/helloTest.xpl -o result:out.xml -p parameters:msg:world

	Inside helloTest.xpl the xslt import is done as follows:

	<p:input port="stylesheet">
        	<p:document href="http://www.example.org/module-hello/helloizer.xsl"/>
        </p:input>

	The systems takes care of resolving the correct path for the given url.

 


              




