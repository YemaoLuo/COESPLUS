package com.coesplus.coes.controller.account;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.coes.dto.AccountPasswordDto;
import com.coesplus.coes.service.StudentService;
import com.coesplus.coes.service.TeacherService;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class AccountController {

    @Resource
    private TeacherService teacherService;

    @Resource
    private StudentService studentService;


    /**
     * 学生自己改密码
     *
     * @author LuoYemao
     * @since 2022/12/1 19:35
     */
    @PatchMapping("/student/account/password")
    public Result studentChangePassword(@RequestBody AccountPasswordDto dto) {
        try {
            if (DigestUtils.md5DigestAsHex(dto.getPrePassword().getBytes()).equals(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()))) {
                return Result.error("新密码旧密码不可相同！");
            }
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取当前登录学生失败！");
            }
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.eq(Student::getId, currentStudent.getId())
                    .eq(Student::getPassword, DigestUtils.md5DigestAsHex(dto.getPrePassword().getBytes()))
                    .select(Student::getId, Student::getPassword);
            Student studentEntity = studentService.getOne(studentQueryWrapper);
            if (ObjectUtils.isEmpty(studentEntity)) {
                return Result.error("旧密码错误！");
            }
            studentEntity.setPassword(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()));
            studentService.updateById(studentEntity);
            return Result.ok();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 老师自己改密码
     *
     * @author LuoYemao
     * @since 2022/12/1 19:35
     */
    @PatchMapping("/teacher/account/password")
    public Result teacherChangePassword(@RequestBody AccountPasswordDto dto) {
        try {
            if (DigestUtils.md5DigestAsHex(dto.getPrePassword().getBytes()).equals(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()))) {
                return Result.error("新密码旧密码不可相同！");
            }
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            if (ObjectUtils.isEmpty(currentTeacher)) {
                return Result.error("获取当前登录老师失败！");
            }
            LambdaQueryWrapper<Teacher> TeacherQueryWrapper = new LambdaQueryWrapper<>();
            TeacherQueryWrapper.eq(Teacher::getId, currentTeacher.getId())
                    .eq(Teacher::getPassword, DigestUtils.md5DigestAsHex(dto.getPrePassword().getBytes()))
                    .select(Teacher::getId, Teacher::getPassword);
            Teacher TeacherEntity = teacherService.getOne(TeacherQueryWrapper);
            if (ObjectUtils.isEmpty(TeacherEntity)) {
                return Result.error("旧密码错误！");
            }
            TeacherEntity.setPassword(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()));
            teacherService.updateById(TeacherEntity);
            return Result.ok();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
