#!/bin/bash
#figures out the package system
function package_management {

	hash apt-get 2>/dev/null
	APT_INSTALLED=$?
	hash yum 2>/dev/null
	RPM_INSTALLED=$?
	#0=> manual 1=> apt 2=> yum
	INSTALL_TYPE=0
	if [ $APT_INSTALLED == "0" ]; then
		INSTALL_TYPE=1
	elif [ $RPM_INSTALLED == "0" ]; then
		INSTALL_TYPE=2
	fi
}
#debian-like based installation
function install_apt {
	echo "Installing dependencies...(apt based installation)"
	#by def
	APT_PKGS="libxml2-dev libxslt1-dev "
	if [ $RUBY_OK != "0" ]; then
		APT_PKGS="$APT_PKGS ruby"
	fi
	if [ $GEMS_OK != "0" ]; then
		APT_PKGS="$APT_PKGS rubygems"
	fi
	apt-get -y install  $APT_PKGS 
	if [ $NOKOGIRI_OK != "0" ]; then
		gem install nokogiri
	fi 
	if [ $MIME_OK != "0" ]; then
		gem install mime-types 
	fi 
	echo "Your system is ready to cope with dp2 CLI";
	exit 0;
}
#feadora/red-hat like based installation
function install_yum {
	echo "Installing dependencies...(yum based installation)"
	#by def
	YUM_PKGS="libxml2-devel libxslt1-devel "
	if [ $RUBY_OK != "0" ]; then
		YUM_PKGS="$YUM_PKGS ruby"
	fi
	if [ $GEMS_OK != "0" ]; then
		YUM_PKGS="$YUM_PKGS rubygems"
	fi
	if [ $NOKOGIRI_OK != "0" ]; then
		YUM_PKGS="$YUM_PKGS rubygem-nokogiri" 
	fi 
	yum -y install  $YUM_PKGS 
	if [ $MIME_OK != "0" ]; then
		gem install mime-types 
	fi 
	echo "Your system is ready to cope with dp2 CLI";
	exit 0;
}
RUBY_OK=-1
NOKOGIRI_OK=-1
MIME_OK=-1
GEMS_OK=-1


hash ruby  2>/dev/null
RUBY_OK=$?

if [ $RUBY_OK == "0" ]; then 
	echo "require 'rubygems'" | ruby &>/dev/null
	GEMS_OK=$?
	echo "require 'rubygems';require 'nokogiri'" | ruby &>/dev/null
	NOKOGIRI_OK=$?
	echo "require 'rubygems';require 'mime/types'" | ruby &>/dev/null
	MIME_OK=$?
fi

if [ $MIME_OK == "0"  -a $NOKOGIRI_OK == "0" ]; then 
	echo "Your system is ready to cope with dp2 CLI";
	exit 0;
fi

#we need super powers to keep on going
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

package_management

case $INSTALL_TYPE in
	0)
	  	echo "Your system doesn't support an automatic installation."
		echo "In order to run the dp2 CLI you need:"
		echo "ruby"
		echo "nokogiri (http://nokogiri.org/tutorials/installing_nokogiri.html)"
		echo "mime-types (available via 'gem install mime-types')"
		;;
	1 )

		install_apt
		;;
	2 )
		install_yum
		;;	

esac



