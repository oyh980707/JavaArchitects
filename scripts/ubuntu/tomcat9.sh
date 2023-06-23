#!/bin/bash

echo 'auto install tomcat9 begining...'

installDir=/opt/soft/tomcat9

sudo wget -P "$installDir" https://mirrors.cnnic.cn/apache/tomcat/tomcat-9/v9.0.33/bin/apache-tomcat-9.0.33.tar.gz

tar -zxf "$installDir"/apache-tomcat-9.0.33.tar.gz -C "$installDir" --strip-components 1







