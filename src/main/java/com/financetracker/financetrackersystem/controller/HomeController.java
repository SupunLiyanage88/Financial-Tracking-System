package com.financetracker.financetrackersystem.controller;

import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.service.JWTService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
public class HomeController {

    private final JWTService jwtService;

    public HomeController(JWTService jwtService){
        this.jwtService = jwtService;
    }
    

    @GetMapping("/")
    public String getHello() {
        return "Hi";
    }

    @PostMapping("/login")
    public String login() {
        return null;
    }

    @GetMapping("/username")
    public String getUsername(@RequestParam String token) {
        return jwtService.getUsername(token);
    }
    
}
