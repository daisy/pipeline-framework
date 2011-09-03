###############################################################################
###          DAISY Pipeline 2 - 1.0 Beta 3 - September 3d, 2011             ###
###############################################################################


 1. What is the DAISY Pipeline 2 ?
 2. Contents of the package
 3. Release notes
 4. Prerequisites
 5. Getting started
 6. Documentation
 7. Known limitations
 8. Contact


1. What is the DAISY Pipeline 2 ?
-------------------------------------------------------------------------------

The DAISY Pipeline 2 is an ongoing project to develop a next generation
framework for automated production of accessible materials for people with
print disabilities. It is the follow-up and total redesign of the original
DAISY Pipeline 1 project.

For more information see:
 - the project page: http://www.daisy.org/pipeline2
 - the development site: http://code.google.com/p/daisy-pipeline/


2. Contents of the package
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


3. Release Notes
-------------------------------------------------------------------------------

The changes since the last release are:

 * Framework
   * major overhaul of the core framework components
   * revamped XProc, Script and Job APIs
   * improved thread safety
   * updated the underlying XProc engine to the latest Calabash build
   * updated third-party dependencies (SLF4J+LogBack, Google Guava)
   * updated the OSGi runtime configuration
 * Web service
   * Updated the web service to the revamped APIs
   * Input files can now be sent as a ZIP
   * Improved validation of request formats
   * Sample Ruby-based client (available in the source repository)
 * Modules
   * all: updated the Script metadata
   * epub3-ocf-utils: fixed the ZIP step
   * dtbook-to-zedai: added referenced files copying
   * dtbook-to-zedai: fixed imggroup/caption issue
   * dtbook-to-zedai: minor fixes

The full list of changes can be found at:
 http://code.google.com/p/daisy-pipeline/w/ReleaseNotes

4. Prerequisites                   
-------------------------------------------------------------------------------

Modules already include their dependent libraries and only require a recent
Java environment (Java SE 6 or later).

To get the latest version of Java, go to http://www.java.com/



5. Getting Started
-------------------------------------------------------------------------------

A) Command line tool:

 1. get the short help by running the launcher script 'dp2.sh' on
 Mac/Linux or 'dp2.bat' on Windows
 2. run with option '-s' to get the list of scripts
 3. run 'dp2.sh -h script-name' to get the detailed description of a script
 converter
 4. execute a job with the '-s script-name -i list-of-sources -t list-of-options' options

For instance:

> dp2.bat -s dtbook-to-zedai -i source=D:/path/to/dtbook.xml -t opt-output-dir=file:/path/to/out/zedai.xml

will run the DTBook to ZedAI converter on Windows.


B) RESTful Web Service:

 1. start the web service by running './dp2ws.sh' on Mac/Linux or 'dp2ws.bat' on Windows
 2. the web service is available on http://localhost:8182/ws/
 3. get the list of scripts by issuing a GET request on http://localhost:8182/ws/scripts



6. Documentation
-------------------------------------------------------------------------------

Command line usage:

Option                                  Description                            
------                                  -----------                            
-h [converter]                          show this help or the help for the     
                                          given converter                      
-i <portName1=file1,...>                list of input ports                    
-l                                      list of available uris                 
-o <portName1=file1,...>                list of output ports                   
-p <port1=param1=value1,...>            list of parameters                     
-s [script name]                        list of available scripts or if a      
                                          script name is present it will be    
                                          executed                             
-t <opt1=value1,...>                    list of options                        
-x <XProc document>                     xproc file to execute


The Web service API is documented on the Pipeline 2 development wiki:
 http://code.google.com/p/daisy-pipeline/wiki/WebServiceAPI

End-user and developer documentation drafts are available on the Pipeline 2
development wiki:
 http://code.google.com/p/daisy-pipeline/w/list?q=Type%3DDoc



7. Known limitations
-------------------------------------------------------------------------------

Framework:
 - the installation path must not contain space characters
 - the execution messages are only available in the debug log, and are
 not very user-friendly
 - no support for localization

DTBook to ZedAI:
 - see http://code.google.com/p/daisy-pipeline/issues/list?can=2&q=DTBook-to-ZedAI

EPUB 3 production:
 - Supports only toc and page-list navigation
 - No support for font mangling
 - No support for encryption

DAISY 2.02 to EPUB 3:
 - see the EPUB 3 production limitations

ZedAI to EPUB 3:
 - no support for NCX generation
 - the produced EPUB 3 may be invalid
 - ZedAI to HTML is partially implemented
 - ZedAI Metadata is not extracted
 - HTML5 outline is modified when producing multiple HTML5 chunks
 - No support for ZedAI XInclude
 - No support for non-book ZedAI Profiles and additional features
 - No support for ZedAI containers

... and some known (and unknown) bugs!



8. Contact 
-------------------------------------------------------------------------------

If you wish to join the effort and contribute to the Pipeline 2 project, feel
free to contact the project lead (Romain Deltour) via email at
 rdeltour(at)gmail(dot)com

Or simply join us on the developers discussion list hosted on Google Groups:
 http://groups.google.com/group/daisy-pipeline-dev

