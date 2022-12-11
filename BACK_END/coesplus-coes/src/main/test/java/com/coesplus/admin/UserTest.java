package com.coesplus.admin;

import com.coesplus.coes.dto.UserDto;
import com.coesplus.common.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UserTest {

   @Test
   public void generatePassword() {
      Student student = new Student();
      student.setId("123456789");
      student.setEmail("bdhcmakbj,@k;a;");
      UserDto userDto = new UserDto(student);
      log.info(userDto.toString());
   }
}
