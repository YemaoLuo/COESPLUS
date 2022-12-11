package com.coesplus.admin.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.NacosDetailVo;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.entity.UserActivate;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system")
@Slf4j
public class SystemController {

    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private UserActivateService userActivateService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private AdministratorService administratorService;

    @Resource
    private DiscoveryClient discoveryClient;


    /**
     * 发送激活邮件
     *
     * @author LuoYemao
     * @since 2022/10/31 21:26
     */
    @GetMapping("/activate/{id}")
    public Result sendActivationEmail(@RequestParam("role") String role, @PathVariable("id") String id, HttpServletRequest request) {
        try {
            String url = request.getHeader("Origin");
            String baseUrl = url + "/login#/ModifyPassword?";
            LambdaQueryWrapper<UserActivate> userActivateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userActivateLambdaQueryWrapper.eq(UserActivate::getUserId, id)
                    .eq(UserActivate::getIsDeleted, 0);
            UserActivate userActivate = userActivateService.getOne(userActivateLambdaQueryWrapper);
            String token;
            if (ObjectUtils.isEmpty(userActivate)) {
                //添加激活信息
                UserActivate userActivate2 = new UserActivate();
                userActivate2.setUserId(id)
                        .setVerifyToken(IdUtil.simpleUUID())
                        .setRole("student");
                userActivateService.save(userActivate2);
                token = userActivate2.getVerifyToken();
            } else {
                token = userActivate.getVerifyToken();
            }
            baseUrl += "verifyToken=" + token;
            log.info(role + "::" + id + "::" + baseUrl);
            String email;
            String msg;
            //查出email
            if (role.equals("student")) {
                LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
                studentQueryWrapper.eq(Student::getId, id)
                        .select(Student::getName, Student::getEmail);
                Student student = studentService.getOne(studentQueryWrapper);
                email = student.getEmail();
                msg = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"></head><body>" + student.getName()
                        + "同学！请尽快重置您的密码！链接为：<a href=\"" + baseUrl + "\">" + baseUrl + "</a></body></html>";
            } else if (role.equals("teacher")) {
                LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
                teacherQueryWrapper.eq(Teacher::getId, id)
                        .select(Teacher::getName, Teacher::getEmail);
                Teacher teacher = teacherService.getOne(teacherQueryWrapper);
                email = teacher.getEmail();
                msg = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"></head><body>" + teacher.getName()
                        + "老师！请尽快重置您的密码！链接为：<a href=\"" + baseUrl + "\">" + baseUrl + "</a></body></html>";
            } else {
                LambdaQueryWrapper<Administrator> adminQueryWrapper = new LambdaQueryWrapper<>();
                adminQueryWrapper.eq(Administrator::getId, id)
                        .select(Administrator::getName, Administrator::getEmail);
                Administrator admin = administratorService.getOne(adminQueryWrapper);
                email = admin.getEmail();
                msg = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"></head><body>" + admin.getName()
                        + "管理员！请尽快重置您的密码！链接为：<a href=\"" + baseUrl + "\">" + baseUrl + "</a></body></html>";
            }
            ThreadUtil.execAsync(() -> {
                mailSendLogService.sendComplexMessage(email, "COES-Plus重置密码", msg);
            });
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取微服务状态
     *
     * @author LuoYemao
     * @since 2022/12/3 14:33
     */
    @GetMapping("/nacos")
    public Result nacosStatus() {
        try {
            Map<String, List<NacosDetailVo>> map = new HashMap<>();
            // 获取注册nacos上面的所有微服务名称
            List<String> list = discoveryClient.getServices();
            // 获取注册nacos上面的所有微服务实例
            for (String s : list) {
                List<ServiceInstance> list2 = discoveryClient.getInstances(s);
                List<NacosDetailVo> voList = new ArrayList<>();
                for (ServiceInstance service : list2) {
                    NacosDetailVo vo = new NacosDetailVo();
                    vo.setServiceId(service.getServiceId())
                            .setUrl(service.getUri().toString())
                            .setWeight(service.getMetadata().get("nacos.weight"))
                            .setStatus(service.getMetadata().get("nacos.healthy"));
                    voList.add(vo);
                }
                map.put(s, voList);
                log.info(map.toString());
            }
            return Result.ok(map);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
