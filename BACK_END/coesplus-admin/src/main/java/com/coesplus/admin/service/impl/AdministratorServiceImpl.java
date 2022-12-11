package com.coesplus.admin.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.AdminisratorMapper;
import com.coesplus.admin.service.AdministratorService;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.service.UserActivateService;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

@Service
@Slf4j
@Transactional
public class AdministratorServiceImpl extends ServiceImpl<AdminisratorMapper, Administrator> implements AdministratorService {

    @Resource
    private UserActivateService userActivateService;
    @Resource
    private MailSendLogService mailSendLogService;


    @Override
    public Result add(Administrator administratorVo) {
        //Vo -> Entity
        Administrator administrator = new Administrator();
        BeanUtils.copyProperties(administratorVo, administrator);
        //校验name，email，telephone均不可重复
        LambdaQueryWrapper<Administrator> administratorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        administratorLambdaQueryWrapper.eq(Administrator::getName, administrator.getName())
                .or().eq(Administrator::getEmail, administrator.getEmail())
                .or().eq(Administrator::getTelephone, administrator.getTelephone());
        long count = this.count(administratorLambdaQueryWrapper);
        if (count > 0) {
            return Result.error("姓名，邮箱，电话不唯一！请重新设置！");
        } else {
            String password = IdUtil.simpleUUID().substring(0, 10);
            administrator.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
            this.save(administrator);
            log.info(administrator.toString());
            //发送密码通知邮件
            String msg = administrator.getName() + "管理员！您COES的初始密码为：" + password + "。请尽快重设密码！";
            ThreadUtil.execAsync(() -> {
                mailSendLogService.sendComplexMessage(administrator.getEmail(), "COES-Plus账户创建", msg);
            });
            return Result.ok();
        }
    }
}
