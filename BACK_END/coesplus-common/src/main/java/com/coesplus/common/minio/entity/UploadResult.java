package com.coesplus.common.minio.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadResult {

    private String fileName;

    private String url;
}
