package br.com.v1nic1us.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/primaryroute")
public class primaryController {
    @GetMapping("/")
    public String primaryRoute() {
        return "teste";
    }
}
