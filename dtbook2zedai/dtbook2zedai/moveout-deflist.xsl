<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    exclude-result-prefixes="dtb" version="2.0">
    
    <xsl:import href="moveout-generic.xsl"/>
    
    <xsl:output indent="yes" method="xml"/>
    
    <xsl:template match="/">
        
        <xsl:message>move definition lists</xsl:message>
        <xsl:call-template name="test-and-move">
            <xsl:with-param name="doc" select="//dtb:dtbook[1]"/>
            
            <xsl:with-param name="target-element" select="'dl'" tunnel="yes"/>
            
            <xsl:with-param name="valid-parents" select="tokenize('annotation,prodnote,sidebar,address,covertitle,div,epigraph,imggroup,caption,code-block,
                kbd,li,note,img,blockquote,level,level1,level2,level3,level4,level5,level6,td,th,samp', ',')" tunnel="yes"/>
            
        </xsl:call-template>
        
    </xsl:template>       
    
</xsl:stylesheet>
