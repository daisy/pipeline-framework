<?xml version="1.0" ?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" 
		xmlns:cd="http://www.daisy.org/daisypipeline/converter_descriptor"
	name="myPipeline" version="1.0">

    <cd:converter name="dtbook-to-zedai" version="1.0" xmlns:cd="http://www.daisy.org/ns/pipeline/converter">
 	 <cd:description>Convert DTBook XML to ZedAI XML</cd:description>  
  	<cd:arg  name="in"
           desc="DTBook input file(s)"
           bind="source"
           bind-type="port" 
           optional="false"
           dir="input"
           sequence="true"
           media-type="application/x-dtbook+xml"/>
  		<cd:arg  name="o"
           desc="Output file path"
           bind="output-file"
           bind-type="option"
           optional="false"
           dir="output"
           type="anyFileURI"
           media-type="application/z3986-auth+xml"/>
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
