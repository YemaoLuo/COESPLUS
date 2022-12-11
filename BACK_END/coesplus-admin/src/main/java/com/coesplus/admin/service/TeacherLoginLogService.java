package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.TeacherLoginLog;

import java.util.List;

public interface TeacherLoginLogService extends IService<TeacherLoginLog> {
    List<LoginRankVo> loginRank();
}
