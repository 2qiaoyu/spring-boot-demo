package com.joham.springbootdemo.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存控制器
 *
 * @author joham
 */
@RestController
public class StockController {

    private Logger logger = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    /**
     * 乐观锁更新库存
     *
     * @param sid
     * @return
     */
    @RequestMapping("/createOptimisticOrder/{sid}")
    public String createOptimisticOrder(@PathVariable int sid) {
        int id = 0;
        try {
            id = stockService.createOptimisticOrder(sid);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return String.valueOf(id);
    }

    /**
     * redis分布式锁更新库存
     *
     * @param sid
     * @return
     */
    @RequestMapping("/createRedisOrder/{sid}")
    public String createRedisOrder(@PathVariable int sid) {
        int id = 0;
        try {
            id = stockService.createRedisOrder(sid);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return String.valueOf(id);
    }
}
