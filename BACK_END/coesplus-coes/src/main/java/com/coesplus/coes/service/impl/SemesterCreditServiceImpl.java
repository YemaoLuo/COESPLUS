package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.SemesterCreditMapper;
import com.coesplus.coes.service.SemesterCreditService;
import com.coesplus.common.entity.SemesterCredit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SemesterCreditServiceImpl extends ServiceImpl<SemesterCreditMapper, SemesterCredit> implements SemesterCreditService {
}
