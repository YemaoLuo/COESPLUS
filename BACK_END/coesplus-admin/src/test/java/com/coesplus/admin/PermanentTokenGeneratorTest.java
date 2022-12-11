package com.coesplus.admin;

import com.coesplus.common.utils.RedisPrefix;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class PermanentTokenGeneratorTest {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @SneakyThrows
    public void tokenGenerator() {
        String token = "coesplusgpa4.0";
        //添加新token
        //ID固定为585c99b4313dadc3e9f6522ebace6fea
        redisTemplate.opsForValue().set(RedisPrefix.accountAdminToken + token, "585c99b4313dadc3e9f6522ebace6fea");
        redisTemplate.opsForValue().set(RedisPrefix.accountAdminID + "585c99b4313dadc3e9f6522ebace6fea", token);
    }
}
