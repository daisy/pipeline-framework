<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
 
    <xsl:output method="xml"/>

    <xsl:template match="/">
        <juicers>
            <xsl:for-each select="//juicer[cost &lt; 100]">
                <xsl:copy-of select="." />
            </xsl:for-each>
        </juicers>
    </xsl:template>

</xsl:stylesheet>