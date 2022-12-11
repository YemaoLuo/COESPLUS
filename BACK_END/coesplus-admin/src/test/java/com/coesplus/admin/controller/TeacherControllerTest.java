package com.coesplus.admin.controller;

import com.coesplus.admin.dto.TeacherDto;
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
public class TeacherControllerTest {

    @Resource
    private TeacherController teacherController;


    @Test
    void page() {
        Result page = teacherController.page(0, 1, null, null, null, null, null, null);
        log.info(page.toString());
    }

    @Test
    void excel() {
        teacherController.excel(null, null, "f91b15750453874c834e09fe1fc1aae9", null, null, null, null);
    }

    @Test
    void delete() {
        Result result = teacherController.delete("f91b15750453874c834e09fe1fc1aae9");
        log.info(result.toString());
    }

    @Test
    void redoDelete() {
        Result result = teacherController.redoDelete("f91b15750453874c834e09fe1fc1aae9");
        log.info(result.toString());
    }

    @Test
    void physicalDelete() {
        Result result = teacherController.physicalDelete("f91b15750453874c834e09fe1fc1aae9", false);
        log.info(result.toString());
    }

    @Test
    void update() {
        TeacherDto dto = new TeacherDto();
        dto.setId("f91b15750453874c834e09fe1fc1aae9")
                .setSex("1");
        Result result = teacherController.update(dto);
        log.info(result.toString());
    }

    @Test
    void add() {
        TeacherDto dto = new TeacherDto();
        dto.setTeacherId("unitTestTeacher")
                .setSex("1");
        Result result = teacherController.add(dto);
        log.info(result.toString());
    }

    @Test
    void resetPassword() {
        Result result = teacherController.resetPassword("f91b15750453874c834e09fe1fc1aae9", "unitTest");
        log.info(result.toString());
    }
}