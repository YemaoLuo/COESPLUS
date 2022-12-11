package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.CourseMapper;
import com.coesplus.admin.service.CourseService;
import com.coesplus.admin.service.CourseStudentService;
import com.coesplus.admin.service.SemesterCourseService;
import com.coesplus.admin.service.SemesterService;
import com.coesplus.common.entity.Course;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.entity.Semester;
import com.coesplus.common.entity.SemesterCourse;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private SemesterCourseService semesterCourseService;
    @Resource
    private SemesterService semesterService;


    @Override
    public Result delete(String id, Boolean force) {
        //验证是否已关联student
        LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
        courseStudentQueryWrapper.eq(CourseStudent::getCourseId, id);
        List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentQueryWrapper);
        //是否强制更新即删除同时删除学生
        if (courseStudentList.size() > 0) {
            if (force) {
                courseStudentList.forEach(student -> student.setIsDeleted(1));
                courseStudentService.updateBatchById(courseStudentList);
            } else {
                return Result.error("当前课程仍存在学生！无法禁用！");
            }
        }
        Course courseById = this.getById(id);
        courseById.setIsDeleted(1);
        this.updateById(courseById);
        log.info(courseById.toString());
        return Result.ok();
    }

    @Override
    public void redoDelete(String id) {
        Course courseById = this.getById(id);
        courseById.setIsDeleted(0);
        LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
        courseStudentQueryWrapper.eq(CourseStudent::getCourseId, id);
        List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentQueryWrapper);
        courseStudentList.forEach(courseStudent -> courseStudent.setIsDeleted(0));
        courseStudentService.updateBatchById(courseStudentList);
        this.updateById(courseById);
        log.info(courseById.toString());
    }

    @Override
    public Result realDelete(String id, Boolean force) {
        //校验是否有正在开启的抢课
        LambdaQueryWrapper<SemesterCourse> scQueryWrapper = new LambdaQueryWrapper<>();
        scQueryWrapper.eq(SemesterCourse::getCourseId, id);
        List<SemesterCourse> scList = semesterCourseService.list(scQueryWrapper);
        if (scList.size() > 0) {
            LambdaQueryWrapper<Semester> semesterQueryWrapper = new LambdaQueryWrapper<>();
            semesterQueryWrapper.in(Semester::getId, scList.stream().map(SemesterCourse::getSemesterId).collect(Collectors.toList()))
                    .eq(Semester::getIsDeleted, 0);
            long count = semesterService.count(semesterQueryWrapper);
            if (count > 0) {
                return Result.error("当前课程在正在抢课的学期中，无法删除！");
            }
        }
        //删除所有对应course_student
        LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
        courseStudentQueryWrapper.eq(CourseStudent::getCourseId, id);
        List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentQueryWrapper);
        if (courseStudentList.size() > 0) {
            if (force) {
                courseStudentService.remove(courseStudentQueryWrapper);
            } else {
                return Result.error("当前课程仍存在学生！无法删除！");
            }
        }
        this.removeById(id);
        return Result.ok();
    }
}
