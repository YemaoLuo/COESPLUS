package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.utils.Result;

public interface AdministratorService extends IService<Administrator> {

    Result add(Administrator administratorVo);
}
