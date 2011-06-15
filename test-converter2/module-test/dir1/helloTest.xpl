<?xml version="1.0" ?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
xmlns:cd="http://www.daisy.org/ns/pipeline/converter"
 
name="myPipeline" version="1.0">
 <p:documentation>
 <cd:converter name="testHello" version="1.0">
	<cd:description> Greeting generator</cd:description>	
	<cd:arg  name="out"  type="output" port="result" desc="the result file"/> 	
	<cd:arg  name="msg"  type="parameter" bind="msg" port="parameters" desc="msg to show" />
    </cd:converter>   	
 </p:documentation>    
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