package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * The Class JobResult.
 */
public class JobResult {

        @Override
        public int hashCode() {
                return this.idx.hashCode()+this.path.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
                if(obj==null || !(obj instanceof JobResult))
                        return false;
                JobResult other=(JobResult) obj;
                return this.idx.equals(other.idx)&&this.path.equals(other.path);

        }

        @Override
        public String toString() {
                return String.format("JobResult [#=%s path='%s']", this.idx,this.path);
        }


        public static class Builder{
                private String idx;
                private URI path;
                private String mediaType;

                /**
                 * Constructs a new instance.
                 */
                public Builder() {
                }

                /**
                 * Sets the idx for this instance.
                 *
                 * @param idx The idx.
                 */
                public Builder withIdx(String idx) {
                        this.idx = idx;
                        return this;
                }

                /**
                 * Sets the path for this instance.
                 *
                 * @param path The path.
                 */
                public Builder withPath(URI path) {
                        this.path = path;
                        return this;
                }

                /**
                 * Sets the mediaType for this instance.
                 *
                 * @param mediaType The mediaType.
                 */
                public Builder withMediaType(String mediaType) {
                        this.mediaType = mediaType;
                        return this;
                }
                
                public JobResult build(){
                        return new  JobResult(this.idx,this.path,this.mediaType);
                }
        }

        //short index for the result 
        private String idx;

        // path to the actual file
        private URI path;

        //media type
        private String mediaType;

        /**
         * Constructs a new instance.
         *
         * @param idx The idx for this instance.
         * @param path The path for this instance.
         * @param mediaType The mediaType for this instance.
         */
        private JobResult(String idx, URI path, String mediaType) {
                this.idx = idx;
                this.path = path;
                this.mediaType = mediaType;
        }

        /**
         * Gets the idx for this instance.
         *
         * @return The idx.
         */
        public String getIdx() {
                return this.idx;
        }

        /**
         * Gets the path for this instance.
         *
         * @return The path.
         */
        public URI getPath() {
                return this.path;
        }

        /**
         * Gets the mediaType for this instance.
         *
         * @return The mediaType.
         */
        public String getMediaType() {
                return this.mediaType;
        }

        /**
         * Returns the size of the file pointed by path in bytes
         * @return the size
         */
        public long getSize() {
                try{
                        File f= new File(path);
                        if( ! f.exists()){
                                throw new IOException(String.format("File not found :",f.getAbsolutePath()));
                        }
                        return f.length();
                }catch (Exception e){
                        throw new RuntimeException("Error calculating result size",e);
                }
        }


}
