package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.SemesterCreditMapper;
import com.coesplus.admin.service.SemesterCreditService;
import com.coesplus.common.entity.SemesterCredit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SemesterCreditServiceImpl extends ServiceImpl<SemesterCreditMapper, SemesterCredit> implements SemesterCreditService {
}
