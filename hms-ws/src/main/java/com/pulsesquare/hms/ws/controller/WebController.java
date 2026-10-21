package com.pulsesquare.hms.ws.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden
public class WebController {

    @GetMapping({"", "/", "/hms", "/hms/"})
    public String apiDocs() {
        return "redirect:/swagger-ui/index.html";
    }
}
