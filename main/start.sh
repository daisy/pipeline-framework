#!/bin/bash

export DP2_HOME=~/daisy/pipeline-assembly/target/pipeline2-1.9.9-SNAPSHOT_linux/daisy-pipeline/
export DP2_DATA=/tmp/daisy

java  -Dorg.daisy.pipeline.ws.authentication=false \
      -Dorg.daisy.pipeline.home=$DP2_HOME \
      -Dorg.daisy.pipeline.data=$DP_DATA \
      -jar target/osgi-less-1.0-SNAPSHOT-jar-with-dependencies.jar
