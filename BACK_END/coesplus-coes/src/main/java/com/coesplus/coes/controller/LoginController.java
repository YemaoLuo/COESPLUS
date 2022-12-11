package com.coesplus.coes.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.coes.dto.UserDto;
import com.coesplus.coes.dto.UserLoginLogDto;
import com.coesplus.coes.service.*;
import com.coesplus.coes.vo.LoginVo;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.StudentLoginLog;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.entity.TeacherLoginLog;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.MinioUtils;
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
    private MailSendLogService mailSendLogService;

    @Resource
    private Ip2regionSearcher ip2regionSearcher;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private FacultyService facultyService;
    @Resource
    private StudentLoginLogService studentLoginLogService;
    @Resource
    private TeacherLoginLogService teacherLoginLogService;

    @Resource
    private MinioUtils minioUtils;


    /**
     * 登录
     *
     * @author LuoYemao
     * @since 2022/12/2 18:41
     */
    @PostMapping()
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        try {
            //限制登陆功能
            String currentCount = redisTemplate.opsForValue().get(RedisPrefix.loginCountCoes + loginVo.getUsername());
            int count = 0;
            if (StringUtils.isNotEmpty(currentCount)) {
                count = Integer.parseInt(currentCount);
                //一分钟内超过五次限制登录
                if (count > 4) {
                    return Result.error("重复登录次数过多，请稍后再试！");
                }
            }
            redisTemplate.opsForValue().set(RedisPrefix.loginCountCoes + loginVo.getUsername(), String.valueOf(count + 1), 1L, TimeUnit.MINUTES);
            //获取IP
            String ip = "";
            if (request.getHeader("x-forwarded-for") == null) {
                ip = request.getRemoteAddr();
            }
            ip = request.getHeader("x-forwarded-for");
            log.info(loginVo.toString() + ip);
            String encryptPassword = DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes());
            String token = IdUtil.simpleUUID();
            String facultyName = "";
            String sexChar = "";
            //鉴别用户id和他是否具有权限，类比sessionid，（存入redis（内存），校验是否合法）
            //redis key是token，value是userid，再反存一遍防止重复登录造成的内存占用
            //token时效30分钟，用户每次请求携带token,拦截后去redis比对
            //name登录

            //查学生表
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.eq(Student::getIsDeleted, 0)
                    .eq(Student::getPassword, encryptPassword)
                    .and(wrapper -> wrapper.eq(Student::getName, loginVo.getUsername())
                            .or().eq(Student::getTelephone, loginVo.getUsername())
                            .or().eq(Student::getEmail, loginVo.getUsername()));

            Student student = studentService.getOne(studentQueryWrapper);
            if (!ObjectUtils.isEmpty(student)) {
                facultyName = facultyService.getById(student.getFacultyId()).getName();
                if (student.getSex().equals("1")) {
                    sexChar = "男";
                } else {
                    sexChar = "女";
                }
            }

            //查老师表
            LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
            teacherQueryWrapper.eq(Teacher::getPassword, encryptPassword)
                    .eq(Teacher::getIsDeleted, 0)
                    .and(wrapper -> wrapper.eq(Teacher::getName, loginVo.getUsername())
                            .or().eq(Teacher::getTelephone, loginVo.getUsername())
                            .or().eq(Teacher::getEmail, loginVo.getUsername()));

            Teacher teacher = teacherService.getOne(teacherQueryWrapper);
            if (!ObjectUtils.isEmpty(teacher)) {
                if (StringUtils.isNotEmpty(teacher.getFacultyId())) {
                    facultyName = facultyService.getById(teacher.getFacultyId()).getName();
                }
                if (teacher.getSex().equals("1")) {
                    sexChar = "男";
                } else {
                    sexChar = "女";
                }
            }

            if (ObjectUtils.isEmpty(student) && ObjectUtils.isEmpty(teacher)) {
                return Result.error("账号或密码错误！");
            } else {//判断老师还是学生
                Object o = ObjectUtils.isEmpty(teacher) ? student : teacher;
                UserDto userDto = new UserDto(o);
                //学院对应
                userDto.setFaculty(facultyName);
                userDto.setSexChar(sexChar);

                if (StringUtils.isEmpty(userDto.getPhoto())) {
                    userDto.setPhoto(minioUtils.preview("e45cc91940ff9621469b324074b40499.png"));
                } else {
                    userDto.setPhoto(minioUtils.preview(userDto.getPhoto()));
                }
                Object l = null;
                if (userDto.getRole().equals("student")) {
                    LambdaQueryWrapper<StudentLoginLog> studentLogQueryWrapper = new LambdaQueryWrapper<>();
                    studentLogQueryWrapper.eq(StudentLoginLog::getStudentId, student.getId())
                            .orderByDesc(StudentLoginLog::getCreateTime)
                            .last("LIMIT 1");
                    //StudentLoginLog loginLog = studentLoginLogService.getOne(studentLogQueryWrapper);
                    l = studentLoginLogService.getOne(studentLogQueryWrapper);
                } else {
                    LambdaQueryWrapper<TeacherLoginLog> teacherLogQueryWrapper = new LambdaQueryWrapper<>();
                    teacherLogQueryWrapper.eq(TeacherLoginLog::getTeacherId, teacher.getId())
                            .orderByDesc(TeacherLoginLog::getCreateTime)
                            .last("LIMIT 1");
                    //TeacherLoginLog loginLog = teacherLoginLogService.getOne(teacherLogQueryWrapper);
                    l = teacherLoginLogService.getOne(teacherLogQueryWrapper);
                }

                //UserLoginLogDto loginLog = new UserLoginLogDto(l);

                if (!ObjectUtils.isEmpty(l)) {
                    UserLoginLogDto loginLog = new UserLoginLogDto(l);
                    String lastIP = StringUtils.isEmpty(loginLog.getIp()) ? "none" : loginLog.getIp();
                    //如果ip变化开启异步发送email通知
                    final String ipInThread = ip;
                    if (!lastIP.equals(ip)) {
                        ThreadUtil.execAsync(() -> {
                            String role = userDto.getRole().equals("student") ? "同学" : "老师";
                            String content = MailContentUtil.build("COES-Plus登录预警", "您的账号于" + ip2regionSearcher.getAddress(ipInThread) + "登陆成功，登录IP发生变化，新登录IP为" + ipInThread + "，请核实是否为本人登录！", userDto.getName() + role);
                            mailSendLogService.sendComplexMessage(userDto.getEmail(), "COES-Plus登录预警", content);
                        });
                    }
                }
                //记录登录日志(分开,因为存不同表)，顺便下一步的前缀处理
                String accountToken = "";
                String accountID = "";
                if (userDto.getRole().equals("student")) {
                    StudentLoginLog log = new StudentLoginLog();
                    log.setStudentId(userDto.getId());
                    log.setIp(ip);
                    studentLoginLogService.save(log);
                    accountToken = RedisPrefix.accountStuToken;
                    accountID = RedisPrefix.accountStuID;
                } else {
                    TeacherLoginLog log = new TeacherLoginLog();
                    log.setTeacherId(userDto.getId());
                    log.setIp(ip);
                    teacherLoginLogService.save(log);
                    accountToken = RedisPrefix.accountTeaToken;
                    accountID = RedisPrefix.accountTeaID;
                }
                //校验是否已存在token
                String existToken = redisTemplate.opsForValue().get(accountID + userDto.getId());
                if (StringUtils.isNotEmpty(existToken)) {
                    //重置token
                    //删除原有token
                    List<String> keys = new ArrayList<>();
                    keys.add(accountID + userDto.getId());
                    keys.add(accountToken + existToken);
                    redisTemplate.delete(keys);
                    //添加新token
                    redisTemplate.opsForValue().set(accountToken + token, userDto.getId(), 30, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().set(accountID + userDto.getId(), token, 30, TimeUnit.MINUTES);
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("token", token);
                    result.put("user", userDto);
                    //result.put("permissions", PermissionVo.generatePermission(userDto.getRole()));
                    LocalDateTime now = LocalDateTime.now();
                    result.put("loginDate", now.getMonth().getValue() + "月" + now.getDayOfMonth() + "日");
                    return Result.ok(result);
                }
                //登陆成功且设置token过期时间为30分钟
                redisTemplate.opsForValue().set(accountToken + token, userDto.getId(), 30, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(accountID + userDto.getId(), token, 30, TimeUnit.MINUTES);
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("token", token);
                result.put("user", userDto);
                //result.put("permissions", PermissionVo.generatePermission(userDto.getRole()));
                LocalDateTime now = LocalDateTime.now();
                result.put("loginDate", now.getMonth().getValue() + "月" + now.getDayOfMonth() + "日");
                return Result.ok(result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登出
     *
     * @author LuoYemao
     * @since 2022/12/2 18:41
     */
    @DeleteMapping()
    public Result logOut(HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            String id;
            if (redisTemplate.hasKey(RedisPrefix.accountStuToken + token)) {
                id = redisTemplate.opsForValue().get(RedisPrefix.accountStuToken + token);
                redisTemplate.delete(RedisPrefix.accountStuToken + token);
                redisTemplate.delete(RedisPrefix.accountStuID + id);
            } else if (redisTemplate.hasKey(RedisPrefix.accountTeaID + token)) {
                id = redisTemplate.opsForValue().get(RedisPrefix.accountTeaToken + token);
                redisTemplate.delete(RedisPrefix.accountTeaToken + token);
                redisTemplate.delete(RedisPrefix.accountTeaID + id);
            }
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}