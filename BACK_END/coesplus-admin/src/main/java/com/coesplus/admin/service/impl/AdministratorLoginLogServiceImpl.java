package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.AdministratorLoginLogMapper;
import com.coesplus.admin.service.AdministratorLoginLogService;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.AdministratorLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@Transactional
public class AdministratorLoginLogServiceImpl extends ServiceImpl<AdministratorLoginLogMapper, AdministratorLoginLog> implements AdministratorLoginLogService {

    @Resource
    private AdministratorLoginLogMapper administratorLoginLogMapper;

    @Override
    public List<LoginRankVo> loginRank() {
        return administratorLoginLogMapper.loginRank();
    }
}
