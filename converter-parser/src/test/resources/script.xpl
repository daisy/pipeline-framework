<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="test-script" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc">

    <p:documentation>
        <xd:short>short description</xd:short>
        <xd:detail>detail description</xd:detail>
        <xd:author>
            <xd:name>John Doe</xd:name>
            <xd:mailto>john.doe@example.com</xd:mailto>
            <xd:organization>ACME</xd:organization>
        </xd:author>
    </p:documentation>

    <p:input port="parameters" kind="parameter"/> 

    <p:input port="source" primary="true" sequence="true" px:media-type="application/x-dtbook+xml">
        <p:documentation>
            <xd:short>source port</xd:short>
        </p:documentation>
    </p:input>

    <p:option name="option1" select="." required="true" px:dir="output" px:type="anyDirURI">
        <p:documentation>
            <xd:short>Option 1</xd:short>
        </p:documentation>
    </p:option>

    <p:documentation>identity step</p:documentation>
    <p:identity/>

</p:declare-step>
