package cn.fuguang.kefu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 智能客服微服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.fuguang.kefu.mapper")
public class ContainerKefuApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerKefuApplication.class, args);
    }
}
