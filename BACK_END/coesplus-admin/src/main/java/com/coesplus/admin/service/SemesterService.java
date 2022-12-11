package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.Semester;
import com.coesplus.common.utils.Result;

public interface SemesterService extends IService<Semester> {

    Result startSemester(String id);

    Result endSemester(String id);
}
