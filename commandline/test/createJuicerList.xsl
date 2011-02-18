<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
 
    <xsl:output method="html"/>

    <xsl:param name="foo"/>
    <xsl:template match="/">
        <html>
            <body>
                <h1>Juicers - <xsl:value-of select="$foo" /></h1>
                <ul>
                    <xsl:for-each select="//juicer/name">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="name">
        <li>
            <xsl:value-of select="." />
        </li>
    </xsl:template>

</xsl:stylesheet>