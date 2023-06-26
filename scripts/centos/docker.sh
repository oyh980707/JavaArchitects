# 预装软件
yum install -y yum-utils device-mapper-persistent-data lvm2

# 设置yum源
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 安装Docker
yum install -y docker-ce

# 查看版本
docker -v

# 配置阿里云镜像
sudo mkdir -p /etc/docker

sudo tee /etc/docker/daemon.json <<-'EOF'
{
"registry-mirrors":["https://n0dwemtq.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker


# 实验 关闭防火墙，不然每次配置很麻烦

# 关闭
systemctl stop firewalld
# 禁止开机启动防火墙
systemctl disable firewalld
# 重启docker
systemctl restart docker

# 开机自启动Docker
systemctl enable docker


# 基本命令

# 查看docker镜像
docker images
# docker搜寻镜像 比如 redis
docker search redis
# docker拉取镜像。默认拉取最新版，假如指定redis版本:docker pull redis:5.0
docker pull redis:5.0
# docker删除镜像
# 先查询，在删除
docker images
docker rmi 7614ae9453d1
# 或者名称版本直接删除
docker rmi redis:5.0
#删除所有镜像
docker rmi `docker images -q`

# 运行
# xx:起名 xxx：镜像 xxxx：版本 -i:表示容器保持一直运行 -t:分配终端输入命令
docker run -i -t --name=xx xxx:xxxx /bin/bash

#运行已有的容器 id通过docker ps -a 查询
docker start id
#查看运行容器
docker ps -a

# 进入容器
docker exec -i -t redis /bin/bash

#启动某个容器 ：name是给容器起的名字
docker start name
#停止某个容器 ：name是给容器起的名字
docker stop name
#删除某个容器 ：name是给容器起的名字
docker rm name
#删除所有容器：不能删除正在运行的容器
docker rm `docker ps -aq`
#查看容器的详细信息：name是给容器起的名字
docker inspect name




# 部署MySQL
docker search mysql
# 拉取mysql镜像
docker pull mysql:5.6
# 创建容器，设置端口映射、目录映射
# 在/root目录下创建mysql目录用于存储mysql数据信息
mkdir ~/mysql
cd ~/mysql

# -p 3307:3306：将容器的 3306 端口映射到宿主机的 3307 端口。
# -v $PWD/conf:/etc/mysql/conf.d：将主机当前目录下的 conf/my.cnf 挂载到容器的 /etc/mysql/my.cnf。配置目录
# -v $PWD/logs:/logs：将主机当前目录下的 logs 目录挂载到容器的 /logs。日志目录
# -v $PWD/data:/var/lib/mysql ：将主机当前目录下的data目录挂载到容器的 /var/lib/mysql 。数据目录
# -e MYSQL_ROOT_PASSWORD=123456：初始化 root 用户的密码。
docker run -id \
-p 3307:3306 \
--name=mysql \
-v $PWD/conf:/etc/mysql/conf.d \
-v $PWD/logs:/logs \
-v $PWD/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123456 \
mysql:5.6

# 进入容器，操作mysql
docker exec -it mysql /bin/bash



# 部署tomcat
docker search tomcat
# 拉取tomcat镜像
docker pull tomcat
# 在/root目录下创建tomcat目录用于存储tomcat数据信息
mkdir ~/tomcat
cd ~/tomcat
# 创建容器
docker run -id --name=tomcat \
-p 8080:8080 \
-v $PWD:/usr/local/tomcat/webapps \
tomcat



# Nginx
# 搜索nginx镜像
docker search nginx
# 拉取nginx镜像
docker pull nginx
# 安装完成后，我们可以使用以下命令来运行 nginx 容器：
docker run --name nginx -p 80:80 -d nginx


# Redis
# 搜索redis镜像
docker search redis
# 拉取redis镜像
docker pull redis:5.0
# 创建容器，设置端口映射
docker run -id --name=c_redis -p 6379:6379 redis:5.0
# 使用外部机器连接redis ./可能有问题
./redis-cli.exe -h 192.168.149.135 -p 6379



# jenkins
# 拉取jenkins镜像
docker   pull   jenkins
# 启动jenkins镜像
docker run -d -p 10000:8080 -p 50000:50000 \
-v /var/jenkins_home:/var/jenkins_home \
-v /opt/soft/maven3:/opt/soft/maven3 \
-v /usr/bin/git:/usr/bin/git \
--name jenkins jenkins/jenkins

