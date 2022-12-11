package com.coesplus.coes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.coes.vo.DashboardGradeVo;
import com.coesplus.common.entity.CourseStudent;

import java.util.List;

public interface CourseStudentService extends IService<CourseStudent> {
    int getCreditInSemester(String id, String id1);

    List<DashboardGradeVo> dashBoardGrade(String id);

    List<DashboardGradeVo> dashBoardGradeTeacher(String id);
}
