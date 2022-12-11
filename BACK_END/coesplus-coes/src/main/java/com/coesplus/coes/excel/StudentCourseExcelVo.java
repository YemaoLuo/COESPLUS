package com.coesplus.coes.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ContentRowHeight(30)
public class StudentCourseExcelVo {

   @ExcelProperty({"学生课程信息表", "主键ID"})
   private String id;

   @ExcelProperty({"学生课程信息表", "课程名"})
   private String name;

   @ExcelProperty({"学生课程信息表", "课程ID"})
   private String courseId;

   @ExcelProperty({"学生课程信息表", "学院"})
   private String facultyName;

   @ExcelProperty({"学生课程信息表", "任课教师"})
   private String teacherName;

   @ExcelProperty({"学生课程信息表", "教室"})
   private String classroom;

   @ExcelProperty({"学生课程信息表", "状态"})
   private String isDeleted;

   @ExcelProperty({"学生课程信息表", "起始时间"})
   private Date startTime;

   @ExcelProperty({"学生课程信息表", "结束时间"})
   private Date endTime;

   @ExcelProperty({"学生课程信息表", "星期"})
   private String day;

   @ExcelProperty({"学生课程信息表", "成绩"})
   private String grade;


   @ExcelProperty({"学生课程信息表", "创建时间"})
   private Date createTime;

   @ExcelProperty({"学生课程信息表", "更新时间"})
   private Date updateTime;

}
