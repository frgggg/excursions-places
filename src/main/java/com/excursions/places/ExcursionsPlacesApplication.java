package com.excursions.places;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExcursionsPlacesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExcursionsPlacesApplication.class, args);
    }
}
