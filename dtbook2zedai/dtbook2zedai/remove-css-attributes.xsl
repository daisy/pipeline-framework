<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    version="2.0"
    xmlns:rend="http://www.daisy.org/ns/z3986/authoring/features/rend/"
    xmlns:its="http://www.w3.org/2005/11/its" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns="http://www.daisy.org/ns/z3986/authoring/">
    
    <xsl:output indent="yes" method="xml"/>
    
    <!-- identity template -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- discard these attributes -->
    <xsl:template match="@height"/>
    <xsl:template match="@width"/>
    <xsl:template match="@border"/>
    <xsl:template match="@cellspacing"/>
    <xsl:template match="@cellpadding"/>
    <xsl:template match="@align"/>
    <xsl:template match="@valign"/>
    
</xsl:stylesheet>
