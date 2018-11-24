package com.joham.springbootdemo.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * email自动补全
 *
 * @author joham
 */

@Service
public class AutoCompleteEmailService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 在ASCII码中，排在z后面的第一个字符是{，排在a前面的第一个字符时`
     * 查找带有abc前缀的单词实际上就是查找介于abbz之后和abd之前的字符串
     */
    private static final String VALID_CHARACTERS = "`abcdefghijklmnopqrstuvwxyz{";

    /**
     * 根据给定前缀生成查找范围
     *
     * @param prefix
     * @return
     */
    public String[] findPrefixRange(String prefix) {
        int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1));
        char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0);
        String start = prefix.substring(0, prefix.length() - 1) + suffix + '{';
        String end = prefix + '{';
        return new String[]{start, end};
    }

    public Set<String> autoCompleteOnPrefix(String guild, String prefix) {
        String[] range = findPrefixRange(prefix);
        //防止同时向一个公会成员发邮件，添加uuid作为每次查询的唯一标示
        String identifier = UUID.randomUUID().toString();
        String start = range[0] + identifier;
        String end = range[1] + identifier;
        String zsetName = "members:" + guild;
        stringRedisTemplate.opsForZSet().add(zsetName, start, 0);
        stringRedisTemplate.opsForZSet().add(zsetName, end, 0);
        Set<String> items;
        while (true) {
            SessionCallback<List<Object>> sessionCallback = new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    //watch确保有序集合在范围查找过程中发生变化时，进行重试
                    operations.watch(zsetName);
                    //获取插入元素的开始位置
                    int sindex = operations.opsForZSet().rank(zsetName, start).intValue();
                    //获取插入元素的结束位置
                    int eindex = operations.opsForZSet().rank(zsetName, end).intValue();
                    operations.multi();
                    //删除插入的元素
                    operations.opsForZSet().remove(zsetName, start);
                    operations.opsForZSet().remove(zsetName, end);
                    //获取插入元素之间的匹配值
                    operations.opsForZSet().range(zsetName, sindex, eindex - 2);
                    return operations.exec();
                }
            };
            List<Object> results = stringRedisTemplate.execute(sessionCallback);
            if (results != null) {
                items = (Set<String>) results.get(results.size() - 1);
                break;
            }
        }
        //若匹配的集合中包含其请求插入的元素，则删除
        items.removeIf(s -> s.indexOf('{') != -1);
        return items;
    }

    /**
     * 加入公会
     *
     * @param guild
     * @param user
     */
    public void joinGuild(String guild, String user) {
        stringRedisTemplate.opsForZSet().add("members:" + guild, user, 0);
    }

    /**
     * 离开公会
     *
     * @param guild
     * @param user
     */
    public void leaveGuild(String guild, String user) {
        stringRedisTemplate.opsForZSet().remove("members:" + guild, user);
    }

}
