package com.coesplus.admin;

import com.coesplus.admin.vo.FileDetailVo;
import com.coesplus.admin.vo.FileItemVo;
import com.coesplus.common.minio.entity.ObjectItem;
import com.coesplus.common.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class fileControllerTest {

    @Resource
    private MinioUtils minioUtils;


    @Test
    public void detail() {
        FileDetailVo fileDetailVo = new FileDetailVo();
        List<ObjectItem> objectItems = minioUtils.listObjects("coesplus");
        Long totalSize = 0L;
        Map<String, Long> separateSize = new HashMap<>();
        List<String> type = new ArrayList<>();
        Map<String, Long> separateAmount = new HashMap<>();
        log.info(objectItems.toString());
        //处理文件名
        for (ObjectItem x : objectItems) {
            x.setObjectName(x.getObjectName().split("\\.")[1]);
            log.info(x.toString());
        }
        for (ObjectItem item : objectItems) {
            totalSize += item.getSize();
            if (separateSize.containsKey(item.getObjectName())) {
                separateSize.put(item.getObjectName(), separateSize.get(item.getObjectName()) + item.getSize() / 1000);
                separateAmount.put(item.getObjectName(), separateAmount.get(item.getObjectName()) + 1);
            } else {
                type.add(item.getObjectName());
                separateSize.put(item.getObjectName(), item.getSize() / 1000);
                separateAmount.put(item.getObjectName(), 1L);
            }
        }
        List<FileItemVo> voList = new ArrayList<>();
        for (int i = 0; i < type.size(); i++) {
            FileItemVo vo = new FileItemVo();
            vo.setType(type.get(i));
            vo.setSeparateAmount(separateAmount.get(type.get(i)));
            vo.setSeparateSize(separateSize.get(type.get(i)));
            voList.add(vo);
        }
        fileDetailVo.setTotalAmount((long) objectItems.size())
                .setTotalSize(totalSize / 1000)
                .setSeparateDetail(voList);
        log.info(fileDetailVo.toString());
    }
}
