package com.coesplus.coes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coesplus.coes.dto.AddCourseDto;
import com.coesplus.common.entity.Semester;
import com.coesplus.common.entity.Student;
import com.coesplus.common.utils.Result;

public interface SemesterService extends IService<Semester> {
    Semester valid(Student student);

    Result add(Semester semester, Student currentStudent, AddCourseDto dto);

    Result drop(Semester semester, Student currentStudent, AddCourseDto dto);
}
