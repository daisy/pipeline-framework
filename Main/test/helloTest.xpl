<?xml version="1.0" ?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" name="myPipeline" version="1.0">
 
   	
     
    <p:input port="parameters" kind="parameter"/>
        
        
    
    <p:output port="result" sequence="true"/>
        
    

    <p:xslt name="trans">
        <p:input port="source">
            <p:inline>
            	<foo/>
            </p:inline>
        </p:input>    
        <p:input port="stylesheet">
        
			<p:document href="http://www.example.org/module-hello/helloizer.xsl"/>
	        
        </p:input>
        <p:input port="parameters" kind="parameter">
            <p:pipe port="parameters" step="myPipeline"/>
        </p:input>
      
    </p:xslt>
   
    
</p:declare-step>