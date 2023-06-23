#!/bin/bash

echo 'auto install begining...'

installDir=/opt/soft/JDK17

#1. 下载
sudo wget -P "$installDir" https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz

setup_jdk(){
	# 检查目录是否存在，如果不存在就创建目录
	if [ ! -d "$installDir" ];then
		mkdir -p "$installDir"
	fi

	sudo tar -zxf "$installDir"/jdk-17_linux-x64_bin.tar.gz -C "$installDir" --strip-components 1

    # 配置环境变量
    if [ "$JAVA_HOME" == "" ];then
        echo '#java environment' >> /etc/profile
        echo "export JAVA_HOME=$installDir" >> /etc/profile
        echo "export CLASSPATH=.:\${JAVA_HOME}/jre/lib/rt.jar:\${JAVA_HOME}/lib/dt.jar:\${JAVA_HOME}/lib/tools.jar" >> /etc/profile
        echo "export PATH=\$PATH:\${JAVA_HOME}/bin" >> /etc/profile
    fi

    source /etc/profile
}

setup_jdk