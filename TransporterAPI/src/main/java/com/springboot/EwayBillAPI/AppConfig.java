package com.springboot.EwayBillAPI;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class AppConfig {

    @PostConstruct
    public void init() {
        // Set the default timezone programmatically
        TimeZone.setDefault(TimeZone.getTimeZone("IST"));
    }
}

