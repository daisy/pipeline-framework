<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xd dtb" version="2.0">

    <xd:doc>
        <xd:desc>This stylesheet un-nests linegroup and lines without regard to validity.</xd:desc>
    </xd:doc>

    <xsl:output indent="yes" method="xml"/>

    <!-- identity template -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="dtb:line">
        <xsl:call-template name="line"/>
    </xsl:template>

    <xsl:template name="line">
        <xsl:param name="line" select="."/>
        <xsl:param name="attributes" select="@*"/>
        <xsl:choose>
            <!--xsl:when test="ancestor-or-self::dtb:code">
                this is how to ignore parsing of certain elements, don't think it's needed for DTBook?
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
                </xsl:when-->
            <xsl:when test="not(ancestor::dtb:linegroup)">
                <!-- treat runaway lines as linegroups -->
                <xsl:call-template name="linegroup">
                    <xsl:with-param name="attributes" select="@*"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="descendant::dtb:line">
                <!--
                    when line has descendant lines,
                    then the line will have to be split into multiple lines
                -->
                <xsl:for-each-group select="*|text()"
                    group-adjacent="not(self::dtb:line) and not(self::dtb:pagenum) or descendant::dtb:line or normalize-space(string-join(self::text(),''))">
                    <xsl:choose>
                        <xsl:when
                            test="not(current-group()/node()) and not(normalize-space(current-group()))">
                            <!-- ignore empty element -->
                        </xsl:when>
                        <xsl:when test="current-grouping-key()">
                            <xsl:choose>
                                <xsl:when test="descendant::dtb:line">
                                    <xsl:for-each select="current-group()">
                                        <xsl:call-template name="line">
                                            <xsl:with-param name="attributes" select="$attributes"/>
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:when test="self::dtb:line or self::dtb:pagenum">
                                    <xsl:copy>
                                        <xsl:apply-templates select="current-group()"/>
                                    </xsl:copy>
                                </xsl:when>
                                <xsl:otherwise>
                                    <line xmlns="http://www.daisy.org/z3986/2005/dtbook/">
                                        <xsl:apply-templates select="current-group()"/>
                                    </line>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="current-group()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each-group>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dtb:linegroup">
        <xsl:call-template name="linegroup"/>
    </xsl:template>

    <xsl:template name="linegroup">
        <xsl:param name="attributes" select="@*"/>
        <xsl:variable name="linegroup" select="."/>
        <xsl:choose>
            <!--xsl:when test="ancestor-or-self::dtb:code">
                this is how to ignore parsing of certain elements, don't think it's needed for DTBook?
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
                </xsl:when-->
            <xsl:when
                test="count(child::*[not((self::dtb:line or self::dtb:pagenum) and not(descendant::*[self::dtb:linegroup]))])>0">
                <!--
                    when linegroup has children which are neither lines nor pagenums, or
                    children which has lines or pagenums with descendant lines or linegroups,
                    then the linegroup will have to be split into multiple linegroups
                -->
                <xsl:element
                    name="{if (self::dtb:line or self::dtb:linegroup) then 'div' else name()}"
                    namespace="http://www.daisy.org/z3986/2005/dtbook/">
                    <xsl:apply-templates select="@id|$attributes"/>
                    <xsl:for-each-group select="*|text()[normalize-space()]"
                        group-adjacent="(self::dtb:line or self::dtb:pagenum) and not(descendant::*[self::dtb:linegroup])">
                        <xsl:choose>
                            <xsl:when
                                test="not(current-group()/node()) and not(normalize-space(current-group()))">
                                <!-- ignore empty element -->
                            </xsl:when>
                            <xsl:when test="current-grouping-key()">
                                <linegroup xmlns="http://www.daisy.org/z3986/2005/dtbook/">
                                    <xsl:apply-templates select="@*[not(name()='id')]|$attributes"/>
                                    <xsl:for-each select="current-group()">
                                        <xsl:call-template name="line"/>
                                    </xsl:for-each>
                                </linegroup>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:for-each select="current-group()">
                                    <xsl:choose>
                                        <xsl:when
                                            test="not(self::dtb:line) and not(self::dtb:linegroup)">
                                            <xsl:call-template name="linegroup"/>
                                        </xsl:when>
                                        <xsl:when test="self::dtb:linegroup or self::dtb:line">
                                            <xsl:call-template name="linegroup">
                                                <xsl:with-param name="attributes"
                                                  select="@* | $linegroup/@*[not(name()='id')]"/>
                                            </xsl:call-template>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:for-each>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each-group>
                </xsl:element>
            </xsl:when>
            <xsl:when test="self::dtb:linegroup">
                <!-- linegroup has only line or pagenum children, which in turn has no line or linegroup descendants -->
                <xsl:if test="normalize-space()">
                    <xsl:copy>
                        <xsl:apply-templates select="@*"/>
                        <xsl:for-each select="*|text()">
                            <xsl:choose>
                                <xsl:when test="not(node()) and not(normalize-space())">
                                    <xsl:apply-templates/>
                                    <!-- ignore empty linegroup -->
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:call-template name="line"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:copy>
                </xsl:if>
            </xsl:when>
            <xsl:when test="self::dtb:line or self::dtb:pagenum">
                <!-- a line or pagenum has been designated to be a linegroup through a call-template; wrap it in <linegroup/> -->
                <xsl:choose>
                    <xsl:when test="descendant::dtb:line">
                        <div xmlns="http://www.daisy.org/z3986/2005/dtbook/">
                            <xsl:apply-templates select="@*|$attributes"/>
                            <xsl:for-each select="node()">
                                <xsl:call-template name="linegroup"/>
                            </xsl:for-each>
                        </div>
                    </xsl:when>
                    <xsl:otherwise>
                        <linegroup xmlns="http://www.daisy.org/z3986/2005/dtbook/">
                            <xsl:apply-templates select="$attributes"/>
                            <xsl:copy-of select="."/>
                        </linegroup>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="self::text()">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <!--
                    same as test="not(self::dtb:linegroup)"
                    an element that is neither a linegroup, a line nor a pagenum has been designated to be a linegroup
                    through a call-template; wrapt it in <linegroup><line/></linegroup
                -->
                <xsl:copy>
                    <xsl:apply-templates select="@*|$attributes"/>
                    <xsl:for-each select="node()">
                        <xsl:call-template name="linegroup"/>
                    </xsl:for-each>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
