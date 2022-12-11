package com.coesplus.admin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.URL;
import java.util.Date;

@Data
@Accessors(chain = true)
@ContentRowHeight(70)
public class TeacherExcelVo {

    @ExcelProperty({"教师信息表", "主键ID"})
    private String id;

    @ExcelProperty({"教师信息表", "姓名"})
    private String name;

    @ExcelProperty({"教师信息表", "教师ID"})
    private String teacherId;

    @ExcelProperty({"教师信息表", "性别"})
    private String sex;

    @ExcelProperty({"教师信息表", "电话"})
    private String telephone;

    @ExcelProperty({"教师信息表", "邮件"})
    private String email;

    @ExcelProperty({"教师信息表", "学院"})
    private String facultyName;

    @ExcelProperty({"教师信息表", "入学年份"})
    private String joinYear;

    @ExcelProperty({"教师信息表", "状态"})
    private String isDeleted;

    @ExcelProperty({"教师信息表", "创建时间"})
    private Date createTime;

    @ExcelProperty({"教师信息表", "更新时间"})
    private Date updateTime;

    @ExcelProperty(value = {"教师信息表", "头像"})
    private URL photo;
}
