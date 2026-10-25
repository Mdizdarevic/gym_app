package hr.algebra.gym_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile/account";
    }

    @GetMapping("/bookings")
    public String bookings() {
        return "bookings/empty"; 
    }

    @GetMapping("/memberships")
    public String memberships() {
        return "memberships/manage";
    }

    @GetMapping("/ai/feedback")
    public String aiFeedback() {
        return "ai/feedback";
    }
}