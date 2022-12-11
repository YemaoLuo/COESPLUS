package com.coesplus.admin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ContentRowHeight(30)
public class CourseExcelVo {

   @ExcelProperty({"课程信息表", "主键ID"})
   private String id;

   @ExcelProperty({"课程信息表", "课程名"})
   private String name;

   @ExcelProperty({"课程信息表", "课程ID"})
   private String courseId;

   @ExcelProperty({"课程信息表", "学院"})
   private String facultyName;

   @ExcelProperty({"课程信息表", "任课教师"})
   private String teacherName;

   @ExcelProperty({"课程信息表", "教室"})
   private String classroom;

   @ExcelProperty({"课程信息表", "状态"})
   private String isDeleted;

   @ExcelProperty({"课程信息表", "起始时间"})
   private Date startTime;

   @ExcelProperty({"课程信息表", "结束时间"})
   private Date endTime;

   @ExcelProperty({"课程信息表", "星期"})
   private String day;

   @ExcelProperty({"课程信息表", "人数上限"})
   private int seat;

   @ExcelProperty({"课程信息表", "已选人数"})
   private int seatChosen;

   @ExcelProperty({"课程信息表", "创建时间"})
   private Date createTime;

   @ExcelProperty({"课程信息表", "更新时间"})
   private Date updateTime;

   @ExcelProperty({"课程信息表", "课程学分"})
   private int credit;

}
