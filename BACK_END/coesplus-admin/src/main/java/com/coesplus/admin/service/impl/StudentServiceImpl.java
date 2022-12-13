package com.coesplus.admin.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.dto.StudentDto;
import com.coesplus.admin.mapper.StudentMapper;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.service.UserActivateService;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.UserActivate;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private UserActivateService userActivateService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private StudentMapper studentMapper;


    @Override
    public Result add(StudentDto studentDto) {
        //studentId必须存在并校验唯一
        if (StringUtils.isEmpty(studentDto.getStudentId()) || StringUtils.isEmpty(studentDto.getName())) {
            return Result.error("学生ID或姓名不可为空！");
        }
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getStudentId, studentDto.getStudentId())
                .or().eq(StringUtils.isNotEmpty(studentDto.getEmail()), Student::getEmail, studentDto.getEmail())
                .or().eq(Student::getName, studentDto.getName());
        long count = this.count(studentQueryWrapper);
        if (count != 0) {
            return Result.error("学生ID或Email或姓名已存在！");
        }
        //Vo -> Entity
        Student student = new Student();
        BeanUtils.copyProperties(studentDto, student);
        String password = IdUtil.simpleUUID().substring(0, 10);
        student.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        this.save(student);
        log.info(student.toString());
        //发送密码通知邮件
        ThreadUtil.execAsync(() -> {
            String content = MailContentUtil.build("COES-Plus账户创建", "您COES的初始密码为：" + password + "。请尽快重设密码！", student.getName() + "同学");
            mailSendLogService.sendComplexMessage(student.getEmail(), "COES-Plus账户创建", content);
        });
        return Result.ok();
    }

    @Override
    public void physicalDelete(String id) {
        this.removeById(id);
        //删除user_activate信息
        LambdaQueryWrapper<UserActivate> userActivateQueryWrapper = new LambdaQueryWrapper<>();
        userActivateQueryWrapper.eq(UserActivate::getUserId, id);
        userActivateService.remove(userActivateQueryWrapper);
    }

    @Override
    public Map<String, Map> getFaultyCount() {
        return studentMapper.getFaultyCount();
    }
}
