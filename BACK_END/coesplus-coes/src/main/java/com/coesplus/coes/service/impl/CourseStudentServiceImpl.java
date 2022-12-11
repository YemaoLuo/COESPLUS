package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.CourseStudentMapper;
import com.coesplus.coes.service.CourseStudentService;
import com.coesplus.coes.vo.DashboardGradeVo;
import com.coesplus.common.entity.CourseStudent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@Transactional
public class CourseStudentServiceImpl extends ServiceImpl<CourseStudentMapper, CourseStudent> implements CourseStudentService {

    @Resource
    private CourseStudentMapper courseStudentMapper;


    @Override
    public int getCreditInSemester(String studentId, String courseId) {
        return courseStudentMapper.getCreditInSemester(studentId, courseId);
    }

    @Override
    public List<DashboardGradeVo> dashBoardGrade(String id) {
        return courseStudentMapper.dashBoardGrade(id);
    }

    @Override
    public List<DashboardGradeVo> dashBoardGradeTeacher(String id) {
        return courseStudentMapper.dashBoardGradeTeacher(id);
    }
}
