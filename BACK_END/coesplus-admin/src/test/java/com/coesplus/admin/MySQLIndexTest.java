//package com.coesplus.admin;
//
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.IdUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.coesplus.admin.service.StudentService;
//import com.coesplus.admin.service.TeacherService;
//import com.coesplus.admin.service.UserActivateService;
//import com.coesplus.common.entity.Student;
//import com.coesplus.common.entity.Teacher;
//import com.coesplus.common.entity.UserActivate;
//import com.coesplus.common.minio.entity.ObjectItem;
//import com.coesplus.common.utils.MinioUtils;
//import com.coesplus.common.utils.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.ObjectUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//@Slf4j
//@SpringBootTest
//public class MySQLIndexTest {
//
//   @Resource
//   private StudentService studentService;
//   @Resource
//   private TeacherService teacherService;
//
//   @Resource
//   private UserActivateService userActivateService;
//
//   @Test
//   public void insertData() {
//      try {
//         Random ran = new Random();
//         List<String> idList = studentService.list().stream().map(Student::getId).collect(Collectors.toList());
//         idList.addAll(teacherService.list().stream().map(Teacher::getId).collect(Collectors.toList()));
//         List<String> roleList = Arrays.asList("student", "teacher");
//         log.info(idList.size() + " ids are ready to insert.");
//         int count = 1;
//         while (true) {
//            UserActivate ua = new UserActivate();
//            ua.setUserId(idList.get(ran.nextInt(idList.size())));
//            ua.setVerifyToken(IdUtil.simpleUUID())
//                    .setRole(roleList.get(ran.nextInt(roleList.size())));
//            userActivateService.save(ua);
//            log.info("Inserting " + count + " data!");
//            count++;
//         }
//      }catch (Exception e) {
//         log.error(e.getMessage(), e);
//         log.info(Result.error(e.getMessage()).toString());
//      }
//   }
//}
