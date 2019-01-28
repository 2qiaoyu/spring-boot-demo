package com.joham.springbootdemo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author joham
 */

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUser")
    @Cacheable(value = "user-key")
    public User getUser() {
        User user = userService.findByUserName("aa");
        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
        return user;
    }

    @RequestMapping("/get")
    public User get() {
        List<User> user = userService.get("%11%");
        if (CollectionUtils.isEmpty(user)) {
            return null;
        }
        return user.get(0);
    }

    @PostMapping("/save")
    public String save(@RequestBody User1 user1) {
        System.out.println(user1.getId());
        System.out.println(user1.getUserName());
        return "1";
    }
}
