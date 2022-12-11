package com.coesplus.admin.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ContentRowHeight(70)
public class FacultyExcelVo {

    @ExcelProperty({"学院信息表", "主键ID"})
    private String id;

    @ExcelProperty({"学院信息表", "学院名"})
    private String name;

    @ExcelProperty({"学院信息表", "状态"})
    private String isDeleted;

    @ExcelProperty({"学院信息表", "创建时间"})
    private Date createTime;

    @ExcelProperty({"学院信息表", "更新时间"})
    private Date updateTime;

}
