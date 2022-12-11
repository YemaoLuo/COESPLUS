package com.coesplus.admin.controller;

import com.coesplus.common.utils.FileUtils;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@Slf4j
@SpringBootTest
@Rollback
@Transactional
class FileControllerTest {

    @Resource
    private FileController fileController;


    @Test
    void upload() {
        MultipartFile file = FileUtils.getMultipartFile(new File("src/main/resources/application.yml"));
        Result result = fileController.upload(file);
        log.info(result.toString());
    }

    @Test
    void cleanPicCache() {
        Result result = fileController.cleanPicCache();
        log.info(result.toString());
    }

    @Test
    void detail() {
        Result result = fileController.detail();
        log.info(result.toString());
    }
}