# Knowledge

## 批量插入List遇到的坑

如下代码：

```java
public void function(){
	List<User> list = new ArrayList<User>();
    User user = new User();
    user.setName = "objk";
    for(int i=0;i<10;i++){
        user.setAge(i);
        list.add(user);
    }
    dao.batchInsert(list);
}
// 如此的代码，最后数据库的结果是10条记录age都为10
// 在以后避免这种写法，不必要的时候尽量不要使用批量插入，即使如此，也需要在内部实现单条插入
// 或者不要使用同一个对象反复的使用。必须使用心得会员或者深拷贝对象
```

## 订单如何防止重复支付

```text
业务系统支付-->支付中心-->第三方支付渠道
							|
业务系统 <-----	支付中心 <-----
```

如上操作流程

防止掉单可以这样处理：

1. 支付订单中增加一个状态为"支付中"，当同一个订单去支付时，先判断该订单是否处于"支付中"的支付流水，当支付的时候需要加个锁。支付完成以后更新支付流水状态的时候再将其改成"支付成功"状态。
2. 支付中心需要自定义一个超时时间(例如：30秒)，在此时间范围内如果没有收到支付成功回调，则应该调用接口主动查询支付结果，比如10s，20s查一次，如果最大查询次数内没有查到结果，应该异常处理。
3. 支付中心收到结果以后，将结果同步给业务系统，可以发送MQ，也可以直接调用，直接调用的话要加重试(例如：Spring boot Retry)
4. 无论是支付中心，还是业务系统，在接受支付结果通知时，都要考虑接口的幂等性，消息只处理一次，其余的忽略
5. 业务应用也应该做超时主动查询支付结果，超时主动查询支付结果的时候可以将这些支付订单放到一张表中，用定时任务去扫

为了防止重复提交，如下处理：

创建订单的时候，用订单信息计算一个哈希值，判断Redis中是否有key，有则不允许重复提交，没有则生成一个新的key，有则不允许重复提交，没有则生成一个新的key，放到redis中设置一个过期时间，然后创建订单。其实就是一段时间内不可以提交重复相同的操作。



## ERROR 1044 (42000): Access denied for user ''@'localhost' to database 'ms

```text
上面的问题是由于mysql.user表中存在一条记录为用户名为''登录地址为本地，我这里是没有设置密码。
每次登录相当于匿名登录，权限也没有。所以需要数据库跳过权限验证修改这张表后方可正常。具体步骤如下：
(以下命令依照数据库和安装方式有所不同,我是用的是MariaDB,对应关闭命令为service mariadb stop)
1. 关闭数据库
	service mysqld stop   //linux下使用   
	net stop mysql    //window下使用
2. 屏蔽权限
    mysqld_safe --skip-grant-table //linux下使用
    或者使用如下命令
	mysqld_safe --user=mysql --skip-grant-tables --skip-networking & //linux下使用
    mysqld --skip-grant-table  //window下使用
3. 、新开起一个终端输入
# mysql -u root mysql
mysql> use mysql;
mysql> select host,user,password from user;//检查表，看是否有什么异常，这就是登录的用户表
mysql> UPDATE user SET Password=PASSWORD('newpassword') where USER='root';
mysql> delete from user where host="localhost" and user=''; // 依照每个人的错误不一样擦做也不一样。我的处理方式是删除本地匿名登录用户
mysql> FLUSH PRIVILEGES;   //更新命令，记得要这句话，否则如果关闭先前的终端，又会出现原来的错误
```

