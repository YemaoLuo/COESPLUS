package com.coesplus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.admin.vo.TeacherCommentDashboardVo;
import com.coesplus.common.entity.TeacherComment;

import java.util.List;

public interface TeacherCommentService extends IService<TeacherComment> {

    List<TeacherCommentDashboardVo> teacherComment();
}
