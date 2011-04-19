<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="dtbook-migrator" type="p2util:dtbook-migrator"
    xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" 
    xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:p2util="http://pipeline.daisy.org/ns/utilities/" 
    xmlns:dc="http://purl.org/dc/terms/"
    exclude-inline-prefixes="cx">
    <!-- 
        
        Upgrades a DTBook file from version 1.1.0, 2005-1, or 2005-2 to 2005-3.  This is part of the utilities module.
        This was ported from the Pipeline 1.  It has a simpler interface, being only concerned with 2005-3 output.
        
        TODO: 
        * copy referenced resources (such as images)
    -->
    
    <p:input port="source" primary="true"/>
    <p:input port="parameters" kind="parameter"/>
    <p:output port="result">
        <p:pipe port="result" step="validate-dtbook"/>
    </p:output>
    
    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl"/>
    
    <p:variable name="version" select="dtb:dtbook/@version"/>
    
    <cx:message>
        <p:with-option name="message" select="concat('Input document version: ', $version)"/>    
    </cx:message>
    
    <p:choose name="main">
        <p:when test="$version = '1.1.0'">
            <p:output port="result"/>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook110to2005-1.xsl"/>
                </p:input>
            </p:xslt>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook2005-1to2.xsl"/>
                </p:input>
            </p:xslt>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook2005-2to3.xsl"/>
                </p:input>
            </p:xslt>
        </p:when>
        <p:when test="$version = '2005-1'">
            <p:output port="result"/>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook2005-1to2.xsl"/>
                </p:input>
            </p:xslt>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook2005-2to3.xsl"/>
                </p:input>
            </p:xslt>
        </p:when>
        <p:when test="$version = '2005-2'">
            <p:output port="result"/>
            <p:xslt>
                <p:input port="stylesheet">
                    <p:document href="dtbook2005-2to3.xsl"/>
                </p:input>
            </p:xslt>
        </p:when>
        <p:when test="$version = '2005-3'">
            <p:output port="result"/>
            <cx:message>
                <p:with-option name="message" select="concat('File is already the most recent version: ', $version)"/>
            </cx:message>
            <p:identity/>
        </p:when>
        <p:otherwise>
            <p:output port="result"/>
            <cx:message>
                <p:with-option name="message" select="concat('Version not identified: ', $version)"/>
            </cx:message>
            <p:identity/>
        </p:otherwise>
    </p:choose>
    
    
    
    <p:validate-with-relax-ng name="validate-dtbook">
        <p:input port="source">
            <p:pipe port="result" step="main"/>
        </p:input>
        <p:input port="schema">
            <p:document href="schema/dtbook-2005-3.rng"/>
        </p:input>
    </p:validate-with-relax-ng>
    
</p:declare-step>
