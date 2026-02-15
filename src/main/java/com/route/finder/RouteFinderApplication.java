package com.route.finder;

import com.route.finder.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = AppConfig.class)
public class RouteFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteFinderApplication.class, args);
    }

}
