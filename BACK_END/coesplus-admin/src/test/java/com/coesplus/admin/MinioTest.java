package com.coesplus.admin;

import com.coesplus.common.minio.entity.UploadResult;
import com.coesplus.common.utils.FileUtils;
import com.coesplus.common.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;

@SpringBootTest
@Slf4j
public class MinioTest {

    @Resource
    private MinioUtils minioUtils;

    @Test
    void testUpload() {
        File file = new File("src/main/resources/bootstrap.properties");
        UploadResult upload = minioUtils.upload(FileUtils.getMultipartFile(file));
        log.info(upload.toString());
    }

    @Test
    void testPreview() {
        String preview = minioUtils.preview("cdc00e7f40f24794b97f074152090cab1665224268654.properties");
        log.info(preview);
    }

    @Test
    void testDelete() {
        String fileName = "cdc00e7f40f24794b97f074152090cab1665224268654.properties";
        boolean flag = minioUtils.remove(fileName);
        if (flag) {
            log.info("Success");
        } else {
            log.error("Fail");
        }
    }

}
