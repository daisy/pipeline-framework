#!/bin/bash

MODULE_DIR=`dirname "$0"`
COMMON_DIR=$MODULE_DIR/../common
LIB_DIR=$COMMON_DIR/lib
CONF_DIR=$COMMON_DIR/conf

usageExit ()
{
	if [ "$2" ]; then echo $2; fi
	echo "Usage: `basename "$0"` [options] FILE"
	echo "    Converts FILE to a ZedAI book."
	echo "    FILE must be a valid DTBook document."
	echo ""
	echo "Options:"
	echo "    -o FILE : the name of the created ZedAI document."
	echo "              default is <name of the dtbook>-zedai.xml"
	echo "    -h      : print this help"
	echo "    -v      : verbose"
	echo ""
	echo "Example:"
	echo "    `basename "$0"` sample/greatpainters.xml"
	echo "    `basename "$0"` -o zedai.xml sample/greatpainters.xml"
	exit ${1:-1}
}

calabash()
{
	# Build the classpath
	for lib in `ls -1 "$LIB_DIR"`; do
		CP=$CP:$LIB_DIR/$lib
	done
	if [ $VERBOSE ]; then
		LOGGING=-Djava.util.logging.config.file=$CONF_DIR/logging-info.properties
	else
		LOGGING=-Djava.util.logging.config.file=$CONF_DIR/logging-severe.properties
	fi

	java  "$LOGGING" -classpath "$CP" -Dcom.xmlcalabash.phonehome=false com.xmlcalabash.drivers.Main -c ${CONF_DIR// /%20}/calabash-config.xml $@
}


# Parse the Options
while getopts "o:hv" OPTION
do
  case $OPTION in
	o) OUT_FILE="$OPTARG";;
	h) usageExit 0 "";;
	v) VERBOSE="true";;
  esac
done
shift $(($OPTIND - 1))

#Check the input file has been set
IN_FILE=$1
if [ -z "$IN_FILE" ]
then	
	usageExit 1 "The input DTBook document must be set\n"
fi

calabash -i source=${IN_FILE// /%20} ${MODULE_DIR// /%20}/src/dtbook2zedai.xpl output=${OUT_FILE// /%20}