package com.coesplus.coes.controller.teacher;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.coes.dto.GradeDto;
import com.coesplus.coes.excel.CourseExcelVo;
import com.coesplus.coes.excel.CourseGradeExcelVo;
import com.coesplus.coes.service.*;
import com.coesplus.coes.vo.CourseVo;
import com.coesplus.coes.vo.StudentGradeVo;
import com.coesplus.common.entity.Course;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/course")
@Slf4j
public class TeacherCourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private StudentService studentService;

    @Resource
    private MinioUtils minioUtils;


    /**
     * 教师分页查询自己教授的课程
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize
            , @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Page page = new Page(currentPage, pageSize);
            //获取userId
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            log.info(currentPage + "::" + pageSize + "::" + currentTeacher);
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.eq(Course::getTeacherId, currentTeacher.getId())
                    .eq(StringUtils.isNotEmpty(isDeleted), Course::getIsDeleted, isDeleted);
            List<Course> courseList = courseService.list(courseLambdaQueryWrapper);
            if (courseList.size() == 0) {
                return Result.ok(page);
            }
            List<CourseVo> courseVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            courseList.forEach(course -> {
                CourseVo vo = new CourseVo();
                BeanUtils.copyProperties(course, vo);
                String facultyName1 = "";
                try {
                    facultyName1 = (String) facultyMap.get(course.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                //获取老师姓名
                vo.setTeacherName(currentTeacher.getName());
                courseVoList.add(vo);
            });
            log.info(currentPage + "::" + pageSize + "::" + courseVoList);
            page.setRecords(courseVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 教师分页查询自己班级学生
     */
    @GetMapping("/student")
    public Result pageStudent(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                              @RequestParam("courseId") String courseId) {
        try {
            Page page = new Page(currentPage, pageSize);
            //获取userId
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            if (ObjectUtils.isEmpty(currentTeacher)) {
                return Result.error("无法获取当前登录老师！");
            }
            Course courseById = courseService.getById(courseId);
            if (ObjectUtils.isEmpty(courseById)) {
                return Result.error("courseId有误！");
            }
            if (!courseById.getTeacherId().equals(currentTeacher.getId())) {
                return Result.error("只可查询您教授的班级的学生！");
            }
            LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentQueryWrapper.eq(CourseStudent::getCourseId, courseId)
                    .select(CourseStudent::getStudentId, CourseStudent::getGrade)
                    .orderByDesc(CourseStudent::getUpdateTime);
            List<CourseStudent> stuList = courseStudentService.list(courseStudentQueryWrapper);
            if (stuList.size() == 0) {
                return Result.ok(page);
            }
            Map<String, String> gradeMap = new HashMap<>();
            for (CourseStudent courseStudent : stuList) {
                gradeMap.put(courseStudent.getStudentId(), courseStudent.getGrade());
            }
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.in(Student::getId, stuList.stream().map(CourseStudent::getStudentId).collect(Collectors.toList()))
                    .eq(Student::getIsDeleted, 0);

            page = studentService.page(page, studentQueryWrapper);
            List<Student> studentList = page.getRecords();
            List<StudentGradeVo> studentVoList = new ArrayList<>();

            //拼装VO
            studentList.forEach(student -> {
                StudentGradeVo vo = new StudentGradeVo();
                BeanUtils.copyProperties(student, vo);
                vo.setGrade(gradeMap.getOrDefault(student.getId(), ""));
                if (student.getSex().equals("1")) {
                    vo.setSex("男");
                } else {
                    vo.setSex("女");
                }
                //转换图像名为url
                try {
                    vo.setPhoto(minioUtils.preview(vo.getPhoto()));
                } catch (Exception e) {
                    vo.setPhoto(minioUtils.preview("e45cc91940ff9621469b324074b40499.png"));
                }
                studentVoList.add(vo);
            });
            log.info(currentPage + "::" + pageSize + "::" + studentVoList);
            page.setRecords(studentVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 教师设置学生成绩
     */
    @PatchMapping()
    public Result giveGrade(@RequestBody GradeDto gradeDto) {
        try {
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.eq(Course::getId, gradeDto.getCourseId())
                    .eq(Course::getIsDeleted, 0);
            Course course = courseService.getOne(courseLambdaQueryWrapper);

            if (course.getTeacherId().equals(currentTeacher.getId())) {
                LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                courseStudentLambdaQueryWrapper.eq(CourseStudent::getStudentId, gradeDto.getStudentId())
                        .eq(CourseStudent::getCourseId, gradeDto.getCourseId());
                CourseStudent courseStudent = courseStudentService.getOne(courseStudentLambdaQueryWrapper);
                courseStudent.setGrade(gradeDto.getGrade());
                courseStudentService.updateById(courseStudent);
                log.info(courseStudent.toString());
                return Result.ok();
            } else
                return Result.error("您不是本门课的老师，没有权限！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出教师课程Excel
     */
    @GetMapping("/excel")
    public void excel(HttpServletResponse response, @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseLambdaQueryWrapper.eq(Course::getTeacherId, currentTeacher.getId())
                    .eq(StringUtils.isNotEmpty(isDeleted), Course::getIsDeleted, isDeleted);
            List<Course> courseList = courseService.list(courseLambdaQueryWrapper);
            if (courseList.size() == 0) {
                return;
            }
            List<CourseExcelVo> courseVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            courseList.forEach(course -> {
                CourseExcelVo vo = new CourseExcelVo();
                BeanUtils.copyProperties(course, vo);
                String facultyName1 = "";
                try {
                    facultyName1 = (String) facultyMap.get(course.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                //获取老师姓名
                vo.setTeacherName(currentTeacher.getName());
                //获取状态
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
                String content = MailContentUtil.build("COES-Plus教师课程信息表", "您的账号于" + DateTime.now() + "教师课程信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentTeacher.getName() + "教师");
                mailSendLogService.sendComplexMessage(currentTeacher.getEmail(), "COES-Plus教师课程信息表", content);
            });
            ExcelUtils.export(response, CourseExcelVo.class, courseVoList, "教师课程信息表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 导出课程学生成绩Excel
     */
    @GetMapping("/excel/grade")
    public void excelGrade(HttpServletResponse response, @RequestParam(value = "courseId") String courseId, @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentLambdaQueryWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(StringUtils.isNotEmpty(isDeleted), CourseStudent::getIsDeleted, isDeleted);


            List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentLambdaQueryWrapper);
            if (courseStudentList.size() == 0) {
                return;
            }
            Map<String, String> gradeMap = new HashMap<>();
            //相同课程不同学生，存学生id
            courseStudentList.forEach(courseStudent -> {
                gradeMap.put(courseStudent.getStudentId(), courseStudent.getGrade());
            });

            //当前课程的所有学生
            List<String> studentIdList = courseStudentList.stream().map(CourseStudent::getStudentId).collect(Collectors.toList());
            LambdaQueryWrapper<Student> studentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            studentLambdaQueryWrapper.in(Student::getId, studentIdList);
            List<Student> studentList = studentService.list(studentLambdaQueryWrapper);

            //当前课程
            Course course = courseService.getById(courseId);

            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //
            List<CourseGradeExcelVo> courseGradeVoList = new ArrayList<>();
            //拼装VO
            studentList.forEach(student -> {
                CourseGradeExcelVo vo = new CourseGradeExcelVo();
                BeanUtils.copyProperties(student, vo);
                //获取成绩
                vo.setGrade(gradeMap.get(vo.getId()));
                //获取学生学院
                String facultyName1 = "";
                try {
                    facultyName1 = (String) facultyMap.get(student.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                //获取学生姓名
                vo.setStudentName(student.getName());
                //获取学生id
                vo.setStudentId(student.getStudentId());
                //获取课程id
                vo.setCourseId(course.getCourseId());
                //获取课程名
                vo.setName(course.getName());
                //获取老师姓名
                vo.setTeacherName(currentTeacher.getName());
                //获取状态
                if (student.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }
                courseGradeVoList.add(vo);
            });
            //发送密码邮件
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus学生成绩表", "您的账号于" + DateTime.now() + "学生成绩表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentTeacher.getName() + "教师");
                mailSendLogService.sendComplexMessage(currentTeacher.getEmail(), "COES-Plus学生成绩表", content);
            });
            ExcelUtils.export(response, CourseGradeExcelVo.class, courseGradeVoList, "学生成绩表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
