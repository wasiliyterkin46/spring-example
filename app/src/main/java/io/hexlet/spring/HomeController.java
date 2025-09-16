package io.hexlet.spring;

import io.hexlet.spring.controller.PostController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HomeController {
    public static void main(String[] args) {
        SpringApplication.run(new Class[]{HomeController.class, PostController.class}, args);
//        SpringApplication.run(HomeController.class, args);
    }

    @GetMapping("/")
    String home() {
        System.out.printf("");
        return "Добро пожаловать в Hexlet Spring Blog!";
    }

    @GetMapping("/about")
    public String about() {
        return "This is simple Spring blog!";
    }

}
