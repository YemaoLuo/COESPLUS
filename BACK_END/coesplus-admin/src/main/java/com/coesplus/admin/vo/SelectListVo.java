package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SelectListVo {

   private String label;

   private String value;
}
