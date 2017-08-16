<p:declare-step version="1.0"
                xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:px="http://www.daisy.org/ns/pipeline/xproc"
                type="px:java-runtime-error">
    
    <p:output port="result"/>
    
    <p:declare-step type="px:java-step">
        <p:input port="source"/>
        <p:output port="result"/>
    </p:declare-step>
    
    <px:java-step>
        <p:input port="source">
            <p:inline>
                <hello/>
            </p:inline>
        </p:input>
    </px:java-step>
    
</p:declare-step>
