package com.coesplus.admin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class testUrlSplit {

   @Test
   public void testSplit() {
      // http://192.168.31.234:8001/#/form/step-form
      String url = "http://127.0.0.1:7002/api/admin/system/activate/0836260e0b68b768dcbf6123bf0eac8d?role=admin";
      String[] split = url.split("/");
      String baseUrl = split[0] + "//" + split[1] + split[2] + "/" + split[3];
      log.info(baseUrl);
   }
}
