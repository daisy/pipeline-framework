<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step type="px:fail" version="1.0"
                xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
                xmlns:d="http://www.daisy.org/ns/pipeline/data">
    
    <p:output port="validation-status" px:media-type="application/vnd.pipeline.status+xml">
        <p:inline>
            <d:validation-status result="error"/>
        </p:inline>
    </p:output>
    
    <p:option name="output-dir" required="true" px:output="result" px:type="anyDirURI"/>

    <p:store>
        <p:input port="source">
            <p:inline>
                <hello world=""/>
            </p:inline>
        </p:input>
        <p:with-option name="href" select="concat($output-dir,'/hello.xml')"/>
    </p:store>
    
</p:declare-step>

