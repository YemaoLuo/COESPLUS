package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.SemesterMapper;
import com.coesplus.admin.service.*;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SemesterServiceImpl extends ServiceImpl<SemesterMapper, Semester> implements SemesterService {

    @Resource
    private SemesterCreditService semesterCreditService;
    @Resource
    private SemesterCourseService semesterCourseService;
    @Resource
    private CourseService courseService;
    @Resource
    private FacultyService facultyService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    // ## 开始抢课 ##
    // 检查所有学院的credit限制已经设置
    // 检查同时开启的semester中没有重复的join_year
    // 查询出所有符合要求的课程并把他们的座位数-已选数存入redis
    // redis需使用事务提交
    // 把数据库中semester的isDeleted改为0
    @Override
    public Result startSemester(String id) {
        try {
            // 获取当前id的semester
            Semester semesterById = this.getById(id);
            if (semesterById.getIsDeleted() == 0) {
                return Result.error("请勿重复开启学期抢课！");
            }
            // 是否已经结束
            if (semesterById.getEndTime().before(new Date())) {
                return Result.error("不能开始已结束的学期抢课！");
            }
            // 检查所有学院的credit限制已经设置
            LambdaQueryWrapper<SemesterCredit> creditQueryWrapper = new LambdaQueryWrapper<>();
            creditQueryWrapper.eq(SemesterCredit::getSemesterId, id)
                    .eq(SemesterCredit::getIsDeleted, 0);
            long creditCount = semesterCreditService.count(creditQueryWrapper);
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
            facultyQueryWrapper.eq(Faculty::getIsDeleted, 0);
            long facultyCount = facultyService.count(facultyQueryWrapper);
            if (creditCount < facultyCount) {
                return Result.error("学分限制未设置完全！请设置完全后再次开启抢课！");
            }
            // 检查同时开启的semester中没有重复的join_year
            LambdaQueryWrapper<Semester> semesterQueryWrapper = new LambdaQueryWrapper<>();
            semesterQueryWrapper.eq(Semester::getIsDeleted, 0)
                    .eq(Semester::getJoinYear, semesterById.getJoinYear());
            long semesterCount = this.count(semesterQueryWrapper);
            if (semesterCount != 0) {
                return Result.error("入学年份限制已存在！请重新设置！");
            }
            // 查询出所有符合要求的课程并把他们的座位数-已选数存入redis
            LambdaQueryWrapper<SemesterCourse> semesterCourseQueryWrapper = new LambdaQueryWrapper<>();
            semesterCourseQueryWrapper.eq(SemesterCourse::getSemesterId, id)
                    .select(SemesterCourse::getCourseId);
            List<String> courseIdList = semesterCourseService.list(semesterCourseQueryWrapper).stream().map(SemesterCourse::getCourseId).collect(Collectors.toList());
            if (courseIdList.size() == 0) {
                return Result.error("当前学期未设置可选课程！");
            }
            LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();
            courseQueryWrapper.in(Course::getId, courseIdList)
                    .select(Course::getSeat, Course::getSeatChosen, Course::getId);
            List<Course> courseList = courseService.list(courseQueryWrapper);
            for (Course course : courseList) {
                int seatRemain = Math.max(course.getSeat() - course.getSeatChosen(), 0);
                course.setSeat(seatRemain);
            }
            log.info(courseList.toString());
            // redis需使用事务提交
            // 开启事务
            redisTemplate.setEnableTransactionSupport(true);
            for (Course course : courseList) {
                redisTemplate.opsForValue().set(RedisPrefix.seatRemain + semesterById.getId() + course.getId(), String.valueOf(course.getSeat()));
                log.info(RedisPrefix.seatRemain + semesterById.getId() + course.getId() + "::" + course.getSeat());
            }
            // 把数据库中semester的isDeleted改为0
            semesterById.setIsDeleted(0);
            this.updateById(semesterById);
            return Result.ok();
        } catch (Exception e) {
            // 回滚事务
            redisTemplate.discard();
            log.error(e.getMessage());
            throw e;
        }
    }

    // ## 结束抢课 ##
    // 查询出所有符合要求的课程ID
    // 从课程中获取剩余座位数 更新Course表剩余座位数
    // 清空redis中的信号量
    // redis需使用事务提交
    // 把数据库中semester的isDeleted改为1
    @Override
    public Result endSemester(String id) {
        try {
            // 获取当前id的semester
            Semester semesterById = this.getById(id);
            if (semesterById.getIsDeleted() == 1) {
                return Result.error("请勿重复关闭学期抢课！");
            }
            // 查询出所有符合要求的课程并把他们的座位数-已选数存入redis
            LambdaQueryWrapper<SemesterCourse> semesterCourseQueryWrapper = new LambdaQueryWrapper<>();
            semesterCourseQueryWrapper.eq(SemesterCourse::getSemesterId, id)
                    .select(SemesterCourse::getCourseId);
            List<String> courseIdList = semesterCourseService.list(semesterCourseQueryWrapper).stream().map(SemesterCourse::getCourseId).collect(Collectors.toList());
            // redis需使用事务提交
            // 开启事务
            redisTemplate.setEnableTransactionSupport(true);
            for (String courseId : courseIdList) {
                String remainStr = redisTemplate.opsForValue().get(RedisPrefix.seatRemain + semesterById.getId() + courseId);
                int remain = Integer.parseInt(StringUtils.isEmpty(remainStr) ? "0" : remainStr);
                Course courseById = courseService.getById(courseId);
                if (remain == 0) {
                    courseById.setSeatChosen(courseById.getSeat());
                } else {
                    courseById.setSeatChosen(courseById.getSeat() - remain);
                }
                courseService.updateById(courseById);
                //删除信号量
                redisTemplate.delete(RedisPrefix.seatRemain + semesterById.getId() + courseId);
                log.info(RedisPrefix.seatRemain + semesterById.getId() + courseId);
            }
            semesterById.setIsDeleted(1);
            this.updateById(semesterById);
            return Result.ok();
        } catch (Exception e) {
            // 回滚事务
            redisTemplate.discard();
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
