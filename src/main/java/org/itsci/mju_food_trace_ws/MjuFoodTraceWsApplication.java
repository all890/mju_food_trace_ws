package org.itsci.mju_food_trace_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MjuFoodTraceWsApplication {

    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/mju_food_trace");
        SpringApplication.run(MjuFoodTraceWsApplication.class, args);
    }

}
