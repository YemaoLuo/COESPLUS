package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.AdministratorLoginLog;

import java.util.List;

public interface AdministratorLoginLogService extends IService<AdministratorLoginLog> {
    List<LoginRankVo> loginRank();
}
