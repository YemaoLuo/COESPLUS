package com.coesplus.admin.controller;

import com.coesplus.admin.vo.FacultyVo;
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
class FacultyControllerTest {

    @Resource
    private FacultyController facultyController;


    @Test
    void page() {
        Result result = facultyController.page(0, 10, null, null);
        log.info(result.toString());
    }

    @Test
    void excel() {
        facultyController.excel(null, null, null);
    }

    @Test
    void delete() {
        Result result = facultyController.delete("10067d5164a24943a73f8c6f88b6185a", false);
        log.info(result.toString());
    }

    @Test
    void redoDelete() {
        Result result = facultyController.redoDelete("10067d5164a24943a73f8c6f88b6185a");
        log.info(result.toString());
    }

    @Test
    void physicalDelete() {
        Result result = facultyController.physicalDelete("10067d5164a24943a73f8c6f88b6185a", false);
        log.info(result.toString());
    }

    @Test
    void update() {
        FacultyVo facultyVo = new FacultyVo();
        facultyVo.setId("10067d5164a24943a73f8c6f88b6185a")
                        .setName("unitTest");
        Result result = facultyController.update(facultyVo);
        log.info(result.toString());
    }

    @Test
    void add() {
        FacultyVo facultyVo = new FacultyVo();
        facultyVo.setName("unitTest");
        Result result = facultyController.add(facultyVo);
        log.info(result.toString());
    }
}