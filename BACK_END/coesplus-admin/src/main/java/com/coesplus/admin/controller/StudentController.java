package com.coesplus.admin.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.dto.StudentDto;
import com.coesplus.admin.excel.StudentExcelVo;
import com.coesplus.admin.service.FacultyService;
import com.coesplus.admin.service.MailSendLogService;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.vo.StudentVo;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
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
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Resource
    private MinioUtils minioUtils;

    @Resource
    private StudentService studentService;
    @Resource
    private MailSendLogService mailSendLogService;
    @Resource
    private FacultyService facultyService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/9/28 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "studentId", required = false) String studentId, @RequestParam(value = "faculty", required = false) String faculty
            , @RequestParam(value = "isDeleted", required = false) String isDeleted, @RequestParam(value = "telephone", required = false) String telephone
            , @RequestParam(value = "joinYear", required = false) String joinYear) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            //条件选择：所给查询字段非空时，模糊匹配
            studentQueryWrapper.like(StringUtils.isNotBlank(name), Student::getName, name)
                    .like(StringUtils.isNotBlank(studentId), Student::getStudentId, studentId)
                    .like(StringUtils.isNotBlank(joinYear), Student::getJoinYear, joinYear)
                    .like(StringUtils.isNotBlank(telephone), Student::getTelephone, telephone)
                    .eq(StringUtils.isNotBlank(isDeleted), Student::getIsDeleted, isDeleted)
                    .orderBy(true, false, Student::getUpdateTime);

            //模糊匹配学院名,再转为对象列->得到相应的学院id
            if (StringUtils.isNotBlank(faculty)) {
                LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
                facultyQueryWrapper.like(Faculty::getName, faculty);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
                //流方法把对象转为string
                List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
                //筛选器判断非空，in看FacultyId是否属于选择的id表
                studentQueryWrapper.in(facultyIdList.size() != 0, Student::getFacultyId, facultyIdList);
            }


            page = studentService.page(page, studentQueryWrapper);
            List<Student> studentList = page.getRecords();
            List<StudentVo> studentVoList = new ArrayList<>();


            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();


            //拼装VO
            studentList.forEach(student -> {
                StudentVo vo = new StudentVo();
                BeanUtils.copyProperties(student, vo);
                String facultyName = "";
                try {
                    facultyName = (String) facultyMap.get(student.getFacultyId()).get("name");
                } catch (Exception e) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
                if (student.getSex().equals("1")) {
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
                studentVoList.add(vo);
            });
            log.info(currentPage + "::" + pageSize + "::" + studentVoList);
            page.setRecords(studentVoList);
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
            , @RequestParam(value = "studentId", required = false) String studentId, @RequestParam(value = "faculty", required = false) String faculty
            , @RequestParam(value = "isDeleted", required = false) String isDeleted, @RequestParam(value = "telephone", required = false) String telephone
            , @RequestParam(value = "joinYear", required = false) String joinYear) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            //获取数据
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            //条件选择：所给查询字段非空时，模糊匹配
            studentQueryWrapper.like(StringUtils.isNotBlank(name), Student::getName, name)
                    .like(StringUtils.isNotBlank(studentId), Student::getStudentId, studentId)
                    .like(StringUtils.isNotBlank(joinYear), Student::getJoinYear, joinYear)
                    .like(StringUtils.isNotBlank(telephone), Student::getTelephone, telephone)
                    .eq(StringUtils.isNotBlank(isDeleted), Student::getIsDeleted, isDeleted)
                    .orderBy(true, false, Student::getUpdateTime);
            //模糊匹配学院名,再转为对象列->得到相应的学院id
            if (StringUtils.isNotBlank(faculty)) {
                LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
                facultyQueryWrapper.like(Faculty::getName, faculty);
                //得到筛选后的学院列，！！!service.list可以得到数据库的表
                List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
                //流方法把对象转为string
                List<String> facultyIdList = facultyList.stream().map(Faculty::getId).collect(Collectors.toList());
                //筛选器判断非空，in看FacultyId是否属于选择的id表
                studentQueryWrapper.in(facultyIdList.size() != 0, Student::getFacultyId, facultyIdList);
            }
            List<Student> studentList = studentService.list(studentQueryWrapper);
            List<StudentExcelVo> studentVoList = new ArrayList<>();
            //获取学院ID与名字map
            Map<String, Map> facultyMap = facultyService.getIDNameMap();
            //拼装VO
            if (studentList.size() == 0) {
                return;
            }
            studentList.forEach(student -> {
                StudentExcelVo vo = new StudentExcelVo();
                BeanUtils.copyProperties(student, vo);
                String facultyName = (String) facultyMap.get(student.getFacultyId()).get("name");
                if (facultyName == null) {
                    facultyName = "";
                }
                vo.setFacultyName(facultyName);
                if (student.getSex().equals("1")) {
                    vo.setSex("男");
                } else {
                    vo.setSex("女");
                }
                if (student.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }
                //转换图像名为url
                try {
                    vo.setPhoto(new URL(minioUtils.preview(student.getPhoto())));
                } catch (Exception e) {
                    try {
                        vo.setPhoto(new URL(minioUtils.preview("e45cc91940ff9621469b324074b40499.png")));
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                studentVoList.add(vo);
            });
            //发送密码邮件
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");;
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus导出课程学生信息表", "您的账号于" + DateTime.now() + "导出课程学生信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentAdmin.getName());
                mailSendLogService.sendComplexMessage(currentAdmin.getEmail(), "COES-Plus导出课程学生信息表", content);
            });
            ExcelUtils.export(response, StudentExcelVo.class, studentVoList, "学生信息表", password);
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
            Student studentById = studentService.getById(id);
            studentById.setIsDeleted(1);
            studentService.updateById(studentById);
            log.info(studentById.toString());
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
            Student studentById = studentService.getById(id);
            studentById.setIsDeleted(0);
            //查看对应的faculty是否还在
            Faculty facultyById = facultyService.getById(studentById.getFacultyId());
            if (ObjectUtils.isEmpty(facultyById)) {
                return Result.error("用户所属学院不存在！请重新设置！");
            }
            studentService.updateById(studentById);
            log.info(studentById.toString());
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
    public Result physicalDelete(@PathVariable("id") String id) {
        try {
            log.info(id);
            studentService.physicalDelete(id);
            return Result.ok();
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
    public Result update(@RequestBody StudentDto studentDto) {
        try {
            //Vo -> Entity
            Student student = new Student();
            BeanUtils.copyProperties(studentDto, student);
            //字典翻译
            if (studentDto.getSex().equals("男")) {
                student.setSex("1");
            } else {
                student.setSex("0");
            }
            studentService.updateById(student);
            log.info(student.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增student
     *
     * @author LuoYemao
     * @since 2022/10/17 23:22
     */
    @PostMapping()
    public Result add(@RequestBody StudentDto studentDto) {
        try {
            return studentService.add(studentDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PatchMapping(value = "/password/{id}")//{}占位符，其中的data作为id
    public Result resetPassword(@PathVariable("id") String id, @RequestBody String newPassword) {
        try {
            //接口逻辑：1. 利用@RequestBody接收password。（见上！！）
            //	  2. 利用@PathVariable接收id。
            //	  3. 使用getById()方法查询出student和teacher的Entity。
            //	  4. 使用DigestUtil.md5Hex()方法对password进行加密。
            //	  5. 使用updateById()方法更新student和teacher的Entity

            //id转实体
            Student studentById = studentService.getById(id);//调用服务层的方法（从id获取对象），得到实体
            studentById.setIsDeleted(0);//检查是否被删除
            String encryptPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());//先加密
            studentById.setPassword(encryptPassword);//再放回对象
            studentService.updateById(studentById);//上传数据库
            log.info(studentById.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}


