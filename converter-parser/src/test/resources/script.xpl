<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="unit-test-script"
    type="px:unit-test-script"
    xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
    xmlns:xd="http://www.daisy.org/ns/pipeline/doc">

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <h1 px:role="name">Unit Test Script</h1>
        <p px:role="desc">detail description</p>
        <a px:role="homepage" href="http://example.org/unit-test-script">homepage</a>
        <div px:role="author">
            <p px:role="name">John Doe</p>
            <p px:role="contact">john.doe@example.com</p>
            <p px:role="organization">ACME</p>
        </div>
    </p:documentation>
    
    <p:pipeinfo>
        <!--fake xproc elements in no namespace-->
        <input port="fake-input"/>
        <input port="fake-parameter" kind="parameter"/>
        <output port="fake-output"/>
        <option name="fake-option"/>
    </p:pipeinfo>

    <p:input port="source" primary="true" sequence="true" px:media-type="application/x-dtbook+xml">
        <p:documentation xmlns="http://www.w3.org/1999/xhtml">
            <p px:role="name">source name</p>
            <p px:role="desc">source description</p>
        </p:documentation>
    </p:input>
    
    <p:input port="source2"/>
    
    <p:input port="parameters" kind="parameter"/>
    
    <p:output port="result" primary="true" sequence="true" px:media-type="application/x-dtbook+xml">
        <p:documentation xmlns="http://www.w3.org/1999/xhtml">
            <p px:role="name">result name</p>
            <p px:role="desc">result description</p>
        </p:documentation>
    </p:output>
    
    <p:output port="result2"/>

    <p:option name="option1" select="." required="true" px:dir="output" px:type="anyDirURI">
        <p:documentation xmlns="http://www.w3.org/1999/xhtml">
            <p px:role="name">Option 1</p>
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
