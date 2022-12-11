package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileItemVo {

   private String type;

   private Long separateAmount;

   private Long separateSize;
}
