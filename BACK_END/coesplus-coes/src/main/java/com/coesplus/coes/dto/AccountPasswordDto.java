package com.coesplus.coes.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountPasswordDto {

   private String prePassword;

   private String newPassword;
}
