package org.example.jobify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String redirectToReact() {
        return "redirect:/"; // Redirect to React's homepage
    }
}
