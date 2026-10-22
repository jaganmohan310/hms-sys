package com.pulsesquare.hms.ws.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web Navigation Controller.
 * Routes root and `/hms` traffic to the static Developer & Operations Portal.
 */
@Controller
@Hidden
public class WebController {

    @GetMapping({"", "/", "/hms", "/hms/"})
    public String hmsPortal() {
        return "forward:/hms/index.html";
    }
}
