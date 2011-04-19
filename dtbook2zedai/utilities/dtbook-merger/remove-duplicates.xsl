<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/" 
    xmlns:dc="http://purl.org/dc/terms/"
    version="2.0">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <!-- sort metadata -->
    <xsl:template match="dtb:head">
        
        <!-- save an identifier -->
        <xsl:variable name="identifier"
            select="dtb:meta[@name='dc:Identifier'][1]/@content"/>
        
        <xsl:variable name="unique-list"
            select="dtb:meta[not(@name=following::dtb:meta/@name)][not(@content=following::dtb:meta/@content)]"/>
        
        
        <!-- copy all non-duplicate metadata except identifiers -->
        <xsl:for-each select="$unique-list">
            
            <xsl:if test="not(@name = 'dc:Identifier') and not(@name = 'dtb:uid')">
                <xsl:copy-of select="."/>
            </xsl:if>
            
        </xsl:for-each>
        
        <!-- create our own identifier to represent the merged version of the book. -->
        <!-- TODO: this should be a user-customizable option -->
        <xsl:element name="meta" namespace="http://www.daisy.org/z3986/2005/dtbook/">
            <xsl:attribute name="dc:Identifier">
                <xsl:value-of select="concat($identifier, 'merged-book')"/>
            </xsl:attribute>
        </xsl:element>
        
    </xsl:template>
    
    <xsl:template match="dtb:book/dtb:frontmatter">
        <xsl:apply-templates/>
    </xsl:template>
    
    <!-- multiple docauthors allowed, just filter the duplicates -->
    <xsl:template match="dtb:docauthor">
        
        <xsl:if test="not(. = preceding-sibling::*)">
            <xsl:copy-of select="."/>
        </xsl:if>
        
    </xsl:template>
    
    <!-- copy the first occurrence and wrap the rest in <p> if they are different -->
    <xsl:template match="dtb:doctitle">
        
        <!-- if this is not a duplicate title -->
        <xsl:if test="not(. = preceding-sibling::*)">
            <xsl:choose>
                <!-- when it's the first doctitle element -->
                <xsl:when test="not(name() = preceding-sibling::*/name())">
                    <xsl:copy-of select="."/>
                </xsl:when>
                <!-- subsequent non-duplicate doctitle elements are wrapped in p -->
                <xsl:otherwise>
                    <xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/">
                        <xsl:apply-templates/>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        
    </xsl:template>
    
    <!-- identity template that strips out empty nodes -->
    <xsl:template match="@*|node()">
        <xsl:if test=". != '' or ./@* != ''">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>