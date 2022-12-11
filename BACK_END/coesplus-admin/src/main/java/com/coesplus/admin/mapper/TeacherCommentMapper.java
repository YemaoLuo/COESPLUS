package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.admin.vo.TeacherCommentDashboardVo;
import com.coesplus.common.entity.TeacherComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeacherCommentMapper extends BaseMapper<TeacherComment> {

    List<TeacherCommentDashboardVo> teacherComment();
}
