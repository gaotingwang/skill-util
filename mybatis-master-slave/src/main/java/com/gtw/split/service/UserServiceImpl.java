package com.gtw.split.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gtw.split.annotation.ReadDataSource;
import com.gtw.split.annotation.WriteDataSource;
import com.gtw.split.domain.User;
import com.gtw.split.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userMapper.getOne(id);
    }

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
//        throw new RuntimeException("抛个错误看看"); // 抛出异常，事务可回滚
    }

    /**
     * 写事务里面调用读
     * 方法内部调用 thisService().save(user)，不能直接调用 save(user),因为是调用 this.save(user) 而非代理类的方法,不会触发AOP拦截
     */
    public void writeAndRead(User user){
        //这里走写库，那后面的读也都要走写库
        thisService().save(user);
        //这是刚刚插入的
        User newUser = thisService().getUser(user.getId());
        System.out.println(newUser);
        //为了测试,3个库中id=1的user是不一样的
        User uuu = thisService().getUser(1L);
        System.out.println(uuu);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userMapper.delete(id);
    }

    /**
     * 自调用注解无法生效代替方法
     */
    private IUserService thisService(){
        return applicationContext.getBean(this.getClass());
    }
}
