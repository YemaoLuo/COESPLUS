package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.StudentLoginLogMapper;
import com.coesplus.admin.service.StudentLoginLogService;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.StudentLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@Transactional
public class StudentLoginLogServiceImpl extends ServiceImpl<StudentLoginLogMapper, StudentLoginLog> implements StudentLoginLogService {

    @Resource
    private StudentLoginLogMapper studentLoginLogMapper;

    @Override
    public List<LoginRankVo> loginRank() {
        return studentLoginLogMapper.loginRank();
    }
}
