###############################################################################
###            DAISY Pipeline 2 - 1.2 - March 16th, 2011                    ###
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

The package includes:

 - a modular runtime framework (based on OSGi) for the Pipeline 2 modules
 - a command line interface to execute pipeline scripts, in the "cli" directory
 - dedicated launchers for the Pipeline 2 Web Service, in the "fwk" directory
 - a set of processing modules providing the following conversions:
   * daisy202-to-epub3 - Convert a DAISY 2.02 fileset to EPUB3
   * dtbook-to-zedai - Convert a DTBook XML document to ZedAI XML
   * dtbook-to-epub3 - Convert a DTBook XML document to EPUB 3
   * zedai-to-epub3 - Convert a ZedAI document to EPUB 3
 - a set of sample documents to test the provided conversions, in the "samples"
   directory



3. Release Notes
-------------------------------------------------------------------------------

The package includes the 1.2 version of the project.

Changes since the last release:

 * Command-line tool
   * Fixed Issue 144: Added a -q|--quiet switch to disable logging of job
     execution messages
   * Fixed Issue 145: Added a setting in the CLI config file to enable debug
     logging statements
   * Fixed Issue 146: Local and remote modes now use two different ports (resp.
     8181 and 8182 by default)
   * Updated the "Alice in Wonderland" ZedAI sample to version 1.0 of the
     Z39.98 Book Profile
 * Web Service API
   * Added the experimental Client Admin API (currently backed by a local XML
     client store)
   * Improved the format of XML payloads, with a more consistent use of IDs and
     cross-resources links
   * Fixed Issue 156: WS now returns HTTP status 404 for non-existing scipts
   * Fixed an issue to prevent local-only options to be set in remote mode.
 * Framework
   * Fixed Issue 143: No longer set non-provided (optional) options to the
     empty string
   * Fixed Issue 152: Full-length error messages are now displayed
   * Fixed Issue 153: Added required local catalog entries to no longer need an
     internet connection at startup
   * Fixed Issue 154: Make the the framework nicely shutdown if the port is
     already bound
 * Modules
   * [daisy202-to-epub3] Fixed Issue 103: Support parent-directory paths when
     resolving SMILs to content in daisy202-to-epub3
   * [daisy202-to-epub3] Fixed Issue 104: Resolving multiple adjacent links
     no-longer produce unexpected results
   * [daisy202-to-epub3] Fixed Issue 142: The output-dir option is now properly
     tagged as of type anyDirURI
   * [daisy202-to-epub3] Improved performance
   * [daisy202-to-epub3] Improved error messages for invalid options
   * [daisy202-to-epub3] Better XHTML 1.0 to XHTML5 upgrade based on a new
     html-utils upgrader
   * [dtbook-to-zedai] Fixed Issue 138: Now produces documents in the
     Z39.98-2012 Book Profile version 1.0
   * [dtbook-to-zedai] Removed the "opt-" prefix for script options
   * [zedai-to-epub3] Fixed Issue 139: Now supports input documents in the
     Z39.98-2012 Book Profile version 1.0
   * [file-utils] Fixed Issue 124: Added pf:file-resolve-relative-uri to
     file-utils

The full list of changes can be found at:
 http://code.google.com/p/daisy-pipeline/w/ReleaseNotes



4. Prerequisites                   
-------------------------------------------------------------------------------

Modules already include their dependent libraries and only require a recent
Java environment (Java SE 6 or later).

To get the latest version of Java, go to http://www.java.com/

The "bin" directory of the Java Runtime Environment installation must be on the
system PATH. Refer to the documentation for more details on how to configure
this on your operating system.

On Mac and Linux, the command line tool requires a Ruby runtime environment
(version 1.8 or above). A Ruby runtime is already bundled in the executable on
Windows.


5. Getting Started
-------------------------------------------------------------------------------

A) Command line tool:

 1. get the short help by running the launcher script 'dp2' on
 Mac/Linux or 'dp2.exe' on Windows from the "cli" directory
 2. run 'dp2 help a-script-name' to get the detailed description of a script
 4. execute a job with the 'dp2 a-script-name' subcommand and specify the
 required options (as given with the 'dp2 help a-script-name' command)

For instance:

> cli\dp2.exe dtbook-to-zedai --i-source samples\dtbook\hauy_valid.xml
--x-output-dir "C:\Users\John Doe\Desktop\out"

will run the DTBook to ZedAI converter on Windows and will output the result in
the "out" directory on the desktop of the user named "John Doe".


B) RESTful Web Service:

 1. start the web service by running 'cli/dp2ws.sh' on Mac/Linux or
 'cli/dp2ws.bat' on Windows
 2. the web service is available on http://localhost:8182/ws/
 3. For example, get the list of scripts by issuing a GET request on
 http://localhost:8182/ws/scripts




6. Documentation
-------------------------------------------------------------------------------

Usage: dp2 command [options]

help				Shows this message or the command help

Script commands:

daisy202-to-epub3	Transforms a DAISY 2.02 publication into an EPUB3
					publication.
upgrade-dtbook		Upgrade a DTBook document from version 1.1.0, 2005-1, or
					2005-2 to version 2005-3.
merge-dtbook		Merge 2 or more DTBook documents.
dtbook-to-zedai		Transforms DTBook XML into ZedAI XML.
dtbook-to-epub3		Converts multiple dtbooks to epub3 format
zedai-to-epub3		Transforms a ZedAI (DAISY 4 XML) document into an EPUB 3
					publication.

Advanced commands:

result				Gets the zip file containing the job results
halt				Stops the WS
delete				Deletes a job
jobs				Shows the status for every job
status				Shows the detailed status for a single job

To get help for a command write:
dp2 help COMMAND


The Web service API is documented on the Pipeline 2 development wiki:
 http://code.google.com/p/daisy-pipeline/wiki/WebServiceAPI

A complete user guide is available on the Pipeline 2 development wiki:
 http://code.google.com/p/daisy-pipeline/wiki/UserGuideIntro



7. Known limitations
-------------------------------------------------------------------------------

Please refer to the issue tracker:
 http://code.google.com/p/daisy-pipeline/issues/list


8. Contact 
-------------------------------------------------------------------------------

If you want to join the effort and contribute to the Pipeline 2 project, feel
free to join us on the developers discussion list hosted on Google Groups:
 http://groups.google.com/group/daisy-pipeline-dev

or contact the project lead (Romain Deltour) via email at
 rdeltour (at) gmail (dot) com
