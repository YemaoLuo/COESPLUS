package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.StudentLoginLogMapper;
import com.coesplus.coes.service.StudentLoginLogService;
import com.coesplus.common.entity.StudentLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class StudentLoginLogServiceImpl extends ServiceImpl<StudentLoginLogMapper, StudentLoginLog> implements StudentLoginLogService {

}
