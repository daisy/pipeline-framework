<p:library version="1.0" xmlns:p="http://www.w3.org/ns/xproc"
    xmlns:cxf="http://xmlcalabash.com/ns/extensions/fileutils"
    xmlns:ml="http://xmlcalabash.com/ns/extensions/marklogic"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema">

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <div>
            <h1>Fileutils Library</h1>
            <h2>Version 1.0</h2>
            <p>The steps defined in this library provide information about files and the ability to
                manipulate them. All implementations are required to support file: scheme URIs.
                Support for other schemes is implementation-defined. </p>
            <p>All <code>href</code> attributes are made absolute with respect to the element on
                which they are specified.</p>
        </div>
    </p:documentation>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>cxf:info</code> step returns a <code>&lt;c:directory></code>,
                <code>&lt;c:file></code>, or <code>&lt;c:other></code> (implementations may also
            return more specific types, e.g., <code>&lt;c:fifo></code> or <code>&lt;c:dev></code>).
            If the document doesn't exist, an empty sequence is returned. The document element of
            the result, if there is one, will have <code>readable</code>, <code>writable</code>,
                <code>hidden</code>, <code>last-modified</code>, and <code>size</code> attributes.
            The <code>readable</code>, <code>writable</code> and <code>hidden</code> attributes are
            boolean and are only present if they are true. The <code>last-modified</code> attribute
            returns the last-modified time in UTC. The <code>size</code> attribute returns the size
            of the file; it is absent for directories and other types that have no meaningful
            size.</p>
    </p:documentation>

    <p:declare-step type="cxf:info">
        <p:output port="result" sequence="true"/>
        <p:option name="href" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>cxf:touch</code> step returns a <code>&lt;c:result></code> containing the
            absolute URI of the touched file. The step fails if the file does not exist and cannot
            be created.</p>
    </p:documentation>

    <p:declare-step type="cxf:touch">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>cxf:tempfile</code> step returns a <code>&lt;c:result></code> containing the
            absolute URI of a temporary file. The temporary file is guaranteed not to already exist
            when <code>cxf:tempfile</code> is called. The file is created in the directory specified
            by the <code>href</code>. The step fails if the directory does not exist.</p>
        <p>If the <code>delete-on-exit</code> option (a boolean) is true, then the temporary file
            will automatically be deleted when the processor terminates.</p>
    </p:documentation>

    <p:declare-step type="cxf:tempfile">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
        <p:option name="delete-on-exit"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>cxf:delete</code> step returns a <code>&lt;c:result></code> containing the
            absolute URI of the deleted file. The step fails if the file does not exist or if it
            cannot be deleted.</p>
    </p:documentation>

    <p:declare-step type="cxf:delete">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>&lt;cxf:mkdir></code> step creates a directory with the name spacified in the
            "href" option. If the name includes more than one directory component, all of the
            intermediate components are created. The path separator is implementation-defined.
            Returns a <code>&lt;c:result></code> containing the absolute filename of the directory
            created. The step fails if the directory cannot be created.</p>
    </p:documentation>

    <p:declare-step type="cxf:mkdir">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>&lt;cxf:copy></code> step returns a <code>&lt;c:result></code> containing the
            absolute URI of the target. The step fails if the file represented by <code>href</code>
            does not exist or if it cannot be copied to the specified target.</p>
    </p:documentation>

    <p:declare-step type="cxf:copy">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
        <p:option name="target" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

    <p:documentation xmlns="http://www.w3.org/1999/xhtml">
        <p>The <code>&lt;cxf:move></code> step returns a <code>&lt;c:result></code> containing the
            absolute URI of the target. The step fails if the file represented by the
                <code>href</code> does not exist or if it cannot be copied to the specified target;
            the source file is deleted if the copy succeeds.</p>
    </p:documentation>

    <p:declare-step type="cxf:move">
        <p:output port="result" primary="false"/>
        <p:option name="href" required="true"/>
        <p:option name="target" required="true"/>
    </p:declare-step>

    <!-- ============================================================ -->

</p:library>
