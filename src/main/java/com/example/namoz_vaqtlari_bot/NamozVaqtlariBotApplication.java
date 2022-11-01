package com.example.namoz_vaqtlari_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class NamozVaqtlariBotApplication {



    public static void main(String[] args) {
        SpringApplication.run(NamozVaqtlariBotApplication.class, args);
    }

}
