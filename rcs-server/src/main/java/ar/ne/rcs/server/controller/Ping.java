package ar.ne.rcs.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("ping")
@RestController
public class Ping {

    @GetMapping("")
    public String resp() {
        return "Hello!";
    }
}
