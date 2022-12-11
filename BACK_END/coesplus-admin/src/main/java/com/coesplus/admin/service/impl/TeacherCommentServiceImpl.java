package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.TeacherCommentMapper;
import com.coesplus.admin.service.TeacherCommentService;
import com.coesplus.admin.vo.TeacherCommentDashboardVo;
import com.coesplus.common.entity.TeacherComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@Transactional
public class TeacherCommentServiceImpl extends ServiceImpl<TeacherCommentMapper, TeacherComment> implements TeacherCommentService {

    @Resource
    private TeacherCommentMapper teacherCommentMapper;


    @Override
    public List<TeacherCommentDashboardVo> teacherComment() {
        return teacherCommentMapper.teacherComment();
    }
}
