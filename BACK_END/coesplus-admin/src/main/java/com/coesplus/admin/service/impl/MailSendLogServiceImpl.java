package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.MailSendLogMapper;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.vo.MailCounterVo;
import com.coesplus.common.entity.MailSendLog;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MailSendLogServiceImpl extends ServiceImpl<MailSendLogMapper, MailSendLog> implements MailSendLogService {

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public Result sendSimpleMessage(String destination, String subject, String content) {
        MailSendLog mail = new MailSendLog();
        mail.setContent(content)
                .setDestination(destination)
                .setSubject(subject);
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom("coesplus@yemaoluo.top");
        smm.setTo(destination);
        smm.setSubject(subject);
        smm.setText(content);
        try {
            javaMailSender.send(smm);
            try {
                this.save(mail);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            mail.setStatus(1)
                    .setReason(e.getMessage());
            this.save(mail);
            return Result.error("发送邮件失败！");
        }
    }

    @Override
    public Result sendComplexMessage(String destination, String subject, String content) {
        MailSendLog mail = new MailSendLog();
        mail.setContent(content)
                .setDestination(destination)
                .setSubject(subject);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mmh = new MimeMessageHelper(message, true);
            mmh.setFrom("coesplus@yemaoluo.top");
            mmh.setTo(destination);
            mmh.setSubject(subject);
            mmh.setText(content, true);
            javaMailSender.send(message);
            try {
                this.save(mail);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            mail.setStatus(1)
                    .setReason(e.getMessage());
            this.save(mail);
            return Result.error("发送邮件失败！");
        }
    }

    @Override
    public Result emailCounter() {
        List<MailCounterVo> result = this.getBaseMapper().emailCounter();
        return Result.ok(result);
    }
}
