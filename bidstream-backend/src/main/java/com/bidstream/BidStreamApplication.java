package com.bidstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BidStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidStreamApplication.class, args);
    }
}
