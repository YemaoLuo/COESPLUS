package com.coesplus.admin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URL;
import java.util.Date;

@Data
@Accessors(chain = true)
@ContentRowHeight(70)
public class StudentExcelVo {

   @ExcelProperty({"学生信息表", "主键ID"})
   private String id;

   @ExcelProperty({"学生信息表", "姓名"})
   private String name;

   @ExcelProperty({"学生信息表", "学生ID"})
   private String studentId;

   @ExcelProperty({"学生信息表", "性别"})
   private String sex;

   @ExcelProperty({"学生信息表", "电话"})
   private String telephone;

   @ExcelProperty({"学生信息表", "邮件"})
   private String email;

   @ExcelProperty({"学生信息表", "学院"})
   private String facultyName;

   @ExcelProperty({"学生信息表", "入学年份"})
   private String joinYear;

   @ExcelProperty({"学生信息表", "状态"})
   private String isDeleted;

   @ExcelProperty({"学生信息表", "创建时间"})
   private Date createTime;

   @ExcelProperty({"学生信息表", "更新时间"})
   private Date updateTime;

   @ExcelProperty(value = {"学生信息表", "头像"})
   private URL photo;
}
