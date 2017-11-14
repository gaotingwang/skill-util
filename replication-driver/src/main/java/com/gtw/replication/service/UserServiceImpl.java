package com.gtw.replication.service;

import com.gtw.replication.dao.UserMapper;
import com.gtw.replication.domain.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 只有readOnly = true开启只读时才走从库查询，其他一律走主库操作
 */
@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserMapper userMapper;

    /**
     * 使用@Transactional(readOnly = true)走从库查询
     */
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userMapper.getOne(id);
    }

    /**
     * 未开启readOnly = true走主库查询
     */
    @Transactional
    public List<User> queryPage(int offset, int limit){
        return this.userMapper.getAll(new RowBounds(offset, limit));
    }

    /**
     * 不加@Transactional注解，相当于没有指定readOnly=true，走主库，无法进行事务回滚
     */
    @Transactional
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
//        throw new RuntimeException("抛个错误看看"); // 抛出异常，事务可回滚
    }

    public void writeAndRead(User user){
        thisService().save(user);

        // 此时走主库还是从库，看此方法外部是否有@Transactional注解
        // 有的话以外部为准走主库，没有的话代理类的方法自己决定，此调用方法有readOnly=true走从库，需要注意若数据还未同步过去，查询到的值为null
        User newUser = thisService().getUser(user.getId());
        System.out.println(newUser);

        // 此处调的是自己本身类，不管是否有@Transactional注解，只要没有限定readOnly=true走的都是主库查询
        User uuu = this.getUser(1L);
        System.out.println(uuu);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userMapper.delete(id);
    }

    /**
     * 自调用注解无法生效代替方法，获取到的是代理类
     */
    private IUserService thisService(){
        return applicationContext.getBean(this.getClass());
    }
}
