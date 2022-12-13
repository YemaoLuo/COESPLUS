package com.coesplus.coes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SemesterCourseVo {

    private String id;

    private String courseName;

    private String facultyName;

    private String teacherName;

    private String courseId;

    private String name;

    private String day;

    private int credit;

    private String classroom;

    private int availableSeat;

    private int seat;

    private int isChosen;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date endTime;
}
