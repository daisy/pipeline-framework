<?xml version="1.0" ?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" 
		xmlns:cd="http://www.daisy.org/daisypipeline/converter_descriptor"
	name="myPipeline" version="1.0">

    <cd:converter name="testHello" version="1.0">
	<cd:description> Test xpl description</cd:description>	
	<cd:arg  name="in"  type="input" port="source" desc="input for hello process" optional="true"/> 	
	<cd:arg  name="out"  type="output" port="result" desc="the result file"/> 	
	<cd:arg  name="o"  type="option" bind="opt" desc="that kind of option that modifies the converter behaviour"/>
	<cd:arg  name="msg"  type="parameter" bind="msg" port="params" desc="msg to show" />
    </cd:converter>	

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
