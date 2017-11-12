# 读写分离

数据库层面大都采用读写分离技术，就是一个Master数据库，多个Slave数据库。Master库负责数据更新和实时数据查询，Slave库当然负责非实时数据查询。因为在实际的应用中，数据库都是读多写少（读取数据的频率高，更新数据的频率相对较少），而读取数据通常耗时比较长，占用数据库服务器的CPU较多，从而影响用户体验。我们通常的做法就是把查询从主库中抽取出来，采用多个从库，使用负载均衡，减轻每个从库的查询压力。

读写分离的实现目前常用的有两种方式：

1. 第一种方式就是定义2个数据库连接，一个是MasterDataSource,另一个是SlaveDataSource。更新数据时读取MasterDataSource，查询数据时读取SlaveDataSource。可以参考[Mybatis多数据源使用](https://github.com/gaotingwang/spring-boot-demo/tree/master/data-mybatis/src/main/java/com/gtw/mybatis/config)。
2. 第二种方式动态数据源切换，就是在程序运行时，把数据源动态织入到程序中，从而选择读取主库还是从库。主要使用的技术是：AbstractRoutingDataSource + aop + annotation。下面详细介绍此种实现方式。

## 知识预备

`AbstractRoutingDataSource`是Spring2.0以后增加的，`AbstractRoutingDataSource`继承了`AbstractDataSource` ，而`AbstractDataSource` 又是`DataSource` 的子类。`DataSource`  是`javax.sql` 的数据源接口，定义如下：

```java
public interface DataSource  extends CommonDataSource,Wrapper {

  Connection getConnection() throws SQLException;

  Connection getConnection(String username, String password)
    throws SQLException;

}
```

`DataSource` 接口定义了2个方法，都是获取数据库连接。再看下`AbstractRoutingDataSource` 如何实现了`DataSource`接口：

```java
public abstract class AbstractRoutingDataSource extends AbstractDataSource implements InitializingBean {
    ....

    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }
    
    ...
}
```

很显然就是调用自己的determineTargetDataSource()  方法获取到connection。determineTargetDataSource方法定义如下：

```java
protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
		// 返回lookupKey
        Object lookupKey = this.determineCurrentLookupKey();
  		// 根据lookupKey从Map中获得数据源
        DataSource dataSource = (DataSource)this.resolvedDataSources.get(lookupKey);
        if(dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }

        if(dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        } else {
            return dataSource;
        }
}
```

resolvedDataSources 和determineCurrentLookupKey定义如下：

```java
private Map<Object, DataSource> resolvedDataSources;

protected abstract Object determineCurrentLookupKey();
```

## 准备工作

看到以上定义，是不是有点思路了，resolvedDataSources是Map类型，我们可以把MasterDataSource和SlaveDataSource存到Map中。

需要实现AbstractRoutingDataSource，实现其determineCurrentLookupKey() 方法，该方法返回Map的key，master或slave。

1. 定义数据源

   - 数据源配置文件

     ```yaml
     demo:
       datasource:
         type: com.alibaba.druid.pool.DruidDataSource
         slave-size: 2
         master:
           url: jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false
           username: root
           password: root
           driver-class-name: com.mysql.jdbc.Driver
         slave1:
           url: jdbc:mysql://localhost:3306/test2?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false
           username: root
           password: root
           driver-class-name: com.mysql.jdbc.Driver
         slave2:
           url: jdbc:mysql://localhost:3306/test3?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false
           username: root
           password: root
           driver-class-name: com.mysql.jdbc.Driver
     ```

   - 数据源配置

     ```java
     @Configuration
     @Slf4j
     public class DataSourceConfiguration {

         /**
          * 此处采用 com.alibaba.druid.pool.DruidDataSource
          */
         @Value("${demo.datasource.type}")
         private Class<? extends DataSource> dataSourceType;

         /**
          * 写库主数据源
          */
         @Primary
         @Bean(name = "masterDataSource")
         @Qualifier("masterDataSource")
         @ConfigurationProperties(prefix="demo.datasource.master")
         public DataSource masterDataSource() {
             log.info("-------------------- Master Data Source Init ---------------------");
             return new com.alibaba.druid.pool.DruidDataSource(); 
         }

         /**
          * 从数据源1
          */
         @Bean(name = "readDataSource1")
         @Qualifier("readDataSource1")
         @ConfigurationProperties(prefix="demo.datasource.slave1")
         public DataSource readDataSource1() {
             log.info("-------------------- Slave Data Source1 Init ---------------------");
             return DataSourceBuilder.create().type(dataSourceType).build();
         }

         /**
          * 从数据源2
          */
         @Bean(name = "readDataSource2")
         @Qualifier("readDataSource2")
         @ConfigurationProperties(prefix="demo.datasource.slave2")
         public DataSource readDataSource2() {
             log.info("-------------------- Slave Data Source2 Init ---------------------");
             return DataSourceBuilder.create().type(dataSourceType).build();
         }
     }
     ```

2. 数据库选则

   ```java
   /**
    * 数据库路由，有多少个数据源就要配置多少个bean
    */
   @Bean
   public AbstractRoutingDataSource roundRobinDataSourceProxy() {
       int dataSourceNumber = Integer.parseInt(slaveSize); // 从库数量

       /**
        * 多数据源切换,所有的数据库源交给AbstractRoutingDataSource类，并由它的determineCurrentLookupKey()进行决定数据源的选择
        */
       AbstractRoutingDataSource proxy = new AbstractRoutingDataSource() {

           private AtomicInteger count = new AtomicInteger(0);

           @Override
           protected Object determineCurrentLookupKey() {
               String typeKey = DataSourceContextHolder.getJdbcType();
               if(typeKey == null){
                   return DataSourceType.WRITE.getType(); // 为空返回主库是否更好
               }

               // 主库直接返回
               if (typeKey.equals(DataSourceType.WRITE.getType())){
                   log.info("dataSource现在库为：" + DataSourceType.WRITE.getType());
                   return DataSourceType.WRITE.getType();
               }

               // 从库简单负载均衡
               int number = count.getAndAdd(1);
               int lookupKey = number % dataSourceNumber;
               String readKey = DataSourceType.READ.getType() + (lookupKey + 1);
               log.info("dataSource现在库为：" + readKey);
               return readKey;
           }
       };

       Map<Object, Object> targetDataSources = new HashMap<>();
       // 主库
       DataSource writeDataSource = (DataSource) context.getBean("masterDataSource");
       targetDataSources.put(DataSourceType.WRITE.getType(), context.getBean("masterDataSource"));
       // 从库
       for (int i = 0; i < dataSourceNumber; i++) {
           targetDataSources.put(DataSourceType.READ.getType() + (i + 1 ), context.getBean("readDataSource" + (i + 1)));
       }

       // 如果找不到，就用配置默认的数据源
       proxy.setDefaultTargetDataSource(writeDataSource); // 默认库为主库
       proxy.setTargetDataSources(targetDataSources);
       return proxy;
   }
   ```

3. 我们需要在程序运行时调用DataSourceContextHolder来choose data source，决定什么时候选则主库什么时候选则从库，对其赋值。下面是实现的核心部分，也就是AOP部分，DataSourceAspect定义如下：

   ```java
   /**
    * 数据源选择切面
    */
   // 必须在事务@EnableTransactionManagement 之前执行，否则在service打开事务打开时，就会调用determineCurrentLookupKey()决定数据库源，而还没有执行切面决定lookupkey会有问题，所以order的值越小，越先执行
   @Order(1) 
   @Component
   @Aspect
   public class DataSourceAspect {

       @Before("@annotation(com.gtw.split.annotation.WriteDataSource)")
       public void setWriteDataSourceType() {
           DataSourceContextHolder.writeSource();
       }

       @Before("@annotation(com.gtw.split.annotation.ReadDataSource)")
       public void setReadDataSourceType() {
           // 如果已经开启写事务了，继续使用写库，即之后的所有读都从写库读
           if(!DataSourceType.WRITE.getType().equals(DataSourceContextHolder.getJdbcType())){
               DataSourceContextHolder.readSource();
           }
       }
   }
   ```

## 开始使用

```java
@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;

    @ReadDataSource
    public PageInfo<User> queryPage(int pageNum, int pageSize){
        Page<User> page = PageHelper.startPage(pageNum, pageSize);
        // PageHelper会自动拦截到下面这查询sql
        this.userMapper.getAll();
        return page.toPageInfo();
    }

    @WriteDataSource
    @Transactional
    public void save(User user) {
        userMapper.insert(user);
        throw new RuntimeException("抛个错误看看"); // 抛出异常，事务可回滚
    }
}
```

