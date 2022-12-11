package com.coesplus.admin.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginVo {

    private String username;

    private String password;
}
