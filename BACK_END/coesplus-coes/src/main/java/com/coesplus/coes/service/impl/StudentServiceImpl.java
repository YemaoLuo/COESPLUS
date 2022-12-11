package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.StudentMapper;
import com.coesplus.coes.service.StudentService;
import com.coesplus.common.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
}
