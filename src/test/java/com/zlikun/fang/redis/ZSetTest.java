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
public class ZSetTest {

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
    public void zset() {

        // 向集合中添加抓取过的url
        jedis.zadd(target_collections ,System.currentTimeMillis() ,"http://125998.fang.com/") ;
        jedis.zadd(target_collections ,System.currentTimeMillis() - 1000 ,"http://125998.fang.com/house/1210125998/housedetail.htm") ;
        jedis.zadd(target_collections ,System.currentTimeMillis() + 1000 ,"http://125998.fang.com/photo/1210125998.htm") ;

        Assert.assertEquals(Long.valueOf(3) ,jedis.zcard(target_collections));
        Assert.assertEquals(Long.valueOf(2) ,jedis.zcount(target_collections ,0 ,System.currentTimeMillis()));
        Assert.assertEquals(Long.valueOf(1) ,jedis.zcount(target_collections ,System.currentTimeMillis() ,Long.MAX_VALUE));

        // 判断待抓取url是否已抓取且未过期
        // 然而无法比较，好吧，初衷OK，但不太可行，还得用SET集合

    }

    @After
    public void destroy() {
        jedis.del(target_collections) ;
        jedis.close();
    }

}
