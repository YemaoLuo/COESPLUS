package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.SemesterCourseMapper;
import com.coesplus.coes.service.SemesterCourseService;
import com.coesplus.common.entity.SemesterCourse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SemesterCourseServiceImpl extends ServiceImpl<SemesterCourseMapper, SemesterCourse> implements SemesterCourseService {
}
