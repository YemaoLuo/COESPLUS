package com.coesplus.coes.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Data
@Accessors(chain = true)
public class PermissionVo {

    private String label;

    private String value;

    public PermissionVo() {
    }

    public static List<PermissionVo> generatePermission(String role) {
        PermissionVo vo = new PermissionVo();
        if (role.equals("student")) {
            vo.setLabel("选课")
                    .setValue("select_course");
        } else {
            vo.setLabel("老师打分")
                    .setValue("score");
        }
        return Arrays.asList(vo);
    }
}
