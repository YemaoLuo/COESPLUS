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
        // 添加新token
        // ID固定为585c99b4313dadc3e9f6522ebace6fea
        redisTemplate.opsForValue().set(RedisPrefix.accountAdminToken + token, "585c99b4313dadc3e9f6522ebace6fea");
        redisTemplate.opsForValue().set(RedisPrefix.accountAdminID + "585c99b4313dadc3e9f6522ebace6fea", token);
    }

    @Test
    @SneakyThrows
    public void tokenGenerator2() {
        try {
            redisTemplate.setEnableTransactionSupport(true);
            String token = "coesplusgpa4.0";
            //添加新token
            //ID固定为585c99b4313dadc3e9f6522ebace6fea
            String key = "COURSE_SEAT_REMAIN_8af24125c38619334b64e7143e8779800012e16670268c77a79e894ffef937c8";
//            redisTemplate.opsForValue().set(key, String.valueOf(50));
            String value = redisTemplate.opsForValue().get(key);
            //redisTemplate.exec();
//            redisTemplate.delete(key);
            log.info(value);
        }catch (Exception e) {
            redisTemplate.discard();
        }
    }
}
