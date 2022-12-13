package com.coesplus.admin.controller;

import com.coesplus.admin.dto.StudentDto;
import com.coesplus.common.utils.Result;
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
public class StudentControllerTest {

    @Resource
    private StudentController studentController;


    @Test
    void page() {
        Result page = studentController.page(1, 10, null, null, null, null, null, null);
        log.info(page.toString());
    }

    @Test
    void excel() {
        studentController.excel(null, null, "00027befaaa915c87c26458885c49945", null, null, null, null);
    }

    @Test
    void delete() {
        Result result = studentController.delete("00027befaaa915c87c26458885c49945");
        log.info(result.toString());
    }

    @Test
    void redoDelete() {
        Result result = studentController.redoDelete("00027befaaa915c87c26458885c49945");
        log.info(result.toString());
    }

    @Test
    void physicalDelete() {
        Result result = studentController.physicalDelete("00027befaaa915c87c26458885c49945");
        log.info(result.toString());
    }

    @Test
    void update() {
        StudentDto dto = new StudentDto();
        dto.setStudentId("00027befaaa915c87c26458885c49945")
                .setSex("1");
        Result result = studentController.update(dto);
        log.info(result.toString());
    }

    @Test
    void add() {
        StudentDto dto = new StudentDto();
        dto.setStudentId("unitTest")
                .setSex("1");
        Result result = studentController.add(dto);
        log.info(result.toString());
    }

    /*@Test
    void resetPassword() {
        Result result = studentController.resetPassword("00027befaaa915c87c26458885c49945", "unitTest");
        log.info(result.toString());
    }*/
}