package com.coesplus.admin.controller;

import com.coesplus.admin.dto.TeacherCommentDto;
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
public class TeacherCommentControllerTest {

    @Resource
    private TeacherCommentController teacherCommentController;


    @Test
    void page() {
        Result page = teacherCommentController.page(1, 10, null, null, null);
        log.info(page.toString());
    }

    @Test
    void add() {
        TeacherCommentDto dto = new TeacherCommentDto();
        dto.setComment("unitTest")
                .setTeacherName("赵庆林")
                .setStudentName("mike");
        Result result = teacherCommentController.add(dto);
        log.info(result.toString());
    }

    @Test
    void physicalDelete() {
        Result result = teacherCommentController.physicalDelete("d39ab73621b4c959a364ff90fbac286d");
        log.info(result.toString());
    }
}