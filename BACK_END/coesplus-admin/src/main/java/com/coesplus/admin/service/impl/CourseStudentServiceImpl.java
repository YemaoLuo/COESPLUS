package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.dto.CourseStudentDto;
import com.coesplus.admin.mapper.CourseStudentMapper;
import com.coesplus.admin.service.CourseService;
import com.coesplus.admin.service.CourseStudentService;
import com.coesplus.common.entity.Course;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional
public class CourseStudentServiceImpl extends ServiceImpl<CourseStudentMapper, CourseStudent> implements CourseStudentService {

    @Resource
    private CourseStudentMapper courseStudentMapper;

    @Resource
    private CourseService courseService;


    @Override
    public Result add(CourseStudentDto courseStudentDto) {
        //studentId必须存在并校验唯一
        if (StringUtils.isEmpty(courseStudentDto.getStudentId()) || StringUtils.isEmpty(courseStudentDto.getCourseId())) {
            return Result.error("课程/学生ID不可为空！");
        }
        LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseStudentLambdaQueryWrapper.eq(CourseStudent::getCourseId, courseStudentDto.getCourseId());
        courseStudentLambdaQueryWrapper.eq(CourseStudent::getStudentId, courseStudentDto.getStudentId());
        long count = this.count(courseStudentLambdaQueryWrapper);
        if (count != 0) {
            return Result.error("学生已选该门课！");
        }
        //检查已选学生数
        Course course = courseService.getById(courseStudentDto.getCourseId());
        if (course.getSeatChosen() == course.getSeat()) {
            return Result.error("该门课已无剩余座位！");
        }

        //Vo -> Entity
        CourseStudent courseStudent = new CourseStudent();
        BeanUtils.copyProperties(courseStudentDto, courseStudent);

        this.save(courseStudent);

        //同步更新已选学生
        Course courseById = courseService.getById(courseStudent.getCourseId());
        LambdaQueryWrapper<CourseStudent> csQueryWrapper = new LambdaQueryWrapper<>();
        csQueryWrapper.eq(CourseStudent::getCourseId, courseStudent.getCourseId());
        courseById.setSeatChosen(Math.toIntExact(this.count(csQueryWrapper) + 1));
        courseService.updateById(courseById);
        return Result.ok();
    }

    @Override
    public void physicalDelete(String id) {
        //同步更新已选学生
        Course courseById = courseService.getById(this.getById(id).getCourseId());
        LambdaQueryWrapper<CourseStudent> csQueryWrapper = new LambdaQueryWrapper<>();
        csQueryWrapper.eq(CourseStudent::getCourseId, courseById.getCourseId());
        courseById.setSeatChosen(Math.toIntExact(this.count(csQueryWrapper) - 1));
        courseService.updateById(courseById);
        this.removeById(id);
    }

    @Override
    public String getKid(String studentId, String courseId) {
        return courseStudentMapper.getKId(studentId, courseId);
    }
}
