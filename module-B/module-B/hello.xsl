<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:modA="http://www.example.org/module-A"
				xmlns:modB="http://www.example.org/module-B"
				version="2.0">
	
	<xsl:import href="http://www.example.org/module-B/hello.xsl"/>
	
	<xsl:function name="modA:hello" as="xs:string">
		<xsl:param name="who" as="xs:string"/>
		<xsl:sequence select="modB:hello($who)"/>
	</xsl:function>
	
</xsl:stylesheet>
