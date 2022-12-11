package com.coesplus.admin;

import cn.hutool.core.thread.ThreadUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
@Transactional
@Rollback
public class RedissionTests {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;

    private int counter = 0;
    private List<Integer> counterList = new ArrayList<>();


    @Test
    void testLock() {
        for (int i = 0; i < 1000; i++) {
            final int no = i;
            ThreadUtil.execAsync(() -> {
                increaseCounterByLock(no);
            });
        }
    }

    @SneakyThrows
    void increaseCounterByLock(int no) {
        RLock lock = redissonClient.getLock("CounterLock");
        boolean isLock = lock.tryLock(5L, 10L, TimeUnit.SECONDS);
        if (isLock) {
            if (counter < 50) {
                //counter += 1;
                log.info(no + "::" + counter);
            }
        }
        lock.unlock();
    }

    @Test
    @SneakyThrows
    void testSemaphore() {
        RSemaphore semaphore = redissonClient.getSemaphore("CounterSemaphore");
        semaphore.release(50);
        for (int i = 0; i < 1000; i++) {
            final int no = i;
            ThreadUtil.execAsync(() -> {
                increaseCounterBySemaphore(no);
            });
        }
        redisTemplate.delete("CounterSemaphore");
    }

    @SneakyThrows
    void increaseCounterBySemaphore(int no) {
        RSemaphore semaphore = redissonClient.getSemaphore("CounterSemaphore");
        boolean acquire = semaphore.tryAcquire();
        if (acquire) {
            //counterList.add(1);
            log.info(no + "::" + counter);
        }else {
            //log.info(no + "::" + counterList.size());
        }
    }

    @Test
    @SneakyThrows
    void testSemaphore2() {
        RSemaphore semaphore = redissonClient.getSemaphore("CounterSemaphore");
        semaphore.release(50);
        for (int i = 0; i < 100; i++) {
            RSemaphore semaphore3 = redissonClient.getSemaphore("CounterSemaphore");
            boolean acquire = semaphore3.tryAcquire();
            log.info(i + "::" + acquire);
        }
    }

    @Test
    @SneakyThrows
    void testSemaphore3() {
        RSemaphore semaphore = redissonClient.getSemaphore("CounterSemaphore");
        semaphore.release(50);
    }
}
