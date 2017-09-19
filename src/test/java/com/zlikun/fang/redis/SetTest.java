package com.zlikun.fang.redis;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by zlikun on 2017/9/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SetTest {

    @Autowired
    JedisPool jedisPool ;

    Jedis jedis ;

    String target_collections = "url:target:collections" ;

    @Before
    public void init() {
        jedis = this.jedisPool.getResource() ;
        jedis.del(target_collections) ;
    }

    /**
     * 使用ZSet结构实现已抓取队列，url过期时间时间戳为分数，每次比较未过期url
     */
    @Test
    public void set() {

        // 向集合中添加抓取过的url
        jedis.sadd(target_collections ,"http://125998.fang.com/") ;
        jedis.sadd(target_collections ,"http://125998.fang.com/house/1210125998/housedetail.htm") ;
        jedis.sadd(target_collections ,"http://125998.fang.com/photo/1210125998.htm") ;

        Assert.assertEquals(Long.valueOf(3) ,jedis.scard(target_collections));

        // 判断待抓取url是否已抓取且未过期
        Assert.assertTrue(jedis.sismember(target_collections ,"http://125998.fang.com/photo/1210125998.htm"));
    }

    @After
    public void destroy() {
        jedis.del(target_collections) ;
        jedis.close();
    }

}
