package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.common.entity.Faculty;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface FacultyMapper extends BaseMapper<Faculty> {

    @MapKey("id")
    Map<String, Map> getIDNameMap();
}
