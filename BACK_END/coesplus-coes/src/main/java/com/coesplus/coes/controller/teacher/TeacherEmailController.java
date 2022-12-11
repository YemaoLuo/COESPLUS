package com.coesplus.coes.controller.teacher;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.coes.service.CourseStudentService;
import com.coesplus.coes.service.MailSendLogService;
import com.coesplus.coes.service.StudentService;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/mail")
@Slf4j
public class TeacherEmailController {

    @Resource
    private CourseStudentService courseStudentService;
    @Resource
    private StudentService studentService;
    @Resource
    private MailSendLogService mailSendLogService;


    /**
     * 给特定班级的同学发送邮件通知
     *
     * @author LuoYemao
     * @since 2022/12/4 12:22
     */
    @GetMapping("/class")
    public Result mailClass(@RequestParam("id") String id, @RequestParam("message") String message) {
        try {
            Teacher currentTeacher = (Teacher) BaseContext.getValue("currentTeacher");
            if (ObjectUtils.isEmpty(currentTeacher)) {
                return Result.error("获取当前老师信息失败！");
            }
            LambdaQueryWrapper<CourseStudent> cssQueryWrapper = new LambdaQueryWrapper<>();
            cssQueryWrapper.eq(CourseStudent::getCourseId, id)
                    .select(CourseStudent::getStudentId);
            List<String> stuIds = courseStudentService.list(cssQueryWrapper).stream().map(CourseStudent::getStudentId).collect(Collectors.toList());
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.in(Student::getId, stuIds)
                    .eq(Student::getIsDeleted, 0)
                    .isNotNull(Student::getEmail)
                    .ne(Student::getEmail, "")
                    .select(Student::getEmail);
            List<String> emailList = studentService.list(studentQueryWrapper).stream().map(Student::getEmail).collect(Collectors.toList());
            ThreadUtil.execAsync(() -> {
                int fail = 0;
                for (String email : emailList) {
                    try {
                        String content = MailContentUtil.build("COES-Plus通知", message, "同学");
                        mailSendLogService.sendComplexMessage(email, "COES-Plus通知", content);
                    }catch (Exception e) {
                        fail += 1;
                    }
                }
                if (StringUtils.isNotEmpty(currentTeacher.getEmail())) {
                    String message2Teacher = currentTeacher.getName() + "您发送的通知发送完成。"
                            + "班级学生" + stuIds.size() + "人。应发送" + emailList.size() + "人。成功发送"
                             + (emailList.size() - fail) + "人！发送内容如下：" + message;
                    String content = MailContentUtil.build("COES-Plus通知", message2Teacher, currentTeacher.getName() + "老师");
                    mailSendLogService.sendComplexMessage(currentTeacher.getEmail(), "COES-Plus通知", content);
                }
            });
            return Result.ok();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
