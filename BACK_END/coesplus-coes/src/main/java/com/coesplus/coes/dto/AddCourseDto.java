package com.coesplus.coes.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddCourseDto {

   private String id;

   private int state;
}
