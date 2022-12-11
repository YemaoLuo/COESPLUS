package com.coesplus.coes.controller.student;

import com.coesplus.coes.service.CourseStudentService;
import com.coesplus.coes.vo.DashboardGradeVo;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.BaseContext;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student/dashboard")
@Slf4j
public class StudentDashboardController {

    @Resource
    private CourseStudentService courseStudentService;


    /**
     * 获取学生每个学期的平均分
     *
     * @author LuoYemao
     * @since 2022/12/3 21:36
     */
    @GetMapping("/grade")
    public Result grade() {
        try {
            Student currentStudent = (Student) BaseContext.getValue("currentStudent");
            if (ObjectUtils.isEmpty(currentStudent)) {
                return Result.error("获取登陆学生信息失败！");
            }
            List<DashboardGradeVo> voList = courseStudentService.dashBoardGrade(currentStudent.getId());

            List<String> semesterList = voList.stream().map(DashboardGradeVo::getSemester).collect(Collectors.toList());
            List<String> avgGrade = voList.stream().map(DashboardGradeVo::getAvgGrade).collect(Collectors.toList());

            Map<String, Object> vo = new HashMap<>();
            vo.put("semester", semesterList);
            vo.put("avgGrade", avgGrade);

            return Result.ok(vo);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
