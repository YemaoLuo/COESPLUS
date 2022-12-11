package com.coesplus.admin.dto;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

@Data
@Accessors(chain = true)
public class UserDto {

    private String id;

    private String email;

    private String name;

    private String role;

    @SneakyThrows
    public UserDto(Object object) {
        Method getId = object.getClass().getMethod("getId");
        this.id = (String) getId.invoke(object);

        Method getEmail = object.getClass().getMethod("getEmail");
        this.email = (String) getEmail.invoke(object);

        Method getName = object.getClass().getMethod("getName");
        this.name = (String) getName.invoke(object);

        String userrole = object.getClass().getSimpleName().equals("Student") ? "学生" : "教师";
        this.role = userrole;
    }

}
