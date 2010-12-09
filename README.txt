=========
Execution
=========

It should be enough to execute dp2.sh in the Main project.

=========
Eclipse
=========

1.Install bndtools
2.Import all the projects
3.Create a new java run configuration
	main class: bndtools.launcher.Main
	prog arguments: launch.properties
	VM args: -Dorg.daisy.pipeline.cmdargs="-l" or the desired command
	

You can also use the OSGI run configuration to run single bundles

=====
Maven
=====
Once you have loaded at least once the projects in eclipse (this wont be necessary on near future).
Install maven 2.

1. execute createMavenRepo to install the necessary bundles to the maven repository
2. "mvn install" in the root folder

This process will take long as mvn has to download all the dependencies and plugins



