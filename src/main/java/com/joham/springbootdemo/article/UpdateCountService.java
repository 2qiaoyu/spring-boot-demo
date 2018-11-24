package com.joham.springbootdemo.article;

import com.mysema.commons.lang.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 计数
 *
 * @author joham
 */
@Service
public class UpdateCountService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //以秒为单位的计数器精度，分别为1秒、5秒、1分钟、5分钟、1小时、5小时、1天
    private static final int[] PRECISION = new int[]{1, 5, 60, 300, 3600, 18000, 86400};

    /**
     * 更新计数器
     *
     * @param name
     * @param count
     */
    void updateCounter(String name, int count) {
        long now = System.currentTimeMillis() / 1000;
        SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                for (int i = 0; i < PRECISION.length; i++) {
                    int prec = PRECISION[i];
                    //取得当前时间片的开始时间
                    long pnow = (now / prec) * prec;
                    String hash = String.valueOf(prec) + '_' + name;
                    //有序集合存储需要记录的时间片
                    operations.opsForZSet().add("known:", hash, i);
                    //时间片点击数散列
                    operations.opsForHash().increment("count_" + hash, String.valueOf(pnow), count);
                }
                return operations.exec();
            }
        };
        stringRedisTemplate.execute(sessionCallback);
    }

    /**
     * 获取点击数
     *
     * @param name
     * @param precision
     * @return
     */
    List<Pair<Integer, Integer>> getCounter(String name, int precision) {
        String hash = String.valueOf(precision) + '_' + name;
        Map<Object, Object> data = stringRedisTemplate.opsForHash().entries("count_" + hash);
        ArrayList<Pair<Integer, Integer>> results = new ArrayList<>();
        data.forEach((k, v) ->
                results.add(Pair.of(Integer.parseInt((String) k), Integer.parseInt((String) v))));
        return results;
    }
}
