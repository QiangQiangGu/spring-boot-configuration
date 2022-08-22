package com.joe.boot.redis.configuration;


import cn.hutool.core.io.FileUtil;
import com.joe.boot.minio.configuration.client.MinIoClient;
import com.joe.boot.redis.configuration.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author QiangQiang Gu
 * @since 2021/8/21 20:27
 */
@SpringBootTest
@RunWith(value = SpringRunner.class)
public class TestApplication {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private MinIoClient minIoClient;

    @Test
    public void test01() {
        Object decrement = redisUtil.get("decrement");
        System.out.println("decrement = " + decrement);
    }

    @Test
    public void test02() {
        String file = "C:\\code\\demo-code\\data\\640.jpg";
        InputStream inputStream = FileUtil.getInputStream(file);
        String s = minIoClient.upload(inputStream, "image/jpeg");
        System.out.println("s = " + s);
    }

}
