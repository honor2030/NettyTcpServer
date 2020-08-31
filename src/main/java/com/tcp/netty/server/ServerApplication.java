package com.tcp.netty.server;

import com.tcp.netty.server.service.ServerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);

        //  서버 시작
        ServerService.start();
    }

}
