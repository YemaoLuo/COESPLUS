package com.coesplus.admin;

import com.coesplus.admin.vo.NacosDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class nacosTest {

    @Resource
    private DiscoveryClient discoveryClient;


    @Test
    public void testListService() {
        try {
            Map<String, List<NacosDetailVo>> map = new HashMap<>();
            // 获取注册nacos上面的所有微服务名称
            List<String> list = discoveryClient.getServices();
            log.info(list.toString());
            // 获取注册nacos上面的所有微服务实例
            for (String s : list) {
                List<ServiceInstance> list2 = discoveryClient.getInstances(s);
                List<NacosDetailVo> voList = new ArrayList<>();
                for (ServiceInstance service : list2) {
                    NacosDetailVo vo = new NacosDetailVo();
                    log.info(service.getServiceId() + "::" + service.getUri() + "::" + service.getMetadata());
                    vo.setServiceId(service.getServiceId())
                            .setUrl(service.getUri().toString())
                            .setWeight(service.getMetadata().get("nacos.weight"))
                            .setStatus(service.getMetadata().get("nacos.healthy"));
                    voList.add(vo);
                }
                map.put(s, voList);
            }
            log.info(map.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
