<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" version="1.0"
    exclude-inline-prefixes="c p">
    <p:option name="opt"  required="true"></p:option>
    <p:input port="source">
        <p:inline><doc>void</doc></p:inline>
    </p:input>
     <p:output port="result"/>
    
    
    <p:string-replace match="//doc/text()" >
        <p:with-option name="replace" select="concat('&quot;',$opt,'&quot;')"/> 
    </p:string-replace>
    
    <p:identity></p:identity>
</p:declare-step>