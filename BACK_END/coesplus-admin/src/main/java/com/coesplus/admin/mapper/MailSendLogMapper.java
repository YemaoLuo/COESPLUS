package com.coesplus.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coesplus.admin.vo.MailCounterVo;
import com.coesplus.common.entity.MailSendLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MailSendLogMapper extends BaseMapper<MailSendLog> {
    List<MailCounterVo> emailCounter();
}
