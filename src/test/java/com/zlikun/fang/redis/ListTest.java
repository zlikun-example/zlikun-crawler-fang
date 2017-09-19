package com.zlikun.fang.redis;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by zlikun on 2017/9/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ListTest {

    @Autowired
    StringRedisTemplate template ;

    String seeds_queue = "url:seeds:queue" ;

    BoundListOperations blo ;

    @Before
    public void init() {
        blo = template.boundListOps(seeds_queue) ;
    }

    /**
     * 使用List结构实现URL队列
     */
    @Test
    public void list() {

        // URL入队
        blo.leftPush("http://125998.fang.com/") ;
        blo.leftPush("http://125998.fang.com/house/1210125998/housedetail.htm") ;
        blo.leftPush("http://125998.fang.com/photo/1210125998.htm") ;

        Assert.assertEquals(Long.valueOf(3) ,blo.size());

        // URL出队
        Assert.assertEquals("http://125998.fang.com/" ,blo.rightPop(3 , TimeUnit.SECONDS)) ;
        Assert.assertEquals(Long.valueOf(2) ,blo.size());

    }

    @After
    public void destroy() {
        template.delete(seeds_queue);
    }

}
