package com.coesplus.admin.controller;

import com.coesplus.admin.dto.CourseDto;
import com.coesplus.admin.dto.CourseStudentDto;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@SpringBootTest
@Rollback
@Transactional
class CourseControllerTest {

    @Resource
    private CourseController courseController;


    @Test
    void page() {
        Result result = courseController.page(0, 10, null, null, null, null, null);
        log.info(result.toString());
    }

    @Test
    void excel() {
        courseController.excel(null, null, "0f6796410d4f1aead47fd1c8285e2744", null, null, null);
    }

    @Test
    void excelStudent() {
        courseController.excelStudent(null, "0f6796410d4f1aead47fd1c8285e2744", null);
    }

    @Test
    void delete() {
        Result result = courseController.delete("0f6796410d4f1aead47fd1c8285e2744", true);
        log.info(result.toString());
    }

    @Test
    void redoDelete() {
        Result result = courseController.redoDelete("0f6796410d4f1aead47fd1c8285e2744");
        log.info(result.toString());
    }

    @Test
    void physicalDelete() {
        Result result = courseController.physicalDelete("0f6796410d4f1aead47fd1c8285e2744", true);
        log.info(result.toString());
    }

    @Test
    void update() {
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseId("unitTest")
                .setId("0f6796410d4f1aead47fd1c8285e2744")
                .setDay(Arrays.asList("test1", "test2"));
        Result result = courseController.update(courseDto);
        log.info(result.toString());
    }

    @Test
    void add() {
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseId("unitTest")
                .setDay(Arrays.asList("test1", "test2"));
        Result result = courseController.add(courseDto);
        log.info(result.toString());
    }

    @Test
    void courseStudent() {
        Result result = courseController.courseStudent(1, 10, "543dd279a9628048bbc64b358afb9e61");
        log.info(result.toString());
    }

    @Test
    void addCourseStudent() {
        CourseStudentDto courseStudentDto = new CourseStudentDto();
        courseStudentDto.setStudentId("00027befaaa915c87c26458885c49945")
                .setCourseId("543dd279a9628048bbc64b358afb9e61");
        Result result = courseController.addCourseStudent(courseStudentDto);
        log.info(result.toString());
    }
}
