package com.coesplus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.dto.TeacherCommentDto;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.service.TeacherCommentService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.admin.vo.TeacherCommentVo;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.entity.TeacherComment;
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
@RequestMapping("/teacherComment")
@Slf4j
public class TeacherCommentController {

    @Resource
    private TeacherCommentService teacherCommentService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private StudentService studentService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/11/24 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                       @RequestParam(value = "comment", required = false) String comment, @RequestParam(value = "studentName", required = false) String studentName,
                       @RequestParam(value = "teacherName", required = false) String teacherName) {
        try {
            Page page = new Page(currentPage, pageSize);
            List<Teacher> teacherList = new ArrayList<>();
            List<Student> studentList = new ArrayList<>();
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
            //查出studentId
            if (StringUtils.isNotBlank(studentName)) {
                LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<Student>();
                studentQueryWrapper.like(StringUtils.isNotBlank(studentName), Student::getName, studentName)
                        .select(Student::getId, Student::getName);
                studentList = studentService.list(studentQueryWrapper);
                if (studentList.size() == 0) {
                    return Result.ok(page);
                }
            }
            //编辑分页查询条件
            LambdaQueryWrapper<TeacherComment> commentQueryWrapper = new LambdaQueryWrapper<TeacherComment>();
            commentQueryWrapper.in(teacherList.size() != 0, TeacherComment::getTeacherId, teacherList.stream().map(Teacher::getId).collect(Collectors.toList()))
            .in(studentList.size() != 0, TeacherComment::getStudentId, studentList.stream().map(Student::getId).collect(Collectors.toList()))
            .like(StringUtils.isNotEmpty(comment), TeacherComment::getComment, comment)
                    .orderByDesc(TeacherComment::getUpdateTime);
            page = teacherCommentService.page(page, commentQueryWrapper);
            List<TeacherComment> records = page.getRecords();
            List<TeacherCommentVo> recordsVo = new ArrayList<>();
            for (TeacherComment record : records) {
                TeacherCommentVo vo = new TeacherCommentVo();
                BeanUtils.copyProperties(record, vo);
                vo.setTeacherName(teacherService.getById(record.getTeacherId()).getName());
                vo.setStudentName(studentService.getById(record.getStudentId()).getName());
                recordsVo.add(vo);
            }
            log.info(currentPage + "::" + pageSize + "::" + recordsVo);
            page.setRecords(recordsVo);
            return Result.ok(page);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增
     *
     * @author LuoYemao
     * @since 2022/11/24 23:22
     */
    @PostMapping()
    public Result add(@RequestBody TeacherCommentDto teacherCommentDto) {
        try {
            if (teacherCommentDto.getComment().length() == 0 || teacherCommentDto.getComment().length() >= 50) {
                return Result.error("评论内容不合法！");
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

    /**
     * 物理删除
     *
     * @author LuoYemao
     * @since 2022/11/24 17:46
     */
    @DeleteMapping(value = "/physical/{id}")
    public Result physicalDelete(@PathVariable("id") String id) {
        try {
            log.info(id);
            teacherCommentService.removeById(id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
