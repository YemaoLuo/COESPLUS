package com.coesplus.admin;

import com.coesplus.common.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ThreadLocalUtilsTest {

   @Test
   void testThreadLocalUtils() {
      BaseContext bs = new BaseContext();
      bs.setValue("123", "321");
      Object value = bs.getValue("123");
      log.info("value: " + value.toString());
      bs.removeValue("123");
   }
}
