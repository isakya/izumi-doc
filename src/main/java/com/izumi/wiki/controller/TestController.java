package com.izumi.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @Value("${test.hello:TEST}") // 冒号后面的是默认值
    private String testHello;

    @GetMapping("/hello")
    public String hello() {
        return "hello world!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "hello world! Post. " + name;
    }
}
