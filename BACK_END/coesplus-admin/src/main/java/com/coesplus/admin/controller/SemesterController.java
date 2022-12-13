package com.coesplus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.SemesterCourseVo;
import com.coesplus.admin.vo.SemesterCreditVo;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/semester")
@Slf4j
public class SemesterController {

    @Resource
    private SemesterService semesterService;
    @Resource
    private SemesterCourseService semesterCourseService;
    @Resource
    private SemesterCreditService semesterCreditService;
    @Resource
    private CourseService courseService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private TeacherService teacherService;


    /**
     * 分页查询学期
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @GetMapping()
    public Result pageSemester(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                               @RequestParam(value = "semester", required = false) String semester,
                               @RequestParam(value = "joinYear", required = false) String joinYear, @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Semester> semesterQueryWrapper = new LambdaQueryWrapper();
            semesterQueryWrapper.eq(StringUtils.isNotEmpty(isDeleted), Semester::getIsDeleted, isDeleted)
                    .like(StringUtils.isNotEmpty(semester), Semester::getSemester, semester)
                    .like(StringUtils.isNotEmpty(joinYear), Semester::getJoinYear, joinYear)
                    .orderByDesc(Semester::getUpdateTime);
            page = semesterService.page(page, semesterQueryWrapper);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增学期
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @PostMapping()
    public Result addSemester(@RequestBody Semester semester) {
        try {
            //数据校验
            if (StringUtils.isEmpty(semester.getSemester())) {
                return Result.error("缺少必要参数学期编号！");
            }
            if (ObjectUtils.isEmpty(semester.getStartTime()) || ObjectUtils.isEmpty(semester.getEndTime())) {
                return Result.error("缺少必要参数开始或结束时间！");
            }
            if (semester.getStartTime().after(semester.getEndTime())) {
                return Result.error("选取开始时间和结束时间不合法！");
            }
            // 开始时默认关闭抢课
            semester.setIsDeleted(1);
            // 存入数据库
            semesterService.save(semester);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除学期
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @DeleteMapping("/{id}")
    public Result deleteSemester(@PathVariable("id") String id) {
        try {
            // 校验是否正在开启
            Semester semester = semesterService.getById(id);
            if (semester.getIsDeleted() == 0) {
                return Result.error("学期抢课开启中！无法删除！");
            }
            semesterService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询学期课程
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @GetMapping("/course")
    public Result pageSemesterCourse(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                                     @RequestParam(value = "semesterId") String semesterId) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<SemesterCourse> semesterCourseQueryWrapper = new LambdaQueryWrapper();
            semesterCourseQueryWrapper.eq(SemesterCourse::getSemesterId, semesterId);
            page = semesterCourseService.page(page, semesterCourseQueryWrapper);
            List<SemesterCourse> semesterCourseList = page.getRecords();
            List<SemesterCourseVo> voList = new ArrayList<>();
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            for (SemesterCourse semesterCourse : semesterCourseList) {
                SemesterCourseVo vo = new SemesterCourseVo();
                BeanUtils.copyProperties(semesterCourse, vo);
                //查出courseName, facultyName, teacherName
                Course courseById = courseService.getById(semesterCourse.getCourseId());
                vo.setCourseName(courseById.getName());
                Teacher teacherById = teacherService.getById(courseById.getTeacherId());
                String teacherName = ObjectUtils.isEmpty(teacherById) ? "" : teacherById.getName();
                vo.setTeacherName(teacherName);
                BeanUtils.copyProperties(courseById, vo);
                vo.setId(semesterCourse.getId());
                if (StringUtils.isNotEmpty(semesterCourse.getFacultyId())) {
                    vo.setFacultyName(facultyMap.get(semesterCourse.getFacultyId()).get("name").toString());
                } else {
                    vo.setFacultyName("");
                }
                voList.add(vo);
            }
            page.setRecords(voList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增学期课程
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @PostMapping("/course")
    public Result addSemesterCourse(@RequestBody SemesterCourse semesterCourse) {
        try {
            //数据校验
            if (StringUtils.isEmpty(semesterCourse.getSemesterId())) {
                return Result.error("缺少必要参数学期编号！");
            }
            if (StringUtils.isEmpty(semesterCourse.getCourseId())) {
                return Result.error("缺少必要参数课程ID！");
            }
            //校验数据是否已存在
            LambdaQueryWrapper<SemesterCourse> semesterCourseQueryWrapper = new LambdaQueryWrapper<>();
            semesterCourseQueryWrapper.eq(SemesterCourse::getSemesterId, semesterCourse.getSemesterId())
                    .eq(SemesterCourse::getCourseId, semesterCourse.getCourseId());
            long count = semesterCourseService.count(semesterCourseQueryWrapper);
            //查询是否存在学期
            Semester semester = semesterService.getById(semesterCourse.getSemesterId());
            if (ObjectUtils.isEmpty(semester)) {
                return Result.error("学期不存在！");
            }
            if (count > 0) {
                return Result.error("课程已存在，不可重复添加！");
            }
            //存入数据库
            semesterCourseService.save(semesterCourse);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改学期课程状态
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49

    @PatchMapping("/course")
    public Result semesterCourseStatus(@RequestParam("state") Integer state, @RequestParam("id") String id) {
        try {
            SemesterCourse byId = semesterCourseService.getById(id);
            byId.setIsDeleted(state);
            semesterCourseService.updateById(byId);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
     */

