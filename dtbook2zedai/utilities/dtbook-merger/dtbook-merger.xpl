<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step version="1.0" name="dtbook-merger" type="p2util:dtbook-merger"
    xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" 
    xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:p2util="http://pipeline.daisy.org/ns/utilities/" 
    
    exclude-inline-prefixes="cx">
    <!-- 
        Merges two or more DTBook files.  This is part of the utilities module.
        
        TODO: 
         * copy referenced resources (such as images)
         * deal with xml:lang (either copy once and put in dtbook/@xml:lang or, if different languages are used, copy the @xml:lang attr into the respective sections.
    -->

    <p:input port="source" primary="true" sequence="true"/>
    <p:input port="parameters" kind="parameter"/>
    <p:output port="result" primary="true">
        <p:pipe port="result" step="validate-zedai"/>
    </p:output>
    
    <p:option name="output" select="''"/>

    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl"/>
    
    <p:for-each name="validate-input">
        <p:output port="result">
            <p:pipe step="ident" port="result"/>
        </p:output>

        <p:iteration-source select="/"/>

        <p:validate-with-relax-ng>
            <p:input port="schema">
                <p:document href="./schema/dtbook-2005-3.rng"/>
            </p:input>
        </p:validate-with-relax-ng>

        <p:identity name="ident"/>

    </p:for-each>

    <p:for-each name="for-each-head">
        <p:iteration-source select="//dtb:dtbook/dtb:head/*">
            <p:pipe port="result" step="validate-input"/>
        </p:iteration-source>
        <p:output port="result"/>

        <p:identity/>
    </p:for-each>

    <p:wrap-sequence name="wrap-head" wrapper="head"
        wrapper-namespace="http://www.daisy.org/z3986/2005/dtbook/">
        <p:input port="source">
            <p:pipe step="for-each-head" port="result"/>
        </p:input>
    </p:wrap-sequence>

    <p:for-each name="for-each-frontmatter">
        <p:output port="result"/>
        <p:iteration-source select="//dtb:dtbook/dtb:book/dtb:frontmatter/*">
            <p:pipe port="result" step="validate-input"/>
        </p:iteration-source>
        <p:identity/>
    </p:for-each>

    <p:wrap-sequence name="wrap-frontmatter" wrapper="frontmatter"
        wrapper-namespace="http://www.daisy.org/z3986/2005/dtbook/">
        <p:input port="source">
            <p:pipe step="for-each-frontmatter" port="result"/>
        </p:input>
    </p:wrap-sequence>

    <p:for-each name="for-each-bodymatter">
        <p:output port="result"/>
        <p:iteration-source select="//dtb:dtbook/dtb:book/dtb:bodymatter/*">
            <p:pipe port="result" step="validate-input"/>
        </p:iteration-source>
        <p:identity/>
    </p:for-each>

    <p:wrap-sequence name="wrap-bodymatter" wrapper="bodymatter"
        wrapper-namespace="http://www.daisy.org/z3986/2005/dtbook/">
        <p:input port="source">
            <p:pipe step="for-each-bodymatter" port="result"/>
        </p:input>
    </p:wrap-sequence>

    <p:for-each name="for-each-rearmatter">
        <p:output port="result"/>
        <p:iteration-source select="//dtb:dtbook/dtb:book/dtb:rearmatter/*">
            <p:pipe port="result" step="validate-input"/>
        </p:iteration-source>
        <p:identity/>
    </p:for-each>

    <p:wrap-sequence name="wrap-rearmatter" wrapper="rearmatter"
        wrapper-namespace="http://www.daisy.org/z3986/2005/dtbook/">
        <p:input port="source">
            <p:pipe step="for-each-rearmatter" port="result"/>
        </p:input>
    </p:wrap-sequence>

    <p:wrap-sequence wrapper="dtbook" wrapper-namespace="http://www.daisy.org/z3986/2005/dtbook/">
        
        <p:input port="source">
            <p:pipe step="wrap-head" port="result"/>
            <p:pipe step="wrap-frontmatter" port="result"/>
            <p:pipe step="wrap-bodymatter" port="result"/>
            <p:pipe step="wrap-rearmatter" port="result"/>
        </p:input>
    </p:wrap-sequence>

    <p:add-attribute match="/dtb:dtbook" attribute-name="version" attribute-value="2005-3"/>
    
    <p:xslt>
        <p:input port="stylesheet">
            <p:document href="remove-duplicates.xsl"/>
        </p:input>
    </p:xslt>

    <p:validate-with-relax-ng name="validate-dtbook">
        <p:input port="schema">
            <p:document href="./schema/dtbook-2005-3.rng"/>
        </p:input>
    </p:validate-with-relax-ng>
    
</p:declare-step>
