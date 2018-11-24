package com.joham.springbootdemo.article;

import com.mysema.commons.lang.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 计数
 *
 * @author joham
 */
@RestController
public class UpdateCountController {

    @Autowired
    private UpdateCountService updateCountService;

    @RequestMapping("updateCount")
    public String updateCount() {
        updateCountService.updateCounter("test", 1);
        return "1";
    }

    @RequestMapping("getCount")
    public List<Pair<Integer, Integer>> getCount(int time) {
        return updateCountService.getCounter("test", time);
    }
}
