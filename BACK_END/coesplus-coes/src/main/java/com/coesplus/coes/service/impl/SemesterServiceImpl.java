package com.coesplus.coes.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.dto.AddCourseDto;
import com.coesplus.coes.mapper.SemesterMapper;
import com.coesplus.coes.service.CourseService;
import com.coesplus.coes.service.CourseStudentService;
import com.coesplus.coes.service.SemesterCreditService;
import com.coesplus.coes.service.SemesterService;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.entity.Semester;
import com.coesplus.common.entity.SemesterCredit;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class SemesterServiceImpl extends ServiceImpl<SemesterMapper, Semester> implements SemesterService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CourseService courseService;
    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private SemesterCreditService semesterCreditService;


    @Override
    public Semester valid(Student currentStudent) {
        DateTime currentTime = DateUtil.date();
        LambdaQueryWrapper<Semester> semesterQueryWrapper = new LambdaQueryWrapper<>();
        semesterQueryWrapper.eq(Semester::getJoinYear, currentStudent.getJoinYear())
                .eq(Semester::getIsDeleted, 0)
                .lt(Semester::getStartTime, currentTime)
                .gt(Semester::getEndTime, currentTime);
        Semester semester = this.getOne(semesterQueryWrapper);
        if (!ObjectUtils.isEmpty(semester)) {
            // 存在可以继续选课
            return semester;
        }
        return null;
    }

    // #选课#
    // 获取信号量
    // 检查学分限制
    // 写选课信息进表
    // 处理异常回滚
    @Override
    public Result add(Semester semester, Student currentStudent, AddCourseDto dto) {
        RLock lock = redissonClient.getLock(RedisPrefix.addLock + currentStudent.getId());
        RSemaphore semaphore = redissonClient.getSemaphore(RedisPrefix.seatRemain + semester.getId() + dto.getId());
        boolean isLock = false;
        try {
            isLock = lock.tryLock(2L, 10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        try {
            if (isLock) {
                boolean acquire = semaphore.tryAcquire();
                if (acquire) {
                    // 校验是否已选
                    LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
                    courseStudentQueryWrapper.eq(CourseStudent::getCourseId, dto.getId())
                            .eq(CourseStudent::getStudentId, currentStudent.getId());
                    long count = courseStudentService.count(courseStudentQueryWrapper);
                    if (count > 0) {
                        semaphore.release();
                        return Result.error("不可重复选课！");
                    }
                    // 校验学分限制
                    LambdaQueryWrapper<SemesterCredit> semesterCreditQueryWrapper = new LambdaQueryWrapper<>();
                    semesterCreditQueryWrapper.eq(SemesterCredit::getSemesterId, semester.getId())
                            .eq(SemesterCredit::getFacultyId, currentStudent.getFacultyId())
                            .select(SemesterCredit::getCreditLimit);
                    SemesterCredit creditLimit = semesterCreditService.getOne(semesterCreditQueryWrapper);
                    int credit = courseStudentService.getCreditInSemester(currentStudent.getId(), semester.getId());
                    int addCredit = courseService.getById(dto.getId()).getCredit() + credit;
                    if (addCredit > creditLimit.getCreditLimit()) {
                        semaphore.release();
                        return Result.error("不可选课！可选学分不足！");
                    }
                    CourseStudent courseStudent = new CourseStudent();
                    courseStudent.setCourseId(dto.getId())
                            .setStudentId(currentStudent.getId());
                    courseStudentService.save(courseStudent);
                    return Result.ok();
                } else {
                    return Result.error("选课失败！课程剩余座位可能不足！");
                }
            }else {
                return Result.error();
            }
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            semaphore.release();
            throw e;
        }finally {
            lock.unlock();
        }
    }

    // #退课#
    // 释放信号量
    // 删选课信息
    // 处理异常回滚
    @Override
    public Result drop(Semester semester, Student currentStudent, AddCourseDto dto) {
        RLock lock = redissonClient.getLock(RedisPrefix.dropLock + currentStudent.getId());
        RSemaphore semaphore = redissonClient.getSemaphore(RedisPrefix.seatRemain + semester.getId() + dto.getId());
        boolean isLock = false;
        try {
            isLock = lock.tryLock(2L, 5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        try {
            if (isLock) {
                //查出是否courseStudent库存在数据
                LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
                courseStudentQueryWrapper.eq(CourseStudent::getCourseId, dto.getId())
                        .eq(CourseStudent::getStudentId, currentStudent.getId());
                long count = courseStudentService.count(courseStudentQueryWrapper);
                if (count == 0) {
                    return Result.error("未选择该课程！不可取消选课！");
                }
                //删除数据
                boolean remove = courseStudentService.remove(courseStudentQueryWrapper);
                //释放型号量
                if (remove) {
                    semaphore.release();
                }
                return Result.ok();
            }else {
                return Result.error();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }finally {
            lock.unlock();
        }
    }
}
