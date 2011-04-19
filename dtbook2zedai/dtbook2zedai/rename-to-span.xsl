<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:its="http://www.w3.org/2005/11/its" exclude-result-prefixes="xs" version="2.0">

    <xsl:output indent="yes" method="xml"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- transform these elements to spans so that they will be normalized as if they were spans
        this works for elements with dtbook inline content models.
    -->

   <xsl:template match="lic">
       <xsl:call-template name="element2span"/>
   </xsl:template>
    
    <xsl:template match="dd/p | dd/address">
        <xsl:call-template name="element2span"/>
    </xsl:template>
    <xsl:template match="dd/dateline">
        <span role="time">
            <xsl:call-template name="copy-attrs"/>
        </span>
    </xsl:template>
    <xsl:template match="dd/author">
        <span role="author">
            <xsl:call-template name="copy-attrs"/>
        </span>
    </xsl:template>
    
    <xsl:template match="abbr/code | acronym/code | dt/code | sub/code | sup/code | w/code">
        <xsl:call-template name="element2span"/>
    </xsl:template>

    <xsl:template
        match="abbr/noteref | acronym/noteref | dt/noteref | sub/noteref | sub/noteref | w/noteref">
        <!-- TODO: warn about loss of data -->
        <xsl:call-template name="element2span"/>
    </xsl:template>

    <xsl:template
        match="abbr/annoref | acronym/annoref | dt/annoref | sub/annoref | sub/annoref | w/annoref">
        <!-- TODO: warn about loss of data -->
        <xsl:call-template name="element2span"/>
    </xsl:template>

    <xsl:template
        match="abbr/kbd | acronym/kbd | dt/kbd | sub/kbd | sup/kbd | w/kbd">
        <xsl:call-template name="element2span"/>
    </xsl:template>
    

    <xsl:template match="abbr/q | acronym/q | dt/q | sub/q | sup/q | w/q | strong/q">
        <xsl:call-template name="element2span"/>
    </xsl:template>

    <xsl:template match="abbr/sent | acronym/sent | dt/sent | sub/sent | sub/sent">
        <span role="sentence">
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="sub/acronym | sub/acronym | w/acronym">
        <span>
            <xsl:choose>
                <xsl:when test="@pronounce = 'yes'">
                    <xsl:attribute name="role">acronym</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="role">initialism</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>
    
    <xsl:template match="sub/abbr | sub/abbr | w/abbr">
        <span role="truncation">
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>    
        </span>
        
    </xsl:template>
    
    <xsl:template match="sub/w | sup/w">
        <span role="word">
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>
    
    <xsl:template match="a/samp | abbr/samp | acronym/samp | author/samp | bdo/samp | bridgehead/samp | byline/samp | 
        cite/samp | dateline/samp | dd/samp | dfn/samp | dt/samp | 
        docauthor/samp | doctitle/samp | em/samp | h1/samp | h2/samp | h3/samp | h4/samp | h5/samp | h6/samp | hd/samp | 
        line/samp | p/samp | q/samp | samp/samp | sent/samp | span/samp | strong/samp | sub/samp | sup/samp | 
        title/samp | w/samp">
                <span role="example">
                    <xsl:call-template name="copy-attrs"/>
                    <xsl:apply-templates/>
                </span>
    </xsl:template>
    
    <xsl:template
        match="abbr/dfn | acronym/dfn | dt/dfn | sub/dfn | sub/dfn | w/dfn">
        <xsl:call-template name="element2span"/>
    </xsl:template>
    
    <xsl:template
        match="abbr/a | acronym/a | dt/a | sub/a | sup/a | w/a">
        <!-- TODO: warn about loss of data -->
        <xsl:call-template name="element2span"/>
    </xsl:template>
    
    <xsl:template match="bdo">
        <span its:dir="{@dir}">
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>
    
    
    <!-- TODO: check if we should check <em> too -->
    <!-- TODO: why did we use @property and not @role?  -->
    <xsl:template match="abbr/cite | acronym/cite | dt/cite | sub/cite | sup/cite | w/cite | strong/cite">
        <!-- generate an ID, we might need it -->
        <xsl:variable name="citeID" select="generate-id()"/>

        <span>
            <xsl:call-template name="copy-attrs"/>

            <!-- if no ID, then give a new ID -->
            <xsl:choose>
                <xsl:when test="@id"/>
                <xsl:otherwise>
                    <xsl:attribute name="id">
                        <xsl:value-of select="$citeID"/>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="./title">
                <span property="title">
                    <xsl:attribute name="about">
                        <xsl:choose>
                            <xsl:when test="@id">
                                <xsl:value-of select="@id"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$citeID"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </span>
            </xsl:if>
            <xsl:if test="./author">
                <span property="author">
                    <xsl:attribute name="about">
                        <xsl:choose>
                            <xsl:when test="@id">
                                <xsl:value-of select="@id"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$citeID"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </span>
            </xsl:if>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <!-- change nested samps to spans -->
    <xsl:template match="samp[samp]">
        <xsl:element name="samp">
            <xsl:for-each select="node()">
                <xsl:choose>
                    <xsl:when test="local-name() = 'samp'">
                        <xsl:element name="span">
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
    
    <!-- generic conversion to span; no roles applied and no special treatment of child elements -->
    <xsl:template name="element2span">
        <span>
            <xsl:call-template name="copy-attrs"/>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template name="copy-attrs">
        <xsl:if test="@id">
            <xsl:attribute name="xml:id" select="@id"/>
        </xsl:if>
        <xsl:copy-of select="@xml:space"/>
        <xsl:copy-of select="@class"/>
        <xsl:copy-of select="@xml:lang"/>
        <xsl:if test="@dir">
            <xsl:attribute name="its:dir" select="@dir"/>
        </xsl:if>

    </xsl:template>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
