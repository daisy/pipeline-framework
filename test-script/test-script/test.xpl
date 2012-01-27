<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="test-script" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" 
    xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:pxi="http://www.daisy.org/ns/pipeline/xproc/internal"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc" xmlns:tmp="http://www.daisy.org/ns/pipeline/tmp"
    xmlns:z="http://www.daisy.org/ns/z3986/authoring/"
    xmlns:saxontest="http://www.example.org/saxontest"
    exclude-inline-prefixes="cx p c cxo px xd pxi z tmp">

    <p:documentation>
        <xd:short>test-script</xd:short>
        <xd:detail>detailed description</xd:detail>
        <xd:author>
            <xd:name>Romain Deltour</xd:name>
            <xd:mailto>romain@example.com</xd:mailto>
            <xd:organization>DAISY Consortium</xd:organization>
        </xd:author>
        <xd:maintainer>Romain Deltour</xd:maintainer>
    </p:documentation>
 	<p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl">
        <p:documentation>
            <xd:short>Calabash extension steps.</xd:short>
        </p:documentation>
    </p:import>
    <p:input port="source" primary="true" sequence="true"/>
    <p:input port="parameters" kind="parameter"/>
    <p:output port="result" primary="true" sequence="true"/>

    <p:option name="output-file" />
    <p:option name="an-option"/>
	 
    <p:identity/>
    <cx:message>
        <p:with-option name="message" select="saxontest:hello('world')"/>
    </cx:message>

	
</p:declare-step>
