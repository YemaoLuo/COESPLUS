package com.coesplus.coes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.coes.mapper.TeacherCommentMapper;
import com.coesplus.coes.service.TeacherCommentService;
import com.coesplus.common.entity.TeacherComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class TeacherCommentServiceImpl extends ServiceImpl<TeacherCommentMapper, TeacherComment> implements TeacherCommentService {
}
