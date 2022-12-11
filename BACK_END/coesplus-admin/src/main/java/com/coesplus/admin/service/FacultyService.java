package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.utils.Result;

import java.util.Map;

public interface FacultyService extends IService<Faculty> {
    Map<String, Map> getIDNameMap();

    void redoDelete(String id);

    Result delete(String id, Boolean force);
}
