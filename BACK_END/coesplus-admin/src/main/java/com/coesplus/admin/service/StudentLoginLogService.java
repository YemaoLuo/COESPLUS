package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.StudentLoginLog;

import java.util.List;

public interface StudentLoginLogService extends IService<StudentLoginLog> {
    List<LoginRankVo> loginRank();
}
