<p:library version="1.0" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:px="http://pipeline.daisy.org/ns/">

    <p:declare-step type="px:add-manifest-entry" name="add-manifest-entry">
        <p:input port="source"/>
        <p:output port="result"/>
        <p:option name="href" required="true"/>
        <p:option name="media-type"/>
        <p:option name="first" select="'false'"/>
        <p:group name="add-manifest-entry.create-entry">
            <p:output port="result"/>
            <p:add-attribute match="/*" attribute-name="href">
                <p:input port="source">
                    <p:inline exclude-inline-prefixes="px">
                        <c:entry/>
                    </p:inline>
                </p:input>
                <p:with-option name="attribute-value" select="$href"/>
            </p:add-attribute>
            <p:choose>
                <p:when test="p:value-available('media-type')">
                    <p:add-attribute match="/*" attribute-name="media-type">
                        <p:with-option name="attribute-value" select="$media-type"/>
                    </p:add-attribute>
                </p:when>
                <p:otherwise>
                    <p:identity/>
                </p:otherwise>
            </p:choose>
        </p:group>
        <p:insert match="/*">
            <p:input port="source">
                <p:pipe port="source" step="add-manifest-entry"/>
            </p:input>
            <p:input port="insertion">
                <p:pipe port="result" step="add-manifest-entry.create-entry"/>
            </p:input>
            <p:with-option name="position"
                select="if ($first='true') then 'first-child' else 'last-child'"/>
        </p:insert>
        <p:label-elements match="c:entry[not(@xml:base)]" attribute="xml:base" label="base-uri(..)"/>
        <p:add-xml-base/>
    </p:declare-step>

    <p:declare-step type="px:create-manifest" name="create-manifest">
        <p:output port="result"/>
        <p:option name="base" required="true"/>
        <p:add-attribute attribute-name="xml:base" match="/*">
            <p:input port="source">
                <p:inline exclude-inline-prefixes="px">
                    <c:manifest/>
                </p:inline>
            </p:input>
            <p:with-option name="attribute-value" select="$base"/>
        </p:add-attribute>
    </p:declare-step>

    <p:declare-step type="px:join-manifests" name="join-manifests">
        <p:input port="source" sequence="true"/>
        <p:output port="result"/>
        <p:wrap-sequence wrapper="c:manifest"/>
        <p:unwrap match="/c:manifest/c:manifest"/>
        <p:label-elements match="c:entry" attribute="href" label="resolve-uri(@href,base-uri())"
            replace="true"/>
        <p:label-elements match="c:manifest" attribute="xml:base"
            label="
            (
            for $pref in
                reverse(
                    for $uri in //@href
                    return
                        for $i in 1 to count(tokenize($uri,'/'))
                        return concat(string-join(
                            for $p in 1 to $i return tokenize($uri,'/')[$p]
                        ,'/'),'/')
                )
            return
                if (every $h in //@href satisfies starts-with($h,$pref)) then $pref else ()
            )[1]
            "
            replace="true"/>
        <p:label-elements match="c:entry" attribute="xml:base" label="/*/@xml:base" replace="true"/>
        <p:label-elements match="c:entry" attribute="href"
            label="if (starts-with(@href,base-uri())) then substring-after(@href,base-uri()) else @href"
            replace="true"/>
        <p:add-xml-base/>
    </p:declare-step>
    
    <p:declare-step type="px:to-zip-manifest" name="to-zip-manifest">
        <p:input port="source"/>
        <p:output port="result"/>
        <p:rename match="c:manifest" new-name="c:zip-manifest"/>
        <p:rename match="c:entry/@href" new-name="name"/>
        <p:label-elements match="c:entry" attribute="href" label="resolve-uri(@name,base-uri())"/>
    </p:declare-step>

</p:library>
