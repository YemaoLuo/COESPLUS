package com.coesplus.coes.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRankVo {

    private String id;

    private String name;

    private int count;
}
