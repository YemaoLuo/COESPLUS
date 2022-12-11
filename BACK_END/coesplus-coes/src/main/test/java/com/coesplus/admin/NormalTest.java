package com.coesplus.admin;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NormalTest {

   @Test
   void timeTest() {
      String formatTime = DateUtil.secondToTime(60 * 60 * 24 * 2 + 3 + 60 * 4);
      String[] splitTime = formatTime.split(":");
      Map<String, String> result = new HashMap<>();
      if (Integer.parseInt(splitTime[0]) >= 24) {
         String day = String.valueOf(Integer.parseInt(splitTime[0]) / 24);
         if (day.length() == 1) {
            day = "0" + day;
         }
         result.put("day", day);
      }else {
         result.put("day", "00");
      }
      String hour = String.valueOf(Integer.parseInt(splitTime[0]) - Integer.parseInt(result.get("day")) * 24);
      if (hour.length() == 1) {
         hour = "0" + hour;
      }
      result.put("hour", hour);
      result.put("minute", splitTime[1]);
      result.put("second", splitTime[2]);
      log.info(result.toString());
   }
}
