<?xml version="1.0" ?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" name="myPipeline" version="1.0">
    <p:input port="source"/>
    <p:input port="xslt"/>
    <p:input port="parameters" kind="parameter"/>
        
        
    
    <p:output port="result" sequence="true"/>
        
    

    <p:xslt name="trans">
        <p:input port="source">
            <p:pipe port="source" step="myPipeline"></p:pipe>
        </p:input>    
        <p:input port="stylesheet">
            <p:pipe port="xslt" step="myPipeline"></p:pipe>    
        </p:input>
        <p:input port="parameters" kind="parameter">
            <p:pipe port="parameters" step="myPipeline"/>
        </p:input>
      
    </p:xslt>
   
    
</p:declare-step>
