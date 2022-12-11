package com.coesplus.admin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

@Slf4j
public class GeneratePassword {

   @Test
   public void generatePassword() {
      String password = "123456";
      log.info(DigestUtils.md5DigestAsHex(password.getBytes()));
   }
}
