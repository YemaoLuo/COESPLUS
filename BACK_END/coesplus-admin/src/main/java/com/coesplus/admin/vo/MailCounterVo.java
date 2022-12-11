package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MailCounterVo {

   private String subject;

   private int count;
}
