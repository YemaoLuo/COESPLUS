package com.coesplus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coesplus.admin.service.FileService;
import com.coesplus.admin.service.StudentService;
import com.coesplus.admin.service.TeacherService;
import com.coesplus.admin.vo.FileDetailVo;
import com.coesplus.admin.vo.FileItemVo;
import com.coesplus.common.entity.Student;
import com.coesplus.common.entity.Teacher;
import com.coesplus.common.minio.entity.ObjectItem;
import com.coesplus.common.utils.MinioUtils;
import com.coesplus.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;
    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;

    @Resource
    private MinioUtils minioUtils;

    /**
     * 上传文件
     *
     * @author LuoYemao
     * @since 2022/10/5 21:01
     */
    @PostMapping()
    public Result upload(@RequestBody MultipartFile file) {
        try {
            return fileService.upload(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清理图片缓存
     *
     * @author LuoYemao
     * @since 2022/10/31 21:26
     */
    @DeleteMapping("/picCache")
    public Result cleanPicCache() {
        try {
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.select(Student::getPhoto);
            List<Student> studentList = studentService.list(studentQueryWrapper);
            List<String> studentPicList = new ArrayList<>();
            for (Student student : studentList) {
                if (ObjectUtils.isEmpty(student)) {
                    studentPicList.add("e45cc91940ff9621469b324074b40499.png");
                } else {
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
                } else {
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
                    minioUtils.remove(objectItem.getObjectName());
                    totalSize += objectItem.getSize();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (totalSize / 1000 / 1000 / 1000 == 0) {
                if (totalSize / 1000 / 1000 == 0) {
                    return Result.ok("删除成功！共清理" + objectItems.size() + "个文件，释放" + totalSize / 1000 + "kb空间！");
                }
                return Result.ok("删除成功！共清理" + objectItems.size() + "个文件，释放" + totalSize / 1000 / 1000 + "mb空间！");
            }
            return Result.ok("删除成功！共清理" + objectItems.size() + "个文件，释放" + totalSize + "GB空间！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }


    /**
     * @author LuoYemao
     * @since 2022/11/5 23:18
     */
    @GetMapping("/detail")
    public Result detail() {
        try {
            FileDetailVo fileDetailVo = new FileDetailVo();
            List<ObjectItem> objectItems = minioUtils.listObjects("coesplus");
            Long totalSize = 0L;
            Map<String, Long> separateSize = new HashMap<>();
            List<String> type = new ArrayList<>();
            Map<String, Long> separateAmount = new HashMap<>();
            log.info(objectItems.toString());
            //处理文件名
            for (ObjectItem x : objectItems) {
                x.setObjectName(x.getObjectName().split("\\.")[1]);
                log.info(x.toString());
            }
            for (ObjectItem item : objectItems) {
                totalSize += item.getSize();
                if (separateSize.containsKey(item.getObjectName())) {
                    separateSize.put(item.getObjectName(), separateSize.get(item.getObjectName()) + item.getSize() / 1000);
                    separateAmount.put(item.getObjectName(), separateAmount.get(item.getObjectName()) + 1);
                } else {
                    type.add(item.getObjectName());
                    separateSize.put(item.getObjectName(), item.getSize() / 1000);
                    separateAmount.put(item.getObjectName(), 1L);
                }
            }
            List<FileItemVo> voList = new ArrayList<>();
            for (int i = 0; i < type.size(); i++) {
                FileItemVo vo = new FileItemVo();
                vo.setType(type.get(i));
                vo.setSeparateAmount(separateAmount.get(type.get(i)));
                vo.setSeparateSize(separateSize.get(type.get(i)));
                voList.add(vo);
            }
            fileDetailVo.setTotalAmount((long) objectItems.size())
                    .setTotalSize(totalSize / 1000)
                    .setSeparateDetail(voList);
            return Result.ok(fileDetailVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        }
    }
}
