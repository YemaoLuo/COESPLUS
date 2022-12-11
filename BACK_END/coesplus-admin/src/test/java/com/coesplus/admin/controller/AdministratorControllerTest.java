package com.coesplus.admin.controller;

import com.coesplus.common.entity.Administrator;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@Rollback
@Transactional
class AdministratorControllerTest {

    @Resource
    private AdministratorController administratorController;


    @Test
    void page() {
        Result result = administratorController.page(1, 10);
        log.info(result.toString());
    }

    @Test
    void add() {
        Administrator administrator = new Administrator();
        administrator.setName("unitTest")
                .setPassword("unitTest")
                .setEmail("unitTest");
        Result result = administratorController.add(administrator);
        log.info(result.toString());
    }

    @Test
    void update() {
        Administrator administrator = new Administrator();
        administrator.setName("unitTest")
                .setPassword("unitTest2")
                .setEmail("unitTest");
        Result result = administratorController.update(administrator);
        log.info(result.toString());
    }
}
