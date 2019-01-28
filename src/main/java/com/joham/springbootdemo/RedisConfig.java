package com.joham.springbootdemo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joham.springbootdemo.stock.RedisLock;
import com.joham.springbootdemo.stock.RedisToolsConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author joham
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public CacheManager cacheManager(StringRedisTemplate stringRedisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(stringRedisTemplate);
        //设置缓存过期时间
        //rcm.setDefaultExpiration(60);//秒
        return rcm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        template.setConnectionFactory(redisConnectionFactory);
        //初始化序列化框架（Jackson2JsonRedisSerializer）
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //初始化序列化框架（StringRedisSerializer）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //设置key的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //设置value的序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hash key的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //设置hash value的序列化方式
        template.setHashValueSerializer(stringRedisSerializer);

        return template;
    }


    @Bean
    public RedisLock build() {
        RedisLock redisLock = new RedisLock.Builder(jedisConnectionFactory, RedisToolsConstant.SINGLE)
                .lockPrefix("lock_")
                .sleepTime(100)
                .build();
        return redisLock;
    }
}