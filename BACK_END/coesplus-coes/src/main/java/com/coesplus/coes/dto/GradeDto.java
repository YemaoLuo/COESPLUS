package com.coesplus.coes.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GradeDto {

    private String courseId;

    private String studentId;

    private String grade;

}
