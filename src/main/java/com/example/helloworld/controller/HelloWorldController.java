package com.example.helloworld.controller;

import com.example.helloworld.dto.HelloWorldResponse;
import com.example.helloworld.service.HelloWorldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping("/hello-world")
    public HelloWorldResponse helloWorld() {
        return helloWorldService.getHelloWorld();
    }
}
