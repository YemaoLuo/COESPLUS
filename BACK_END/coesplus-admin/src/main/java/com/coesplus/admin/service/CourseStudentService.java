package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.dto.CourseStudentDto;
import com.coesplus.common.entity.CourseStudent;
import com.coesplus.common.utils.Result;

public interface CourseStudentService extends IService<CourseStudent> {
    Result add(CourseStudentDto courseStudentDto);

    void physicalDelete(String id);

    String getKid(String id, String id1);
}
