package com.gtw.split.routsource.config;

import com.github.pagehelper.PageHelper;
import com.gtw.split.routsource.config.source.DataSourceContextHolder;
import com.gtw.split.routsource.config.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@AutoConfigureAfter(DataSourceConfiguration.class)
//@EnableTransactionManagement(order = 10)
@MapperScan("com.gtw.split.mapper")
@Slf4j
public class MybatisConfiguration extends MybatisAutoConfiguration {
    @Autowired
    private ApplicationContext context;
    // 从库个数
    @Value("${demo.datasource.slave-size}")
    private String slaveSize;

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactorys() throws Exception {
        log.info("-------------------- 重载 sqlSessionFactory init ---------------------");
        return super.sqlSessionFactory(roundRobinDataSourceProxy());
    }

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
//                    return DataSourceType.WRITE.getType(); // 为空返回主库是否更好？这样不使用注解时可以默认走主库，否则必须使用注解才行
                    throw new NullPointerException("数据库路由时，决定使用哪个数据库源类型不能为空...");
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

    /**
     * 事务管理
     * @param roundRobinDataSourceProxy 数据库路由
     * @return 事务管理对象
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManagers(AbstractRoutingDataSource roundRobinDataSourceProxy) {
        log.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(roundRobinDataSourceProxy);
    }

    /**
     * 分页插件
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        p.setProperty("returnPageInfo", "check");
        p.setProperty("params", "count=countSql");
        pageHelper.setProperties(p);
        return pageHelper;
    }

}
