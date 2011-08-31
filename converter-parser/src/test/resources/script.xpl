<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="test-script" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc">

    <p:documentation>
        <xd:short>short description</xd:short>
        <xd:detail>detail description</xd:detail>
        <xd:homepage>homepage</xd:homepage>
        <xd:author>
            <xd:name>John Doe</xd:name>
            <xd:mailto>john.doe@example.com</xd:mailto>
            <xd:organization>ACME</xd:organization>
        </xd:author>
    </p:documentation>
    
    <p:pipeinfo>
        <!--fake xproc elements in no namespace-->
        <input port="fake-input"/>
        <input port="fake-parameter" kind="parameter"/>
        <output port="fake-output"/>
        <option name="fake-option"/>
    </p:pipeinfo>

    <p:input port="source" primary="true" sequence="true" px:media-type="application/x-dtbook+xml">
        <p:documentation>
            <xd:short>source name</xd:short>
            <xd:detail>source description</xd:detail>
        </p:documentation>
    </p:input>
    
    <p:input port="source2"/>
    
    <p:input port="parameters" kind="parameter"/>
    
    <p:output port="result" primary="true" sequence="true" px:media-type="application/x-dtbook+xml">
        <p:documentation>
            <xd:short>result name</xd:short>
            <xd:detail>result description</xd:detail>
        </p:documentation>
    </p:output>
    
    <p:output port="result2"/>

    <p:option name="option1" select="." required="true" px:dir="output" px:type="anyDirURI">
        <p:documentation>
            <xd:short>Option 1</xd:short>
        </p:documentation>
    </p:option>
    
    <p:declare-step type="foo">
        <p:input port="source"/>
        <p:input port="params" kind="parameter"/>
        <p:output port="result"/>
        <p:option name="option"></p:option>
        <p:identity/>
    </p:declare-step>

    <p:documentation>identity step</p:documentation>
    <p:identity/>

</p:declare-step>
