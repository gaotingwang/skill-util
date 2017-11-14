# 读写分离

常见实现数据库读写分离的方案是在程序中手动指定多个数据源，比较麻烦。 针对Master/Slave，MySQL自带了一个ReplicationDriver的jdbc driver。详见文档：[http://dev.mysql.com/doc/connector-j/en/connector-j-master-slave-replication-connection.html](http://dev.mysql.com/doc/connector-j/en/connector-j-master-slave-replication-connection.html)，里面写了它的用法及sample。**它是通过Connection事务的readonly属性实现读和写操作的分离。**

实际使用中需注意ReplicationDriver的url协议头：jdbc:mysql:replication://master,slave1,slave2。这个是mysql自己扩展的jdbc url协议。另外必须指定Connection的readonly属性。当然如果用spring就很方便了，配置一个事务aop即可。

简单而又强大，无代码入侵。当然如果使用其它数据库就没办法使用了，不过可以参照ReplicationDriver源码自己实现。

无需改动其他代码，只需要数据库驱动使用`ReplicationDriver`，url采用`jdbc:mysql:replication://`形式。

```yaml
spring:
  datasource:
    # ReplicationDriver 规定url中第一个主机是master主机地址，剩下的全部是slave主机。另外有两个参数autoReconnect和roundRobinLoadBalance必须指定为true
    url: jdbc:mysql:replication://localhost:3306,localhost:3306,localhost:3306/test1?useUnicode=true&characterEncoding=utf8&autoReconnect=true&roundRobinLoadBalance=true
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.ReplicationDriver
```

