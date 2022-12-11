package com.coesplus.coes.controller.student;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.coes.excel.StudentCourseExcelVo;
import com.coesplus.coes.service.*;
import com.coesplus.coes.vo.StudentCourseVo;
import com.coesplus.common.entity.Course;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.ExcelUtils;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student/course")
@Slf4j
public class StudentCourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private MailSendLogService mailSendLogService;


    /**
     * 学生分页查询自己选择的课程
     *
     * @author LuoYemao
     * @since 2022/11/13 14:55
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize
            , @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Page page = new Page(currentPage, pageSize);
            //获取userId
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            log.info(currentPage + "::" + pageSize + "::" + currentStudent);
            LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentLambdaQueryWrapper.eq(CourseStudent::getStudentId, currentStudent.getId())
                    .eq(StringUtils.isNotEmpty(isDeleted), CourseStudent::getIsDeleted, isDeleted);
            List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentLambdaQueryWrapper);
            if (courseStudentList.size() == 0) {
                return Result.ok(page);
            }
            Map<String, String> gradeMap = new HashMap<>();
            courseStudentList.forEach(courseStudent -> {
                gradeMap.put(courseStudent.getCourseId(), courseStudent.getGrade());
            });
            List<String> courseIdList = courseStudentList.stream().map(CourseStudent::getCourseId).collect(Collectors.toList());
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.in(Course::getId, courseIdList);
            page = courseService.page(page, courseLambdaQueryWrapper);
            List<Course> courseList = page.getRecords();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            List<StudentCourseVo> courseVoList = new ArrayList<>();
            //拼装VO
            courseList.forEach(course -> {
                StudentCourseVo vo = new StudentCourseVo();
                BeanUtils.copyProperties(course, vo);
                vo.setGrade(gradeMap.get(vo.getId()));
                String facultyName1 = "";
                try {
                    facultyName1 = (String) facultyMap.get(course.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                //获取老师姓名
                if (StringUtils.isNotEmpty(course.getTeacherId())) {
                    vo.setTeacherName(teacherService.getById(course.getTeacherId()).getName());
                } else {
                    vo.setTeacherName("");
                }
                courseVoList.add(vo);
            });
            page.setRecords(courseVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出学生课程Excel
     */
    @GetMapping("/excel")
    public void excel(HttpServletResponse response, @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentLambdaQueryWrapper.eq(CourseStudent::getStudentId, currentStudent.getId())
                    .eq(StringUtils.isNotEmpty(isDeleted), CourseStudent::getIsDeleted, isDeleted);
            List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentLambdaQueryWrapper);
            if (courseStudentList.size() == 0) {
                return;
            }
            Map<String, String> gradeMap = new HashMap<>();
            courseStudentList.forEach(courseStudent -> {
                gradeMap.put(courseStudent.getCourseId(), courseStudent.getGrade());
            });
            List<String> courseIdList = courseStudentList.stream().map(CourseStudent::getCourseId).collect(Collectors.toList());
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.in(Course::getId, courseIdList);
            List<Course> courseList = courseService.list(courseLambdaQueryWrapper);
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            List<StudentCourseExcelVo> courseVoList = new ArrayList<>();
            //拼装VO
            courseList.forEach(course -> {
                StudentCourseExcelVo vo = new StudentCourseExcelVo();
                BeanUtils.copyProperties(course, vo);
                vo.setGrade(gradeMap.get(vo.getId()));
                String facultyName1 = "";
                try {
                    facultyName1 = (String) facultyMap.get(course.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                //获取老师姓名
                if (StringUtils.isNotEmpty(course.getTeacherId())) {
                    vo.setTeacherName(teacherService.getById(course.getTeacherId()).getName());
                } else {
                    vo.setTeacherName("");
                }
                if (course.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }
                courseVoList.add(vo);
            });
            //发送密码邮件
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus学生课程信息表", "您的账号于" + DateTime.now() + "导出教师信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentStudent.getName() + "同学");
                mailSendLogService.sendComplexMessage(currentStudent.getEmail(), "COES-Plus学生课程信息表", content);
            });
            ExcelUtils.export(response, StudentCourseExcelVo.class, courseVoList, "学生课程信息表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
