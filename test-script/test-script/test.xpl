<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="test-script" type="px:test-script" 
    xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" 
    xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:pxi="http://www.daisy.org/ns/pipeline/xproc/internal"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc" 
    xmlns:tmp="http://www.daisy.org/ns/pipeline/tmp"
    xmlns:z="http://www.daisy.org/ns/z3986/authoring/"
    xmlns:saxontest="http://www.example.org/saxontest"
    exclude-inline-prefixes="cx p c cxo px xd pxi z tmp">

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <h1 px:role="name">test script</h1>
        <p px:role="desc">Detailed description</p>
        <a px:role="homepage" href="http://daisy.org/pipeline/wiki/test-script">Module homepage</a>
        <div px:role="author">
            <p px:role="name">Romain Deltour</p>
            <p px:role="contact">romain@example.com</p>
            <p px:role="organization">DAISY Consortium</p>
        </div>
    </p:documentation>
 	
    <p:input port="source" primary="true" sequence="true"/>
    <p:input port="parameters" kind="parameter"/>
    <p:output port="result" primary="true" sequence="true"/>

    <p:option name="output-file" />
    <p:option name="an-option"/>
	
    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl">
        <p:documentation xmlns="http://www.w3.org/1999/xhtml">
            <p px:role="desc">Calabash extension steps.</p>
        </p:documentation>
    </p:import>
    
    <p:identity/>
    <cx:message>
        <p:with-option name="message" select="saxontest:hello('world')"/>
    </cx:message>

	
</p:declare-step>
