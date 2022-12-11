package com.coesplus.coes.dto;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

@Data
@Accessors(chain = true)
public class UserLoginLogDto {

    private String id;

    private String ip;

    private String role;

    @SneakyThrows
    public UserLoginLogDto(Object object) {
        Method getId = object.getClass().getMethod("getId");
        this.id = (String) getId.invoke(object);

        Method getEmail = object.getClass().getMethod("getIp");
        this.ip = (String) getEmail.invoke(object);

        String username = object.getClass().getSimpleName().equals("StudentLoginLog") ? "学生" : "教师";
        this.role = username;
    }

}
