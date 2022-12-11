package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.TeacherMapper;
import com.coesplus.coes.service.TeacherService;
import com.coesplus.common.entity.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
