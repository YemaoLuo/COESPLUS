package com.coesplus.admin.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.LoginVo;
import com.coesplus.common.entity.*;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/account")
@Slf4j
public class LoginController {

    @Resource
    private AdministratorService administratorService;
    @Resource
    private AdministratorLoginLogService administratorLoginLogService;
    @Resource
    private MailSendLogService mailSendLogService;

    @Resource
    private Ip2regionSearcher ip2regionSearcher;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserActivateService userActivateService;

    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;


    @PostMapping()
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        try {
            //限制登陆功能
            String currentCount = redisTemplate.opsForValue().get(RedisPrefix.loginCountAdmin + loginVo.getUsername());
            int count = 0;
            if (StringUtils.isNotEmpty(currentCount)) {
                count = Integer.parseInt(currentCount);
                //一分钟内超过五次限制登录
                if (count > 4) {
                    return Result.error("重复登录次数过多，请稍后再试！");
                }
            }
            redisTemplate.opsForValue().set(RedisPrefix.loginCountAdmin + loginVo.getUsername(), String.valueOf(count + 1), 1L, TimeUnit.MINUTES);
            //获取IP
            String ip = "";
            if (request.getHeader("x-forwarded-for") == null) {
                ip = request.getRemoteAddr();
            }
            ip = request.getHeader("x-forwarded-for");
            log.info(loginVo.toString() + ip);
            String encryptPassword = DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes());
            String token = IdUtil.simpleUUID();
            //鉴别用户id和他是否具有权限，类比sessionid，（存入redis（内存），校验是否合法）
            //redis key是token，value是userid，再反存一遍防止重复登录造成的内存占用
            //token时效30分钟，用户每次请求携带token,拦截后去redis比对
            //name登录
            LambdaQueryWrapper<Administrator> adminQueryWrapper = new LambdaQueryWrapper<>();
            adminQueryWrapper.eq(Administrator::getIsDeleted, 0)
                    .eq(Administrator::getPassword, encryptPassword)
                    .eq(Administrator::getName, loginVo.getUsername())
                    .or().eq(Administrator::getTelephone, loginVo.getUsername())
                    .or().eq(Administrator::getEmail, loginVo.getUsername());
            Administrator administrator = administratorService.getOne(adminQueryWrapper);
            if (ObjectUtils.isEmpty(administrator)) {
                return Result.error("账号或密码错误！");
            } else {
                LambdaQueryWrapper<AdministratorLoginLog> adminLogQueryWrapper = new LambdaQueryWrapper<>();
                adminLogQueryWrapper.eq(AdministratorLoginLog::getAdminId, administrator.getId())
                        .orderByDesc(AdministratorLoginLog::getCreateTime)
                        .last("LIMIT 1");
                AdministratorLoginLog loginLog = administratorLoginLogService.getOne(adminLogQueryWrapper);
                if (!ObjectUtils.isEmpty(loginLog)) {
                    String lastIP = StringUtils.isEmpty(loginLog.getIp()) ? "none" : loginLog.getIp();
                    //如果ip变化开启异步发送email通知
                    final String ipInThread = ip;
                    if (!lastIP.equals(ip)) {
                        ThreadUtil.execAsync(() -> {
                            String content = MailContentUtil.build("COES-Plus登录预警", "您的账号于" + ip2regionSearcher.getAddress(ipInThread) + "登陆成功，登录IP发生变化，新登录IP为" + ipInThread + "，请核实是否为本人登录！", administrator.getName());
                            mailSendLogService.sendComplexMessage(administrator.getEmail(), "COES-Plus登录预警", content);
                        });
                    }
                }
                //记录登录日志
                AdministratorLoginLog logInLog = new AdministratorLoginLog();
                logInLog.setAdminId(administrator.getId());
                logInLog.setIp(ip);
                administratorLoginLogService.save(logInLog);
                //校验是否已存在token
                String existToken = redisTemplate.opsForValue().get(RedisPrefix.accountAdminID + administrator.getId());
                if (StringUtils.isNotEmpty(existToken)) {
                    //重置token
                    //删除原有token
                    List<String> keys = new ArrayList<>();
                    keys.add(RedisPrefix.accountAdminID + administrator.getId());
                    keys.add(RedisPrefix.accountAdminToken + existToken);
                    log.info("重复登陆删除旧key：" + keys);
                    redisTemplate.delete(keys);
                    //添加新token
                    redisTemplate.opsForValue().set(RedisPrefix.accountAdminToken + token, administrator.getId(), 30, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().set(RedisPrefix.accountAdminID + administrator.getId(), token, 30, TimeUnit.MINUTES);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("token", token);
                    result.put("user", administrator);
                    LocalDateTime now = LocalDateTime.now();
                    result.put("loginDate", now.getMonth().getValue() + "月" + now.getDayOfMonth() + "日");
                    return Result.ok(result);
                }
                //登陆成功且设置token过期时间为30分钟
                redisTemplate.opsForValue().set(RedisPrefix.accountAdminToken + token, administrator.getId(), 30, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(RedisPrefix.accountAdminID + administrator.getId(), token, 30, TimeUnit.MINUTES);
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("token", token);
                result.put("user", administrator);
                LocalDateTime now = LocalDateTime.now();
                result.put("loginDate", now.getMonth().getValue() + "月" + now.getDayOfMonth() + "日");
                return Result.ok(result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping()
    public Result logOut(HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            log.info(token);
            String id = redisTemplate.opsForValue().get(RedisPrefix.accountAdminToken + token);
            redisTemplate.delete(RedisPrefix.accountAdminToken + token);
            redisTemplate.delete(RedisPrefix.accountAdminID + id);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @PatchMapping()
    public Result firstLogIn(@RequestParam("verifyToken") String verifyToken, @RequestBody String newPassword) {
        try {
            LambdaQueryWrapper<UserActivate> userActivateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userActivateLambdaQueryWrapper.eq(UserActivate::getVerifyToken, verifyToken)
                    .eq(UserActivate::getIsDeleted, 0);
            UserActivate userActivate = userActivateService.getOne(userActivateLambdaQueryWrapper);
            if (ObjectUtils.isEmpty(userActivate)) {
                return Result.error("认证令牌失效！");
            }
            if (userActivate.getRole().equals("student")) {
                Student studentById = studentService.getById(userActivate.getUserId());//调用服务层的方法（从id获取对象），得到实体
                String encryptPassword = DigestUtils.md5DigestAsHex(newPassword.split("\"")[3].getBytes());//先加密
                studentById.setPassword(encryptPassword);//再放回对象
                studentById.setIsDeleted(0);
                studentService.updateById(studentById);//上传数据库
            }//改为存在

            else if (userActivate.getRole().equals("teacher")) {
                Teacher teacherById = teacherService.getById(userActivate.getUserId());//调用服务层的方法（从id获取对象），得到实体
                String encryptPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());//先加密
                teacherById.setPassword(encryptPassword);//再放回对象
                teacherById.setIsDeleted(0);
                teacherService.updateById(teacherById);//上传数据库
            }//改为存在

            else if (userActivate.getRole().equals("administrator")) {
                Administrator administratorById = administratorService.getById(userActivate.getUserId());//调用服务层的方法（从id获取对象），得到实体
                String encryptPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());//先加密
                administratorById.setPassword(encryptPassword);//再放回对象
                administratorById.setIsDeleted(0);
                administratorService.updateById(administratorById);//上传数据库
            }//改为存在

            userActivate.setIsDeleted(1);
            userActivateService.updateById(userActivate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

}
