package com.coesplus.coes.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class FileDetailVo {

   private Map<String, Integer> separateAmount;

   private Long totalSize;

   private Long totalAmount;

   private Map<String, Long> separateSize;
}
