package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.TeacherLoginLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeacherLoginLogMapper extends BaseMapper<TeacherLoginLog> {

    List<LoginRankVo> loginRank();
}
