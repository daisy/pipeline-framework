<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xd dtb" version="2.0">
    
    <xsl:output indent="yes" method="xml"/>
    
    <!-- identity template -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- change nested samps to spans -->
    <xsl:template match="dtb:samp[dtb:samp]">
        <xsl:element name="samp" namespace="http://www.daisy.org/z3986/2005/dtbook/">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name() = 'samp'">
                        <xsl:element name="span" namespace="http://www.daisy.org/z3986/2005/dtbook/">
                            <xsl:attribute name="role">example</xsl:attribute>
                            <xsl:apply-templates select="@*|node()"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
                
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
    
</xsl:stylesheet>
