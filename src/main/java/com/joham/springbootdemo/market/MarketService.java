package com.joham.springbootdemo.market;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * redis事务
 *
 * @author joham
 */
@Service
public class MarketService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean listItem(String itemId, String sellerId, long price) {
        String inventory = "inventory:" + sellerId;
        String item = itemId + '_' + sellerId;
        //事物
        SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                /**
                 * WATCH 命令用于在事务开始之前监视任意数量的键:
                 * 当调用 EXEC 命令执行事务时， 如果任意一个被监视的键已经被其他客户端修改了
                 * 那么整个事务不再执行， 直接返回失败
                 * 此处用来监视包裹是否发生变化
                 */
                operations.watch(inventory);
                if (!operations.opsForSet().isMember(inventory, itemId)) {
                    operations.unwatch();
                    return null;
                }
                operations.multi();
                operations.opsForZSet().add("market:", item, price);
                operations.opsForSet().remove(inventory, itemId);
                return operations.exec();
            }
        };
        return redisTemplate.execute(sessionCallback) == null ? false : true;
    }

    public boolean purchaseItem(String buyerId, String itemId, String sellerId) {
        String buyer = "user:" + buyerId;
        String seller = "user:" + sellerId;
        String item = itemId + '_' + sellerId;
        String inventory = "inventory:" + buyerId;
        //事物
        SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                //监视市场和卖家信息
                operations.watch(Lists.newArrayList("market:", buyer));
                //获取商品售价
                long sellPrice = operations.opsForZSet().score("market:", item).longValue();
                //获取买家钱数
                long funds = Long.valueOf((String) operations.opsForHash().get(buyer, "funds"));
                if (sellPrice > funds) {
                    operations.unwatch();
                    return null;
                }
                operations.multi();
                operations.opsForHash().increment(seller, "funds", sellPrice);
                operations.opsForHash().increment(buyer, "funds", -sellPrice);
                operations.opsForSet().add(inventory, itemId);
                operations.opsForZSet().remove("market:", item);
                return operations.exec();
            }
        };
        return redisTemplate.execute(sessionCallback) == null ? false : true;
    }

}
