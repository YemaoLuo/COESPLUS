//package com.coesplus.admin;
//
//import cn.hutool.core.date.LocalDateTimeUtil;
//import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
//import com.coesplus.admin.schedule.RCScheduleTask;
//import com.coesplus.admin.service.CronService;
//import com.coesplus.common.entity.Cron;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.jni.Time;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//
//import javax.annotation.Resource;
//
//@SpringBootTest
//@Slf4j
//public class scheduleTaskTest {
//
//   @Resource
//   private CronService iCronService;
//
//   @Resource
//   private RCScheduleTask rcScheduleTask;
//
//
//   @Test
//   @SneakyThrows
//   public void testRun() {
//      Cron cron = iCronService.getById(1);
//      if (ObjectUtils.isNotEmpty(cron)) {
//         boolean flag = rcScheduleTask.start(cron);
//         if (flag) {
//            log.info(cron + "执行成功！");
//         }else {
//            log.error(cron + "执行失败！");
//         }
//      }
//      Thread.sleep(10000);
//   }
//}
