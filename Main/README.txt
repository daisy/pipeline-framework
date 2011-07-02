###############################################################################
###          DAISY Pipeline 2 - 1.0 Beta 1 - July 2nd, 2011                 ###
###############################################################################



What is the DAISY Pipeline 2 ?
-------------------------------------------------------------------------------

The DAISY Pipeline 2 is an ongoing project to develop a next generation
framework for automated production of accessible materials for people with
print disabilities. It is the follow-up and total redesign of the original
DAISY Pipeline 1 project.

For more information see:
 - the project page: http://www.daisy.org/pipeline2
 - the development site: http://code.google.com/p/daisy-pipeline/



Contents of the package
-------------------------------------------------------------------------------

This package is an early beta release for the 1.0 version due in September
2011. This is beta software with many rough edges, and it is not feature
complete. It however contains an operational runtime and a set of functional
converters.

The package includes:

 - a modular runtime framework (based on OSGi) for the Pipeline 2
 modules, executable as a command line tool or via a REST web API.
 - a set of processing modules providing the following conversions:
   * dtbook-to-zedai - Convert DTBook XML to ZedAI XML
   * upgrade-dtbook - DTBook utility for upgrading to DTBook 2005-3.
   * merge-dtbook - DTBook utility for merging two or more files.
   * zedai-to-epub3 - ZedAI to EPUB 3
   * daisy202-to-epub3 - DAISY 2.02 to EPUB3
 - a set of sample documents to test the provided conversions



Prerequisites                   
-------------------------------------------------------------------------------

Modules already include their dependent libraries and only require a recent
Java environment (Java SE 6 or later).

To get the latest version of Java, go to http://www.java.com/



Getting Started
-------------------------------------------------------------------------------

A) Command line tool:

 1. get the short help by running the launcher script 'dp2.sh' on
 Mac/Linux or 'dp2.bat' on Windows
 2. run with option '-c' to get the list of converters
 3. run 'dp2.sh -h a-converter-name' to get the list of arguments for a
 converter
 4. execute a job with the '-c converter-name -a list-of-arguments' options

For instance:

> dp2.bat -c dtbook-to-zedai -a in=file:/D:/path/to/dtbook.xml,o=file:/path/to/out/zedai.xml

will run the DTBook to ZedAI converter on Windows.


B) RESTful Web Service:

 1. start the web service by running './dp2ws.sh' on Mac/Linux or 'dp2ws.bat' on Windows
 2. the web service is available on http://localhost:8182/ws/
 3. get the list of converters by issuing a GET request on http://localhost:8182/ws/converters



Documentation
-------------------------------------------------------------------------------

Command line usage:

Option                                  Description                            
------                                  -----------                            
-a                                      list of arguments in the format        
                                          arg1=value1,arg2=value2 (only with - 
                                          c modifier)                          
-c [converter]                          List of available converters or if a   
                                          converter name is present it will be 
                                          executed using the -a arguments      
-h [converter]                          Showe this help or the help for the    
                                          given converter                      
-i                                      list of input ports in the format      
                                          portName1=file1,portName2=file2      
                                          (only with -x modifier)              
-l                                      List of available uris                 
-o                                      list of output ports in the format     
                                          portName1=file1,portName2=file2      
-p                                      list of parameters in the format       
                                          port1=param1=value1,                 
                                          port1=param2=value2 (only with -x    
                                          modifier)                            
-t                                      list of options in the format          
                                          opt1=value1,opt2=value2 (only with - 
                                          x modifier)                          
-x                                      xproc file to execute


The Web service API is documented on the Pipeline 2 development wiki:
 http://code.google.com/p/daisy-pipeline/wiki/WebServiceAPI

End-user and developer documentation drafts are available on the Pipeline 2
development wiki:
 http://code.google.com/p/daisy-pipeline/w/list?q=Type%3DDoc



Known limitations
-------------------------------------------------------------------------------

Framework:
 - the installation path must not contain space characters
 - the launcher scripts outputs a verbose header advertising the
 underlying launcher tool (OPS4J Pax Runner)
 - converter arguments must be absolute 'file:' URIs
 - the execution may take a significant startup time
 - the execution messages are only available in the debug log, and are
 not very user-friendly
 - no support for localization

DTBook to ZedAI:
 - see http://code.google.com/p/daisy-pipeline/issues/list?can=2&q=DTBook-to-ZedAI

EPUB 3 production:
 - No support for NCX
 - Supports only toc and page-list navigation
 - No support for font mangling
 - No support for encryption

DAISY 2.02 to EPUB 3:
 - see the EPUB 3 production limitations
 - No support for text-only EPUB 3 production

ZedAI to EPUB 3:
 - the produced EPUB 3 may be invalid
 - ZedAI to HTML is partially implemented
 - ZedAI Metadata is not extracted
 - HTML5 outline is modified when producing multiple HTML5 chunks
 - No support for ZedAI XInclude
 - No support for non-book ZedAI Profiles and additional features
 - No support for ZedAI containers

... and some known (and unknown) bugs!



Contact 
-------------------------------------------------------------------------------

If you wish to join the effort and contribute to the Pipeline 2 project, feel
free to contact the project lead (Romain Deltour) via email at
 rdeltour(at)gmail(dot)com

Or simply join us on the developers discussion list hosted on Google Groups:
 http://groups.google.com/group/daisy-pipeline-dev

