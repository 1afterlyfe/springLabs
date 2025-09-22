package org.example.myspringwebhelloworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloPageController {
    @GetMapping("/home")
    public String index() {
        return "index";
    }
}
