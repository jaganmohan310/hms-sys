package com.pulsesquare.hms.ws.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden
public class WebController {

    @GetMapping({"", "/", "/login"})
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping({"/hms", "/hms/"})
    public String hmsDocs() {
        return "redirect:/swagger-ui/index.html";
    }
}
