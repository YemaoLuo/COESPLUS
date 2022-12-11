package com.coesplus.coes.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ContentRowHeight(30)
public class CourseGradeExcelVo {

   @ExcelProperty({"学生成绩表", "主键ID"})
   private String id;

   @ExcelProperty({"学生成绩表", "课程名"})
   private String name;

   @ExcelProperty({"学生成绩表", "课程ID"})
   private String courseId;

   @ExcelProperty({"学生成绩表", "任课教师"})
   private String teacherName;

   @ExcelProperty({"学生成绩表", "学生学院"})
   private String facultyName;

   @ExcelProperty({"学生成绩表", "学生名"})
   private String studentName;

   @ExcelProperty({"学生成绩表", "学生ID"})
   private String studentId;

   @ExcelProperty({"学生成绩表", "成绩"})
   private String grade;

   @ExcelProperty({"学生成绩表", "状态"})
   private String isDeleted;


}
