<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" version="1.0">
    <p:input port="source">
        <p:inline>
            <doc1>one</doc1>
        </p:inline>
        
        <p:inline>
            <doc2>two</doc2>
        </p:inline>
        
    </p:input>
    <p:output port="result" sequence="true"/>
    <p:identity/>
</p:declare-step>