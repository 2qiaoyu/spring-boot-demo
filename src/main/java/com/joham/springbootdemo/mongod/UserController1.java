package com.joham.springbootdemo.mongod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mongodb测试
 *
 * @author qiaoyu
 */
@RestController
@RequestMapping("/user")
public class UserController1 {

    @Autowired
    private UserDao userDao;

    /**
     * 添加
     *
     * @throws Exception
     */
    @RequestMapping("/add")
    public void testSaveUser(){
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUserName("小明");
        user.setPassWord("fffooo123");
        userDao.saveUser(user);
    }

    /**
     * 查询
     */
    @RequestMapping("/find")
    public void findUserByUserName() {
        UserEntity user = userDao.findUserByUserName("小明");
        System.out.println("user is " + user);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public void updateUser() {
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUserName("天空");
        user.setPassWord("fffxxxx");
        userDao.updateUser(user);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public void deleteUserById() {
        userDao.deleteUserById(1L);
    }
}
