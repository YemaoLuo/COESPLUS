package com.coesplus.coes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.Faculty;

import java.util.Map;

public interface FacultyService extends IService<Faculty> {
    Map<String, Map> getIDNameMap();
}
