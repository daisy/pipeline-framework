
MAVEN_REPO=mavenrepo/
THIRD_PARTY=3rdPartyBundles

#installs the jar bundle to the maven repository and updates the felix orb file to easily use them in eclipse

function installBundle {
	file=$1
	groupId=$2
	artifactId=$3
	version=$4
	localRepo=$5
	pomFile=$groupId/$artifactId/$version/$artifactId-$version.pom
	orbFile=$localRepo/repository.xml
	mvn install:install-file -Dfile=$file -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackaging=jar  -DgeneratePom=true
	
	mvn org.apache.felix:maven-bundle-plugin:install-file -DobrRepository=$orbFlie -DpomFile='${settings.localRepository}'/$pomFile
}


#	mvn install:install-file -Dfile=cnf/repo/osgi.core/osgi.core-4.2.1.jar -DgroupId=3rdPartyBundles -DartifactId=osgi.core -Dversion=4.2.1 -Dpackaging=jar -DlocalRepositoryPath=mavenrepo/ -DgeneratePom=true
#	mvn org.apache.felix:maven-bundle-plugin:install-file -DobrRepository="mavenrepo/repository.xml" -DpomFile=mavenrepo/3rdPartyBundles/osgi.core/4.2.1/osgi.core-4.2.1.pom


if [ -z $1 ]; then
	echo "[INFO] Creating basic repository for the project"
#	rm -rf $MAVEN_REPO
#	mkdir $MAVEN_REPO
#	touch $MAVEN_REPO/repository.xml
#	echo "<repository></repository>" >$MAVEN_REPO/repository.xml 
	#setup for our needs
	#osgi core
	installBundle "cnf/repo/osgi.core/osgi.core-4.2.1.jar" $THIRD_PARTY "osgi.core" "4.2.1" $MAVEN_REPO
	#osgi eclipse
	installBundle "cnf/repo/org.eclipse.osgi/org.eclipse.osgi-3.6.1.jar" $THIRD_PARTY "org.eclipse.osgi" "3.6.1" $MAVEN_REPO
	#felix scr
	installBundle "cnf/repo/org.apache.felix.scr/org.apache.felix.scr-1.4.0.jar" $THIRD_PARTY "org.apache.felix.scr" "1.4.0" $MAVEN_REPO
	#stax2-api
	installBundle "cnf/repo/stax2-api/stax2-api-3.0.2.jar" $THIRD_PARTY "stax2-api" "3.0.2" $MAVEN_REPO
	#woodstox-core
	installBundle "cnf/repo/woodstox-core-lgpl/woodstox-core-lgpl-4.0.8.jar" $THIRD_PARTY "woodstox-core-lgpl" "4.0.8" $MAVEN_REPO
	#guava
	installBundle "cnf/repo/guava/guava-1.6.0.jar" $THIRD_PARTY "guava" "1.6.0" $MAVEN_REPO
	#jopt
	installBundle "cnf/repo/jopt-simple-3.2/jopt-simple-3.2-0.0.0.jar" $THIRD_PARTY "jopt" "3.2" $MAVEN_REPO
	
fi
