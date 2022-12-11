package com.coesplus.coes.controller.teacher;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coesplus.coes.service.TeacherCommentService;
import com.coesplus.coes.vo.TeacherCommentVo;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.entity.TeacherComment;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/teacher/teacherComment")
@Slf4j
public class TeacherCommentController {

    @Resource
    private TeacherCommentService teacherCommentService;


    /**
     * 分页查询
     *
     * @author LuoYemao
     * @since 2022/11/25 21:01
     */
    @GetMapping()
    public Result page(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        try {
            Page page = new Page(currentPage, pageSize);
            Teacher teacher = (Teacher) BaseContext.getValue("currentTeacher");
            //编辑分页查询条件
            LambdaQueryWrapper<TeacherComment> commentQueryWrapper = new LambdaQueryWrapper<TeacherComment>();
            commentQueryWrapper.eq(TeacherComment::getTeacherId, teacher.getId())
                    .orderByDesc(TeacherComment::getUpdateTime);
            page = teacherCommentService.page(page, commentQueryWrapper);
            List<TeacherComment> records = page.getRecords();
            List<TeacherCommentVo> recordsVo = new ArrayList<>();
            for (TeacherComment record : records) {
                TeacherCommentVo vo = new TeacherCommentVo();
                BeanUtils.copyProperties(record, vo);
                vo.setTeacherName(teacher.getName());
                recordsVo.add(vo);
            }
            log.info(currentPage + "::" + pageSize + "::" + recordsVo);
            page.setRecords(recordsVo);
            return Result.ok(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
