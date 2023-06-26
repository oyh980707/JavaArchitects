#!/bin/bash

echo 'auto install Tomcat9 begining...'

installDir=/opt/soft/Maven3

sudo wget -P "$installDir" https://dlcdn.apache.org/maven/maven-3/3.9.2/binaries/apache-maven-3.9.2-bin.tar.gz

tar -zxf "$installDir"/apache-maven-3.9.2-bin.tar.gz -C "$installDir" --strip-components 1

echo "export MAVEN_HOME=$installDir">>/etc/profile
echo "export PATH=\$PATH:\${MAVEN_HOME}/bin" >> /etc/profile

source /etc/profile

echo $(mvn -v)




<mirror>
    <id>alimaven</id>  
    <mirrorOf>central</mirrorOf>  
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url>  
</mirror>
