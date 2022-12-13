package com.coesplus.admin.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.admin.excel.FacultyExcelVo;
import com.coesplus.admin.service.*;
import com.coesplus.admin.vo.FacultyVo;
import com.coesplus.admin.vo.SelectListVo;
import com.coesplus.common.entity.Administrator;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.ExcelUtils;
import com.coesplus.common.utils.MailContentUtil;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/faculty")
@Slf4j
public class FacultyController {

    @Resource
    private FacultyService facultyService;
    @Resource
    private CourseService courseService;
    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;

    @Resource
    private MailSendLogService mailSendLogService;

    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/10/7 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize,
                       @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            Page page = new Page(currentPage, pageSize);
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();

            //条件选择：所给查询字段非空时，模糊匹配
            facultyQueryWrapper.like(StringUtils.isNotBlank(name), Faculty::getName, name)
                    .eq(StringUtils.isNotBlank(isDeleted), Faculty::getIsDeleted, isDeleted)
                    .orderBy(true, false, Faculty::getUpdateTime);
            page = facultyService.page(page, facultyQueryWrapper);
            List<Faculty> facultyList = page.getRecords();
            List<FacultyVo> facultyVoList = new ArrayList<>();
            //拼装VO
            facultyList.forEach(faculty -> {
                FacultyVo vo = new FacultyVo();
                BeanUtils.copyProperties(faculty, vo);
                facultyVoList.add(vo);
            });
            log.info(currentPage + "::" + pageSize + "::" + facultyVoList);
            page.setRecords(facultyVoList);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 返回下拉菜单所有学院
     *
     * @author LuoYemao
     * @since 2022/12/1 22:12
     */
    @GetMapping("/select")
    public Result selectList() {
        try {
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();
            facultyQueryWrapper.select(Faculty::getName, Faculty::getId);
            List<Faculty> list = facultyService.list(facultyQueryWrapper);
            List<SelectListVo> voList = new ArrayList<>();
            for (Faculty faculty : list) {
                SelectListVo vo = new SelectListVo();
                vo.setLabel(faculty.getName())
                        .setValue(faculty.getId());
                voList.add(vo);
            }
            return Result.ok(voList);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
    }

    /**
     * 导出Excel
     */
    @GetMapping("/excel")
    public void excel(HttpServletResponse response, @RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "isDeleted", required = false) String isDeleted) {
        try {
            LambdaQueryWrapper<Faculty> facultyQueryWrapper = new LambdaQueryWrapper<>();

            //条件选择：所给查询字段非空时，模糊匹配
            facultyQueryWrapper.like(StringUtils.isNotBlank(name), Faculty::getName, name)
                    .eq(StringUtils.isNotBlank(isDeleted), Faculty::getIsDeleted, isDeleted)
                    .orderBy(true, false, Faculty::getUpdateTime);

            List<Faculty> facultyList = facultyService.list(facultyQueryWrapper);
            List<FacultyExcelVo> facultyVoList = new ArrayList<>();
            //拼装VO
            if (facultyList.size() == 0) {
                return;
            }
            facultyList.forEach(faculty -> {
                FacultyExcelVo vo = new FacultyExcelVo();
                BeanUtils.copyProperties(faculty, vo);

                if (faculty.getIsDeleted() == 0) {
                    vo.setIsDeleted("正常");
                } else {
                    vo.setIsDeleted("禁用");
                }

                facultyVoList.add(vo);
            });

            //发送密码邮件
            Administrator currentAdmin = (Administrator) BaseContext.getValue("currentAdmin");
            String password = IdUtil.simpleUUID().substring(0, 6);
            ThreadUtil.execAsync(() -> {
                String content = MailContentUtil.build("COES-Plus导出学院信息表通知", "您的账号于" + DateTime.now() + "导出学院信息表，密码为" + password + "。请注意信息安全，防止信息泄露！", currentAdmin.getName());
                mailSendLogService.sendComplexMessage(currentAdmin.getEmail(), "COES-Plus导出学院信息表通知", content);
            });
            ExcelUtils.export(response, FacultyExcelVo.class, facultyVoList, "学院信息表", password);

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
    public Result delete(@PathVariable("id") String id, @RequestParam(value = "force", defaultValue = "false") Boolean force) {
        try {
            log.info(id + "::" + force);
            return facultyService.delete(id, force);
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
            facultyService.redoDelete(id);
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
            log.info(id + "::" + force);
            //删除所有对应student&teacher
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.eq(Student::getFacultyId, id);
            LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
            teacherQueryWrapper.eq(Teacher::getFacultyId, id);
            List<Student> studentList = studentService.list(studentQueryWrapper);
            List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
            if (studentList.size() > 0 && teacherList.size() > 0) {
                if (force) {
                    facultyService.removeById(id);
                } else {
                    return Result.error("当前学院仍存在老师和学生！无法删除！");
                }
            } else if (studentList.size() > 0) {
                if (force) {
                    facultyService.removeById(id);
                } else {
                    return Result.error("当前学院仍存在学生！无法删除！");
                }
            } else if (teacherList.size() > 0) {
                if (force) {
                    facultyService.removeById(id);
                } else {
                    return Result.error("当前学院仍存在老师！无法删除！");
                }
            }
            facultyService.removeById(id);
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
    public Result update(@RequestBody FacultyVo facultyVo) {
        try {
            //Vo -> Entity
            Faculty faculty = new Faculty();
            BeanUtils.copyProperties(facultyVo, faculty);
            facultyService.updateById(faculty);
            log.info(faculty.toString());
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增faculty
     *
     * @author LuoYemao
     * @since 2022/10/17 23:22
     */
    @PostMapping()
    public Result add(@RequestBody FacultyVo facultyVo) {
        try {
            //Vo -> Entity
            Faculty faculty = new Faculty();
            BeanUtils.copyProperties(facultyVo, faculty);
            facultyService.save(faculty);
            return Result.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
