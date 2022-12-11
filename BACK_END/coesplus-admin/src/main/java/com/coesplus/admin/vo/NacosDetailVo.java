package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NacosDetailVo {

   private String serviceId;

   private String url;

   private String status;

   private String weight;
}
