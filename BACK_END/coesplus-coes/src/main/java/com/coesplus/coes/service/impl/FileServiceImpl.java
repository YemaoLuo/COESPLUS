package com.coesplus.coes.service.impl;

import com.coesplus.coes.service.FileService;
import com.coesplus.common.minio.entity.UploadResult;
import com.coesplus.common.utils.MinioUtils;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional
public class FileServiceImpl implements FileService {

    @Resource
    private MinioUtils minioUtils;

    @Override
    public Result upload(MultipartFile file) {
        UploadResult upload = minioUtils.upload(file);
        log.info(upload.toString());
        return Result.ok(upload);
    }
}
