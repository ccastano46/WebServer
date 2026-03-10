package org.example.controller;

import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;
import org.example.module.annotations.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String greetingTemplate = "Hello, %s!";
    private final AtomicLong requestCounter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello " + name;
    }
}
