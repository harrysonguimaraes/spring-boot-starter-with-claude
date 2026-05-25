package com.example.helloworld.service;

import com.example.helloworld.dto.HelloWorldResponse;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {

    public HelloWorldResponse getHelloWorld() {
        return new HelloWorldResponse("hello world!");
    }
}
