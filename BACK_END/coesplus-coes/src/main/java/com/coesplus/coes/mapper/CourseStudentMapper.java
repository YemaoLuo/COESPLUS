package com.coesplus.coes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.coes.vo.DashboardGradeVo;
import com.coesplus.common.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {

    int getCreditInSemester(@Param("studentId") String studentId, @Param("courseId") String courseId);

    List<DashboardGradeVo> dashBoardGrade(@Param("id") String id);

    List<DashboardGradeVo> dashBoardGradeTeacher(@Param("id") String id);
}
