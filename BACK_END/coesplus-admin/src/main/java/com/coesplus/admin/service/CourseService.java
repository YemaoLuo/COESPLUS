package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.Course;
import com.coesplus.common.utils.Result;

public interface CourseService extends IService<Course> {

    Result delete(String id, Boolean force);

    void redoDelete(String id);

    Result realDelete(String id, Boolean force);
}
