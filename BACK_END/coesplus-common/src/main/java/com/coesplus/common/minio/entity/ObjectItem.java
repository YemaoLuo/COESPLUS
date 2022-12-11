package com.coesplus.common.minio.entity;

import lombok.Data;

@Data
public class ObjectItem {

    private String objectName;

    private Long size;
}
