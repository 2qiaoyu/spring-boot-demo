package com.joham.springbootdemo.shiro;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;

    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoDao.findByUsername(username);
    }
}