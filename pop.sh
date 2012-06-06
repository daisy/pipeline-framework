#!/bin/sh

basedir=`pwd`

# Cleanup

find . -type d -name bin -depth 2 -exec rm -R {} \;
find . -type d -name target -depth 2 -exec rm -R {} \;
find . -type f -name .DS_Store -delete
hg rm Main
hg rm calabash-step-hello
hg rm commandline
hg rm daisy*
hg rm eclipse-extras
hg rm publish_bundles 
hg rm repo
hg rm target-platform
hg rm test
hg rm test-script
hg rm test-xpath
hg rm common-stax/salida
for file in `find . -type d -not -path "./.hg*"`; do
	if [ -d "$file/META-INF" ]; then
		cd "$file"
		hg rm -f build.properties
		hg rm -f .project
		hg rm -f .classpath
		cd "$basedir"
	fi
done

# Refactoring

hg rm  framework-new/build.properties framework-new/META-INF framework-new/OSGI-INF framework-new/pom.xml
hg rm framework-new/.project framework-new/.settings framework-new/.classpath
hg mv module-uri-resolver/OSGI-INF/resolver.xml module-registry/OSGI-INF/
hg mv module-uri-resolver/src module-registry/
hg rm module-uri-resolver
hg mv module-common modules-api
hg mv module-registry modules-registry
hg mv converter-parser/OSGI-INF framework-core/
hg mv converter-parser/src/main/java/* framework-core/src/
hg mv converter-parser/src/test/resources/script.xpl framework-core/test/
hg mv converter-parser/src/test/java/* framework-core/test/
hg rm converter-parser
hg mv org.daisy.pipeline.logging logging-activator
hg mv xmlcatalog.test xmlcatalog-tests

for file in `find . -type d -not -path "./.hg*"`; do
	# echo $file
	if [ -d "$file/META-INF" ]; then
		cd "$file"
		PROJECT_ID=`basename $file` 
		echo "Processing "$PROJECT_ID" ..."
		
		mkdir -p src/main/java
		mkdir -p src/main/resources
		mkdir -p src/test/java
		mkdir -p src/test/resources

		mv META-INF/MANIFEST.MF MANIFEST.TXT
		hg rm META-INF/MANIFEST.MF
		if [ -f META-INF/persistence.xml ]; then hg mv META-INF src/main/resources; fi;
		rm -R META-INF
		if [ -d OSGI-INF ]; then hg mv OSGI-INF src/main/resources; fi;
		
		if [ -d src/org ]; then hg mv src/org src/main/java; fi;
		if [ -d resources ]; then hg mv resources src/test; fi;
		if [ -d test ]; then hg mv test/* src/test/java; fi;

		find src/main/java -type f -name "packageinfo" -exec hg rm {} \;
		for package in `find . -type f -name "package.html"`; do
			pushd `dirname $package` > /dev/null
			echo "/**" > package-info.java
			cat package.html | sed -e '/</ d' -e 's/\(.*\)/ * \1/' >> package-info.java
			echo  " */" >> package-info.java
			echo "package `echo $package | sed -e 's$\./src/main/java/\(.*\)/package.html$\1$' -e 's$/$.$g'`;" >> package-info.java
			hg rm -f package.html
			rm package.html
			hg add package-info.java
			popd > /dev/null
		done
		
		for resource in `find src/main/java -type f -not -name "*.java"`; do
			hg mv $resource src/main/resources${resource#src/main/java}
		done
		for resource in `find src/test/java -type f -not -name "*.java"`; do
			hg mv $resource src/test/resources${resource#src/test/java}
		done

		PROJECT_NAME=`sed -nE 's/Bundle-Name: *(.*) *$/\1/p' MANIFEST.txt` 
		cat > pom.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.daisy.example.pipeline</groupId>
    <artifactId>framework-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
  </parent>

  <groupId>org.daisy.example.pipeline</groupId>
  <artifactId>$PROJECT_ID</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>bundle</packaging>

  <name>$PROJECT_NAME</name>

</project>
EOF
	cd "$basedir"
	fi
done