
package com.opportunity360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Opportunity360BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                Opportunity360BackendApplication.class, args);
    }
}
