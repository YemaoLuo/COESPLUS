package com.coesplus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.LoginRankVo;
import com.coesplus.admin.vo.TeacherCommentDashboardVo;
import com.coesplus.common.entity.AdministratorLoginLog;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.entity.StudentLoginLog;
import com.coesplus.common.entity.TeacherLoginLog;
import com.coesplus.common.utils.RedisPrefix;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

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
    private TeacherLoginLogService teacherLoginLogService;
    @Resource
    private StudentLoginLogService studentLoginLogService;
    @Resource
    private TeacherCommentService teacherCommentService;
    @Resource
    private AdministratorLoginLogService administratorLoginLogService;
    @Resource
    private MailSendLogService mailSendLogService;


    /**
     * 查看登陆记录地区占比数
     *
     * @author LuoYemao
     * @since 2022/11/6 21:26
     */
    @GetMapping("/login/address")
    public Result loginAddress() {
        try {
            Map<String, Integer> result = new HashMap<>();
            LambdaQueryWrapper<AdministratorLoginLog> adminQueryWrapper = new LambdaQueryWrapper<>();
            adminQueryWrapper.select(AdministratorLoginLog::getIp)
                    .isNotNull(AdministratorLoginLog::getIp);
            List<AdministratorLoginLog> adminIpList = administratorLoginLogService.list(adminQueryWrapper);
            LambdaQueryWrapper<StudentLoginLog> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.select(StudentLoginLog::getIp)
                    .isNotNull(StudentLoginLog::getIp);
            List<StudentLoginLog> studentIpList = studentLoginLogService.list(studentQueryWrapper);
            LambdaQueryWrapper<TeacherLoginLog> teacherQueryWrapper = new LambdaQueryWrapper<>();
            teacherQueryWrapper.select(TeacherLoginLog::getIp)
                    .isNotNull(TeacherLoginLog::getIp);
            List<TeacherLoginLog> teacherIpList = teacherLoginLogService.list(teacherQueryWrapper);
            List<String> ipList = new ArrayList<>();
            ipList.addAll(adminIpList.stream().map(AdministratorLoginLog::getIp).collect(Collectors.toList()));
            ipList.addAll(teacherIpList.stream().map(TeacherLoginLog::getIp).collect(Collectors.toList()));
            ipList.addAll(studentIpList.stream().map(StudentLoginLog::getIp).collect(Collectors.toList()));
            for (String ip : ipList) {
                IpInfo ipInfo = ip2regionSearcher.btreeSearch(ip);
                //处理内网IP
                if (StringUtils.isEmpty(ipInfo.getCountry())) {
                    result.put("内网", result.getOrDefault("内网", 0) + 1);
                }
                //处理非中国IP
                else if (!ipInfo.getCountry().equals("中国")) {
                    result.put(ipInfo.getCountry(), result.getOrDefault(ipInfo.getCountry(), 0) + 1);
                }
                //类似澳门地区IP
                else if (StringUtils.isEmpty(ipInfo.getCity())) {
                    result.put(ipInfo.getProvince(), result.getOrDefault(ipInfo.getProvince(), 0) + 1);
                }
                //正常城市IP处理
                else {
                    result.put(ipInfo.getCity(), result.getOrDefault(ipInfo.getCity(), 0) + 1);
                }
            }
            return Result.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计用户登录次数排名(10位)
     *
     * @author LuoYemao
     * @since 2022/11/6 19:53
     */
    @GetMapping("/login/rank")
    public Result loginRank() {
        try {
            Map<String, List<LoginRankVo>> result = new HashMap<>();
            List<LoginRankVo> adminResult = administratorLoginLogService.loginRank();
            List<LoginRankVo> teacherResult = teacherLoginLogService.loginRank();
            List<LoginRankVo> studentResult = studentLoginLogService.loginRank();
            result.put("administrator", adminResult);
            result.put("student", studentResult);
            result.put("teacher", teacherResult);
            return Result.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
    }

    /**
     * 查看登陆用户数
     */
    @GetMapping("/login/counter")
    public Result loginCounter() {
        try {
            Collection administratorNum = redisTemplate.keys(RedisPrefix.accountAdminID + "*");
            Collection studentNum = redisTemplate.keys(RedisPrefix.accountStuID + "*");
            Collection teacherNum = redisTemplate.keys(RedisPrefix.accountTeaID + "*");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("administrator", administratorNum.size());
            result.put("student", studentNum.size());
            result.put("teacher", teacherNum.size());

            return Result.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看激活学生数
     */
    @GetMapping("/student")
    public Result activeStudent() {
        try {
            //筛选激活学生
            //LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            // studentQueryWrapper.eq(Student::getIsDeleted, 0);
            //筛选非禁用学院
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
            facultyQueryWrapper.eq(Faculty::getIsDeleted, 0)
                    .select(Faculty::getId);
            List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);

            //流方法把学院id转为string
            List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
            Map<String, Object> result = new HashMap<String, Object>();
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //得出学生根据学院id的计数
            Map<String, Map> studentCounterMap = studentService.getFaultyCount();

            //遍历未禁用学院
            for (int i = 0; i < facultyIdList.size(); i++) {
                if (studentCounterMap.containsKey(facultyIdList.get(i)))
                    result.put((String) facultyMap.get(facultyIdList.get(i)).get("name"), studentCounterMap.get(facultyIdList.get(i)).get("count"));
                else
                    result.put((String) facultyMap.get(facultyIdList.get(i)).get("name"), 0);
            }
            if (studentCounterMap.containsKey(null))
                result.put("未设置学院", studentCounterMap.get(facultyIdList.get(1)).get("count"));
            return Result.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看激活教师数
     */
    @GetMapping("/teacher")
    public Result activeTeacher() {
        try {
            //筛选非禁用学院
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
            facultyQueryWrapper.eq(Faculty::getIsDeleted, 0)
                    .select(Faculty::getId);
            List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);

            //流方法把学院id转为string
            List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
            Map<String, Object> result = new HashMap<String, Object>();
            Map<String, Map> facultyMap = facultyService.getIDNameMap();

            //得出教师根据学院id的计数
            Map<String, Map> teacherCounterMap = teacherService.getFaultyCount();

            //遍历未禁用学院
            for (int i = 0; i < facultyIdList.size(); i++) {
                if (teacherCounterMap.containsKey(facultyIdList.get(i)))
                    result.put((String) facultyMap.get(facultyIdList.get(i)).get("name"), teacherCounterMap.get(facultyIdList.get(i)).get("count"));
                else
                    result.put((String) facultyMap.get(facultyIdList.get(i)).get("name"), 0);
            }
            if (teacherCounterMap.containsKey(null))
                result.put("未设置学院", teacherCounterMap.get(null).get("count"));

            return Result.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看邮件发送统计记录
     *
     * @author LuoYemao
     * @since 2022/11/13 16:43
     */
    @GetMapping("/email")
    public Result emailCounter() {
        try {
            return mailSendLogService.emailCounter();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
    }

    /**
     * 查看教师评论统计记录
     *
     * @author LuoYemao
     * @since 2022/11/27 16:43
     */
    @GetMapping("/comment")
    public Result teacherComment() {
        try {
            List<TeacherCommentDashboardVo> vo = teacherCommentService.teacherComment();
            log.info(vo.toString());
            return Result.ok(vo);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
    }
}
