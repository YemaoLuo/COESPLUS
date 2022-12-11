package com.coesplus.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.minio.entity.ObjectItem;
import com.coesplus.common.utils.MinioUtils;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class CleanPicCacheTest {

   @Resource
   private StudentService studentService;
   @Resource
   private TeacherService teacherService;

   @Resource
   private MinioUtils minioUtils;

   @Test
   public void cleanPicCache() {
      try {
         LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
         studentQueryWrapper.select(Student::getPhoto);
         List<Student> studentList = studentService.list(studentQueryWrapper);
         List<String> studentPicList = new ArrayList<>();
         for (Student student : studentList) {
            if (ObjectUtils.isEmpty(student)) {
               studentPicList.add("e45cc91940ff9621469b324074b40499.png");
            }else {
               studentPicList.add(student.getPhoto());
            }
         }
         LambdaQueryWrapper<Teacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
         teacherQueryWrapper.select(Teacher::getPhoto);
         List<Teacher> teacherList = teacherService.list(teacherQueryWrapper);
         List<String> teacherPicList = new ArrayList<>();
         for (Teacher teacher : teacherList) {
            if (ObjectUtils.isEmpty(teacher)) {
               teacherPicList.add("e45cc91940ff9621469b324074b40499.png");
            }else {
               teacherPicList.add(teacher.getPhoto());
            }
         }
         //利用studentPicList存储所有Pic
         studentPicList.addAll(teacherPicList);
         //去重
         studentPicList = studentPicList.stream().distinct().collect(Collectors.toList());
         //查询已有的文件
         List<ObjectItem> objectItems = minioUtils.listObjects("coesplus");
         for (int i = 0; i < objectItems.size(); i++) {
            if (studentPicList.contains(objectItems.get(i).getObjectName())) {
               objectItems.remove(i);
               i--;
            }
         }
         log.info(objectItems.toString());
         //需要删除的就是objectItems
         Long totalSize = 0L;
         for (ObjectItem objectItem : objectItems) {
            try {
               //删除文件
               //minioUtils.remove(objectItem.getObjectName());
               totalSize += objectItem.getSize();
            } catch (Exception e) {
               log.error(e.getMessage(), e);
            }
         }
         if (totalSize/1000/1000 == 0) {
            totalSize /= 1000;
         }
         log.info(Result.ok("删除成功！共清理" + objectItems.size() + "个文件，释放" +  totalSize + "kb空间！").toString());
      }catch (Exception e) {
         log.error(e.getMessage(), e);
         log.info(Result.error(e.getMessage()).toString());
      }
   }
}
