package com.joham.springbootdemo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 登录
 *
 * @author joham
 */
@Service
public class LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String login(String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForHash().put("token:", token, username);
        return "1";
    }

    public String checkToken(String token) {
        return (String) redisTemplate.opsForHash().get("token:", token);
    }

    public String addCart(String token, String item, int count) {
        redisTemplate.opsForHash().put("cart:" + token, item, String.valueOf(count));
        return "1";
    }
}
