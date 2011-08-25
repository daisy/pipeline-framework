<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="test-script" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:pxi="http://www.daisy.org/ns/pipeline/xproc/internal"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc" xmlns:tmp="http://www.daisy.org/ns/pipeline/tmp"
    xmlns:z="http://www.daisy.org/ns/z3986/authoring/"
    exclude-inline-prefixes="cx p c cxo px xd pxi z tmp">

    <p:documentation>
        <xd:short>short documentation</xd:short>
        <xd:detail>detailed description</xd:detail>
        <xd:author>
            <xd:name>Romain Deltour</xd:name>
            <xd:mailto>romain@example.com</xd:mailto>
            <xd:organization>DAISY Consortium</xd:organization>
        </xd:author>
        <xd:maintainer>Romain Deltour</xd:maintainer>
    </p:documentation>

    <p:input port="source" primary="true" sequence="true"/>
    <p:input port="parameters" kind="parameter"/>
    <p:output port="result" primary="true" sequence="true"/>

    <p:option name="output-file" required="true"/>
    <p:option name="an-option"/>

    <p:identity/>

</p:declare-step>
