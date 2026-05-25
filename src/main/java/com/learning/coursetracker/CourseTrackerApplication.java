package com.learning.coursetracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.learning.coursetracker.mapper")
public class CourseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseTrackerApplication.class, args);
    }
}
