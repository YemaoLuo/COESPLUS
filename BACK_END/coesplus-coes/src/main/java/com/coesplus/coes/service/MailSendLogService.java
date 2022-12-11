package com.coesplus.coes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.common.entity.MailSendLog;
import com.coesplus.common.utils.Result;

public interface MailSendLogService extends IService<MailSendLog> {

    Result sendSimpleMessage(String destination, String subject, String content);

    Result sendComplexMessage(String destination, String subject, String content);
}
