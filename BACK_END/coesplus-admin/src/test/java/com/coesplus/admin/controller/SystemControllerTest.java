package com.coesplus.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
@Transactional
@Rollback
public class SystemControllerTest {

    @Resource
    private SystemController systemController;


    @Test
    void sendActivationEmail() {
        systemController.sendActivationEmail("student", "00027befaaa915c87c26458885c49945", null);
    }
}