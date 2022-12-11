package com.coesplus.admin.controller;

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
class DashboardControllerTest {

    @Resource
    private DashboardController dashboardController;


    @Test
    void loginAddress() {
        Result result = dashboardController.loginAddress();
        log.info(result.toString());
    }

    @Test
    void loginRank() {
        Result result = dashboardController.loginRank();
        log.info(result.toString());
    }

    @Test
    void loginCounter() {
        Result result = dashboardController.loginCounter();
        log.info(result.toString());
    }

    @Test
    void activeStudent() {
        Result result = dashboardController.activeStudent();
        log.info(result.toString());
    }

    @Test
    void activeTeacher() {
        Result result = dashboardController.activeTeacher();
        log.info(result.toString());
    }

    @Test
    void emailCounter() {
        Result result = dashboardController.emailCounter();
        log.info(result.toString());
    }

    @Test
    void teacherComment() {
        Result result = dashboardController.teacherComment();
        log.info(result.toString());
    }
}