package com.coesplus.coes.controller.student;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.coes.dto.AddCourseDto;
import com.coesplus.coes.service.*;
import com.coesplus.coes.vo.SemesterCourseVo;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student/semester")
@Slf4j
public class StudentSemesterController {

    @Resource
    private SemesterService semesterService;
    @Resource
    private SemesterCourseService semesterCourseService;
    @Resource
    private SemesterCreditService semesterCreditService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private CourseService courseService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private CourseStudentService courseStudentService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 检测是否存在可选课程
     *
     * @Author: Luo Yemao
     * @Date: 2022/11/29
     */
    @GetMapping("/valid")
    public Result valid() {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取登陆学生信息失败！");

            }
            if (StringUtils.isEmpty(currentStudent.getJoinYear())) {
                return Result.error("缺少入学年份信息！不可参与选课！");
            }
            if (StringUtils.isEmpty(currentStudent.getFacultyId())) {
                return Result.error("缺少归属学院！不可参与选课！");
            }
            Semester semester = semesterService.valid(currentStudent);
            if (!ObjectUtils.isEmpty(semester)) {
                // 存在可以继续选课
                return Result.ok(semester.getId());
            }
            // 查找可能存在的最近的选课
            DateTime currentTime = DateUtil.date();
            LambdaQueryWrapper<Semester> semesterQueryWrapper2 = new LambdaQueryWrapper<>();
            semesterQueryWrapper2.eq(Semester::getJoinYear, currentStudent.getJoinYear())
                    .eq(Semester::getIsDeleted, 0)
                    .gt(Semester::getStartTime, currentTime)
                    .orderByAsc(Semester::getStartTime);
            Semester semester2 = semesterService.getOne(semesterQueryWrapper2);
            if (!ObjectUtils.isEmpty(semester2)) {
                long betweenSeconds = DateUtil.between(currentTime, semester2.getStartTime(), DateUnit.SECOND);
                long tenDay2Seconds = 10 * 24 * 60 * 60;
                if (betweenSeconds > tenDay2Seconds) {
                    return Result.error("查询不到即将开启的选课时段！");
                }
                String formatTime = DateUtil.secondToTime(Math.toIntExact(betweenSeconds));
                String[] splitTime = formatTime.split(":");
                Map<String, String> result = new HashMap<>();
                if (Integer.parseInt(splitTime[0]) >= 24) {
                    String day = String.valueOf(Integer.parseInt(splitTime[0]) / 24);
                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    result.put("day", day);
                } else {
                    result.put("day", "00");
                }
                String hour = String.valueOf(Integer.parseInt(splitTime[0]) - Integer.parseInt(result.get("day")) * 24);
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                result.put("hour", hour);
                result.put("minute", splitTime[1]);
                result.put("second", splitTime[2]);
                return Result.error(500, result);
            }
            return Result.error("查询不到即将开启的选课时段！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询可选课程
     *
     * @author LuoYemao
     * @since 2022/11/29 21:22
     */
    @GetMapping()
    public Result pageCourse(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取登陆学生信息失败！");

            }
            if (StringUtils.isEmpty(currentStudent.getJoinYear())) {
                return Result.error("缺少入学年份信息！不可参与选课！");
            }
            if (StringUtils.isEmpty(currentStudent.getFacultyId())) {
                return Result.error("缺少归属学院！不可参与选课！");
            }
            Semester currentSemester = semesterService.valid(currentStudent);
            if (!ObjectUtils.isEmpty(currentSemester)) {
                Page page = new Page(currentPage, pageSize);
                // 查出semesterCourse
                LambdaQueryWrapper<SemesterCourse> semesterCourseQueryWrapper = new LambdaQueryWrapper<>();
                semesterCourseQueryWrapper.eq(SemesterCourse::getSemesterId, currentSemester.getId())
                        .and(wrapper -> wrapper.eq(SemesterCourse::getFacultyId, currentStudent.getFacultyId())
                                .or().isNull(SemesterCourse::getFacultyId));
                page = semesterCourseService.page(page, semesterCourseQueryWrapper);
                List<SemesterCourse> courseList = page.getRecords();
                if (courseList.size() == 0) {
                    return Result.ok(page);
                }
                List<SemesterCourseVo> voList = new ArrayList<>();
                Map<String, Map> facultyMap = facultyService.getIDNameMap();
                for (SemesterCourse course : courseList) {
                    SemesterCourseVo vo = new SemesterCourseVo();
                    Map map = facultyMap.get(course.getFacultyId());
                    if (ObjectUtils.isEmpty(map)) {
                        vo.setFacultyName("");
                    } else {
                        vo.setFacultyName((String) map.get("name"));
                    }
                    Course courseById = courseService.getById(course.getCourseId());
                    BeanUtils.copyProperties(courseById, vo);
                    vo.setTeacherName(teacherService.getById(courseById.getTeacherId()).getName());
                    String remainSeat = redisTemplate.opsForValue().get(RedisPrefix.seatRemain + currentSemester.getId() + course.getCourseId());
                    vo.setAvailableSeat(StringUtils.isEmpty(remainSeat) ? 0 : Integer.parseInt(remainSeat));
                    LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
                    courseStudentQueryWrapper.eq(CourseStudent::getCourseId, course.getCourseId())
                            .eq(CourseStudent::getIsDeleted, 0);
                    long count = courseStudentService.count(courseStudentQueryWrapper);
                    if (count > 0) {
                        vo.setIsChosen(1);
                    } else {
                        vo.setIsChosen(0);
                    }
                    vo.setCourseName(courseById.getName());
                    voList.add(vo);
                }
                page.setRecords(voList);
                return Result.ok(page);
            } else {
                return Result.error("当前没有正在进行中的抢课！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 加选，退选
     *
     * @author LuoYemao
     * @since 2022/11/29 21:22
     */
    @PostMapping("/course")
    public Result course(@RequestBody AddCourseDto dto) {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取登陆学生信息失败！");

            }
            if (StringUtils.isEmpty(currentStudent.getJoinYear())) {
                return Result.error("缺少入学年份信息！不可参与选课！");
            }
            if (StringUtils.isEmpty(currentStudent.getFacultyId())) {
                return Result.error("缺少归属学院！不可参与选课！");
            }
            Semester semester = semesterService.valid(currentStudent);
            log.info(currentStudent + "::" + semester + "::" + dto);
            if (dto.getState() == 0) {
                // 查询是否已经选择
                LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
                courseStudentQueryWrapper.eq(CourseStudent::getCourseId, dto.getId())
                        .eq(CourseStudent::getStudentId, currentStudent.getStudentId());
                long count = courseStudentService.count(courseStudentQueryWrapper);
                if (count != 0) {
                    return Result.error("不可重复选课！");
                }
                // 加选
                return semesterService.add(semester, currentStudent, dto);
            } else {
                // 退选
                return semesterService.drop(semester, currentStudent, dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取已选学分
     *
     * @author LuoYemao
     * @since 2022/11/29 21:22
     */
    @GetMapping("/credit")
    public Result getCredit() {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取登陆学生信息失败！");

            }
            if (StringUtils.isEmpty(currentStudent.getJoinYear())) {
                return Result.error("缺少入学年份信息！不可参与选课！");
            }
            if (StringUtils.isEmpty(currentStudent.getFacultyId())) {
                return Result.error("缺少归属学院！不可参与选课！");
            }
            Semester semester = semesterService.valid(currentStudent);
            int chosenCredit = courseStudentService.getCreditInSemester(currentStudent.getId(), semester.getId());
            LambdaQueryWrapper<SemesterCredit> creditQueryWrapper = new LambdaQueryWrapper<>();
            creditQueryWrapper.eq(SemesterCredit::getSemesterId, semester.getId())
                    .eq(SemesterCredit::getFacultyId, currentStudent.getFacultyId())
                    .select(SemesterCredit::getCreditLimit);
            int creditLimit = semesterCreditService.getOne(creditQueryWrapper).getCreditLimit();
            Map<String, Integer> resultMap = new HashMap<>();
            resultMap.put("chosenCredit", chosenCredit);
            resultMap.put("creditLimit", creditLimit);
            return Result.ok(resultMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
