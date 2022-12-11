package com.coesplus.coes.interceptor;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.coes.service.StudentService;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class StudentLoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private StudentService studentService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //允许从URL中获取，方便测试
        String token = StringUtils.isNotEmpty(request.getParameter("token")) ? request.getParameter("token") : request.getHeader("token");
        log.info("token: " + token);
        if (token == null) {
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().println(JSONUtil.toJsonStr(Result.error(403, "您没有权限！请重新登录！")));
                return false;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        //方便测试使用
        //TODO
        if (token.equals("coesplusgpa4.0")) {
            return true;
        }
        String userId = redisTemplate.opsForValue().get(RedisPrefix.accountStuToken + token);
        if (StringUtils.isEmpty(userId)) {
            //redirect登陆页面
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().println(JSONUtil.toJsonStr(Result.error(403, "您没有权限！请重新登录！")));
                return false;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        } else {
            LambdaQueryWrapper<Student> adminQueryWrapper = new LambdaQueryWrapper<>();
            adminQueryWrapper.eq(Student::getId, userId);
            Student currentStudent = studentService.getOne(adminQueryWrapper);
            log.info(currentStudent.toString());
            BaseContext.setValue("currentStudent", currentStudent);
            //重置token时间
            redisTemplate.opsForValue().set(RedisPrefix.accountStuToken + token, currentStudent.getId(), 30, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(RedisPrefix.accountStuID + currentStudent.getId(), token, 30, TimeUnit.MINUTES);
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //销毁threadLocal
        BaseContext.removeValue("currentStudent");
        log.info("销毁ThreadLocal完成");
    }
}