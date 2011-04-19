<?xml version="1.0" encoding="UTF-8"?>

<p:declare-step version="1.0" name="transform-dtbook2zedai" type="d2z:transform-dtbook2zedai"
    xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step"
    xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:d2z="http://pipeline.daisy.org/ns/dtbook2zedai/" exclude-inline-prefixes="cx">


    <!-- 
        
        Transforms a file from DTBook-2005-3 to ZedAI.
        Is only concerned with elements and attributes; does not store anything or extract any additional data (css, meta).        
    -->

    <!-- input must be a valid DTBook 2005-3document -->
    <p:input port="source" primary="true"/>

    <p:input port="parameters" kind="parameter"/>

    <!-- output is ZedAI, not valid -->
    <p:output port="result" primary="true">
        <p:pipe port="result" step="translate-dtbook2zedai"/>
    </p:output>

    <p:option name="css-filename" required="true"/>
    <p:option name="mods-filename" required="true"/>

    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl"/>

    <p:group name="rename-elements">
        <p:output port="result"/>
            
        <!-- preprocess certain inline elements by making them into spans -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="rename-to-span.xsl"/>
            </p:input>
        </p:xslt>

        <!-- identify block-level code/kbd elements vs phrase-level -->
        <p:xslt name="code">
            <p:input port="stylesheet">
                <p:document href="rename-code-kbd.xsl"/>
            </p:input>
        </p:xslt>
    </p:group>

    <p:group name="convert-elements">
        <p:output port="result"/>
        <!-- convert br to ln -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="convert-linebreaks.xsl"/>
            </p:input>
            <p:input port="source"><p:pipe step="rename-elements" port="result"/></p:input>
        </p:xslt>
        <!-- group items in definition lists -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="group-deflist-contents.xsl"/>
            </p:input>
        </p:xslt>
    </p:group>

    <!-- Normalize DTBook content model -->
    <p:group name="normalize-content-model">
        <p:output port="result"/>
        
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-imggroup.xsl"/>
            </p:input>
        </p:xslt>

        <!-- move lists out of paragraphs -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-list.xsl"/>
            </p:input>
        </p:xslt>

        <!-- move definition lists out of paragraphs -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-deflist.xsl"/>
            </p:input>
        </p:xslt>

        <!-- move producer notes out of inline elements -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-prodnote.xsl"/>
            </p:input>
        </p:xslt>

        <!-- normalize mixed block/inline content models -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="normalize-block-inline.xsl"/>
            </p:input>
        </p:xslt>
        <!-- normalize mixed section/block content models -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="normalize-section-block.xsl"/>
            </p:input>
        </p:xslt>
        <!-- normalize definition lists by relocating illegal elements from definitions -->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-definition-contents.xsl"/>
            </p:input>
        </p:xslt>

        <!-- normalize code by moving out block-level elements-->
        <p:xslt>
            <p:input port="stylesheet">
                <p:document href="moveout-code.xsl"/>
            </p:input>
        </p:xslt>
    </p:group>
    
    <!--<p:store>
        <p:with-option name="href" select="'/tmp/normalized.xml'"/>
    </p:store>
    -->
    <!-- Translate element and attribute names from DTBook to ZedAI -->
    <p:xslt name="translate-dtbook2zedai">
        <p:with-param name="mods-filename" select="$mods-filename"/>
        <p:with-param name="css-filename" select="$css-filename"/>
        <p:input port="stylesheet">
            <p:document href="./translate-dtbook2zedai.xsl"/>
        </p:input>
        <p:input port="source">
            <p:pipe step="normalize-content-model" port="result"/>
        </p:input>
    </p:xslt>

</p:declare-step>
