package com.joham.springbootdemo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 *
 * @author joham
 */

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String username) {
        return loginService.login(username);
    }

    /**
     * 验证是否登录
     */
    @RequestMapping(value = "checkToken", method = RequestMethod.GET)
    public String checkToken(String token) {
        return loginService.checkToken(token);
    }

    @RequestMapping(value = "addCart", method = RequestMethod.POST)
    public String addCart(String token, String item, int count) {
        return loginService.addCart(token, item, count);
    }
}
