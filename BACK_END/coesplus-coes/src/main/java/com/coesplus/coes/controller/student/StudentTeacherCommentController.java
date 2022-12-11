package com.coesplus.coes.controller.student;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.coes.dto.TeacherCommentDto;
import com.coesplus.coes.service.*;
import com.coesplus.coes.vo.TeacherCommentVo;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student/teacherComment")
@Slf4j
public class StudentTeacherCommentController {

    @Resource
    private TeacherCommentService teacherCommentService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private StudentService studentService;
    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private CourseService courseService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/11/25 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                       @RequestParam(value = "teacherName", required = false) String teacherName) {
        try {
            Page page = new Page(currentPage, pageSize);
            List<Teacher> teacherList = new ArrayList<>();
            //查出teacherId
            if (StringUtils.isNotBlank(teacherName)) {
                LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<Teacher>();
                teacherQueryWrapper.like(StringUtils.isNotBlank(teacherName), Teacher::getName, teacherName)
                        .select(Teacher::getId, Teacher::getName);
                teacherList = teacherService.list(teacherQueryWrapper);
                if (teacherList.size() == 0) {
                    return Result.ok(page);
                }
            }
            //编辑分页查询条件
            LambdaQueryWrapper<TeacherComment> commentQueryWrapper = new LambdaQueryWrapper<TeacherComment>();
            commentQueryWrapper.in(teacherList.size() != 0, TeacherComment::getTeacherId, teacherList.stream().map(Teacher::getId).collect(Collectors.toList()))
                    .orderByDesc(TeacherComment::getUpdateTime);
            page = teacherCommentService.page(page, commentQueryWrapper);
            List<TeacherComment> records = page.getRecords();
            List<TeacherCommentVo> recordsVo = new ArrayList<>();
            for (TeacherComment record : records) {
                TeacherCommentVo vo = new TeacherCommentVo();
                BeanUtils.copyProperties(record, vo);
                vo.setTeacherName(teacherService.getById(record.getTeacherId()).getName());
                String studentName = studentService.getById(record.getStudentId()).getName();
                if (studentName.length() > 1 && studentName.length() < 3) {
                    studentName = studentName.substring(0, 1) + "*";
                }else if (studentName.length() >= 3) {
                    studentName = studentName.substring(0, 1) + "*" + studentName.substring(studentName.length() - 1, studentName.length());
                }
                vo.setStudentName(studentName);
                recordsVo.add(vo);
            }
            log.info(currentPage + "::" + pageSize + "::" + recordsVo);
            page.setRecords(recordsVo);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询自己发表的评论
     *
     * @author LuoYemao
     * @since 2022/11/25 21:01
     */
    @GetMapping("/self")
    public Result pageSelf(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取当前登录学生信息失败！");
            }
            Page page = new Page(currentPage, pageSize);
            //编辑分页查询条件
            LambdaQueryWrapper<TeacherComment> commentQueryWrapper = new LambdaQueryWrapper<TeacherComment>();
            commentQueryWrapper.eq(TeacherComment::getStudentId, currentStudent.getId())
                    .orderByDesc(TeacherComment::getUpdateTime);
            page = teacherCommentService.page(page, commentQueryWrapper);
            List<TeacherComment> records = page.getRecords();
            List<TeacherCommentVo> recordsVo = new ArrayList<>();
            for (TeacherComment record : records) {
                TeacherCommentVo vo = new TeacherCommentVo();
                BeanUtils.copyProperties(record, vo);
                vo.setTeacherName(teacherService.getById(record.getTeacherId()).getName());
                vo.setStudentName(currentStudent.getName());
                recordsVo.add(vo);
            }
            log.info(currentPage + "::" + pageSize + "::" + recordsVo);
            page.setRecords(recordsVo);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除自己的评论
     *
     * @author LuoYemao
     * @since 2022/11/25 23:22
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String commentId) {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取当前登录学生信息失败！");
            }
            TeacherComment commentById = teacherCommentService.getById(commentId);
            if (ObjectUtils.isEmpty(commentById)) {
                return Result.error("评论不存在！");
            }
            if (!commentById.getStudentId().equals(currentStudent.getId())) {
                return Result.error("不可删除非他人的评论！");
            }
            teacherCommentService.removeById(commentId);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增
     *
     * @author LuoYemao
     * @since 2022/11/25 23:22
     */
    @PostMapping()
    public Result add(@RequestBody TeacherCommentDto teacherCommentDto) {
        try {
            if (teacherCommentDto.getComment().length() == 0 || teacherCommentDto.getComment().length() >= 50) {
                return Result.error("评论内容不合法！");
            }
            //只有学生上过课才可评价老师
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("无法获取当前登录学生！");
            }else {
                teacherCommentDto.setStudentName(currentStudent.getName());
                LambdaQueryWrapper<CourseStudent> courseStudentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                courseStudentLambdaQueryWrapper.eq(CourseStudent::getStudentId, currentStudent.getId())
                                .select(CourseStudent::getCourseId);
                LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
                teacherQueryWrapper.eq(Teacher::getName, teacherCommentDto.getTeacherName())
                        .select(Teacher::getId);
                List<String> teacherIdList = teacherService.list(teacherQueryWrapper).stream().map(Teacher::getId).collect(Collectors.toList());
                List<String> courseIdList = courseStudentService.list(courseStudentLambdaQueryWrapper).stream().map(CourseStudent::getCourseId).collect(Collectors.toList());
                if (teacherIdList.size() == 0 || courseIdList.size() == 0) {
                    return Result.error("老师不存在！");
                }
                LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();
                courseQueryWrapper.in(Course::getId, courseIdList)
                        .in(Course::getTeacherId, teacherIdList);
                long count = courseService.count(courseQueryWrapper);
                if (count == 0) {
                    return Result.error("您没有上过该老师课程，无法评论！");
                }
            }
            //获取student teacher id
            LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<Teacher>();
            teacherQueryWrapper.eq(Teacher::getName, teacherCommentDto.getTeacherName());
            Teacher teacherByName = teacherService.getOne(teacherQueryWrapper);
            if (ObjectUtils.isEmpty(teacherByName)) {
                return Result.error("老师不存在！");
            }
            String teacherId = teacherByName.getId();
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<Student>();
            studentQueryWrapper.eq(Student::getName, teacherCommentDto.getStudentName());
            Student studentByName = studentService.getOne(studentQueryWrapper);
            if (ObjectUtils.isEmpty(studentByName)) {
                return Result.error("学生不存在！");
            }
            String studentId = studentByName.getId();
            TeacherComment comment = new TeacherComment();
            comment.setComment(teacherCommentDto.getComment())
                    .setTeacherId(teacherId)
                    .setStudentId(studentId);
            teacherCommentService.save(comment);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
