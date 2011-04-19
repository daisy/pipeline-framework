<?xml version="1.0" encoding="UTF-8"?>

<p:declare-step version="1.0" name="metadata-generator" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:cx="http://xmlcalabash.com/ns/extensions"
    xmlns:cxo="http://xmlcalabash.com/ns/extensions/osutils"
    xmlns:p2util="http://pipeline.daisy.org/ns/utilities/" exclude-inline-prefixes="cx"
    type="p2util:metadata-generator">
    
    <p:input port="source" primary="true"/>
    <p:input port="parameters" kind="parameter"/>
    
    <p:option name="output" select="''"/>
    
    <p:import href="http://xmlcalabash.com/extension/steps/library-1.0.xpl"/>
    
    <p:variable name="mods-file"
        select="resolve-uri(
        if ($output='') then concat(
        if (matches(base-uri(/),'[^/]+\..+$'))
        then replace(tokenize(base-uri(/),'/')[last()],'\..+$','')
        else tokenize(base-uri(/),'/')[last()],'-mods.xml')
        else if (ends-with($output,'.xml')) then $output 
        else concat($output,'.xml'), base-uri(/))">
        <p:pipe step="metadata-generator" port="source"/>
        
    </p:variable>
    
    
    <!-- Validate DTBook Input-->
    <p:validate-with-relax-ng assert-valid="true" name="validate-dtbook">
        <p:input port="schema">
            <p:document href="schema/dtbook-2005-3.rng"/>
        </p:input>
    </p:validate-with-relax-ng>
    
    
    <!-- create MODS metadata record -->
    <p:xslt name="create-mods">
        <p:input port="source">
            <p:pipe step="validate-dtbook" port="result"/>
        </p:input>
        <p:input port="stylesheet">
            <p:document href="generate-mods.xsl"/>
        </p:input>
    </p:xslt>
    
    <!-- write the MODS metadata record -->
    <p:store>
        <p:input port="source">
            <p:pipe step="create-mods" port="result"/>
        </p:input>
        <p:with-option name="href" select="$mods-file"/>
    </p:store>
    
</p:declare-step>
