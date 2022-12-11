package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.UserActivateMapper;
import com.coesplus.admin.service.UserActivateService;
import com.coesplus.common.entity.UserActivate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserActivateServiceImpl extends ServiceImpl<UserActivateMapper, UserActivate> implements UserActivateService {
}
