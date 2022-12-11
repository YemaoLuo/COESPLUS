package com.coesplus.coes.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Accessors(chain = true)
public class StudentCourseVo {

   private String id;

   private String courseId;

   private String name;

   private String facultyName;

   private String teacherName;

   private String classroom;

   private String grade;

   private int isDeleted;

   @JsonFormat(pattern = "HH:mm")
   @DateTimeFormat(pattern = "HH:mm")
   private Date startTime;

   @JsonFormat(pattern = "HH:mm")
   @DateTimeFormat(pattern = "HH:mm")
   private Date endTime;

   private String day;

   @TableField(fill = FieldFill.INSERT)
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date createTime;

   @TableField(fill = FieldFill.UPDATE)
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date updateTime;
}
