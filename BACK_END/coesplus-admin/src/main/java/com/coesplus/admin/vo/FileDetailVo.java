package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class FileDetailVo {

   private Long totalSize;

   private Long totalAmount;

   private List<FileItemVo> separateDetail;
}
