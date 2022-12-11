package com.coesplus.coes.config;

import com.coesplus.coes.interceptor.StudentLoginInterceptor;
import com.coesplus.coes.interceptor.TeacherLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private TeacherLoginInterceptor teacherLoginInterceptor;
    @Resource
    private StudentLoginInterceptor studentLoginInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(teacherLoginInterceptor)
                .addPathPatterns("/teacher/**")
                .excludePathPatterns("/account/**");

        registry.addInterceptor(studentLoginInterceptor)
                .addPathPatterns("/student/**")
                .excludePathPatterns("/account/**");
    }
}
