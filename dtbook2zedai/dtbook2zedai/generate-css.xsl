<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" version="2.0"
    xmlns:rend="http://www.daisy.org/ns/z3986/authoring/features/rend/"
    xmlns:its="http://www.w3.org/2005/11/its" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:z="http://www.daisy.org/ns/z3986/authoring/">
    
    <xsl:output method="text"/>
    
    <xsl:template match="/">
        
        <xsl:apply-templates select="//z:object"/>
        <xsl:apply-templates select="//z:table"/>
        <xsl:apply-templates select="//z:col"/>
        <xsl:apply-templates select="//z:colgroup"/>
        <xsl:apply-templates select="//z:th"/>
        <xsl:apply-templates select="//z:td"/>
        <xsl:apply-templates select="//z:tr"/>
        <xsl:apply-templates select="//z:thead"/>
        <xsl:apply-templates select="//z:tbody"/>
        <xsl:apply-templates select="//z:tfoot"/>
        
    </xsl:template>
    
    <xsl:template match="z:object">
        <xsl:if test="@height or @width">
            #<xsl:value-of select="@xml:id"/>{
                <xsl:if test="@height">
                    height: <xsl:value-of select="@height"/>;
                </xsl:if>
                <xsl:if test="@width">
                    width: <xsl:value-of select="@height"/>;
                </xsl:if>
            }
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="z:table">
        <xsl:if test="@width or @border or @cellspacing or @cellpadding">
            #<xsl:value-of select="@xml:id"/>{
            <xsl:if test="@width">
                width: <xsl:value-of select="@width"/>;
            </xsl:if>
            <xsl:if test="@border">
                border: <xsl:value-of select="@border"/>;
            </xsl:if>
            <xsl:if test="@cellspacing">
                cellspacing: <xsl:value-of select="@cellspacing"/>;
            </xsl:if>
            <xsl:if test="@cellpadding">
                cellpadding: <xsl:value-of select="@cellpadding"/>;
            </xsl:if>
            }
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="z:col | z:colgroup">
        <xsl:if test="@width or @align or @valign">
            #<xsl:value-of select="@xml:id"/>{
            <xsl:if test="@width">
                width: <xsl:value-of select="@width"/>;
            </xsl:if>
            <xsl:if test="@align">
                align: <xsl:value-of select="@align"/>;
            </xsl:if>
            <xsl:if test="@valign">
                valign: <xsl:value-of select="@valign"/>;
            </xsl:if>
            }
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="z:th | z:td | z:tr | z:tbody | z:tfoot | z:thead">
        <xsl:if test="@align or @valign">
            #<xsl:value-of select="@xml:id"/>{
            <xsl:if test="@align">
                align: <xsl:value-of select="@align"/>;
            </xsl:if>
            <xsl:if test="@valign">
                valign: <xsl:value-of select="@valign"/>;
            </xsl:if>
            }
        </xsl:if>
    </xsl:template>
    
    
</xsl:stylesheet>
