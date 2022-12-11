package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TeacherCommentDashboardVo {

   private String name;

   private int count;
}
