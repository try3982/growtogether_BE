package com.campfiredev.growtogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GrowtogetherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrowtogetherApplication.class, args);
    }

}
