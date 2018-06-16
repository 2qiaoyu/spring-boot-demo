package com.joham.springbootdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiaoyu
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public List<User> get(String email) {
        return userRepository.findByEmailLike(email);
    }

}
