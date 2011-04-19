###############################################################################
###             DAISY Pipeline 2 - DTBook to ZedAI module                   ###
###############################################################################



About the DTBook to ZedAI module
-------------------------------------------------------------------------------

The 'DTBook to ZedAI' module converts a single DTBook document into a ZedAI
document in the "Book Profile".

For more information on the ongoing development, see the dedicated wiki page:
   http://code.google.com/p/daisy-pipeline/wiki/DTBook2ZedAI


Demo
-------------------------------------------------------------------------------

Convert the provided "Great Painters" sample DTBook to a ZedAI document:

On Linux/Mac:

$ dtbook2zedai.sh -o greatpainter-zedai.xml sample/greatpainters.xml

On Windows:

> dtbook2zedai.bat -o greatpainter-zedai.xml sample\greatpainters.xml

The input DTBook and produced ZedAI are validated against their RelaxNG schemas.


Known limitations
-------------------------------------------------------------------------------

 - Only support a single DTBook document as input.
 - Satellite files (e.g. referenced images) are not copied through.
 - A limited set of DTBook content models are not properly handled.
