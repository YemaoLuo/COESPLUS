package com.coesplus.coes.dto;

import com.coesplus.coes.vo.PermissionVo;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserDto {

    private String id;

    private String email;

    private String name;

    private String role;

    private String photo;
    
    private String joinYear;

    private String faculty;

    private String telephone;

    private String sexChar;

    private List<PermissionVo> permissions;


    @SneakyThrows
    public UserDto(Object object) {
        Method getId = object.getClass().getMethod("getId");
        this.id = (String) getId.invoke(object);

        Method getEmail = object.getClass().getMethod("getEmail");
        this.email = (String) getEmail.invoke(object);

        Method getName = object.getClass().getMethod("getName");
        this.name = (String) getName.invoke(object);

        Method getPhoto = object.getClass().getMethod("getPhoto");
        this.photo = (String) getPhoto.invoke(object);

        Method getTelephone = object.getClass().getMethod("getTelephone");
        this.telephone = (String) getTelephone.invoke(object);

        Method getJoinYear = object.getClass().getMethod("getJoinYear");
        this.joinYear = (String) getJoinYear.invoke(object);

        String userRole = object.getClass().getSimpleName().equals("Student") ? "student" : "teacher";
        this.role = userRole;

        List<PermissionVo> permissions = PermissionVo.generatePermission(userRole);
        this.permissions = permissions;
    }

}
