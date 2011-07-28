<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" version="1.0">
    <p:input port="source" sequence="true"/>
         
    <p:output port="result"/>
    <p:wrap-sequence wrapper="docs"/>
</p:declare-step>