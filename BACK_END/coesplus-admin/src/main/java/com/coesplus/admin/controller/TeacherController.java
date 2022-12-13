package com.coesplus.admin.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.dto.TeacherDto;
import com.coesplus.admin.excel.TeacherExcelVo;
import com.coesplus.admin.service.FacultyService;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.admin.vo.TeacherVo;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    @Resource
    private MinioUtils minioUtils;

    @Resource
    private TeacherService teacherService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private FacultyService facultyService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/10/5 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize
            , @RequestParam(value = "name", required = false) String name, @RequestParam(value = "teacherId", required = false) String teacherId
            , @RequestParam(value = "isDeleted", required = false) String isDeleted, @RequestParam(value = "telephone", required = false) String telephone
            , @RequestParam(value = "faculty", required = false) String faculty, @RequestParam(value = "joinYear", required = false) String joinYear) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
            //添加额外分页查询条件
            teacherQueryWrapper.like(StringUtils.isNotBlank(name), Teacher::getName, name)
                    .like(StringUtils.isNotBlank(teacherId), Teacher::getTeacherId, teacherId)
                    .like(StringUtils.isNotBlank(joinYear), Teacher::getJoinYear, joinYear)
                    .like(StringUtils.isNotBlank(telephone), Teacher::getTelephone, telephone)
                    .eq(StringUtils.isNotBlank(isDeleted), Teacher::getIsDeleted, isDeleted)
                    .orderBy(true,false, Teacher::getUpdateTime);
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
            facultyQueryWrapper.like(StringUtils.isNotBlank(faculty), Faculty::getName, faculty);
            List<String> facultyIdList = facultyService.list(facultyQueryWrapper).stream().map(Faculty::getId).collect(Collectors.toList());
            if (facultyIdList.size() == 0) {
                return Result.ok(page);
            }
            teacherQueryWrapper.in(Teacher::getFacultyId, facultyIdList);
            page = teacherService.page(page, teacherQueryWrapper);
            List<Teacher> teacherList = page.getRecords();
            List<TeacherVo> teacherVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            teacherList.forEach(teacher -> {
                TeacherVo vo = new TeacherVo();
                BeanUtils.copyProperties(teacher, vo);
                String facultyName = "";
                try {
                    facultyName = (String) facultyMap.get(teacher.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
                if (teacher.getSex().equals("1")) {
                    vo.setSex("男");
                } else {
                    vo.setSex("女");
                }
                //转换图像名为url
                try {
                    vo.setPhoto(minioUtils.preview(vo.getPhoto()));
                } catch (Exception e) {
                    vo.setPhoto(minioUtils.preview("e45cc91940ff9621469b324074b40499.png"));
                }
                teacherVoList.add(vo);
            });
            log.info(currentPage + "::" + pageSize + "::" + teacherVoList);
            page.setRecords(teacherVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出Excel
     *
     * @author LuoYemao
     * @since 2022/10/19 10:53
     */
    @GetMapping("/excel")
    public void excel(HttpServletResponse response, @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "teacherId", required = false) String teacherId, @RequestParam(value = "faculty", required = false) String faculty
            , @RequestParam(value = "isDeleted", required = false, defaultValue = "0") String isDeleted, @RequestParam(value = "telephone", required = false) String telephone
            , @RequestParam(value = "joinYear", required = false) String joinYear) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            //获取数据
            LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
            //条件选择：所给查询字段非空时，模糊匹配
            teacherQueryWrapper.like(StringUtils.isNotBlank(name), Teacher::getName, name)
                    .like(StringUtils.isNotBlank(teacherId), Teacher::getTeacherId, teacherId)
                    .like(StringUtils.isNotBlank(joinYear), Teacher::getJoinYear, joinYear)
                    .like(StringUtils.isNotBlank(telephone), Teacher::getTelephone, telephone)
                    .eq(StringUtils.isNotBlank(isDeleted), Teacher::getIsDeleted, isDeleted)
                    .orderBy(true, false, Teacher::getUpdateTime);
            //模糊匹配学院名,再转为对象列->得到相应的学院id
            if (StringUtils.isNotBlank(faculty)) {
                LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
                facultyQueryWrapper.like(Faculty::getName, faculty);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
                //流方法把对象转为string
                List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
                //筛选器判断非空，in看FacultyId是否属于选择的id表
                teacherQueryWrapper.in(facultyIdList.size() != 0, Teacher::getFacultyId, facultyIdList);
            }
            List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
            List<TeacherExcelVo> teacherVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            if (teacherList.size() == 0) {
                return;
            }
            //拼装VO
            teacherList.forEach(teacher -> {
                TeacherExcelVo vo = new TeacherExcelVo();
                BeanUtils.copyProperties(teacher, vo);
                String facultyName = (String) facultyMap.get(teacher.getFacultyId()).get("name");
                if (facultyName == null) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
                if (teacher.getSex().equals("1")) {
                    vo.setSex("男");
                } else {
                    vo.setSex("女");
                }
                if (teacher.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }
                //转换图像名为url
                try {
                    vo.setPhoto(new URL(minioUtils.preview(teacher.getPhoto())));
                } catch (Exception e) {
                    try {
                        vo.setPhoto(new URL(minioUtils.preview("e45cc91940ff9621469b324074b40499.png")));
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                teacherVoList.add(vo);
            });
            //发送密码邮件
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");;
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus导出教师信息表", "您的账号于" + DateTime.now() + "导出教师信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentAdmin.getName());
                mailSendLogService.sendComplexMessage(currentAdmin.getEmail(), "COES-Plus导出教师信息表", content);
            });
            ExcelUtils.export(response, TeacherExcelVo.class, teacherVoList, "教师信息表", password);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 虚拟删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable("id") String id) {
        try {
            Teacher teacherById = teacherService.getById(id);
            teacherById.setIsDeleted(1);
            teacherService.updateById(teacherById);
            log.info(teacherById.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @PutMapping(value = "/{id}")
    public Result redoDelete(@PathVariable("id") String id) {
        try {
            Teacher teacherById = teacherService.getById(id);
            teacherById.setIsDeleted(0);
            //查看对应的faculty是否还在
            Faculty facultyById = facultyService.getById(teacherById.getFacultyId());
            if (ObjectUtils.isEmpty(facultyById)) {
                return Result.error("用户所属学院不存在！请重新设置！");
            }
            teacherService.updateById(teacherById);
            log.info(teacherById.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 物理删除
     *
     * @author LuoYemao
     * @since 2022/10/6 17:46
     */
    @DeleteMapping(value = "/physical/{id}")
    public Result physicalDelete(@PathVariable("id") String id, @RequestParam(value = "force", defaultValue = "false") Boolean force) {
        try {
            log.info(id);
            return teacherService.physicalDelete(id, force);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新
     *
     * @author LuoYemao
     * @since 2022/10/6 22:31
     */
    @PatchMapping()
    public Result update(@RequestBody TeacherDto teacherDto) {
        try {
            //Vo -> Entity
            Teacher teacher = new Teacher();
            BeanUtils.copyProperties(teacherDto, teacher);
            //字典翻译
            if (teacher.getSex().equals("男")) {
                teacher.setSex("1");
            } else {
                teacher.setSex("0");
            }
            teacherService.updateById(teacher);
            log.info(teacher.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增teacher
     *
     * @author LuoYemao
     * @since 2022/10/17 23:22
     */
    @PostMapping()
    public Result add(@RequestBody TeacherDto teacherDto) {
        try {
            return teacherService.add(teacherDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    /*@PatchMapping(value = "/password/{id}")//{}占位符，其中的data作为id
    public Result resetPassword(@PathVariable("id") String id, @RequestBody String newPassword) {
        try {
            //接口逻辑：1. 利用@RequestBody接收password。（见上！！）
            //	  2. 利用@PathVariable接收id。
            //	  3. 使用getById()方法查询出student和teacher的Entity。
            //	  4. 使用DigestUtil.md5Hex()方法对password进行加密。
            //	  5. 使用updateById()方法更新student和teacher的Entity

            //id转实体
            Teacher teacherById = teacherService.getById(id);//调用服务层的方法（从id获取对象），得到实体
            teacherById.setIsDeleted(0);//检查是否被删除
            String encryptPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());//先加密
            teacherById.setPassword(encryptPassword);//再放回对象
            teacherService.updateById(teacherById);//上传数据库
            log.info(teacherById.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }*/
}
