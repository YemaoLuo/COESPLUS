package com.coesplus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coesplus.admin.mapper.FacultyMapper;
import com.coesplus.admin.service.FacultyService;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.common.entity.Faculty;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class FacultyServiceImpl extends ServiceImpl<FacultyMapper, Faculty> implements FacultyService {

    @Resource
    private FacultyMapper facultyMapper;

    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;


    @Override
    public Map<String, Map> getIDNameMap() {
        return facultyMapper.getIDNameMap();
    }

    @Override
    public void redoDelete(String id) {
        Faculty facultyById = this.getById(id);
        if (facultyById.getIsDeleted() == 0) {
            return;
        }
        facultyById.setIsDeleted(0);
        this.updateById(facultyById);
        //恢复所有学生和老师账户
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getFacultyId, id);
        List<Student> studentList = studentService.list(studentQueryWrapper);
        LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
        teacherQueryWrapper.eq(Teacher::getFacultyId, id);
        List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
        studentList.forEach(student -> student.setIsDeleted(0));
        teacherList.forEach(teacher -> teacher.setIsDeleted(0));
        studentService.updateBatchById(studentList);
        teacherService.updateBatchById(teacherList);
        log.info(facultyById.toString());
        log.info(studentList.toString());
        log.info(teacherList.toString());
    }

    @Override
    public Result delete(String id, Boolean force) {
        //验证是否已关联student和teacher
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getFacultyId, id);
        List<Student> studentList = studentService.list(studentQueryWrapper);
        LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
        teacherQueryWrapper.eq(Teacher::getFacultyId, id);
        List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
        //是否强制更新即删除同时删除学生
        if (teacherList.size() > 0 || studentList.size() > 0) {
            if (force) {
                studentList.forEach(student -> student.setIsDeleted(1));
                teacherList.forEach(teacher -> teacher.setIsDeleted(1));
                studentService.updateBatchById(studentList);
                teacherService.updateBatchById(teacherList);
            } else {
                return Result.error("当前学院仍存在老师或学生！无法删除！");
            }
        }
        Faculty facultyById = this.getById(id);
        facultyById.setIsDeleted(1);
        this.updateById(facultyById);
        log.info(facultyById.toString());
        return Result.ok();
    }
}