    /**
     * 删除学期课程
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @DeleteMapping("/course/{id}")
    public Result deleteSemesterCourse(@PathVariable("id") String id) {
        try {
            semesterCourseService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询学分限制
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @GetMapping("/credit")
    public Result pageSemesterCredit(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                                     @RequestParam(value = "semesterId") String semesterId) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<SemesterCredit> semesterCreditQueryWrapper = new LambdaQueryWrapper();
            semesterCreditQueryWrapper.eq(SemesterCredit::getSemesterId, semesterId)
                    .orderByDesc(SemesterCredit::getUpdateTime);
            page = semesterCreditService.page(page, semesterCreditQueryWrapper);
            List<SemesterCredit> records = page.getRecords();
            List<SemesterCreditVo> voList = new ArrayList();
            for (SemesterCredit record : records) {
                SemesterCreditVo vo = new SemesterCreditVo();
                BeanUtils.copyProperties(record, vo);
                String facultyName = facultyService.getById(record.getFacultyId()).getName();
                vo.setFacultyName(facultyName);
                voList.add(vo);
            }
            page.setRecords(voList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增学分限制
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @PostMapping("/credit")
    public Result addSemesterCredit(@RequestBody SemesterCredit semesterCredit) {
        try {
            //校验参数
            if (StringUtils.isEmpty(semesterCredit.getSemesterId())) {
                return Result.error("学期ID不可为空！");
            }
            if (StringUtils.isEmpty(semesterCredit.getFacultyId())) {
                return Result.error("学院ID不可为空！");
            }
            //校验是否重复
            LambdaQueryWrapper<SemesterCredit> semesterCourseQueryWrapper = new LambdaQueryWrapper<>();
            semesterCourseQueryWrapper.eq(SemesterCredit::getSemesterId, semesterCredit.getSemesterId())
                    .eq(SemesterCredit::getFacultyId, semesterCredit.getFacultyId());
            long count = semesterCreditService.count(semesterCourseQueryWrapper);
            if (count > 0) {
                return Result.error("已存在学分限制！不可重复设置！");
            }
            semesterCreditService.save(semesterCredit);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除学分限制
     *
     * @author LuoYemao
     * @since 2022/11/27 21:49
     */
    @DeleteMapping("/credit/{id}")
    public Result deleteSemesterCredit(@PathVariable("id") String id) {
        try {
            semesterCreditService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 切换选课状态
     *
     * @author LuoYemao
     * @since 2022/11/28 23:09
     */
    @GetMapping("/{id}")
    public Result switchSemesterState(@PathVariable("id") String id, @RequestParam("state") Integer state) {
        try {
            if (state == 0) {
                //开始抢课
                return semesterService.startSemester(id);
            }else {
                //结束抢课
                return semesterService.endSemester(id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
