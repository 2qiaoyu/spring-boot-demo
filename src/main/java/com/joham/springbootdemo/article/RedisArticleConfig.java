package com.joham.springbootdemo.article;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author qiaoyu
 */

@Configuration
public class RedisArticleConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
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
}
