package com.coesplus.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan({"com.coesplus.common", "com.coesplus.admin"})
@EnableDiscoveryClient
public class CoesplusAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoesplusAdminApplication.class, args);
    }
}
