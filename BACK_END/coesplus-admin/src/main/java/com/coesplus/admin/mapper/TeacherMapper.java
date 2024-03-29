package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.common.entity.Teacher;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    @MapKey("faculty_id")
    //map的key，简单粗暴别想多
    Map<String, Map> getFaultyCount();
}
