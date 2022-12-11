package com.coesplus.admin.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.dto.CourseDto;
import com.coesplus.admin.dto.CourseStudentDto;
import com.coesplus.admin.excel.CourseExcelVo;
import com.coesplus.admin.excel.StudentExcelVo;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.CourseVo;
import com.coesplus.admin.vo.StudentVo;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private StudentService studentService;
    @Resource
    private MinioUtils minioUtils;
    @Resource
    private MailSendLogService mailSendLogService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/10/5 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                       @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "courseId", required = false) String courseId, @RequestParam(value = "facultyName", required = false) String facultyName
            , @RequestParam(value = "isDeleted", required = false) String isDeleted, @RequestParam(value = "teacherName", required = false) String teacherName
    ) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();

            //条件选择：所给查询字段非空时，模糊匹配
            courseQueryWrapper.like(StringUtils.isNotBlank(name), Course::getName, name)
                    .like(StringUtils.isNotBlank(courseId), Course::getCourseId, courseId)
                    .eq(StringUtils.isNotBlank(isDeleted), Course::getIsDeleted, isDeleted)
                    .orderBy(true, false, Course::getUpdateTime);
            //.like(StringUtils.isNotBlank(teacherName), Course::getTeacherId, teacherName)

            //模糊匹配教师名,再转为对象列->得到相应的教师id
            if (StringUtils.isNotBlank(teacherName)) {
                LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
                teacherQueryWrapper.like(Teacher::getName, teacherName);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
                //流方法把对象转为string
                List<String> teacherIdList = teacherList.stream().map(Teacher::getId).collect(Collectors.toList());
                //筛选器判断非空，in看teacherId是否属于选择的id表
                courseQueryWrapper.in(teacherIdList.size() != 0, Course::getTeacherId, teacherIdList);
            }

            //模糊匹配学院名,再转为对象列->得到相应的学院id
            if (StringUtils.isNotBlank(facultyName)) {
                LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
                facultyQueryWrapper.like(Faculty::getName, facultyName);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
                //流方法把对象转为string
                List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
                //筛选器判断非空，in看FacultyId是否属于选择的id表
                courseQueryWrapper.in(facultyIdList.size() != 0, Course::getFacultyId, facultyIdList);
            }

            page = courseService.page(page, courseQueryWrapper);
            List<Course> courseList = page.getRecords();
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
                if (StringUtils.isNotEmpty(course.getTeacherId())) {
                    vo.setTeacherName(teacherService.getById(course.getTeacherId()).getName());
                } else {
                    vo.setTeacherName("");
                }
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
     * 导出Excel
     */
    @GetMapping("/excel")
    public void excel(HttpServletResponse response,
                      @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "courseId", required = false) String courseId, @RequestParam(value = "facultyName", required = false) String facultyName
            , @RequestParam(value = "isDeleted", required = false) String isDeleted, @RequestParam(value = "teacherName", required = false) String teacherName
    ) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            //获取数据
            LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();

            //条件选择：所给查询字段非空时，模糊匹配
            courseQueryWrapper.like(StringUtils.isNotBlank(name), Course::getName, name)
                    .like(StringUtils.isNotBlank(courseId), Course::getCourseId, courseId)
                    .eq(StringUtils.isNotBlank(isDeleted), Course::getIsDeleted, isDeleted)
                    .orderBy(true, false, Course::getUpdateTime);

            //模糊匹配教师名,再转为对象列->得到相应的教师id
            if (StringUtils.isNotBlank(teacherName)) {
                LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
                teacherQueryWrapper.like(Teacher::getName, teacherName);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
                //流方法把对象转为string
                List<String> teacherIdList = teacherList.stream().map(Teacher::getId).collect(Collectors.toList());
                //筛选器判断非空，in看teacherId是否属于选择的id表
                courseQueryWrapper.in(teacherIdList.size() != 0, Course::getTeacherId, teacherIdList);
            }

            //模糊匹配学院名,再转为对象列->得到相应的学院id
            if (StringUtils.isNotBlank(facultyName)) {
                LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
                facultyQueryWrapper.like(Faculty::getName, facultyName);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
                //流方法把对象转为string
                List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
                //筛选器判断非空，in看FacultyId是否属于选择的id表
                courseQueryWrapper.in(facultyIdList.size() != 0, Course::getFacultyId, facultyIdList);
            }

            List<Course> courseList = courseService.list(courseQueryWrapper);
            List<CourseExcelVo> courseVoList = new ArrayList<>();

            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            if (courseList.size() == 0) {
                return;
            }
            courseList.forEach(course -> {
                CourseExcelVo vo = new CourseExcelVo();
                BeanUtils.copyProperties(course, vo);
                String facultyName1;
                try {
                    facultyName1 = (String) facultyMap.get(course.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName1 = "";
                }
                vo.setFacultyName(facultyName1);
                if (course.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }

                courseVoList.add(vo);
            });
            //发送密码邮件
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus导出课程信息表通知", "您的账号于" + DateTime.now() + "导出课程信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentAdmin.getName());
                mailSendLogService.sendComplexMessage(currentAdmin.getEmail(), "COES-Plus导出课程信息表通知", content);
            });
            ExcelUtils.export(response, CourseExcelVo.class, courseVoList, "课程信息表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 导出学生Excel
     */
    @GetMapping("/excel/student")
    public void excelStudent(HttpServletResponse response, @RequestParam(value = "courseId") String courseId
            , @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            //获取数据
            LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentLambdaQueryWrapper.eq(StringUtils.isNotBlank(courseId), CourseStudent::getCourseId, courseId)
                    .eq(StringUtils.isNotBlank(isDeleted), CourseStudent::getIsDeleted, isDeleted);

            List<String> studentIdList = courseStudentService.list(courseStudentLambdaQueryWrapper).stream().map(CourseStudent::getStudentId).collect(Collectors.toList());

            //获取数据
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            //条件选择：所给查询字段非空时，模糊匹配

            studentQueryWrapper.in(Student::getId, studentIdList)
                    .orderBy(true, false, Student::getUpdateTime);

            List<Student> studentList = studentService.list(studentQueryWrapper);
            List<StudentExcelVo> studentVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            if (studentList.size() == 0) {
                return;
            }
            studentList.forEach(student -> {
                StudentExcelVo vo = new StudentExcelVo();
                BeanUtils.copyProperties(student, vo);
                String facultyName;
                try {
                    facultyName = (String) facultyMap.get(student.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
                if (student.getSex().equals("1")) {
                    vo.setSex("男");
                } else {
                    vo.setSex("女");
                }
                if (student.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }
                //转换图像名为url
                try {
                    vo.setPhoto(new URL(minioUtils.preview(student.getPhoto())));
                } catch (Exception e) {
                    try {
                        vo.setPhoto(new URL(minioUtils.preview("e45cc91940ff9621469b324074b40499.png")));
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                studentVoList.add(vo);
            });
            //发送密码邮件
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus导出课程信息表", "您的账号于" + DateTime.now() + "导出课程信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentAdmin.getName());
                mailSendLogService.sendComplexMessage(currentAdmin.getEmail(), "COES-Plus导出课程信息表", content);
            });
            ExcelUtils.export(response, StudentExcelVo.class, studentVoList, "学生信息表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 虚拟删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable("id") String id, @RequestParam(value = "force", defaultValue = "false") Boolean force) {
        try {
            return courseService.delete(id, force);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @PutMapping(value = "/{id}")
    public Result redoDelete(@PathVariable("id") String id) {
        try {
            courseService.redoDelete(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 物理删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @DeleteMapping(value = "/physical/{id}")
    public Result physicalDelete(@PathVariable("id") String id, @RequestParam(value = "force", defaultValue = "false") Boolean force) {
        try {
            log.info(id + "::" + force);
            return courseService.realDelete(id, force);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新
     *
     * @author LuoYemao
     * @since 2022/10/6 22:31
     */
    @PatchMapping()
    public Result update(@RequestBody CourseDto courseDto) {
        try {
            Course courseById = courseService.getById(courseDto.getId());
            courseDto.setStartTime(courseDto.getStartTime() == null ? courseById.getStartTime() : courseDto.getStartTime());
            courseDto.setEndTime(courseDto.getEndTime() == null ? courseById.getEndTime() : courseDto.getEndTime());
            //开始时间早于结束时间
            if (courseDto.getStartTime().after(courseDto.getEndTime())) {
                return Result.error("开始结束时间不合法！");
            }
            //Vo -> Entity
            Course course = new Course();
            BeanUtils.copyProperties(courseDto, course);
            course.setDay(courseDto.getDay());
            courseService.updateById(course);
            log.info(course.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增Course
     *
     * @author LuoYemao
     * @since 2022/10/17 23:22
     */
    @PostMapping()
    public Result add(@RequestBody CourseDto courseDto) {
        try {
            //courseID必须唯一
            if (StringUtils.isEmpty(courseDto.getCourseId())) {
                return Result.error("课程ID不可为空！");
            }
            //开始时间早于结束时间
            if (courseDto.getStartTime().after(courseDto.getEndTime())) {
                return Result.error("开始结束时间不合法！");
            }
            LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();
            courseQueryWrapper.eq(Course::getCourseId, courseDto.getCourseId());
            long count = courseService.count(courseQueryWrapper);
            if (count != 0) {
                return Result.error("课程ID已存在！");
            }
            //Vo -> Entity
            Course course = new Course();
            BeanUtils.copyProperties(courseDto, course);
            course.setDay(courseDto.getDay());
            courseService.save(course);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * Courseid查学生
     */
    @GetMapping("/student")
    public Result courseStudent(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                                @RequestParam("courseId") String courseId) {
        try {
            Page page = new Page(currentPage, pageSize);
            //导出学生id的list
            LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            courseStudentLambdaQueryWrapper.eq(CourseStudent::getCourseId, courseId)
                    .eq(CourseStudent::getIsDeleted, 0);
            List<CourseStudent> courseStudentList = courseStudentService.list(courseStudentLambdaQueryWrapper);
            List<String> courseStudentIdList = courseStudentList.stream().map(CourseStudent::getStudentId).collect(Collectors.toList());
            //选择出学生
            if (courseStudentIdList.size() == 0) {
                return Result.ok(page);
            }
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.in(Student::getId, courseStudentIdList);
            page = studentService.page(page, studentQueryWrapper);
            List<Student> studentList = page.getRecords();
            List<StudentVo> studentVoList = new ArrayList<>();

            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();

            //拼装VO
            studentList.forEach(student -> {
                StudentVo vo = new StudentVo();
                BeanUtils.copyProperties(student, vo);
                String id = courseStudentService.getKid(student.getId(), courseId);
                vo.setId(id);
                String facultyName = "";
                try {
                    facultyName = (String) facultyMap.get(student.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
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
            page.setRecords(studentVoList);

            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增课程学生
     */
    @PostMapping("/student")
    public Result addCourseStudent(@RequestBody CourseStudentDto courseStudentDto) {
        try {
            return courseStudentService.add(courseStudentDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }


    /**
     * 物理删除
     */
    @DeleteMapping(value = "/student/physical/{id}")
    public Result physicalDelete(@PathVariable("id") String id) {
        try {
            log.info(id);
            courseStudentService.physicalDelete(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}