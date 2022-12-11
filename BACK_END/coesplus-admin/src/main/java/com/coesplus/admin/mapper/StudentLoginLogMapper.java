package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.common.entity.StudentLoginLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentLoginLogMapper extends BaseMapper<StudentLoginLog> {

    List<LoginRankVo> loginRank();
}
