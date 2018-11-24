package com.joham.springbootdemo.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 自动补全
 *
 * @author joham
 */
@RestController
public class AutoCompleteEmailController {

    @Autowired
    private AutoCompleteEmailService autoCompleteEmailService;

    /**
     * 加入公会
     *
     * @param user
     * @return
     */
    @RequestMapping("joinGuild")
    public String joinGuild(String user) {
        autoCompleteEmailService.joinGuild("test", user);
        return "1";
    }

    /**
     * 离开工会
     *
     * @param user
     * @return
     */
    @RequestMapping("leaveGuild")
    public String leaveGuild(String user) {
        autoCompleteEmailService.leaveGuild("test", user);
        return "1";
    }

    /**
     * 自动补全
     *
     * @param prefix
     * @return
     */
    @RequestMapping("autoCompleteOnPrefix")
    public Set<String> autoCompleteOnPrefix(String prefix) {
        return autoCompleteEmailService.autoCompleteOnPrefix("test", prefix);
    }
}
