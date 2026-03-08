package org.example.controller;

import org.example.module.annotations.GetMapping;
import org.example.module.annotations.RequestParam;
import org.example.module.annotations.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/bye")
    public String bye() {
        return "Goodbye World!";
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/info")
    public String info(@RequestParam(value = "name") String name,
                       @RequestParam(value = "years") String years) {
        return "Hola " + name + ", tienes " + years + " años!";
    }

    @GetMapping("/profile")
    public String profile(@RequestParam(value = "name") String name,
                          @RequestParam(value = "city") String city,
                          @RequestParam(value = "job") String job) {
        return "Hola " + name + ", vives en " + city + " y trabajas como " + job + ".";
    }


    public String ignored() {
        return "Este método no tiene @GetMapping, será ignorado por el framework.";
    }
}
