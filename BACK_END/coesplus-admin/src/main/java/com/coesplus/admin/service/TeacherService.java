package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.dto.TeacherDto;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.Result;

import java.util.Map;

public interface TeacherService extends IService<Teacher> {
    Result add(TeacherDto teacherVo);

    Result physicalDelete(String id, boolean force);

    Map<String, Map> getFaultyCount();
}
