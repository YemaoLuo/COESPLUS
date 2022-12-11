package com.coesplus.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.coesplus.admin.service.AdministratorLoginLogService;
import com.coesplus.common.entity.AdministratorLoginLog;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class IPAddressTest {

    @Resource
    private AdministratorLoginLogService administratorLoginLogService;

    @Resource
    private Ip2regionSearcher ip2regionSearcher;


    @Test
    void testIPAdress() {
        String ip1 = "180.94.153.64";
        String ip2 = "223.104.72.255";
        String ip3 = "120.239.206.73";
        log.info(ip2regionSearcher.getAddress(ip1));
        log.info(ip2regionSearcher.getAddress(ip2));
        log.info(ip2regionSearcher.getAddress(ip3));
    }

    @Test
    void testDashBoard() {
        LambdaQueryWrapper<AdministratorLoginLog> adminQueryWrapper = new LambdaQueryWrapper<>();
        adminQueryWrapper.select(AdministratorLoginLog::getIp)
                .isNotNull(AdministratorLoginLog::getIp);
        List<AdministratorLoginLog> adminIpList = administratorLoginLogService.list(adminQueryWrapper);
        Map<String, Integer> result = new HashMap<>();
        for (AdministratorLoginLog adminIp : adminIpList) {
            IpInfo ipInfo = ip2regionSearcher.btreeSearch(adminIp.getIp());
            try {
                if (ObjectUtils.isNull(ipInfo)) {
                    if (result.containsKey("未知")) {
                        result.put("未知", result.get("未知") + 1);
                    } else {
                        result.put("未知", 1);
                    }
                }
                //处理内网IP
                else if (StringUtils.isEmpty(ipInfo.getCountry())) {
                    if (result.containsKey("内网")) {
                        result.put("内网", result.get("内网") + 1);
                    } else {
                        result.put("内网", 1);
                    }
                }
                //处理非中国IP
                else if (!ipInfo.getCountry().equals("中国")) {
                    if (result.containsKey(ipInfo.getCountry())) {
                        result.put(ipInfo.getCountry(), result.get(ipInfo.getCountry()) + 1);
                    } else {
                        result.put(ipInfo.getCountry(), 1);
                    }
                }
                //类似澳门地区IP
                else if (StringUtils.isEmpty(ipInfo.getCity())) {
                    if (result.containsKey(ipInfo.getProvince())) {
                        result.put(ipInfo.getProvince(), result.get(ipInfo.getProvince()) + 1);
                    } else {
                        result.put(ipInfo.getProvince(), 1);
                    }
                }
                //正常城市IP处理
                else if (result.containsKey(ipInfo.getCity())) {
                    result.put(ipInfo.getCity(), result.get(ipInfo.getCity()) + 1);
                } else {
                    result.put(ipInfo.getCity(), 1);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info(result.toString());
    }
}
