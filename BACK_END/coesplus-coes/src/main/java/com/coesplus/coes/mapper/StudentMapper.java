package com.coesplus.coes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.common.entity.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    @MapKey("faculty_id")
//map的key，简单粗暴别想多
    Map<String, Map> getFaultyCount();
}
