package com.coesplus.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CoesplusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoesplusGatewayApplication.class, args);
    }

}
