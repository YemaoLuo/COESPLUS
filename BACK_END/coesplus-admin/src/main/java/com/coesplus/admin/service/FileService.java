package com.coesplus.admin.service;

import com.coesplus.common.utils.Result;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    Result upload(MultipartFile file);
}
