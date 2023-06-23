#!/bin/bash

echo 'auto install Tomcat9 begining...'

installDir=/opt/soft/Tomcat9

sudo wget -P "$installDir" https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.76/bin/apache-tomcat-9.0.76.tar.gz

tar -zxf "$installDir"/apache-tomcat-9.0.76.tar.gz -C "$installDir" --strip-components 1







