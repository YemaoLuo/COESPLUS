package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.dto.StudentDto;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.Result;

import java.util.Map;

public interface StudentService extends IService<Student> {
    Result add(StudentDto studentDto);

    void physicalDelete(String id);

    Map<String, Map> getFaultyCount();
}
