<p:library version="1.0" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:cx="http://xmlcalabash.com/ns/extensions" xmlns:xsd="http://www.w3.org/2001/XMLSchema">

    <p:declare-step type="cx:zip">
        <p:input port="source" sequence="true" primary="true"/>
        <p:input port="manifest"/>
        <p:output port="result"/>
        <p:option name="href" required="true" cx:type="xsd:anyURI"/>
        <p:option name="compression-method" cx:type="stored|deflated"/>
        <p:option name="compression-level"
            cx:type="smallest|fastest|default|huffman|none"/>
        <p:option name="command" select="'update'"
            cx:type="update|freshen|create|delete"/>
        
        <p:option name="byte-order-mark" cx:type="xsd:boolean"/>
        <p:option name="cdata-section-elements" select="''" cx:type="ListOfQNames"/>
        <p:option name="doctype-public" cx:type="xsd:string"/>
        <p:option name="doctype-system" cx:type="xsd:anyURI"/>
        <p:option name="encoding" cx:type="xsd:string"/>
        <p:option name="escape-uri-attributes" select="'false'"
            cx:type="xsd:boolean"/>
        <p:option name="include-content-type" select="'true'" cx:type="xsd:boolean"/>
        <p:option name="indent" select="'false'" cx:type="xsd:boolean"/>
        <p:option name="media-type" cx:type="xsd:string"/>
        <p:option name="method" select="'xml'" cx:type="xsd:QName"/>
        <p:option name="normalization-form" select="'none'"
            cx:type="NormalizationForm"/>
        <p:option name="omit-xml-declaration" select="'true'" cx:type="xsd:boolean"/>
        <p:option name="standalone" select="'omit'" cx:type="true|false|omit"/>
        <p:option name="undeclare-prefixes" cx:type="xsd:boolean"/>
        <p:option name="version" select="'1.0'" cx:type="xsd:string"/>
    </p:declare-step>
    

</p:library>
