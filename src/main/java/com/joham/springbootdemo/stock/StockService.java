package com.joham.springbootdemo.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 库存服务
 *
 * @author joham
 */
@Service
public class StockService {

    private Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Autowired
    private RedisLock redisLock;

    @Transactional(rollbackFor = Exception.class)
    public int createOptimisticOrder(int sid) {
        int id = 0;
        //校验库存
        Stock stock = stockMapper.getStockById(sid);
        if (0 == stock.getSale()) {
            logger.info("库存不足");
        } else {
            //创建订单
            id = createOrder(stock);

            //乐观锁更新库存
            saleStockOptimistic(stock);
        }
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public int createRedisOrder(int sid) {
        int id = 0;
        //校验库存
        Stock stock = stockMapper.getStockById(sid);
        if (0 == stock.getSale()) {
            logger.info("库存不足");
        } else {
            //加锁
            String value = UUID.randomUUID().toString();
            boolean lock = redisLock.tryLock(stock.getId().toString(), value);
            if (lock) {
                //抢到锁，创建订单
                id = createOrder(stock);
                //扣减库存
                saleStock(stock);
                //释放锁
                redisLock.unlock(stock.getId().toString(), value);
            } else {
                logger.info("购买失败");
            }
        }
        return id;
    }

    /**
     * 乐观锁更新库存
     *
     * @param stock
     */
    private void saleStockOptimistic(Stock stock) {
        int count = stockMapper.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new RuntimeException("并发更新库存失败");
        }
    }

    /**
     * 普通更新库存
     *
     * @param stock
     */
    private void saleStock(Stock stock) {
        int count = stockMapper.updateStock(stock);
        if (count == 0) {
            throw new RuntimeException("更新库存失败");
        }
    }

    /**
     * 创建订单
     *
     * @param stock
     * @return
     */
    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        int id = stockOrderMapper.insertSelective(order);
        return id;
    }
}
