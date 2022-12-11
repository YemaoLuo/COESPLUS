package com.coesplus.admin.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class CourseDto {

    private String id;

    private String courseId;

    private String name;

    private String facultyId;

    private String teacherId;

    private String classroom;

    private int isDeleted;

    private int credit;

    @JsonFormat(pattern = "HH:mm")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm")
    private Date endTime;

    private List<String> day;

    public String getDay() {
        String dayStr = "";
        for (int i = 0; i < day.size(); i++) {
            dayStr = dayStr + day.get(i) + ";";
        }
        return dayStr;
    }

    private int seat;

    private int seatChosen;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
