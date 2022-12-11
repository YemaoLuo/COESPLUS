package com.coesplus.admin.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.dto.TeacherDto;
import com.coesplus.admin.mapper.TeacherMapper;
import com.coesplus.admin.service.CourseService;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.admin.service.UserActivateService;
import com.coesplus.common.entity.Course;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.entity.UserActivate;
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
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private UserActivateService userActivateService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private CourseService courseService;
    @Resource
    private TeacherMapper teacherMapper;


    @Override
    public Result add(TeacherDto teacherDto) {
        //studentId必须存在并校验唯一
        if (StringUtils.isEmpty(teacherDto.getTeacherId()) || StringUtils.isEmpty(teacherDto.getName())) {
            return Result.error("老师ID或姓名不可为空！");
        }
        LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
        teacherQueryWrapper.or()
                .eq(Teacher::getTeacherId, teacherDto.getTeacherId())
                .eq(StringUtils.isNotEmpty(teacherDto.getEmail()), Teacher::getEmail, teacherDto.getEmail())
                .eq(Teacher::getName, teacherDto.getName());
        long count = this.count(teacherQueryWrapper);
        if (count != 0) {
            return Result.error("老师ID或Email或姓名已存在！");
        }
        //Vo -> Entity
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherDto, teacher);
        //字典翻译
        if (teacherDto.getSex().equals("男")) {
            teacher.setSex("1");
        } else {
            teacher.setSex("0");
        }
        String password = IdUtil.simpleUUID().substring(0, 10);
        teacher.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        this.save(teacher);
        log.info(teacher.toString());
        //发送密码通知邮件
        String msg = teacher.getName() + "老师！您COES的初始密码为：" + password + "。请尽快重设密码！";
        ThreadUtil.execAsync(() -> {
            mailSendLogService.sendComplexMessage(teacher.getEmail(), "COES-Plus账户创建", msg);
        });
        return Result.ok();
    }

    @Override
    public Result physicalDelete(String id, boolean force) {
        //校验是否绑定课程
        LambdaQueryWrapper<Course> courseQueryWrapper = new LambdaQueryWrapper<>();
        courseQueryWrapper.eq(Course::getTeacherId, id);
        long count = courseService.count(courseQueryWrapper);
        if (count != 0 && !force) {
            return Result.error("当前老师有正在进行中的课程，不可删除！");
        }
        this.removeById(id);
        //删除user_activate信息
        LambdaQueryWrapper<UserActivate> userActivateQueryWrapper = new LambdaQueryWrapper<>();
        userActivateQueryWrapper.eq(UserActivate::getUserId, id);
        userActivateService.remove(userActivateQueryWrapper);
        return Result.ok();
    }

    @Override
    public Map<String, Map> getFaultyCount() {
        return teacherMapper.getFaultyCount();
    }
}
