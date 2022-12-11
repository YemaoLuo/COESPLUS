package com.coesplus.coes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan({"com.coesplus.common", "com.coesplus.coes"})
@EnableDiscoveryClient
public class CoesplusCoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoesplusCoesApplication.class, args);
    }

}
