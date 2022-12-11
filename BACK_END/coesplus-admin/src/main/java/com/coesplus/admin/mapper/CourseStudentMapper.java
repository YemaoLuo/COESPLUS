package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.common.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {

    String getKId(@Param("studentId") String studentId, @Param("courseId") String courseId);
}
