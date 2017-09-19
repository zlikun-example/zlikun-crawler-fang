package com.zlikun.fang.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by zlikun on 2017/9/19.
 */
@Configuration
public class BeanConfigure {

    @Value("${spring.redis.host}")
    String host ;
    @Value("${spring.redis.port:6379}")
    int port ;

    @Bean @Primary
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig() ;
        config.setMaxIdle(50);
        config.setMaxTotal(50);
        config.setMinIdle(5);
        config.setMaxWaitMillis(1500);
        config.setTestWhileIdle(true);
        return new JedisPool(config ,host ,port) ;
    }

}
