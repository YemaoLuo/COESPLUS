package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.FacultyMapper;
import com.coesplus.coes.service.FacultyService;
import com.coesplus.common.entity.Faculty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
public class FacultyServiceImpl extends ServiceImpl<FacultyMapper, Faculty> implements FacultyService {


    @Override
    public Map<String, Map> getIDNameMap() {
        return this.getBaseMapper().getIDNameMap();
    }
}
