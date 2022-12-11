package com.coesplus.coes.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StudentGradeVo {

    private String id;

    private String name;

    private String studentId;

    private String sex;

    private String grade;

    private String photo;
}
