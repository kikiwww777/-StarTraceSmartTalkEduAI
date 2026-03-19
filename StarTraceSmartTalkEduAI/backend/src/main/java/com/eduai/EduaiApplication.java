package com.eduai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.eduai.Mapper")
@EnableScheduling
public class EduaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduaiApplication.class, args);
    }

}
