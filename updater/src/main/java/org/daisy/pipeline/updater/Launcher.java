package org.daisy.pipeline.updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Launcher {

        private String bin;
        private String site;
        private String deployPath;
        private String localReleaseDescriptor;
        private String version;

        private static final String SERVICE_FLAG="-service=";
        private static final String VERSION_FLAG="-version=";
        private static final String INSTALL_DIR_FLAG="-install-dir=";
        private static final String LOCAL_DESCRIPTOR="-descriptor=";


        /**
         * @param bin
         * @param site
         * @param deployPath
         * @param localReleaseDescriptor
         */
        public Launcher(String bin, String site, String deployPath,
                        String localReleaseDescriptor,String version) {
                this.bin = bin;
                this.site = site;
                this.deployPath = deployPath;
                this.localReleaseDescriptor = localReleaseDescriptor;
                this.version=version;
        }

        public InputStream launch() throws IOException {
                ProcessBuilder pb = new ProcessBuilder(
                                this.bin,
                                String.format("%s%s",SERVICE_FLAG,this.site),
                                String.format("%s%s",VERSION_FLAG,this.version),
                                String.format("%s%s",LOCAL_DESCRIPTOR,this.localReleaseDescriptor),
                                String.format("%s%s",INSTALL_DIR_FLAG,this.deployPath)
                                );
                pb.redirectErrorStream(true);
                Process p=pb.start();
                return p.getInputStream();

        }

}
