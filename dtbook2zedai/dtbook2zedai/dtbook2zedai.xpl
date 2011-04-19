<?xml version="1.0" encoding="UTF-8"?>

<p:declare-step version="1.0" name="dtbook2zedai" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:d2z="http://pipeline.daisy.org/ns/dtbook2zedai/" 
    xmlns:p2util="http://pipeline.daisy.org/ns/utilities/"
    xmlns:cd="http://www.daisy.org/daisypipeline/converter_descriptor"
    exclude-inline-prefixes="cx">
 	<p:documentation>
 		<cd:converter name="dtbook2zedai" version="1.0">
		<cd:description>Dtbook to zedai </cd:description>	
		<cd:arg  name="out"  type="option" bind="output" desc="the output file" optional="true"/> 	
		<cd:arg  name="input"  type="input" port="source"   desc="input dtbook file" />
		<cd:arg  name="parameters"  type="input" port="parameters"   desc="parameters to the dtbook 2 zedai" /> 
    	</cd:converter>   	
 	</p:documentation>   
    <!-- 
        
        The main entry point for the DTBook2ZedAI module.
        http://code.google.com/p/daisy-pipeline/wiki/DTBook2ZedAI
        
    -->

    <p:input port="source" primary="true" sequence="true"/>
    <p:input port="parameters" kind="parameter"/>

    <p:option name="output" select="''"/>

    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl"/>
    <p:import href="transform-dtbook2zedai.xpl"/>
    <p:import href="../utilities/metadata-generator/metadata-generator.xpl"/>
    <p:import href="../utilities/dtbook-merger/dtbook-merger.xpl"/>
    <p:import href="../utilities/dtbook-migrator/dtbook-migrator.xpl"/>
    
    <p:variable name="zedai-file"
        select="resolve-uri(
                    if ($output='') then concat(
                        if (matches(base-uri(/),'[^/]+\..+$'))
                        then replace(tokenize(base-uri(/),'/')[last()],'\..+$','')
                        else tokenize(base-uri(/),'/')[last()],'-zedai.xml')
                    else if (ends-with($output,'.xml')) then $output 
                    else concat($output,'.xml'), base-uri(/))">
        <p:pipe step="dtbook2zedai" port="source"/>

    </p:variable>

    <p:variable name="mods-file" select="replace($zedai-file, '.xml', '-mods.xml')"/>

    <p:variable name="css-file" select="replace($zedai-file, '.xml', '.css')"/>

    
    <!-- upgrade DTBook -->
    <p2util:dtbook-migrator name="upgrade-dtbook"/>
        
    <!-- Merge documents -->
    <p:count name="num-input-documents" limit="2"/>

    <p:choose name="dtbook-merger">
        <p:when test=".//c:result[. > 1]">
            <p:output port="result"/>
            <p2util:dtbook-merger>
                <p:input port="source">
                    <p:pipe port="result" step="upgrade-dtbook"/>
                </p:input>
            </p2util:dtbook-merger>
        </p:when>
        <p:otherwise>
            <p:output port="result"/>
            <p:identity>
                <p:input port="source">
                    <p:pipe port="result" step="upgrade-dtbook"/>
                </p:input>
            </p:identity>
        </p:otherwise>
    </p:choose>
    
    
    <!-- Validate DTBook Input-->
    <p:validate-with-relax-ng assert-valid="true" name="validate-dtbook">
        <p:input port="schema">
            <p:document href="schema/dtbook-2005-3.rng"/>
        </p:input>
        <p:input port="source">
            <p:pipe port="result" step="dtbook-merger"/>
        </p:input>
    </p:validate-with-relax-ng>
    
    <cx:message message="hello"/>
    
    <!-- create MODS metadata record -->
    <p2util:metadata-generator name="generate-metadata">
        <p:input port="source">
            <p:pipe step="validate-dtbook" port="result"/>
        </p:input>
        <p:with-option name="output" select="$mods-file"/>
     </p2util:metadata-generator>
    
    <!-- normalize and transform -->
    <d2z:transform-dtbook2zedai name="transform-dtbook2zedai">
        <p:input port="source">
            <p:pipe port="result" step="validate-dtbook"/>
        </p:input>
        <p:with-option name="css-filename" select="$css-file"/>
        <p:with-option name="mods-filename" select="$mods-file"/>
    </d2z:transform-dtbook2zedai>

    <!-- This is a step here instead of being an external library, because the following properties are required for generating CSS:
        * elements are stable (no more moving them around and potentially changing their IDs)
        * CSS information is still available
    -->
    <p:xslt name="generate-css">
        <p:input port="source">
            <p:pipe step="transform-dtbook2zedai" port="result"/>
        </p:input>
        <p:input port="stylesheet">
            <p:inline>
                <!-- This is a wrapper to XML-ify the raw CSS output.  XProc will only accept it this way. -->
                <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
                    <xsl:import href="generate-css.xsl"/>
                    <xsl:template match="/">
                        <css-data>
                            <xsl:apply-imports/>
                        </css-data>
                    </xsl:template>
                </xsl:stylesheet>
            </p:inline>
        </p:input>
    </p:xslt>
    
    <!-- Now that we've generated CSS, strip the style attributes -->
    <p:xslt name="remove-css-attributes">
        <p:input port="stylesheet">
            <p:document href="remove-css-attributes.xsl"/>
        </p:input>
        <p:input port="source">
            <p:pipe step="transform-dtbook2zedai" port="result"/>
        </p:input>
    </p:xslt>

    <!-- Validate the ZedAI output -->
    <p:validate-with-relax-ng name="validate-zedai" assert-valid="false">
        <p:input port="schema">
            <p:document href="schema/z3986a-book-0.8/z3986a-book.rng"/>
        </p:input>
        <p:input port="source">
            <p:pipe port="result" step="remove-css-attributes"/>
        </p:input>
    </p:validate-with-relax-ng>


    <!-- write files-->
    <!-- write the ZedAI file -->
    <p:store>
        <p:input port="source">
            <p:pipe step="validate-zedai" port="result"/>
        </p:input>
        <p:with-option name="href" select="$zedai-file"/>
    </p:store>

    <!-- write the CSS file (first strip it of its xml wrapper) -->
    <p:string-replace match="/text()" replace="''">
        <p:input port="source">
            <p:pipe step="generate-css" port="result"/>
        </p:input>
    </p:string-replace>
    <p:store method="text">
        <p:with-option name="href" select="$css-file"/>
    </p:store>
</p:declare-step>
