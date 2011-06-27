<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:modA="http://www.example.org/module-A"
				xmlns:modB="http://www.example.org/module-B"
				xmlns:cd="http://www.daisy.org/ns/pipeline/converter"
				version="2.0">
	<cd:converter name="testHello" version="1.0">
		<cd:description> Test xpl description</cd:description>	
		<cd:arg  name="in"  type="input" port="source" desc="input for hello process" optional="true"/> 	
		<cd:arg  name="out"  type="output" port="result" desc="the result file"/> 	
		<cd:arg  name="o"  type="option" bind="opt" desc="that kind of option that modifies the converter behaviour"/>
		<cd:arg  name="msg"  type="parameter" bind="msg" port="params" desc="msg to show" />
    </cd:converter>	
	<xsl:import href="http://www.example.org/module-B/hello.xsl"/>

	<xsl:function name="modA:hello" as="xs:string">
		<xsl:param name="who" as="xs:string"/>
		<xsl:sequence select="modB:hello($who)"/>
	</xsl:function>
	
</xsl:stylesheet>
